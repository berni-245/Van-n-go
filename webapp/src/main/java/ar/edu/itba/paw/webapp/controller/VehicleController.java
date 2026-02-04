package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.VehicleNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// TODO must improve the updateVehicle and updateAvailability the in the back
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
            @QueryParam("page") @DefaultValue("1") int page
    ) {
        Driver driver = ds.findById(driverId);
        List<Vehicle> vehiclesFromModel;
        if (zoneId == null)
            vehiclesFromModel = ds.getVehicles(driver, page);
        else
            vehiclesFromModel = ds.getVehicles(driver, zoneId, size, maxPrice, weekDay);
        List<VehicleDTO> vehicles = vehiclesFromModel.stream().map(VehicleDTO.mapper(uriInfo)).toList();
        return Response.ok(new GenericEntity<>(vehicles) {
        }).build();
    }

    @POST
    @PreAuthorize("@authService.isAuthenticatedUser(#driverId)")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response createVehicle(@PathParam("driverId") int driverId, VehicleDTO dto) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.addVehicle(
                driver, dto.getPlateNumber(), dto.getVolumeM3(),
                dto.getDescription(), new ArrayList<>(), dto.getHourlyRate(),
                null, null);
        VehicleDTO vehicleDTO = VehicleDTO.fromVehicle(uriInfo, v);
        return Response.created(vehicleDTO.getSelf()).entity(vehicleDTO).build();
    }

    @GET
    @Path("{vehicleId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getVehicleById(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        return Response.ok().entity(VehicleDTO.fromVehicle(uriInfo, vehicle)).build();
    }

    @PATCH
    @Path("{vehicleId}")
    @PreAuthorize("@authService.isAuthenticatedUser(#driverId)")
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
                dto.getDescriptionOr(v.getDescription()), v.getZones().stream().map(Zone::getId).toList(),
                dto.getHourlyRateOr(v.getHourlyRate()), v.getImgId(), null, null
        );
        return Response.ok().entity(VehicleDTO.fromVehicle(uriInfo, v)).build();
    }

    @GET
    @Path("{vehicleId}/picture")
    @Produces({"image/jpeg", "image/png"})
    public Response getVehiclePicture(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId) {
        Driver driver = ds.findById(driverId);
        Vehicle vehicle = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        Integer imageId = vehicle.getImgId();
        if (imageId != null) {
            byte[] pictureData = imageService.getImage(imageId).getData();
            if (pictureData != null) {
                return Response.ok(pictureData).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("{vehicleId}/picture")
    @PreAuthorize("@authService.isAuthenticatedUser(#driverId)")
    @Produces({"image/jpeg", "image/png"})
    @Consumes({"image/jpeg", "image/png"})
    public Response uploadVehiclePicture(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId, byte[] imageData) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        ds.updateVehicle(
                driver, vehicleId, v.getPlateNumber(), v.getVolume(),
                v.getDescription(), v.getZones().stream().map(Zone::getId).toList(),
                v.getHourlyRate(), v.getImgId(),
                "vehicle_" + v.getId() + "_picture", imageData
        );
        Integer imageId = v.getImgId();
        if (imageId != null) {
            byte[] pictureData = imageService.getImage(imageId).getData();
            if (pictureData != null) {
                return Response.ok(pictureData).build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND).build();
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
    @PreAuthorize("@authService.isAuthenticatedUser(#driverId)")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateAvailability(@PathParam("driverId") int driverId, @PathParam("vehicleId") int vehicleId, UpdateAvailabilityDTO dto) {
        Driver driver = ds.findById(driverId);
        Vehicle v = ds.findVehicleById(driver, vehicleId).orElseThrow(VehicleNotFoundException::new);
        // TODO might change these with the refactor
        dto.setZonesIfNull(v.getZones().stream().map(Zone::getId).toList());
        dto.setTimeSlotsIfNull(
                v.getAvailability().stream()
                        .map(a -> new WeekTimeSlotDTO(a.getWeekDay().name(), a.getShiftPeriod().name()))
                        .toList()
        );
        ds.updateAvailability(
                v,
                dto.getDayTimeSlots(DayOfWeek.MONDAY),
                dto.getDayTimeSlots(DayOfWeek.TUESDAY),
                dto.getDayTimeSlots(DayOfWeek.WEDNESDAY),
                dto.getDayTimeSlots(DayOfWeek.THURSDAY),
                dto.getDayTimeSlots(DayOfWeek.FRIDAY),
                dto.getDayTimeSlots(DayOfWeek.SATURDAY),
                dto.getDayTimeSlots(DayOfWeek.SUNDAY)
        );
        ds.updateVehicle(
                driver, vehicleId, v.getPlateNumber(), v.getVolume(),
                v.getDescription(), dto.getZones(),
                v.getHourlyRate(), v.getImgId(), null, null
        );
        AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        availabilityDTO.setZones(
                dto.getZones().stream().map(id -> uriInfo
                        .getBaseUriBuilder().path("api").path("zones")
                        .path(String.valueOf(id)).build()).toList()
        );
        availabilityDTO.setTimeSlots(dto.getTimeSlots());
        return Response.ok(availabilityDTO).build();
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