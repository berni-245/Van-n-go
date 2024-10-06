package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Repository
public class BookingJdbcDao implements BookingDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcBookingInsert;

    private final ClientDao clientDao;
    private final DriverDao driverDao;
    private final VehicleDao vehicleDao;
    private final ImageDao imageDao;
    private final ZoneDao zoneDao;

    private final RowMapper<Booking> ROW_MAPPER;

    public BookingJdbcDao(final DataSource ds) {
        clientDao = new ClientJdbcDao(ds);
        driverDao = new DriverJdbcDao(ds);
        vehicleDao = new VehicleJdbcDao(ds);
        imageDao = new ImageJdbcDao(ds);
        zoneDao = new ZoneJdbcDao(ds);

        jdbcTemplate = new JdbcTemplate(ds);
        jdbcBookingInsert = new SimpleJdbcInsert(ds)
                .usingGeneratedKeyColumns("id")
                .withTableName("booking");

        ROW_MAPPER = (rs, rowNum) -> {
            Vehicle vehicle = vehicleDao.findById(rs.getLong("vehicle_id")).orElseThrow();

            return new Booking(
                    rs.getLong("id"),
                    clientDao.findById(rs.getLong("client_id")).orElseThrow(),
                    driverDao.findById(vehicle.getDriverId()).orElseThrow(),
                    vehicle,
                    zoneDao.getZone(rs.getLong("zone_id")).orElseThrow(),
                    rs.getDate("date").toLocalDate(),
                    getHourInterval(rs.getLong("hour_start_id"), rs.getLong("hour_end_id")),
                    BookingState.valueOf(rs.getString("state").toUpperCase()),
                    rs.getObject("rating", Integer.class),
                    rs.getString("review"),
                    rs.getObject("proof_of_payment", Integer.class)
            );
        };
    }

    @Override
    public Optional<Booking> appointBooking(long vehicleId, long clientId, long zoneId, LocalDate date, HourInterval hourInterval) {
        if (date.isBefore(LocalDate.now()) ||
                ! isVehicleAvailableInThatTimeAndZone(vehicleId, zoneId, date, hourInterval) ||
                isVehicleBookedForThatTime(vehicleId, date, hourInterval) ||
                isClientAlreadyAppointedForThatTimeAndZone(vehicleId, clientId, zoneId, date, hourInterval)
                )
            return Optional.empty();

        Long generatedBookingId = Objects.requireNonNull(jdbcTemplate.queryForObject(
                """
                INSERT INTO booking (date, hour_start_id, hour_end_id, client_id, vehicle_id, zone_id, state)
                VALUES (?, ?, ?, ?, ?, ?, ?::state)
                RETURNING id""",
                new Object[]{date.toString(), hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(), clientId, vehicleId, zoneId, BookingState.PENDING.name()},
                new int[]{Types.DATE, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.VARCHAR},
                Long.class
        ));

        return getBookingById(generatedBookingId);
    }

    @Override
    public Optional<Booking> getBookingById(long bookingId) {
        return jdbcTemplate.query("""
                    select *
                    from booking
                    where id = ?""",
                new Object[]{bookingId},
                new int[]{Types.BIGINT},
                ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<Booking> getBookings(long driverId) {
        return jdbcTemplate.query("""
                        select b.id, b.date, b.hour_start_id, b.hour_end_id, b.client_id, b.vehicle_id, b.zone_id, b.state, b.proof_of_payment, b.rating, b.review
                        from booking b join vehicle v on b.vehicle_id = v.id
                        where driver_id = ?
                        order by b.date, b.hour_start_id, b.hour_end_id""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getBookingsByDate(long driverId, LocalDate date) {
        return jdbcTemplate.query("""
                        select b.id, b.date, b.hour_start_id, b.hour_end_id, b.client_id, b.vehicle_id, b.zone_id, b.state, b.proof_of_payment, b.rating, b.review
                        from booking b join vehicle v on b.vehicle_id = v.id
                        where driver_id = ? and date = ?""",
                new Object[]{driverId, date.toString()},
                new int[]{Types.BIGINT, Types.DATE},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        return jdbcTemplate.query("""
                        select *
                        from booking
                        where vehicle_id = ?
                        order by date, hour_start_id, hour_end_id""",
                new Object[]{vehicleId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date) {
        return jdbcTemplate.query("""
                        select *
                        from booking
                        where vehicle_id = ? and date = ?""",
                new Object[]{vehicleId, date.toString()},
                new int[]{Types.BIGINT, Types.DATE},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getClientBookings(long clientId) {
        return jdbcTemplate.query("""
                        select *
                        from booking
                        where client_id = ? AND date >= CURRENT_DATE
                        order by b.date, b.hour_start_id, b.hour_end_id""",
                new Object[]{clientId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getClientHistory(long clientId) {
        return jdbcTemplate.query("""
                        select *
                        from booking
                        where client_id = ? AND date < CURRENT_DATE
                        order by b.date, b.hour_start_id, b.hour_end_id""",
                new Object[]{clientId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public void acceptBooking(long bookingId) {
        Optional<Booking> bookingOptional = getBookingById(bookingId);
        if(bookingOptional.isEmpty()) {
            return;
        }
        Booking booking = bookingOptional.get();
        HourInterval bookingHI = booking.getHourInterval();
        if(isVehicleBookedForThatTime(booking.getVehicle().getId(), booking.getDate(), booking.getHourInterval()) ||
                ! booking.getBookingState().equals(BookingState.PENDING))
            return;
        jdbcTemplate.update("""
                    update booking
                    set state = ?::state
                    where id = ?""",
                new Object[]{BookingState.ACCEPTED.toString(), bookingId},
                new int[]{Types.VARCHAR, Types.BIGINT});
        jdbcTemplate.update("""
                update booking
                set state = ?::state
                where id != ?
                and date = ?
                and vehicle_id = ?
                and (
                        (hour_start_id >= ?
                        and hour_start_id <= ?)
                    or  (hour_end_id >= ?
                        and hour_end_id <= ?))""",
                new Object[]{BookingState.REJECTED.toString(), bookingId,
                        booking.getDate().toString(), booking.getVehicle().getId(),
                        bookingHI.getStartHourBlockId(), bookingHI.getEndHourBlockId(),
                        bookingHI.getStartHourBlockId(), bookingHI.getEndHourBlockId()},
                new int[]{Types.VARCHAR, Types.BIGINT, Types.DATE, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT});
    }

    @Override
    public void rejectBooking(long bookingId) {
        jdbcTemplate.update("""
                    update booking
                    set state = ?::state
                    where id = ?""",
                new Object[]{BookingState.REJECTED.toString(), bookingId},
                new int[]{Types.VARCHAR, Types.BIGINT});
    }

    @Override
    public void setRatingAndReview(long bookingId, int rating, String review) {
        jdbcTemplate.update("""
                    update booking
                    set rating = ?, review = ?
                    where id = ?
                """,
                new Object[]{rating, review, bookingId},
                new int[]{Types.INTEGER, Types.VARCHAR, Types.BIGINT});
        Long driverId = jdbcTemplate.queryForObject("""
                select driver_id
                from booking b join vehicle v on b.vehicle_id = v.id
                where b.id = ?
                """,
                new Object[]{bookingId},
                new int[]{Types.BIGINT},
                Long.class);
        if (driverId != null) {
            driverDao.updateDriverRating(driverId);
        }
    }

    private HourInterval getHourInterval(long hour_start_id, long hour_end_id) {
        int startHour = Objects.requireNonNull(jdbcTemplate.queryForObject("""
                            select t_start
                            from hour_block
                            where id = ?""",
                new Object[]{hour_start_id},
                new int[]{Types.BIGINT},
                Time.class)).toLocalTime().getHour();
        int endHour = Objects.requireNonNull(jdbcTemplate.queryForObject("""
                            select t_end
                            from hour_block
                            where id = ?""",
                new Object[]{hour_end_id},
                new int[]{Types.BIGINT},
                Time.class)).toLocalTime().getHour();

        return new HourInterval(startHour, endHour);
    }

    private boolean isVehicleAvailableInThatTimeAndZone(long vehicleId, long zoneId, LocalDate date, HourInterval hourInterval) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from weekly_availability wa
                        where wa.vehicle_id = ?
                            and wa.zone_id = ?
                            and wa.week_day = (
                                select case when extract(dow from ?::date) = 0 then 7
                                            else extract(dow from ?::date) end)
                            and wa.hour_block_id >= ?
                            and wa.hour_block_id <= ?
                """,
                new Object[]{vehicleId, zoneId, date.toString(), date.toString(), hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId()},
                new int[]{Types.BIGINT, Types.BIGINT, Types.DATE, Types.DATE, Types.BIGINT, Types.BIGINT},
                Integer.class
                );
        return count != null && count == hourInterval.getHourCount();
    }

    private boolean isVehicleBookedForThatTime(long vehicleId, LocalDate date, HourInterval hourInterval) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from booking b
                        where b.vehicle_id = ? and b.date = ? and b.state = ?::state
                        and (
                                (b.hour_start_id >= ?
                                and b.hour_start_id <= ?)
                            or  (b.hour_end_id >= ?
                                and b.hour_end_id <= ?)
                        )""",
                new Object[]{vehicleId, date.toString(), BookingState.ACCEPTED.toString(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId()},
                new int[]{Types.BIGINT, Types.DATE, Types.VARCHAR,
                        Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT},
                Integer.class);

        return count != null && count > 0;
    }

    private boolean isClientAlreadyAppointedForThatTimeAndZone(long vehicleId, long clientId, long zoneId, LocalDate date, HourInterval hourInterval) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from booking b
                        where b.vehicle_id = ? and b.date = ? and b.client_id = ? and b.zone_id = ?
                        and (
                                (b.hour_start_id >= ?
                                and b.hour_start_id <= ?)
                            or  (b.hour_end_id >= ?
                                and b.hour_end_id <= ?)
                        )""",
                new Object[]{vehicleId, date.toString(), clientId, zoneId,
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId()},
                new int[]{Types.BIGINT, Types.DATE, Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT},
                Integer.class);

        return count != null && count > 0;
    }
}
