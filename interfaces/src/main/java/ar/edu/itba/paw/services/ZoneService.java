package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Zone;

import java.util.List;
import java.util.Optional;

public interface ZoneService {
    List<Zone> getAllZones();

    Optional<Zone> getZone(long id);

    boolean isValidZone(long zoneId);
}