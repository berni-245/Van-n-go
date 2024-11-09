package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
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

    private int uploadImage(String fileName, byte[] imgData) {
        Image image = new Image();
        image.setFileName(fileName);
        image.setData(imgData);
        em.persist(image);
        em.flush();
        return image.getId();
    }

    @Override
    public int uploadPop(byte[] bin, String fileName, Booking booking) {
        int imageId = uploadImage(fileName, bin);
        booking.setPop(imageId);
        em.merge(booking);
        return imageId;
    }

    @Override
    public int uploadVehicleImage(byte[] bin, String fileName, Vehicle vehicle) {
        int imageId = uploadImage(fileName, bin);
        vehicle.setImgId(imageId);
        em.merge(vehicle);
        return imageId;
    }

    @Override
    public int uploadPfp(byte[] bin, String fileName, User user) {
        int imageId = uploadImage(fileName, bin);
        user.setPfp(imageId);
        em.merge(user);
        return imageId;
    }
}