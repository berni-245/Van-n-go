package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.BookingDao;
import ar.edu.itba.paw.persistence.ClientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService{
    private final UserService userService;

    private final ClientDao clientDao;

    private final BookingDao bookingDao;

    @Autowired
    public ClientServiceImpl(UserService userService, ClientDao clientDao, BookingDao bookingDao) {
        this.userService = userService;
        this.clientDao = clientDao;
        this.bookingDao = bookingDao;
    }

    @Override
    public Client create(String username, String mail, String password) {
        User user = userService.create(username, mail, password);
        return clientDao.create(user);
    }

    @Override
    public Optional<Client> findById(long id) {
        return clientDao.findById(id);
    }

    @Override
    public Optional<Booking> appointBooking(long driverId, long clientId, LocalDate date) {
        return bookingDao.appointBooking(driverId, clientId, date);
    }

    @Override
    public Optional<Booking> appointBooking(String driverUsername, String clientUsername, LocalDate date) {
        return bookingDao.appointBooking(
                userService.findByUsername(driverUsername).getId(),
                userService.findByUsername(clientUsername).getId(),date);
    }

    @Override
    public List<Booking> getBookings(long id) {
        return bookingDao.getClientBookings(id);
    }
}
