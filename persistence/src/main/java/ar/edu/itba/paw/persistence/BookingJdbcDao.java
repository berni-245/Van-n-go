package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

public class BookingJdbcDao implements BookingDao{
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcBookingInsert;
    private final SimpleJdbcInsert jdbcReservationInsert;

    private static final RowMapper<Booking> ROW_MAPPER =
            (rs, rowNum) -> new Booking(
                    rs.getLong("booking_id"),
                    rs.getLong("client_id"),
                    rs.getDate("date")
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
    public Optional<Booking> appointBooking(long driverId, long clientId, Date date) {
        if(isDriverAppointedForThatDay(driverId, date))
            return Optional.empty();

        Map<String, Date> bookingData = Map.of("date", date);
        final Number generatedBookingId = jdbcBookingInsert.executeAndReturnKey(bookingData);

        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("driver_id", driverId);
        reservationData.put("client_id", clientId);
        reservationData.put("booking_id", generatedBookingId);
        jdbcReservationInsert.execute(reservationData);
        return Optional.of(new Booking(generatedBookingId.longValue(), clientId, date));
    }

    @Override
    public List<Booking> getBookings(long driverId) {
        return jdbcTemplate.query("""
                        select booking_id, client_id, date
                        from booking b join reservation r on b.id = r.booking_id
                        where driver_id = ?""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);

    }

    @Override
    public List<Booking> getBookingsByDate(long driverId, Date date) {
        return jdbcTemplate.query("""
                    select booking_id, client_id, date
                    from booking b join reservation r on b.id = r.booking_id
                    where driver_id = ? and date = ?""",
                new Object[]{driverId, date},
                new int[]{Types.BIGINT, Types.DATE},
                ROW_MAPPER);
    }

    private boolean isDriverAppointedForThatDay(long driverId, Date date) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from booking b join reservation r on b.id = r.booking_id
                        where driver_id = ?""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                Integer.class);

        return count != null && count > 0;
    }
}
