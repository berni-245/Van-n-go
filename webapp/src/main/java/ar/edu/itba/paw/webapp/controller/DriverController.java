package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class DriverController extends ParentController {
    private static final Logger log = LoggerFactory.getLogger(DriverController.class);
    @Autowired
    private DriverService ds;
    @Autowired
    private ZoneService zs;
    @Autowired
    private ImageService is;

    public DriverController(final DriverService ds, ZoneService zs, ImageService is) {
        this.ds = ds;
        this.zs = zs;
        this.is = is;
    }

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.POST)
    public ModelAndView addVehiclePost(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            BindingResult errors,
            @RequestParam(value = "vehicleImg", required = false) MultipartFile vehicleImg,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            return addVehicleGet(vehicleForm);
        }
        String imgFilename = null;
        byte[] imgData = null;
        if (vehicleImg != null) {
            imgFilename = vehicleImg.getOriginalFilename();
            try {
                imgData = vehicleImg.getBytes();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        ds.addVehicle(
                loggedUser.getId(),
                vehicleForm.getPlateNumber(),
                vehicleForm.getVolume(),
                vehicleForm.getDescription(),
                vehicleForm.getZoneIds(),
                vehicleForm.getRate(),
                imgFilename,
                imgData
        );
        List<Toast> toasts = List.of(new Toast(
                ToastType.success, "toast.vehicle.add.success"
        ));
        redirectAttributes.addFlashAttribute("toasts", toasts);
        return redirect("/driver/vehicles");
    }

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.GET)
    public ModelAndView addVehicleGet(@ModelAttribute("vehicleForm") VehicleForm vehicleForm) {
        ModelAndView mav = new ModelAndView("driver/add_vehicle");
        mav.addObject("zones", zs.getAllZones());
        return mav;
    }

    @RequestMapping(path = "/vehicle/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getVehicleImage(
            @RequestParam("imgId") int imgId,
            @ModelAttribute("loggedUser") User loggedUser
    ) {
        Image vehicleImg = is.getImage(imgId);
        if (vehicleImg == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String fileName = vehicleImg.getFileName();
        String contentType;
        if (fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        if (fileName.toLowerCase().endsWith(".png"))
            contentType = "image/png";
        else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(null);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(vehicleImg.getData().length);
        return new ResponseEntity<>(vehicleImg.getData(), headers, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/driver/vehicle/{plateNumber}/edit/availability",
            method = RequestMethod.POST
    )
    public ModelAndView addAvailability(
            @PathVariable(name = "plateNumber") String plateNumber,
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("availabilityForm") AvailabilityForm form,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Vehicle> vehicle = ds.findVehicleByPlateNumber(loggedUser, plateNumber);
        if (vehicle.isEmpty()) return new ModelAndView();
        ds.updateAvailability(
                vehicle.get(),
                form.getMondayShiftPeriods(),
                form.getTuesdayShiftPeriods(),
                form.getWednesdayShiftPeriods(),
                form.getThursdayShiftPeriods(),
                form.getFridayShiftPeriods(),
                form.getSaturdayShiftPeriods(),
                form.getSundayShiftPeriods()
        );
        List<Toast> toasts = List.of(new Toast(
                ToastType.success, "toast.availability.edit.success"
        ));
        redirectAttributes.addFlashAttribute("toasts", toasts);
        return redirect("/driver/vehicle/%s/edit", plateNumber);
    }

    @RequestMapping(path = "/driver/vehicles")
    public ModelAndView vehiclesDashboard(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        final ModelAndView mav = new ModelAndView("driver/vehicles");
        mav.addObject("vehicles", ds.getVehicles(loggedUser, page));
        long totalRecords = ds.getVehicleCount(loggedUser);
        int totalPages = (int) Math.ceil((double) totalRecords / Pagination.VEHICLES_PAGE_SIZE);
        mav.addObject("totalPages", totalPages);
        mav.addObject("currentPage", page);
        mav.addObject("toasts", model.getAttribute("toasts"));
        return mav;
    }


    @RequestMapping(path = "/driver/profile")
    public ModelAndView profile(@ModelAttribute("loggedUser") Driver loggedUser) {
        ModelAndView mav = new ModelAndView("user/profile");
        mav.addObject("loggedUser", loggedUser);
        return mav;
    }

    @RequestMapping(path = "/driver/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfileForm(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors
    ) {
        form.setOldUsername(loggedUser.getUsername());
        form.setOldMail(loggedUser.getMail());
        if (!errors.hasErrors()) {
            form.setMail(loggedUser.getMail());
            form.setUsername(loggedUser.getUsername());
            form.setCbu(loggedUser.getCbu());
            form.setDescription(loggedUser.getDescription());
        }
        return new ModelAndView("/user/profileEdit");
    }

    @RequestMapping(path = "/driver/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors
    ) {
        if (errors.hasErrors()) return editProfileForm(loggedUser, form, errors);
        ds.editProfile(loggedUser, form.getUsername(), form.getMail(), form.getDescription(), form.getCbu());
        return redirect("/driver/profile");
    }


    @RequestMapping(path = "/driver/vehicle/{plateNumber}/edit", method = RequestMethod.GET)
    public ModelAndView editVehicleGet(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable(name = "plateNumber") String plateNumber,
            @ModelAttribute("vehicleForm") VehicleForm form,
            BindingResult errors,
            @ModelAttribute("availabilityForm") AvailabilityForm avForm,
            Model model
    ) {
        var vehicle = ds.findVehicleByPlateNumber(loggedUser, plateNumber);
        if (vehicle.isPresent()) {
            if (errors != null && !errors.hasErrors()) form.setAll(vehicle.get());
            avForm.setAll(vehicle.get());
            var mav = new ModelAndView("driver/edit_vehicle");
            mav.addObject("vehicle", vehicle.get());
            String[] days = new String[]{
                    "monday", "tuesday", "wednesday", "thursday",
                    "friday", "saturday", "sunday"
            };
            mav.addObject("days", days);
            mav.addObject("shiftPeriods", ShiftPeriod.values());
            mav.addObject("zones", zs.getAllZones());
            mav.addObject("toasts", model.getAttribute("toasts"));
            return mav;
        } else {
            return new ModelAndView();
        }
    }

    @RequestMapping(path = "/driver/vehicle/{plateNumber}/edit", method = RequestMethod.POST)
    public ModelAndView editVehiclePost(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable(name = "plateNumber") String ogPlateNumber,
            @Valid @ModelAttribute("vehicleForm") VehicleForm form,
            BindingResult errors,
            @RequestParam(value = "vehicleImg", required = false) MultipartFile vehicleImg,
            @ModelAttribute("availabilityForm") AvailabilityForm avForm,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            return editVehicleGet(loggedUser, ogPlateNumber, form, errors, avForm, null);
        }
        String imgFilename = null;
        byte[] imgData = null;
        if (vehicleImg != null) {
            imgFilename = vehicleImg.getOriginalFilename();
            try {
                imgData = vehicleImg.getBytes();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        ds.updateVehicle(
                loggedUser,
                form.getId(),
                form.getPlateNumber(),
                form.getVolume(),
                form.getDescription(),
                form.getZoneIds(),
                form.getRate(),
                form.getImgId(),
                imgFilename,
                imgData
        );
        List<Toast> toasts = Collections.singletonList(new Toast(
                ToastType.success, "toast.vehicle.edit.success"
        ));
        redirectAttributes.addFlashAttribute("toasts", toasts);
        return redirect("/driver/vehicle/%s/edit", form.getPlateNumber());
    }

    private boolean addBookingData(ModelAndView mav, Driver driver, BookingState state, int currentPage) {
        int totPages = ds.getBookingPages(driver, state);
        if (totPages == 0) return true;
        if (currentPage > totPages || currentPage < 1) return false;
        String stateLowerCase = state.toString().toLowerCase();
        String stateCapitalized = stateLowerCase.substring(0, 1).toUpperCase() + stateLowerCase.substring(1);
        mav.addObject("tot" + stateCapitalized + "Pages", totPages);
        mav.addObject(stateLowerCase + "Page", currentPage);
        mav.addObject(stateLowerCase + "Bookings", ds.getBookings(driver, state, currentPage));
        return true;
    }

    @RequestMapping(path = "/driver/bookings")
    public ModelAndView driverBookings(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam(name = "pendingPage", defaultValue = "1") int pendingPage,
            @RequestParam(name = "acceptedPage", defaultValue = "1") int acceptedPage,
            @RequestParam(name = "finishedPage", defaultValue = "1") int finishedPage,
            @RequestParam(name = "rejectedPage", defaultValue = "1") int rejectedPage,
            @RequestParam(name = "canceledPage", defaultValue = "1") int canceledPage,
            @RequestParam(name = "activeTab", defaultValue = "PENDING") BookingState activeTab
    ) {
        final ModelAndView mav = new ModelAndView("driver/home");
        mav.addObject("currentDate", LocalDate.now());

        boolean errors = !addBookingData(mav, loggedUser, BookingState.PENDING, pendingPage) ||
                !addBookingData(mav, loggedUser, BookingState.ACCEPTED, acceptedPage) ||
                !addBookingData(mav, loggedUser, BookingState.FINISHED, finishedPage) ||
                !addBookingData(mav, loggedUser, BookingState.REJECTED, rejectedPage) ||
                !addBookingData(mav, loggedUser, BookingState.CANCELED, canceledPage);
        if (errors) return new ModelAndView();

        mav.addObject("activeTab", activeTab);
        return mav;
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/accept", method = RequestMethod.POST)
    public ModelAndView acceptBooking(
            @PathVariable("id") long bookingId
    ) {
        ds.acceptBooking(bookingId, LocaleContextHolder.getLocale());
        return redirect("/");
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/finish", method = RequestMethod.POST)
    public ModelAndView finishBooking(
            @PathVariable("id") long bookingId
    ) {
        ds.finishBooking(bookingId);
        return redirect("/");
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/reject", method = RequestMethod.POST)
    public ModelAndView rejectBooking(
            @PathVariable("id") long bookingId
    ) {
        ds.rejectBooking(bookingId, LocaleContextHolder.getLocale());
        return redirect("/");
    }

    @RequestMapping(path = "/driver/vehicle/{plateNumber}/delete", method = RequestMethod.POST)
    public ModelAndView deleteVehicle(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable("plateNumber") String plateNumber,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Vehicle> v = ds.findVehicleByPlateNumber(loggedUser, plateNumber);
        if (v.isPresent()) {
            ds.deleteVehicle(v.get());
            List<Toast> toasts = Collections.singletonList(new Toast(
                    ToastType.success, "toast.vehicle.delete.success"
            ));
            redirectAttributes.addFlashAttribute("toasts", toasts);
            return redirect("/driver/vehicles");
        } else {
            log.error("Vehicle `{}` not found", plateNumber);
            return new ModelAndView();
        }
    }


    @RequestMapping(path = "/driver/change/password")
    public ModelAndView changePassword(@ModelAttribute("loggedUser") Driver loggedUser, @ModelAttribute("changePasswordForm") ChangePasswordForm form) {
        ModelAndView mav = new ModelAndView("public/changePassword");
        mav.addObject("loggedUser", loggedUser);
        mav.addObject("userTypePath", "driver");
        return mav;
    }

    @RequestMapping(path = "/driver/change/password", method = RequestMethod.POST)
    public ModelAndView postChangePassword(@ModelAttribute("loggedUser") Driver loggedUser, @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form, BindingResult errors) {
        if (errors.hasErrors()) {
            return changePassword(loggedUser, form);
        }
        ds.updatePassword(loggedUser.getId(), form.getPassword());
        return new ModelAndView("redirect:/profile");
    }


}