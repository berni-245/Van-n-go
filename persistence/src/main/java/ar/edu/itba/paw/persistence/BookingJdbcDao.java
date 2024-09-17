package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.User;
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

    private final RowMapper<Booking> ROW_MAPPER =
            (rs, rowNum) -> new Booking(
                    rs.getLong("booking_id"),

                    clientIdToUser(rs.getLong("client_id")),
                    rs.getDate("date").toLocalDate(),
                    rs.getBoolean("is_confirmed")
            );

    public BookingJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcBookingInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("booking");
        jdbcReservationInsert = new SimpleJdbcInsert(ds)
                .withTableName("reservation");
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
        return Optional.of(new Booking(generatedBookingId.longValue(), clientIdToUser(clientId), date, false));
    }

    @Override
    public List<Booking> getBookings(long driverId) {
        return jdbcTemplate.query("""
                        select booking_id, client_id, date, is_confirmed
                        from booking b join reservation r on b.id = r.booking_id
                        where driver_id = ? and is_confirmed = ?""",
                new Object[]{driverId, Boolean.FALSE},
                new int[]{Types.BIGINT, Types.BOOLEAN},
                ROW_MAPPER);

    }

    @Override
    public List<Booking> getBookingsByDate(long driverId, LocalDate date) {
        return jdbcTemplate.query("""
                    select booking_id, client_id, date, is_confirmed
                    from booking b join reservation r on b.id = r.booking_id
                    where driver_id = ? and date = ? and is_confirmed = ?""",
                new Object[]{driverId, date.toString(), Boolean.FALSE},
                new int[]{Types.BIGINT, Types.DATE, Types.BOOLEAN},
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
                    where booking_id != ?""",
                new Object[]{bookingId},
                new int[]{Types.BIGINT});
    }

    @Override
    public void rejectBooking(long bookingId) {
        jdbcTemplate.update("""
                    delete 
                    from booking
                    where booking_id = ?""",
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

    private User clientIdToUser(long clientId) {
        return jdbcTemplate.query("""
                    select id, username, mail, password
                    from app_user
                    where id = ?""",
                new Object[]{clientId},
                new int[]{Types.BIGINT},
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("mail"),
                        rs.getString("password")
                )).stream().findFirst().orElseThrow();
    }
}
