package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.persistence.ZoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZoneServiceImpl implements ZoneService {

    private final ZoneDao zoneDao;

    @Autowired
    public ZoneServiceImpl(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    @Override
    public List<Zone> getAllZones() {
        return zoneDao.getAllZones();
    }

    @Override
    public Optional<Zone> getZone(long id) {
        return zoneDao.getZone(id);
    }

    @Override
    public boolean isValidZone(long zoneId) {
        return zoneDao.isValidZone(zoneId);
    }
}
