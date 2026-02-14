package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.VehicleNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.vehicle.*;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import ar.edu.itba.paw.webapp.validation.ValidImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Path("/api/drivers/{driverId}/vehicles")
@Component
public class VehicleController {

    @Context
    private UriInfo uriInfo;

    private final DriverService ds;
    private final ImageService imageService;

    @Autowired
    public VehicleController(DriverService ds, ImageService imageService) {
        this.ds = ds;
        this.imageService = imageService;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getVehicles(
            @PathParam("driverId") int driverId,
            @QueryParam("zoneId") Integer zoneId,
            @QueryParam("size") Size size,
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("weekDay") DayOfWeek weekDay,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("mustHaveAvailability") @DefaultValue("false") boolean mustHaveAvailability
    ) {
        Driver driver = ds.findById(driverId);
        int maxPages = ds.getVehiclePages(
                driver, zoneId, size, maxPrice,
                weekDay, mustHaveAvailability
        );
        Pagination.validatePage(page, maxPages);
        List<Vehicle> vehiclesFromModel = ds.getVehicles(
                driver, zoneId, size, maxPrice,
                weekDay, page, mustHaveAvailability
        );
        List<VehicleDTO> vehicles = vehiclesFromModel.stream().map(VehicleDTO.mapper(uriInfo)).toList();
        Response.ResponseBuilder response = Response.ok(new GenericEntity<>(vehicles) {
        });
        return UriUtils.addPaginationLinks(response, page, maxPages, uriInfo).build();
    }

    @POST
    @PreAuthorize("@authService.isAuthUser(#driverId)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createVehicle(@PathParam("driverId") int driverId, @Valid final CreateVehicleDTO dto) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.addVehicle(
                driver, dto.getPlateNumber(), dto.getVolumeM3(),
                dto.getDescription(), dto.getHourlyRate()
        );
        VehicleDTO vehicleDTO = VehicleDTO.fromVehicle(uriInfo, v);
        return Response.created(vehicleDTO.getSelf()).entity(vehicleDTO).build();
    }

    @GET
    @Path("{vehicleId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getVehicleById(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        return Response.ok(VehicleDTO.fromVehicle(uriInfo, vehicle)).build();
    }

    @PATCH
    @Path("{vehicleId}")
    @PreAuthorize("@authService.isAuthUser(#driverId)")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVehicle(
            @PathParam("driverId") int driverId,
            @PathParam("vehicleId") int vehicleId,
            UpdateVehicleDTO dto
    ) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        ds.updateVehicle(
                driver, vehicleId, dto.getPlateNumberOr(v.getPlateNumber()), dto.getVolumeM3Or(v.getVolume()),
                dto.getDescriptionOr(v.getDescription()), dto.getHourlyRateOr(v.getHourlyRate())
        );
        return Response.ok(VehicleDTO.fromVehicle(uriInfo, v)).build();
    }

    @DELETE
    @Path("{vehicleId}")
    @PreAuthorize("@authService.isAuthUser(#driverId)")
    public Response deleteVehicle(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        ds.deleteVehicle(vehicle);

        return Response.noContent().build();
    }

    @GET
    @Path("{vehicleId}/picture")
    @Produces({"image/jpeg", "image/png"})
    public Response getVehiclePicture(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        Integer imageId = vehicle.getImgId();
        if (imageId != null) {
            Image picture = imageService.getImage(imageId);
            if (picture.getData() != null) {
                return Response.ok(picture.getData()).type(picture.getType()).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{vehicleId}/picture")
    @PreAuthorize("@authService.isAuthUser(#driverId)")
    @Produces({"image/jpeg", "image/png", "application/json"})
    @Consumes({"image/jpeg", "image/png"})
    public Response uploadVehiclePicture(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId, @ValidImage byte[] imageData,
                                         @HeaderParam("Content-Type") String type) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        ds.updatePicture(v, "vehicle_" + v.getId() + "_picture", imageData, type);
        return Response.noContent().build();
    }

    @GET
    @Path("{vehicleId}/availability")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response listAvailability(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        List<Availability> availabilitiesTimes = vehicle.getAvailability();
        List<Zone> zones = vehicle.getZones();
        AvailabilityDTO dto = AvailabilityDTO.fromAvailabilities(uriInfo, availabilitiesTimes, zones);
        return Response.ok(dto).build();
    }

    @PATCH
    @Path("{vehicleId}/availability")
    @PreAuthorize("@authService.isAuthUser(#driverId)")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateAvailability(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId, UpdateAvailabilityDTO dto) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        dto.setZonesIfNull(v.getZones().stream().map(Zone::getId).toList());
        dto.setTimeSlotsIfNull(v.getAvailability().stream()
                .map(a -> new WeekTimeSlotDTO(a.getWeekDay().name(), a.getShiftPeriod().name()))
                .toList()
        );
        ds.updateAvailability(v, dto.getZones(), getMapDayTimeSlots(dto));
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setZones(dto.getZones().stream().map(id -> uriInfo
                .getBaseUriBuilder().path("api").path("zones")
                .path(String.valueOf(id)).build()).toList()
        );
        availabilityDTO.setTimeSlots(dto.getTimeSlots());
        return Response.ok(availabilityDTO).build();
    }

    private Map<DayOfWeek, ShiftPeriod[]> getMapDayTimeSlots(UpdateAvailabilityDTO dto) {
        Map<DayOfWeek, ShiftPeriod[]> toReturn = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            toReturn.put(day, dto.getDayTimeSlots(day));
        }
        return toReturn;
    }

    @GET
    @Path("{vehicleId}/booked-times")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response listBookedTimes(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        List<Booking> bookings = vehicle.getBookings();
        List<DatedTimeSlotDTO> bookedTimes = bookings.stream()
                .filter(b -> LocalDate.now().isBefore(b.getDate()))
                .filter(b -> b.getState().equals(BookingState.ACCEPTED))
                .map(DatedTimeSlotDTO::fromBooking)
                .toList();
        return Response.ok(new GenericEntity<>(bookedTimes) {
        }).build();
    }
}