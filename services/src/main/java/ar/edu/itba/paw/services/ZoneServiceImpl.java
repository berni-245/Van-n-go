package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Zone;
import ar.edu.itba.paw.persistence.ZoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ZoneServiceImpl implements ZoneService {

    private final ZoneDao zoneDao;

    @Autowired
    public ZoneServiceImpl(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    @Transactional
    @Override
    public List<Zone> getAllZones() {
        return zoneDao.getAllZones();
    }

    @Transactional
    @Override
    public Optional<Zone> getZone(int id) {
        return zoneDao.getZone(id);
    }

    @Transactional
    @Override
    public boolean isValidZone(int zoneId) {
        return zoneDao.isValidZone(zoneId);
    }

    @Override
    public Zone getClientZone(Client client) {
        return zoneDao.getClientZone(client);
    }
}
