package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class BookingJdbcDao implements BookingDao {
    private final JdbcTemplate jdbcTemplate;

    private final ClientDao clientDao;
    private final DriverDao driverDao;
    private final VehicleDao vehicleDao;
    private final ZoneDao zoneDao;

    private final RowMapper<Booking> ROW_MAPPER;

    public BookingJdbcDao(final DataSource ds) {
        clientDao = new ClientJdbcDao(ds);
        driverDao = new DriverJdbcDao(ds);
        vehicleDao = new VehicleJdbcDao(ds);
        zoneDao = new ZoneJpaDao();       //Deberian ser con autowired...

        jdbcTemplate = new JdbcTemplate(ds);

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
                    rs.getObject("proof_of_payment", Integer.class),
                    rs.getString("job_description")
            );
        };
    }

    @Override
    public Optional<Booking> appointBooking(long vehicleId, long clientId, long zoneId, LocalDate date, HourInterval hourInterval, String jobDesription) {
        if (date.isBefore(LocalDate.now()) ||
            !isVehicleAvailableInThatTimeAndZone(vehicleId, zoneId, date, hourInterval) ||
            isVehicleBookedForThatTime(vehicleId, date, hourInterval) ||
            isClientAlreadyAppointedForThatTimeAndZone(vehicleId, clientId, zoneId, date, hourInterval)
        )
            return Optional.empty();

        jdbcTemplate.update(
                """
                        INSERT INTO booking (
                            date,
                            hour_start_id,
                            hour_end_id,
                            client_id,
                            vehicle_id,
                            zone_id,
                            state,
                            job_description
                        )
                        VALUES (?, ?, ?, ?, ?, ?, cast(? as state), ?)""",
                new Object[]{
                        date.toString(),
                        hourInterval.getStartHourBlockId(),
                        hourInterval.getEndHourBlockId(),
                        clientId,
                        vehicleId,
                        zoneId,
                        BookingState.PENDING.name(),
                        jobDesription
                },
                new int[]{
                        Types.DATE,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.VARCHAR,
                        Types.VARCHAR
                }
        );

        return jdbcTemplate.query("""
                        select *
                        from booking
                        where date = ? and hour_start_id = ? and hour_end_id = ?
                        and client_id = ? and vehicle_id = ? and zone_id = ?
                        and state = cast(? as state)""",
                new Object[]{
                        date.toString(),
                        hourInterval.getStartHourBlockId(),
                        hourInterval.getEndHourBlockId(),
                        clientId,
                        vehicleId,
                        zoneId,
                        BookingState.PENDING.name()
                },
                new int[]{
                        Types.DATE,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.BIGINT,
                        Types.VARCHAR
                },
                ROW_MAPPER).stream().findFirst();
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
    public List<Booking> getDriverBookings(long driverId, int offset) {
        return jdbcTemplate.query("""
                        select b.id, b.date, b.hour_start_id,
                        b.hour_end_id, b.client_id, b.vehicle_id,
                        b.zone_id, b.state,
                        b.proof_of_payment, b.rating,
                        b.review, b.job_description
                        from booking b join vehicle v on b.vehicle_id = v.id
                        where driver_id = ?
                        order by b.date, b.hour_start_id, b.hour_end_id limit ? offset ?""",
                new Object[]{driverId, Pagination.BOOKINGS_PAGE_SIZE, offset},
                new int[]{Types.BIGINT, Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getDriverHistory(long driverId, int offset) {
        return jdbcTemplate.query("""
                        select b.id, b.date, b.hour_start_id,
                        b.hour_end_id, b.client_id, b.vehicle_id,
                        b.zone_id, b.state,
                        b.proof_of_payment, b.rating,
                        b.review, b.job_description
                        from booking b join vehicle v on b.vehicle_id = v.id
                        where driver_id = ? AND b.date < CURRENT_DATE
                        order by b.date, b.hour_start_id, b.hour_end_id limit ? offset ?""",
                new Object[]{driverId, Pagination.BOOKINGS_PAGE_SIZE, offset},
                new int[]{Types.BIGINT, Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public List<Booking> getAllDriverBookings(long driverId) {
        return jdbcTemplate.query("""
                        select b.id, b.date, b.hour_start_id,
                        b.hour_end_id, b.client_id, b.vehicle_id,
                        b.zone_id, b.state,
                        b.proof_of_payment, b.rating,
                        b.review, b.job_description
                        from booking b join vehicle v on b.vehicle_id = v.id
                        where driver_id = ?
                        order by b.date, b.hour_start_id, b.hour_end_id""",
                new Object[]{driverId},
                new int[]{Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public int getDriverBookingCount(long driverId) {
        String sql = """
                select count(*)
                from booking b
                join vehicle v on b.vehicle_id = v.id
                where driver_id = ?""";
        Integer aux = jdbcTemplate.queryForObject(sql, new Object[]{driverId}, Integer.class);
        if(aux == null)
            return 0;
        return aux;
    }

    @Override
    public int getDriverHistoryCount(long driverId) {
        String sql = """
                select count(*)
                from booking b
                join vehicle v on b.vehicle_id = v.id
                where driver_id = ? AND b.date < CURRENT_DATE""";
        Integer aux = jdbcTemplate.queryForObject(sql, new Object[]{driverId}, Integer.class);
        if(aux == null)
            return 0;
        return aux;
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
    public List<Booking> getClientBookings(long clientId, int offset) {
        return jdbcTemplate.query("""
                        select *
                        from booking
                        where client_id = ? AND date >= CURRENT_DATE
                        order by date, hour_start_id, hour_end_id limit ? offset ?""",
                new Object[]{clientId, Pagination.BOOKINGS_PAGE_SIZE, offset},
                new int[]{Types.BIGINT, Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public int getClientBookingCount(long clientId) {
        String sql = """
                select count(*)
                from booking
                where client_id = ? AND date >= CURRENT_DATE""";
        Integer aux = jdbcTemplate.queryForObject(sql, new Object[]{clientId}, Integer.class);
        if(aux == null)
            return 0;
        return aux;
    }

    @Override
    public List<Booking> getClientHistory(long clientId, int offset) {
        return jdbcTemplate.query("""
                        select *
                        from booking
                        where client_id = ? AND date < CURRENT_DATE
                        order by date, hour_start_id, hour_end_id limit ? offset ?""",
                new Object[]{clientId, Pagination.BOOKINGS_PAGE_SIZE, offset},
                new int[]{Types.BIGINT, Types.BIGINT, Types.BIGINT},
                ROW_MAPPER);
    }

    @Override
    public int getClientHistoryCount(long clientId) {
        String sql = """
                select count(*)
                from booking
                where client_id = ? AND date < CURRENT_DATE""";
        Integer aux = jdbcTemplate.queryForObject(sql, new Object[]{clientId}, Integer.class);
        if(aux == null)
            return 0;
        return aux;
    }

    @Override
    public void acceptBooking(long bookingId) {
        Optional<Booking> bookingOptional = getBookingById(bookingId);
        if (bookingOptional.isEmpty()) {
            return;
        }
        Booking booking = bookingOptional.get();
        HourInterval bookingHI = booking.getHourInterval();
        if (isVehicleBookedForThatTime(booking.getVehicle().getId(), booking.getDate(), booking.getHourInterval()) ||
            !booking.getState().equals(BookingState.PENDING))
            return;
        jdbcTemplate.update("""
                        update booking
                        set state = cast(? as state)
                        where id = ?""",
                new Object[]{BookingState.ACCEPTED.toString(), bookingId},
                new int[]{Types.VARCHAR, Types.BIGINT});
        jdbcTemplate.update("""
                        update booking
                        set state = cast(? as state)
                        where id != ?
                        and date = ?
                        and vehicle_id = ?
                        and (
                                (hour_start_id >= ?
                                and hour_start_id <= ?)
                            or  (hour_end_id >= ?
                                and hour_end_id <= ?))
                            or  (hour_start_id < ?
                                and hour_end_id > ?)""",
                new Object[]{BookingState.REJECTED.toString(), bookingId,
                        booking.getDate().toString(), booking.getVehicle().getId(),
                        bookingHI.getStartHourBlockId(), bookingHI.getEndHourBlockId(),
                        bookingHI.getStartHourBlockId(), bookingHI.getEndHourBlockId(),
                        bookingHI.getStartHourBlockId(), bookingHI.getEndHourBlockId()},
                new int[]{
                        Types.VARCHAR, Types.BIGINT,
                        Types.DATE, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT
                });
    }

    @Override
    public void rejectBooking(long bookingId) {
        jdbcTemplate.update("""
                        update booking
                        set state = cast(? as state)
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
                                    and wa.week_day = {fn DAYOFWEEK(cast(? as date))} - 1
                                    and wa.hour_block_id >= ?
                                    and wa.hour_block_id <= ?
                        """,
                new Object[]{
                        vehicleId,
                        zoneId,
                        date.toString(),
                        hourInterval.getStartHourBlockId(),
                        hourInterval.getEndHourBlockId()
                },
                new int[]{Types.BIGINT, Types.BIGINT, Types.DATE, Types.BIGINT, Types.BIGINT},
                Integer.class
        );
        return count != null && count == hourInterval.getHourCount();
    }

    private boolean isVehicleBookedForThatTime(long vehicleId, LocalDate date, HourInterval hourInterval) {
        Integer count = jdbcTemplate.queryForObject("""
                        select count(*)
                        from booking b
                        where b.vehicle_id = ? and b.date = ? and b.state = cast(? as state)
                        and (
                                (b.hour_start_id >= ?
                                and b.hour_start_id <= ?)
                            or  (b.hour_end_id >= ?
                                and b.hour_end_id <= ?)
                            or  (b.hour_start_id < ?
                                and b.hour_end_id > ?)
                        )""",
                new Object[]{vehicleId, date.toString(), BookingState.ACCEPTED.toString(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId()},
                new int[]{Types.BIGINT, Types.DATE, Types.VARCHAR,
                        Types.BIGINT, Types.BIGINT,
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
                            or  (b.hour_start_id < ?
                                and b.hour_end_id > ?)
                        )""",
                new Object[]{vehicleId, date.toString(), clientId, zoneId,
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId(),
                        hourInterval.getStartHourBlockId(), hourInterval.getEndHourBlockId()},
                new int[]{Types.BIGINT, Types.DATE, Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT,
                        Types.BIGINT, Types.BIGINT},
                Integer.class);

        return count != null && count > 0;
    }
}
