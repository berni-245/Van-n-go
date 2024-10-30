package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilitySearchForm;
import ar.edu.itba.paw.webapp.form.BookingForm;
import ar.edu.itba.paw.webapp.form.BookingReviewForm;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ClientController {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    @Autowired
    private DriverService ds;
    @Autowired
    private ZoneService zs;
    @Autowired
    private ClientService cs;
    @Autowired
    private ImageService is;

    private static final Gson gson = new Gson();

    public ClientController(DriverService ds, ClientService cs, ZoneService zs, ImageService is) {
        this.ds = ds;
        this.cs = cs;
        this.zs = zs;
        this.is = is;
    }

    @RequestMapping(path = "/upload/pop", method = RequestMethod.POST)
    public String submit(@RequestParam("proofOfPayment") MultipartFile file, @RequestParam("bookingId") long bookingId, @RequestParam("driverId") long driverId, @ModelAttribute("loggedUser") User loggedUser) {
        if (file == null || file.isEmpty()) {
            return "redirect:/client/bookings";
        }
        try {
            is.uploadPop(file.getBytes(), file.getOriginalFilename(), bookingId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "redirect:/client/bookings";
    }

    @RequestMapping("/client/bookings")
    public ModelAndView bookings(
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        ModelAndView mav = new ModelAndView("client/bookings");
        if (model.containsAttribute("toasts")) {
            mav.addObject("toasts", model.getAttribute("toasts"));
        }
        mav.addObject("bookings", cs.getBookings(loggedUser.getId(),page));
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", (int) Math.ceil((double)cs.getTotalBookingCount(loggedUser.getId()) / Pagination.BOOKINGS_PAGE_SIZE));
        return mav;
    }

    @RequestMapping("/client/history")
    public ModelAndView clientHistory(
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        List<Booking> paginatedHistory = cs.getHistory(loggedUser.getId(), page);
        int totalRecords = cs.getTotalHistoryCount(loggedUser.getId());
        int totalPages = (int) Math.ceil((double) totalRecords / Pagination.BOOKINGS_PAGE_SIZE);
        ModelAndView mav = new ModelAndView("client/history");
        mav.addObject("history", paginatedHistory);
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", totalPages);
        return mav;
    }

    @RequestMapping(path = "/client/history", method = RequestMethod.POST)
    public ModelAndView sendReview(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("bookingReviewForm") BookingReviewForm form,
            BindingResult errors,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        if (errors.hasErrors()) {
            return clientHistory(loggedUser, form, page);
        }
        cs.setBookingRatingAndReview(form.getBookingID(), form.getRating(), form.getReview());
        return new ModelAndView("redirect:/client/history");
    }

    @RequestMapping("/client/search")
    public ModelAndView search(
            @ModelAttribute("loggedUser") Client loggedUser,
            @Valid @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) return new ModelAndView();
        ModelAndView mav = new ModelAndView("client/search");
        List<Zone> zones = zs.getAllZones();
        mav.addObject("zones", zones);
        return mav;
    }

    @RequestMapping("/client/availability")
    public ModelAndView availability(
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam(name = "zoneId", required = false) Long zoneId,
            @RequestParam(name = "size", required = false) Size size,
            @Valid @ModelAttribute("availabilitySearchForm") AvailabilitySearchForm form,
            BindingResult errors,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        // The default should be the client's zone.
        if (zoneId == null) {
            zoneId = 1L;
            form.setZoneId(zoneId);
        }
        if (size == null) {
            size = Size.SMALL;
            form.setSize(size);
        }
        if (errors.hasErrors()) return new ModelAndView();
        final ModelAndView mav = new ModelAndView("client/availability");
        List<Driver> drivers = ds.getAll(zoneId, size, page);
        List<Zone> zones = zs.getAllZones();
        mav.addObject("drivers", drivers);
        mav.addObject("zones", zones);
        mav.addObject("currentPage", page);
        mav.addObject("totalPages", (int) Math.ceil((double) ds.totalMatches(zoneId,size) / Pagination.SEARCH_PAGE_SIZE));
        mav.addObject("zoneId", zoneId);
        mav.addObject("size", size);
        return mav;
    }

    @ResponseBody
    @RequestMapping(path = "/client/availability/active", method = RequestMethod.GET)
    public String vehicleActiveAvailability(
            @RequestParam(name = "vehicleId") long vehicleId,
            @RequestParam(name = "zoneId") long zoneId,
            @RequestParam(name = "date") String date
    ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date2 = LocalDate.parse(date, formatter);
//        return gson.toJson(ds.activeAvailabilities(vehicleId, zoneId, date2));
        return gson.toJson("{}");
    }

    @RequestMapping(path = "/client/availability/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView driverAvailability(
            @PathVariable(name = "id") long id,
            @RequestParam(name = "zoneId") long zoneId,
            @RequestParam(name = "size") Size size,
            @ModelAttribute("loggedUser") Client loggedUser,
            @ModelAttribute("bookingForm") BookingForm form
    ) {
        Optional<Driver> driver = ds.findById(id);
        if (driver.isPresent()) {
            final ModelAndView mav = new ModelAndView("client/driverAvailability");
            mav.addObject("driverId", id);
            var vehicles = ds.getVehicles(driver.get(), zoneId, size);
            Set<Integer> workingDays = new HashSet<>();
            // TODO en lugar de aceptar todos los días, aceptar los que tengan mismo zoneId y size (se necesita un método)
            for(DayOfWeek dow : DayOfWeek.values())
                workingDays.add(dow.getValue() % 7);

            mav.addObject("vehicles", vehicles);
            mav.addObject("workingDays", workingDays);
            var bookings = ds.getAllBookings(driver.get().getId());
            mav.addObject("bookings", bookings);
            mav.addObject("driver", driver.get());
            Optional<Zone> zone = zs.getZone(zoneId);
            if (zone.isEmpty()) return new ModelAndView();
            mav.addObject("zone", zone.get());
            mav.addObject("size", size.name());
            mav.addObject("sizeLowerCase", size.name().toLowerCase());
            return mav;
        } else {
            return new ModelAndView("redirect:/403");
        }
    }

    @RequestMapping(path = "/client/availability/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView appointBooking(
            @PathVariable(name = "id") long id,
            @ModelAttribute("loggedUser") Client loggedUser,
            @RequestParam(name = "size") Size size,
            @ModelAttribute("bookingForm") BookingForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        List<Toast> toasts = new ArrayList<>();
        if (errors.hasErrors()) {
            return driverAvailability(id, form.getZoneId(), size, loggedUser, form);
        }
        try {
            Optional<Booking> booking = Optional.ofNullable(cs.appointBooking(
                    form.getVehicleId(),
                    loggedUser,
                    form.getZoneId(),
                    form.getDestinationId(),
                    form.getDate(),
                    ShiftPeriod.valueOf(form.getShiftPeriod()),
                    form.getJobDescription(),
                    LocaleContextHolder.getLocale()
            ));
            if (booking.isEmpty()) {
                toasts.add(new Toast(
                        ToastType.danger, "toast.booking.error"
                ));
                redirectAttributes.addFlashAttribute("toasts", toasts);
                return new ModelAndView("redirect:/availability/%d?zoneId=%d&size=%s".formatted(id, form.getZoneId(), size.name()));
            }
            toasts.add(new Toast(
                    ToastType.danger, "toast.booking.success"
            ));
            return new ModelAndView("redirect:/client/bookings");
        } catch (Exception e) {
            toasts.add(new Toast(
                    ToastType.danger, "toast.booking.error"
            ));
            redirectAttributes.addFlashAttribute("toasts", toasts);
            return new ModelAndView("redirect:/availability/" + id);
        }
    }

}