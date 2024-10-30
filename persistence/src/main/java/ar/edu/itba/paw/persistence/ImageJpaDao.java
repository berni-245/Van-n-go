/*TODO: Descomentar, refactorizar y testear cuando se haya hecho los entities de Booking y User
package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
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
    public Image getImage(long id) {
        return em.find(Image.class, id);
    }

    private Integer uploadImage(String fileName, byte[] imgData) {
        Image image = new Image();
        image.setFileName(fileName);
        image.setData(imgData);
        em.persist(image);
        em.flush();
        return (int) image.getId();
    }

    @Override
    public long uploadPop(byte[] bin, String fileName, long bookingId) {
        Integer imageId = uploadImage(fileName, bin);
        em.createQuery("""
                UPDATE Booking b
                SET b.pop = :imageId
                WHERE b.id = :bookingId
                """)
                .setParameter("imageId", imageId)
                .setParameter("bookingId", bookingId)
                .executeUpdate();
        return imageId;
    }

    @Override
    public long uploadVehicleImage(byte[] bin, String fileName, long vehicleId) {
        Integer imageId = uploadImage(fileName, bin);
        em.createQuery("""
                UPDATE Vehicle v
                SET v.imgId = :imageId
                WHERE v.id = :vehicleId
                """)
                .setParameter("imageId", imageId)
                .setParameter("vehicleId", vehicleId)
                .executeUpdate();
        return imageId;
    }

    @Override
    public long uploadPfp(byte[] bin, String fileName, long userId) {
        Integer imageId = uploadImage(fileName, bin);
        em.createQuery("""
                 UPDATE User u
                 SET u.pfp = :imageId
                 WHERE u.id = :userId
                 """)
                .setParameter("imageId", imageId)
                .setParameter("userId", userId)
                .executeUpdate();
        return imageId;
    }
}
*/