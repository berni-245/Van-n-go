package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.AvailabilityForm;
import ar.edu.itba.paw.webapp.form.ChangePasswordForm;
import ar.edu.itba.paw.webapp.form.ChangeUserInfoForm;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import ar.edu.itba.paw.webapp.interfaces.Bookings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Controller
public class DriverController implements Bookings {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverController.class);
    @Autowired
    private DriverService ds;
    @Autowired
    private ClientService cs;
    @Autowired
    private ZoneService zs;
    @Autowired
    private ImageService is;
    @Autowired
    private MessageService ms;

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.POST)
    public ModelAndView addVehiclePost(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("vehicleForm") VehicleForm vehicleForm,
            BindingResult errors,
            @RequestParam(value = "vehicleImg", required = false) MultipartFile vehicleImg,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in VehicleForm by {}", loggedUser.getUsername());
            return addVehicleGet(vehicleForm);
        }
        String imgFilename = null;
        byte[] imgData = null;
        if (vehicleImg != null) {
            imgFilename = vehicleImg.getOriginalFilename();
            imgData = vehicleImg.getBytes();
        }
        ds.addVehicle(
                loggedUser,
                vehicleForm.getPlateNumber(),
                vehicleForm.getVolume(),
                vehicleForm.getDescription(),
                vehicleForm.getZoneIds(),
                vehicleForm.getRate(),
                imgFilename,
                imgData
        );
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.vehicle.add.success"));
        return redirect("/driver/vehicles");
    }

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.GET)
    public ModelAndView addVehicleGet(@ModelAttribute("vehicleForm") VehicleForm vehicleForm) {
        ModelAndView mav = new ModelAndView("driver/add_vehicle");
        mav.addObject("zones", zs.getAllZones());
        return mav;
    }

    @RequestMapping(value = "/driver/chat", method = RequestMethod.GET)
    public ModelAndView chat(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("recipientId") Integer recipientId
    ) {
        final ModelAndView mav = new ModelAndView("driver/chat");
        Client client = cs.findById(recipientId);
        Booking booking = cs.getBookingById(bookingId).orElseThrow();
        List<Message> messages = ms.getConversation(booking, client, loggedUser);
        mav.addObject("recipient", client);
        mav.addObject("booking", booking);
        mav.addObject("messages", messages);
        mav.addObject("clientZone", zs.getClientZone(client));
        return mav;
    }

    @RequestMapping(path = "/driver/send", method = RequestMethod.POST)
    public ModelAndView send(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam("content") String content,
            @RequestParam("bookingId") Integer bookingId,
            @RequestParam("recipientId") Integer recipientId
    ) {
        ms.sendDriverMessage(bookingId, loggedUser, recipientId, content);
        return redirect("/driver/chat?bookingId=%d&recipientId=%d", bookingId, recipientId);
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
        Vehicle vehicle = ds.findVehicleByPlateNumber(loggedUser, plateNumber).orElseThrow();
        ds.updateAvailability(
                vehicle,
                form.getMondayShiftPeriods(),
                form.getTuesdayShiftPeriods(),
                form.getWednesdayShiftPeriods(),
                form.getThursdayShiftPeriods(),
                form.getFridayShiftPeriods(),
                form.getSaturdayShiftPeriods(),
                form.getSundayShiftPeriods()
        );
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.availability.edit.success"));
        return redirect("/driver/vehicle/%s/edit", plateNumber);
    }

    @RequestMapping(path = "/driver/vehicles")
    public ModelAndView vehiclesDashboard(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        final ModelAndView mav = new ModelAndView("driver/vehicles");
        int totalPages = ds.getVehicleCount(loggedUser);
        page = Pagination.validatePage(page,totalPages);
        mav.addObject("vehicles", ds.getVehicles(loggedUser, page));
        mav.addObject("totalPages", totalPages);
        mav.addObject("currentPage", page);
        mav.addObject("toasts", model.getAttribute("toasts"));
        return mav;
    }


    @RequestMapping(path = "/driver/profile", method = RequestMethod.GET)
    public ModelAndView profile(@ModelAttribute("loggedUser") Driver loggedUser) {
        return new ModelAndView("user/profile");
    }

    @RequestMapping(path = "/driver/profile/edit", method = RequestMethod.GET)
    public ModelAndView editProfileForm(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors
    ) {
        if (!errors.hasErrors()) {
            form.setMail(loggedUser.getMail());
            form.setUsername(loggedUser.getUsername());
            form.setCbu(loggedUser.getCbu());
            form.setDescription(loggedUser.getDescription());
            form.setLanguage(loggedUser.getLanguage().name());
        }
        ModelAndView mav = new ModelAndView("/user/profileEdit");
        mav.addObject("languages", Language.values());
        return mav;
    }

    @RequestMapping(path = "/driver/profile/edit", method = RequestMethod.POST)
    public ModelAndView editProfile(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("changeUserInfoForm") ChangeUserInfoForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in ChangeUserInfoForm by {}", loggedUser.getUsername());
            return editProfileForm(loggedUser, form, errors);
        }
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.user.change.userData.success"));
        ds.editProfile(loggedUser, form.getUsername(), form.getMail(), form.getDescription(), form.getCbu(), form.getLanguage());
        return redirect("/driver/profile");
    }


    @RequestMapping(path = "/driver/vehicle/{plateNumber}/edit", method = RequestMethod.GET)
    public ModelAndView editVehicleGet(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable(name = "plateNumber") String plateNumber,
            @ModelAttribute("vehicleForm") VehicleForm form,
            BindingResult errors,
            @ModelAttribute("availabilityForm") AvailabilityForm avForm
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
    ) throws IOException {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in VehicleForm for vehicle {}", form.getPlateNumber());
            return editVehicleGet(loggedUser, ogPlateNumber, form, errors, avForm);
        }
        String imgFilename = null;
        byte[] imgData = null;
        if (vehicleImg != null) {
            imgFilename = vehicleImg.getOriginalFilename();
            imgData = vehicleImg.getBytes();
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
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.vehicle.edit.success"));
        return redirect("/driver/vehicle/%s/edit", form.getPlateNumber());
    }

    @RequestMapping(path = "/driver/bookings")
    public ModelAndView driverBookings(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam(name = "pendingPage", defaultValue = "1") int pendingPage,
            @RequestParam(name = "acceptedPage", defaultValue = "1") int acceptedPage,
            @RequestParam(name = "finishedPage", defaultValue = "1") int finishedPage,
            @RequestParam(name = "rejectedPage", defaultValue = "1") int rejectedPage,
            @RequestParam(name = "canceledPage", defaultValue = "1") int canceledPage,
            @RequestParam(name = "activeTab", defaultValue = "PENDING") BookingState activeTab,
            RedirectAttributes redirectAttrs
    ) {
        return userBookings(
                "driver/bookings",
                ds,
                loggedUser,
                pendingPage,
                acceptedPage,
                finishedPage,
                rejectedPage,
                canceledPage,
                activeTab,
                redirectAttrs
        );
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/accept", method = RequestMethod.POST)
    public ModelAndView acceptBooking(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable("id") int bookingId
    ) {
        ds.acceptBooking(bookingId, loggedUser);
        return redirect("/driver/bookings?activeTab=ACCEPTED");
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/finish", method = RequestMethod.POST)
    public ModelAndView finishBooking(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable("id") int bookingId
    ) {
        ds.finishBooking(bookingId, loggedUser);
        return redirect("/driver/bookings?activeTab=FINISHED");
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/reject", method = RequestMethod.POST)
    public ModelAndView rejectBooking(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable("id") int bookingId
    ) {
        ds.rejectBooking(bookingId, loggedUser);
        return redirect("/driver/bookings?activeTab=REJECTED");
    }

    @RequestMapping(path = "/driver/booking/{id:\\d+}/cancel", method = RequestMethod.POST)
    public ModelAndView cancelBooking(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @PathVariable("id") int bookingId
    ) {
        ds.cancelBooking(bookingId, loggedUser);
        return redirect("/driver/bookings?activeTab=CANCELED");
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
            setToasts(redirectAttributes, new Toast(ToastType.success, "toast.vehicle.delete.success"));
            return redirect("/driver/vehicles");
        } else {
            LOGGER.error("Vehicle `{}` not found", plateNumber);
            return new ModelAndView();
        }
    }


    @RequestMapping(path = "/driver/change/password", method = RequestMethod.GET)
    public ModelAndView changePassword(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @ModelAttribute("changePasswordForm") ChangePasswordForm form
    ) {
        return new ModelAndView("public/changePassword");
    }

    @RequestMapping(path = "/driver/change/password", method = RequestMethod.POST)
    public ModelAndView postChangePassword(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("changePasswordForm") ChangePasswordForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            LOGGER.warn("Invalid params in ChangePasswordForm for {}", loggedUser.getUsername());
            return changePassword(loggedUser, form);
        }
        ds.updatePassword(loggedUser, form.getPassword());
        setToasts(redirectAttributes, new Toast(ToastType.success, "toast.user.change.password.success"));
        return new ModelAndView("redirect:/profile");
    }


}