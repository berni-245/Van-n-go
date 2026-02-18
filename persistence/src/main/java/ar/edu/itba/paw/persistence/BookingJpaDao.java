package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.*;
import ar.edu.itba.paw.models.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Repository
public class BookingJpaDao implements BookingDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Booking appointBooking(
            Vehicle vehicle,
            Client client,
            Zone originZone,
            Zone destinationZone,
            LocalDate date,
            ShiftPeriod period,
            String jobDescription
    ) {
        if (date.isBefore(LocalDate.now())) throw new TimeAlreadyPassedException();
        checkVehicleAvailability(vehicle, originZone, date, period);
        checkIfVehicleIsAlreadyAccepted(vehicle, date, period);
        checkIfClientIsAlreadyAppointed(vehicle, client, originZone, date, period);

        Booking toReturn = new Booking(client, vehicle, originZone, destinationZone, date, period, BookingState.PENDING, jobDescription);
        em.persist(toReturn);
        return toReturn;
    }

    @Override
    public void acceptBooking(Booking booking) {
        checkIfVehicleIsAlreadyAccepted(booking.getVehicle(), booking.getDate(), booking.getShiftPeriod());
        booking.setState(BookingState.ACCEPTED);

        for (Booking bookingToReject : getBookingsByVehicle(booking.getVehicle(), booking.getDate(), booking.getShiftPeriod(), BookingState.PENDING))
            bookingToReject.setState(BookingState.REJECTED);

        em.merge(booking);
    }

    @Override
    public void rejectBooking(Booking booking) {
        booking.setState(BookingState.REJECTED);
        em.merge(booking);
    }

    @Override
    public void finishBooking(Booking booking) {
        booking.setState(BookingState.FINISHED);
        em.merge(booking);
    }

    @Override
    public void cancelBooking(Booking booking) {
        booking.setState(BookingState.CANCELED);
        em.merge(booking);
    }

    private final static int a = 2;

    @Override
    @Scheduled(cron = "0 0 6,19,20 * * ?")
    public void checkPending() {
        List<ShiftPeriod> shifts = ShiftPeriod.getActualAndPreviousShiftPeriod(LocalTime.now());
        TypedQuery<Booking> bookingQuery = em.createQuery("SELECT b " +
                "FROM Booking b WHERE b.state = :state and ( b.date < current_date or (b.date = current_date and b.shiftPeriod IN :shifts ) ) ", Booking.class);
        bookingQuery.setParameter("state", BookingState.PENDING);
        bookingQuery.setParameter("shifts", shifts);
        List<Booking> bookings = bookingQuery.getResultList();
        for (Booking booking : bookings) {
            rejectBooking(booking);
        }
    }

    @Override
    public Booking getClientBookingById(Client client, int bookingId) {
        return em.createQuery("from Booking where id = :id and client = :client", Booking.class)
                .setParameter("id", bookingId)
                .setParameter("client", client)
                .getResultList().stream().findFirst()
                .orElseThrow(() -> new ForbiddenUserBookingAccessException(client, bookingId));
    }

    @Override
    public Booking getDriverBookingById(Driver driver, int bookingId) {
        return em.createQuery("from Booking where id = :id and vehicle.driver = :driver", Booking.class)
                .setParameter("id", bookingId)
                .setParameter("driver", driver)
                .getResultList().stream().findFirst()
                .orElseThrow(() -> new ForbiddenUserBookingAccessException(driver, bookingId));
    }

    @Override
    public List<Booking> getDriverBookings(Driver driver, BookingState state, int offset) {
        var nativeQueryList = em.createNativeQuery("""
                        select b.id
                        from booking b join vehicle v on b.vehicle_id = v.id
                        where v.driver_id = :driverId and b.state = :state
                        order by b.date
                        """)
                .setParameter("driverId", driver.getId())
                .setParameter("state", state.toString())
                .setFirstResult(offset)
                .setMaxResults(Pagination.BOOKINGS_PAGE_SIZE)
                .getResultList();

        return em.createQuery("from Booking where id in (:bookingIds) order by date", Booking.class)
                .setParameter("bookingIds", nativeQueryList)
                .getResultList();
    }

    @Override
    public int getDriverBookingCount(Driver driver, BookingState state) {
        return em.createQuery("""
                        select count(*) from Booking b
                        join Vehicle v on b.vehicle = v
                        where v.driver = :driver and b.state = :state
                        """, Long.class)
                .setParameter("driver", driver)
                .setParameter("state", state)
                .getSingleResult().intValue();
    }

    @Override
    public List<Booking> getClientBookings(Client client, BookingState state, int offset) {
        var nativeQueryList = em.createNativeQuery("""
                        select b.id
                        from booking b where b.client_id = :clientId and b.state = :state
                        order by b.date
                        """)
                .setParameter("clientId", client.getId())
                .setParameter("state", state.toString())
                .setFirstResult(offset)
                .setMaxResults(Pagination.BOOKINGS_PAGE_SIZE)
                .getResultList();

        return em.createQuery("from Booking where id in (:bookingIds) order by date", Booking.class)
                .setParameter("bookingIds", nativeQueryList)
                .getResultList();
    }

    @Override
    public int getClientBookingCount(Client client, BookingState state) {
        return em.createQuery("""
                        select count(*) from Booking b
                        where b.client = :client
                        and b.state = :state
                        """, Long.class)
                .setParameter("client", client)
                .setParameter("state", state)
                .getSingleResult().intValue();
    }

    @Override
    public void setRatingAndReview(Booking booking, int rating, String review) {
        int finishedBookingsWithRating = getFinishedBookingsWithRatingCountByDriver(booking.getDriver());
        booking.setReview(review);
        booking.setRating(rating);
        Driver driver = booking.getVehicle().getDriver();
        Double oldRating = driver.getRating();
        if (oldRating == null) {
            oldRating = 0.0;
        }
        double newRating = ((oldRating * finishedBookingsWithRating) + rating) / (finishedBookingsWithRating + 1);
        driver.setRating(newRating);
        em.merge(driver);
    }

    private int getFinishedBookingsWithRatingCountByDriver(Driver driver) {
        TypedQuery<Booking> query = em.createQuery(
                """
                        select b from Booking b join b.vehicle v
                        where v.driver = :driver
                        and b.state = :state
                        and b.rating is not null"""
                , Booking.class
        );
        query.setParameter("driver", driver);
        query.setParameter("state", BookingState.FINISHED);
        return query.getResultList().size();
    }

    private void checkVehicleAvailability(Vehicle vehicle, Zone zone, LocalDate date, ShiftPeriod sp) {
        if (!vehicle.getZones().contains(zone) || vehicle.isDeleted())
            throw new VehicleNotAvailableException();

        TypedQuery<Availability> avQuery = em.createQuery(
                """
                        from Availability as av
                        where av.vehicle = :vehicle and av.weekDay = :weekDay and av.shiftPeriod = :sp
                        """, Availability.class);
        avQuery.setParameter("vehicle", vehicle);
        avQuery.setParameter("weekDay", date.getDayOfWeek());
        avQuery.setParameter("sp", sp);
        if (avQuery.getResultList().isEmpty())
            throw new VehicleNotAvailableException();
    }

    private void checkIfVehicleIsAlreadyAccepted(Vehicle vehicle, LocalDate date, ShiftPeriod shiftPeriod) {
        if (!getBookingsByVehicle(vehicle, date, shiftPeriod, BookingState.ACCEPTED).isEmpty())
            throw new VehicleIsAlreadyAcceptedException();
    }

    private List<Booking> getBookingsByVehicle(Vehicle vehicle, LocalDate date, ShiftPeriod sp, BookingState bs) {
        TypedQuery<Booking> query = em.createQuery(
                """
                        from Booking as b
                        where b.vehicle = :vehicle and b.date = :date and b.shiftPeriod = :sp and b.state = :bs
                        """,
                Booking.class
        );
        query.setParameter("vehicle", vehicle);
        query.setParameter("date", date);
        query.setParameter("sp", sp);
        query.setParameter("bs", bs);
        return query.getResultList();
    }

    private void checkIfClientIsAlreadyAppointed(Vehicle vehicle, Client client, Zone zone, LocalDate date, ShiftPeriod sp) {
        TypedQuery<Booking> bookingQuery = em.createQuery("""
                From Booking b
                where b.vehicle = :vehicle and b.client = :client and b.originZone = :zone and b.date = :date and b.shiftPeriod = :sp
                """, Booking.class);
        bookingQuery.setParameter("vehicle", vehicle);
        bookingQuery.setParameter("client", client);
        bookingQuery.setParameter("zone", zone);
        bookingQuery.setParameter("date", date);
        bookingQuery.setParameter("sp", sp);
        if (!bookingQuery.getResultList().isEmpty())
            throw new ClientAlreadyAppointedException();
    }

    @Override
    public boolean existsBookingBetween(Client client, Driver driver) {
        return existsBookingBetween(client.getId(), driver.getId());
    }

    @Override
    public boolean existsBookingBetween(int clientId, int driverId) {
        Long count = em.createQuery("""
                        Select COUNT(b) from Booking b join b.vehicle v
                        where v.driver.id = :driverId and b.client.id = :clientId
                        """, Long.class)
                .setParameter("clientId", clientId)
                .setParameter("driverId", driverId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean hasAnAcceptedBooking(Vehicle vehicle) {
        TypedQuery<Vehicle> query = em.createQuery(
                "from Booking b where b.vehicle = :vehicle and b.state = :state",
                Vehicle.class
        );
        query.setParameter("vehicle", vehicle);
        query.setParameter("state", BookingState.ACCEPTED);
        return !query.getResultList().isEmpty();
    }

    @Override
    public void rejectAllPendingBookingsFromVehicle(Vehicle vehicle) {
        em.flush();
        Query query = em.createQuery("""
                    update Booking b set b.state = :newState
                    where b.vehicle = :vehicle and b.state = :oldState
                """);

        query.setParameter("vehicle", vehicle);
        query.setParameter("oldState", BookingState.PENDING);
        query.setParameter("newState", BookingState.REJECTED);
        query.executeUpdate();
        em.clear();
    }

    @Override
    public List<Booking> getBookingsByVehicleInYearMonth(Vehicle vehicle, YearMonth yearMonth) {
        YearMonth currentYearMonth = YearMonth.now();
        LocalDate currentDate = LocalDate.now();

        if (yearMonth.isBefore(currentYearMonth))
            throw new TimeAlreadyPassedException();

        LocalDate start = yearMonth.atDay(yearMonth.equals(currentYearMonth) ? currentDate.getDayOfMonth() : 1);
        LocalDate end = yearMonth.atEndOfMonth();

        TypedQuery<Booking> query = em.createQuery(
                """
                        from Booking b
                        where b.vehicle = :vehicle
                          and b.date between :start and :end
                        """,
                Booking.class
        );

        query.setParameter("vehicle", vehicle);
        query.setParameter("start", start);
        query.setParameter("end", end);

        return query.getResultList();
    }

}
