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
    public void uploadPop(byte[] bin, String fileName, Booking booking, String type) {
        Integer oldPopId = booking.getPop();
        if (oldPopId != null) {
            Image oldPop = em.find(Image.class, oldPopId);
            em.remove(oldPop);
        }
        if (bin.length != 0) {
            int imageId = uploadImage(fileName, bin, type);
            booking.setPop(imageId);
            em.merge(booking);
            return;
        }
        booking.setPop(null);
        em.merge(booking);
    }

    @Override
    public void uploadVehicleImage(byte[] bin, String fileName, Vehicle vehicle, String type) {
        Integer oldImgId = vehicle.getImgId();
        if (oldImgId != null) {
            Image oldImage = em.find(Image.class, oldImgId);
            em.remove(oldImage);
        }
        if (bin.length != 0) {
            int imageId = uploadImage(fileName, bin, type);
            vehicle.setImgId(imageId);
            em.merge(vehicle);
            return;
        }
        vehicle.setImgId(null);
        em.merge(vehicle);
    }


    @Override
    public void uploadPfp(byte[] bin, String fileName, User user, String type) {
        Integer oldPfp = user.getPfp();
        if (oldPfp != null) {
            Image oldImage = em.find(Image.class, oldPfp);
            if (oldImage != null) {
                em.remove(oldImage);
            }
        }
        if (bin.length != 0) {
            int imageId = uploadImage(fileName, bin, type);
            user.setPfp(imageId);
            em.merge(user);
            return;
        }
        user.setPfp(null);
        em.merge(user);

    }
}