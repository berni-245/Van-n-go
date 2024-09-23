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
    @Autowired
    private UserService userService;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private BookingDao bookingDao;

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
    public List<Booking> getBookings(long id) {
        return bookingDao.getClientBookings(id);
    }
}
