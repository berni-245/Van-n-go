package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Repository
public class BookingJdbcDao implements BookingDao{
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcBookingInsert;
    private final SimpleJdbcInsert jdbcReservationInsert;

    private final ClientDao clientDao;

    private final RowMapper<Booking> ROW_MAPPER;

    public BookingJdbcDao(final DataSource ds) {
        clientDao = new ClientJdbcDao(ds);
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcBookingInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("booking");
        jdbcReservationInsert = new SimpleJdbcInsert(ds)
                .withTableName("reservation");

        ROW_MAPPER = (rs, rowNum) -> new Booking(
                rs.getLong("booking_id"),

                clientDao.findById(rs.getLong("client_id")).orElseThrow(),
                rs.getDate("date").toLocalDate(),
                rs.getBoolean("is_confirmed")
        );
    }

    @Override
    public Optional<Booking> appointBooking(long driverId, long clientId, LocalDate date) {
        if(isDriverBookedForThatDay(driverId, date) || isClientAlreadyAppointed(driverId, clientId, date))
            return Optional.empty();

        Map<String, Object> bookingData = Map.of("date", date);
        final Number generatedBookingId = jdbcBookingInsert.executeAndReturnKey(bookingData);

        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("driver_id", driverId);
        reservationData.put("client_id", clientId);
        reservationData.put("booking_id", generatedBookingId);
        reservationData.put("is_confirmed", 0);
        jdbcReservationInsert.execute(reservationData);
        return Optional.of(new Booking(generatedBookingId.longValue(), clientDao.findById(clientId).orElseThrow(), date, false));
    }

    @Override
    public List<Booking> getBookings(long driverId) {
        return jdbcTemplate.query("""
                        select booking_id, client_id, date, is_confirmed
                        from booking b join reservation r on b.id = r.booking_id
                        where driver_id = ?""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);

    }

    @Override
    public List<Booking> getBookingsByDate(long driverId, LocalDate date) {
        return jdbcTemplate.query("""
                    select booking_id, client_id, date, is_confirmed
                    from booking b join reservation r on b.id = r.booking_id
                    where driver_id = ? and date = ?""",
                new Object[]{driverId, date.toString()},
                new int[]{Types.BIGINT, Types.DATE},
                ROW_MAPPER);
    }

    @Override
    public void acceptBooking(long bookingId) {
        jdbcTemplate.update("""
                    update reservation
                    set is_confirmed = ?
                    where booking_id = ?""",
                new Object[]{Boolean.TRUE, bookingId},
                new int[]{Types.BOOLEAN, Types.BIGINT});
        jdbcTemplate.update("""
                    delete
                    from booking
                    where id != ? and date = (
                        select distinct date
                        from booking b2 where b2.id = ?
                    )""",
                new Object[]{bookingId,bookingId},
                new int[]{Types.BIGINT,Types.BIGINT});
    }

    @Override
    public void rejectBooking(long bookingId) {
        jdbcTemplate.update("""
                    delete
                    from booking
                    where id = ?""",
                new Object[]{bookingId},
                new int[]{Types.BIGINT});
    }

    private boolean isDriverBookedForThatDay(long driverId, LocalDate date) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from booking b join reservation r on b.id = r.booking_id
                        where driver_id = ? and date = ? and is_confirmed = ?""",
                new Object[]{driverId, date.toString(), Boolean.TRUE},
                new int[]{Types.BIGINT, Types.DATE, Types.BOOLEAN},
                Integer.class);

        return count != null && count > 0;
    }

    private boolean isClientAlreadyAppointed(long driverId, long clientId, LocalDate date) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from booking b join reservation r on b.id = r.booking_id
                        where driver_id = ? and client_id = ? and date = ?""",
                new Object[]{driverId, clientId, date.toString()},
                new int[]{Types.BIGINT, Types.BIGINT, Types.DATE},
                Integer.class);

        return count != null && count > 0;
    }
}
