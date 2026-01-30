package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Availability;
import ar.edu.itba.paw.models.Zone;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class AvailabilityDTO {
    private List<URI> zones;
    private List<WeekTimeSlotDTO> timeSlots;

    public AvailabilityDTO() {

    }

    public static AvailabilityDTO fromAvailabilities(UriInfo uriInfo, List<Availability> availabilities, List<Zone> zones) {
        AvailabilityDTO dto = new AvailabilityDTO();
        dto.zones = zones.stream()
                .map(ZoneDTO.mapper(uriInfo)).map(ZoneDTO::getSelf)
                .toList();
        dto.timeSlots = availabilities.stream()
                .map(a -> new WeekTimeSlotDTO(a.getWeekDay().name(), a.getShiftPeriod().name()))
                .toList();
        return dto;
    }

    public List<URI> getZones() {
        return zones;
    }

    public void setZones(List<URI> zones) {
        this.zones = zones;
    }

    public List<WeekTimeSlotDTO> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<WeekTimeSlotDTO> timeSlots) {
        this.timeSlots = timeSlots;
    }
}
