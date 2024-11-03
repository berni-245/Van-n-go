package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.ClientAlreadyAppointedException;
import ar.edu.itba.paw.exceptions.TimeAlreadyPassedException;
import ar.edu.itba.paw.exceptions.VehicleIsAlreadyAcceptedException;
import ar.edu.itba.paw.exceptions.VehicleNotAvailableException;
import ar.edu.itba.paw.models.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingJpaDao implements BookingDao {
    @PersistenceContext
    private EntityManager em;

    @Transactional
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
        if (date.isBefore(LocalDate.now()))
            throw new TimeAlreadyPassedException();
        checkVehicleAvailability(vehicle, originZone, date, period);
        checkIfVehicleIsAlreadyAccepted(vehicle, date, period);
        checkIfClientIsAlreadyAppointed(vehicle, client, originZone, date, period);

        Booking toReturn = new Booking(client, vehicle, originZone, destinationZone, date, period, BookingState.PENDING, jobDescription);
        em.persist(toReturn);
        return toReturn;
    }

    @Transactional
    @Override
    public void acceptBooking(Booking booking) {
        checkIfVehicleIsAlreadyAccepted(booking.getVehicle(), booking.getDate(), booking.getShiftPeriod());
        booking.setState(BookingState.ACCEPTED);

        for (Booking bookingToReject : getBookingsByVehicle(booking.getVehicle(), booking.getDate(), booking.getShiftPeriod(), BookingState.PENDING))
            bookingToReject.setState(BookingState.REJECTED);

        em.merge(booking);
    }

    @Transactional
    @Override
    public void rejectBooking(Booking booking) {
        booking.setState(BookingState.REJECTED);
        em.merge(booking);
    }

    @Transactional
    @Override
    public void finishBooking(Booking booking) {
        booking.setState(BookingState.FINISHED);
        em.merge(booking);
    }

    @Override
    public Optional<Booking> getBookingById(long bookingId) {
        return Optional.ofNullable(em.find(Booking.class, bookingId));
    }

    @Transactional
    @Override
    public List<Booking> getDriverBookings(Driver driver, BookingState state, int offset) {
        var bookingIds = em.createNativeQuery("""
                        select b.id from booking b join vehicle v on v.id = b.vehicle_id
                        where v.driver_id = :driverId and b.state = :state
                        order by b.date
                        """)
                .setParameter("driverId", driver.getId())
                .setParameter("state", state.toString())
                .setFirstResult(offset)
                .setMaxResults(Pagination.BOOKINGS_PAGE_SIZE)
                .getResultList();

        // The return type of the previous query is a List<Integer> and for some reason
        // Integer and Long (Booking id is Long) cannot be compared directly so I need
        // to convert the values to Long.
        List<Long> bookingIds2 = bookingIds.stream().map(id -> ((Integer) id).longValue()).toList();

        return em.createQuery("from Booking where id in (:bookingIds) order by date", Booking.class)
                .setParameter("bookingIds", bookingIds2)
                .getResultList();
    }

    @Override
    public long getDriverBookingCount(Driver driver, BookingState state) {
        return em.createQuery("""
                        select count(*) from Booking b
                        join Vehicle v on b.vehicle = v
                        where v.driver = :driver and b.state = :state
                        """, Long.class)
                .setParameter("driver", driver)
                .setParameter("state", state)
                .getSingleResult();
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        Vehicle vehicle = em.find(Vehicle.class, vehicleId);
        if (vehicle == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle = :vehicle", Booking.class);
        query.setParameter("vehicle", vehicle); //TODO: hacer vehicle equals
        return query.getResultList();
    }

    @Override
    public List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date) {
        Vehicle vehicle = em.find(Vehicle.class, vehicleId);
        if (vehicle == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle = :vehicle and b.date = :date", Booking.class);
        query.setParameter("vehicle", vehicle);
        query.setParameter("date", date.toString());
        return query.getResultList();
    }

    @Override
    public List<Booking> getClientBookings(long clientId, int offset) {
        var bookingIds = em.createNativeQuery("""
                        select b.id from Booking b
                        where b.client_id = :clientId
                        and b.date >= current_date
                        order by b.date
                        """)
                .setParameter("clientId", clientId)
                .setFirstResult(offset)
                .setMaxResults(Pagination.BOOKINGS_PAGE_SIZE)
                .getResultList();
        List<Long> bookingIds2 = bookingIds.stream().map(id -> ((Integer) id).longValue()).toList();
        return em.createQuery("from Booking where id in (:bookingIds) order by date", Booking.class)
                .setParameter("bookingIds", bookingIds2)
                .getResultList();
    }

    @Override
    public long getClientBookingCount(long clientId) {
        Client client = em.find(Client.class, clientId);
        return em.createQuery("""
                         select count(*) from Booking b
                         where b.client = :client
                         and b.date >= current_date
                         """, Long.class)
                .setParameter("client",client)
                .getSingleResult();
    }

    @Override
    public List<Booking> getClientHistory(long clientId, int offset) {
        var bookingIds = em.createNativeQuery("""
                        select b.id from booking b
                        where b.client_id = :clientId
                        and b.date < current_date
                        order by b.date
                        """)
                .setParameter("clientId", clientId)
                .setFirstResult(offset)
                .setMaxResults(Pagination.BOOKINGS_PAGE_SIZE)
                .getResultList();
        List<Long> bookingIds2 = bookingIds.stream().map(id -> ((Integer) id).longValue()).toList();
        return em.createQuery("from Booking where id in (:bookingIds) order by date", Booking.class)
                .setParameter("bookingIds", bookingIds2)
                .getResultList();
    }

    @Override
    public long getClientHistoryCount(long clientId) {
        Client client = em.find(Client.class, clientId);
        return em.createQuery("""
                         select count(*) from Booking b
                         where b.client = :client
                         and b.date < current_date
                         """, Long.class)
                .setParameter("client",client)
                .getSingleResult();
    }


    @Transactional
    @Override
    public void setRatingAndReview(long bookingId, int rating, String review) {
        Booking booking = getBookingById(bookingId).orElseThrow(); //TODO: revisar el manejo de exceptions y optionals
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

    @Override
    public List<Booking> getAllDriverBookings(long id) {
        return List.of();
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

    private void checkVehicleAvailability(Vehicle vehicle, Zone zone, LocalDate date, ShiftPeriod sp) {
        if (!vehicle.getZones().contains(zone))
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
        if( !bookingQuery.getResultList().isEmpty())
            throw new ClientAlreadyAppointedException();
    }
}
