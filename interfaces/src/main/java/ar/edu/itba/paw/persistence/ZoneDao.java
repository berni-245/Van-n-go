package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Zone;

import java.util.List;
import java.util.Optional;

public interface ZoneDao {
    Optional<Zone> getZone(long zoneId);

    boolean isValidZone(long zoneId);

    List<Zone> getAllZones();
}