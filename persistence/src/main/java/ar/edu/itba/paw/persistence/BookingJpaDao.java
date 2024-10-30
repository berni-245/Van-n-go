package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exceptions.VehicleAlreadyAcceptedException;
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

    @Override
    public Booking appointBooking(
            Vehicle vehicle,
            Client client,
            Zone originZone,
            LocalDate date,
            ShiftPeriod period,
            String jobDescription
    ) {
        throw new UnsupportedOperationException("Not supported yet.");

        // Check if booking is valid.

        // Booking booking = new Booking(
        //         null, client, driver, vehicle,
        //         originZone, date, period,
        //         BookingState.PENDING, null,
        //         null, null, jobDescription
        // );
        // em.persist(booking);
        // return booking;
    }

    // Creo que había que hacerlo transactional para que se persistan los cambios
    // hechos a la entity
    @Transactional
    @Override
    public void acceptBooking(Booking booking) throws VehicleAlreadyAcceptedException {
        if(isVehicleAlreadyAccepted(booking)) {
            throw new VehicleAlreadyAcceptedException();
        }
        booking.setState(BookingState.ACCEPTED);

        for(Booking bookingToReject : getBookingsByVehicle(booking.getVehicle(), booking.getDate(), booking.getShiftPeriod(), BookingState.PENDING))
            bookingToReject.setState(BookingState.REJECTED);

        em.persist(booking);
    }

    @Override
    public void rejectBooking(long bookingId) {
        Optional<Booking> booking = getBookingById(bookingId);
        if(booking.isEmpty()) {
            throw new IllegalArgumentException("Booking with id " + bookingId + " not found"); //TODO: crear exceptoin propia
        }
        booking.get().setState(BookingState.REJECTED);
    }

    @Override
    public Optional<Booking> getBookingById(long bookingId) {
        return Optional.ofNullable(em.find(Booking.class, bookingId));
    }

    @Override //TODO: revisar al migrar Driver a JPA
    public List<Booking> getDriverBookings(long driverId, int offset) {
        Driver driver = em.find(Driver.class, driverId);
        if(driver == null) {
            //throws...
        }
        TypedQuery<Vehicle> vehiclesQuery = em.createQuery("From Vehicle v where v.driver = :driver", Vehicle.class);
        vehiclesQuery.setParameter("driver", driver);
        List<Vehicle> vehicles = vehiclesQuery.getResultList();

        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle in :vehicles", Booking.class); //TODO: revisar si hace el equals de java
        query.setParameter("vehicles", vehicles);
        query.setFirstResult(offset);
        query.setMaxResults(Pagination.BOOKINGS_PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public int getDriverBookingCount(long driverId) {
        Driver driver = em.find(Driver.class, driverId);
        if(driver == null) {
            //throws...
        }
        TypedQuery<Vehicle> vehiclesQuery = em.createQuery("From Vehicle v where v.driver = :driver", Vehicle.class);
        vehiclesQuery.setParameter("driver", driver);
        List<Vehicle> vehicles = vehiclesQuery.getResultList();

        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle in :vehicles", Booking.class); //TODO: revisar si hace el equals de java
        query.setParameter("vehicles", vehicles);
        return query.getResultList().size();
    }


    @Override
    public List<Booking> getDriverHistory(long driverId, int offset) {
        Driver driver = em.find(Driver.class, driverId);
        if(driver == null) {
            //throws...
        }
        TypedQuery<Vehicle> vehiclesQuery = em.createQuery("From Vehicle v where v.driver = :driver", Vehicle.class);
        vehiclesQuery.setParameter("driver", driver);
        List<Vehicle> vehicles = vehiclesQuery.getResultList();

        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle in :vehicles and b.date < CURRENT_DATE", Booking.class); //TODO: revisar si hace el equals de java
        query.setParameter("vehicles", vehicles);
        query.setFirstResult(offset);
        query.setMaxResults(Pagination.BOOKINGS_PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public int getDriverHistoryCount(long driverId) {
        Driver driver = em.find(Driver.class, driverId);
        if(driver == null) {
            //throws...
        }
        TypedQuery<Vehicle> vehiclesQuery = em.createQuery("From Vehicle v where v.driver = :driver", Vehicle.class);
        vehiclesQuery.setParameter("driver", driver);
        List<Vehicle> vehicles = vehiclesQuery.getResultList();

        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle in :vehicles and b.date < CURRENT_DATE", Booking.class); //TODO: revisar si hace el equals de java
        query.setParameter("vehicles", vehicles);
        return query.getResultList().size();
    }

    @Override
    public List<Booking> getBookingsByVehicle(long vehicleId) {
        Vehicle vehicle = em.find(Vehicle.class, vehicleId);
        if(vehicle == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle = :vehicle", Booking.class);
        query.setParameter("vehicle", vehicle); //TODO: hacer vehicle equals
        return query.getResultList();
    }

    @Override
    public List<Booking> getBookingsByVehicleAndDate(long vehicleId, LocalDate date) {
        Vehicle vehicle = em.find(Vehicle.class, vehicleId);
        if(vehicle == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.vehicle = :vehicle and b.date = :date", Booking.class);
        query.setParameter("vehicle", vehicle);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Booking> getClientBookings(long clientId, int offset) {
        Client client = em.find(Client.class, clientId);
        if(client == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.client = :client and b.date >= CURRENT_DATE", Booking.class);
        query.setParameter("client", client); //TODO: hacer equals driver
        query.setFirstResult(offset);
        query.setMaxResults(Pagination.BOOKINGS_PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public int getClientBookingCount(long clientId) {
        Client client = em.find(Client.class, clientId);
        if(client == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.client = :client and b.date >= CURRENT_DATE", Booking.class);
        query.setParameter("client", client); //TODO: hacer equals driver
        //TODO: Hacer que estos counts no sean un asco
        return query.getResultList().size();
    }

    @Override
    public List<Booking> getClientHistory(long clientId, int offset) {
        Client client = em.find(Client.class, clientId);
        if(client == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.client = :client and b.date < CURRENT_DATE ", Booking.class);
        query.setParameter("client", client);
        query.setFirstResult(offset);
        query.setMaxResults(Pagination.BOOKINGS_PAGE_SIZE);
        return query.getResultList();
    }

    @Override
    public int getClientHistoryCount(long clientId) {
        Client client = em.find(Client.class, clientId);
        if(client == null) {
            //throws...
        }
        TypedQuery<Booking> query = em.createQuery("From Booking as b where b.client = :client and b.date < CURRENT_DATE ", Booking.class);
        query.setParameter("client", client);
        return query.getResultList().size();
    }

    @Override
    public void setRatingAndReview(long bookingId, int rating, String review) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Booking> getAllDriverBookings(long id) {
        return List.of();
    }

    private boolean isVehicleAlreadyAccepted(Booking booking) {
        return ! getBookingsByVehicle(booking.getVehicle(), booking.getDate(), booking.getShiftPeriod(), booking.getState()).isEmpty();
    }

    private List<Booking> getBookingsByVehicle(Vehicle vehicle, LocalDate date, ShiftPeriod sp, BookingState bs) {
        TypedQuery<Booking> query = em.createQuery(
                """
                from Booking as b
                where b.vehicle = :vehicle and b.date = :date and b.shiftPeriod = :sp and b.state = :bs
                """, Booking.class);
        query.setParameter("vehicle", vehicle);
        query.setParameter("date", date);
        query.setParameter("sp", sp);
        query.setParameter("bs", bs);
        return query.getResultList();
    }
}
