package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Booking;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.Vehicle;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
@Transactional
public class ImageJpaDao implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Image getImage(int id) {
        return em.find(Image.class, id);
    }

    private int uploadImage(String fileName, byte[] imgData, String type) {
        Image image = new Image();
        image.setFileName(fileName);
        image.setData(imgData);
        image.setType(type);
        em.persist(image);
        em.flush();
        return image.getId();
    }

    @Override
    public int uploadPop(byte[] bin, String fileName, Booking booking, String type) {
        int imageId = uploadImage(fileName, bin, type);
        booking.setPop(imageId);
        em.merge(booking);
        return imageId;
    }

    @Override
    public int uploadVehicleImage(byte[] bin, String fileName, Vehicle vehicle, String type) {
        int imageId = uploadImage(fileName, bin, type);
        vehicle.setImgId(imageId);
        em.merge(vehicle);
        return imageId;
    }

    @Override
    public int uploadPfp(byte[] bin, String fileName, User user, String type) {
        int imageId = uploadImage(fileName, bin, type);
        user.setPfp(imageId);
        em.merge(user);
        return imageId;
    }
}