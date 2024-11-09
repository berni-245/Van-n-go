package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.models.Zone;

import java.util.List;
import java.util.Optional;

public interface ZoneDao {
    Optional<Zone> getZone(int zoneId);

    boolean isValidZone(int zoneId);

    List<Zone> getAllZones();

    List<Zone> getZonesById(List<Integer> zoneIds);

    Zone getClientZone(Client client);
}