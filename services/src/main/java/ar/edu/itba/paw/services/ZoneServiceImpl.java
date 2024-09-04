package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.persistence.ZoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneServiceImpl implements ZoneService {
    @Autowired
    private ZoneDao zoneDao;

    public ZoneServiceImpl(final ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    @Override
    public List<Zone> getAllZones() {
        return zoneDao.getAllZones();
    }
}
