package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ZoneService;
import ar.edu.itba.paw.webapp.form.AvailabilityForm;
import ar.edu.itba.paw.webapp.form.VehicleForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class DriverController {
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
        final Vehicle vehicle = ds.addVehicle(
                loggedUser.getId(),
                vehicleForm.getPlateNumber(),
                vehicleForm.getVolume(),
                vehicleForm.getDescription(),
                vehicleForm.getRate()
        );
        if(vehicleImg != null) {
            try{
                is.uploadVehicleImage(vehicleImg.getBytes(),vehicleImg.getOriginalFilename(),(int) vehicle.getId());
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }
        List<Toast> toasts = Collections.singletonList(new Toast(
                ToastType.success, "toast.vehicle.add.success"
        ));
        redirectAttributes.addFlashAttribute("toasts", toasts);
        return new ModelAndView("redirect:/driver/vehicles");
    }

    @RequestMapping(path="/vehicle/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getVehicleImage(@RequestParam("vehicleId") int vehicleId, @ModelAttribute("loggedUser") User loggedUser){
        Image vehicleImg = is.getVehicleImage(vehicleId);
        if(vehicleImg==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String fileName = vehicleImg.getFileName();
        String contentType;
        if(fileName == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        if(fileName.toLowerCase().endsWith(".png"))
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

    @RequestMapping(path = "/driver/vehicle/add", method = RequestMethod.GET)
    public ModelAndView addVehicleGet(@ModelAttribute("vehicleForm") VehicleForm vehicleForm) {
        return new ModelAndView("driver/add_vehicle");
    }

    @RequestMapping(path = "/driver/availability/add", method = RequestMethod.POST)
    public ModelAndView addAvailability(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @Valid @ModelAttribute("availabilityForm") AvailabilityForm form,
            BindingResult errors,
            RedirectAttributes redirectAttributes
    ) {
        if (errors.hasErrors()) {
            return addAvailabilityForm(loggedUser, form);
        }
        ds.addWeeklyAvailability(
                loggedUser.getId(),
                form.getWeekDays(),
                form.getTimeStart(),
                form.getTimeEnd(),
                form.getZoneIds(),
                form.getVehicleIds()
        );
        List<Toast> toasts = Collections.singletonList(new Toast(
                ToastType.success, "toast.availability.add.success"
        ));
        redirectAttributes.addFlashAttribute("toasts", toasts);
        return new ModelAndView("redirect:/driver/availability");
    }

    @RequestMapping(path = "/driver/availability/add", method = RequestMethod.GET)
    public ModelAndView addAvailabilityForm(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @ModelAttribute("availabilityForm") AvailabilityForm availabilityForm
    ) {
        Optional<Driver> driver = ds.findById(loggedUser.getId());

        final ModelAndView mav = new ModelAndView("driver/add_availability");
        mav.addObject("vehicles", ds.getVehicles(loggedUser.getId()));
        mav.addObject("zones", zs.getAllZones());
        return mav;
    }

    @RequestMapping(path = "/driver/vehicles")
    public ModelAndView vehiclesDashboard(
            @ModelAttribute("loggedUser") Driver loggedUser,
            Model model
    ) {
        final ModelAndView mav = new ModelAndView("driver/vehicles");
        mav.addObject("vehicles", ds.getVehicles(loggedUser.getId()));
        if (model.containsAttribute("toasts")) {
            mav.addObject("toasts", model.getAttribute("toasts"));
        }
        return mav;
    }

    @RequestMapping(path = "/driver/availability")
    public ModelAndView availabilityDashboard(
            @ModelAttribute("loggedUser") Driver loggedUser,
            Model model
    ) {
        final ModelAndView mav = new ModelAndView("driver/availability");
        mav.addObject("vehicles", ds.getVehiclesFull(loggedUser.getId()));
        if (model.containsAttribute("toasts")) {
            mav.addObject("toasts", model.getAttribute("toasts"));
        }
        return mav;
    }

    @RequestMapping(path = "/driver/vehicle/edit", method = RequestMethod.GET)
    public ModelAndView editVehicleGet(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam(name = "plateNumber") String plateNumber,
            @ModelAttribute("vehicleForm") VehicleForm form
    ) {
        var vehicle = ds.findVehicleByPlateNumber(loggedUser.getId(), plateNumber);
        if (vehicle.isPresent()) {
            form.setAll(vehicle.get());
            var mav = new ModelAndView("driver/edit_vehicle");
            mav.addObject("plateNumber", plateNumber);
            mav.addObject("vehicleId", vehicle.get().getId());
            return mav;
        } else {
            return new ModelAndView();
        }
    }

    @RequestMapping(path = "/driver/vehicle/edit", method = RequestMethod.POST)
    public ModelAndView editVehiclePost(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam("ogPlateNumber") String ogPlateNumber,
            @Valid @ModelAttribute("vehicleForm") VehicleForm form,
            BindingResult errors,
            @RequestParam(value = "vehicleImg", required = false) MultipartFile vehicleImg
    ) {
        // Clearly hay que buscar la manera correcta de ignorar un error particular.
        if (errors.hasErrors()) {
            boolean ignoreError = false;
            if (errors.getErrorCount() == 1 && form.getPlateNumber().equals(ogPlateNumber)) {
                for (var error : errors.getFieldErrors("plateNumber")) {
                    if (error.getCode().equals("ValidPlateNumber")) {
                        ignoreError = true;
                    }
                }
            }
            if (!ignoreError) {
                return editVehicleGet(loggedUser, ogPlateNumber, form);
            }
        }
        boolean success = ds.updateVehicle(loggedUser.getId(), new Vehicle(
                form.getId(),
                loggedUser.getId(),
                form.getPlateNumber(),
                form.getVolume(),
                form.getDescription(),
                null,
                form.getRate()
        ));
        Image aux = is.getVehicleImage((int) form.getId());
        if(vehicleImg != null && !vehicleImg.isEmpty() && (aux == null || !aux.getFileName().equals(vehicleImg.getOriginalFilename())))
            try{
                is.uploadVehicleImage(vehicleImg.getBytes(), vehicleImg.getOriginalFilename(),(int) form.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        return new ModelAndView("redirect:/driver/vehicles");
    }

    @RequestMapping(path = "/driver/availability/edit", method = RequestMethod.GET)
    public ModelAndView editAvailabilityGet(
            @ModelAttribute("loggedUser") Driver loggedUser,
            @RequestParam(name = "availabilityId") long availabilityId,
            @ModelAttribute("vehicleForm") VehicleForm form
    ) {
//        var vehicle = ds.findVehicleByPlateNumber(loggedUser.getId(), availabilityId);
//        if (vehicle.isPresent()) {
//            form.setAll(vehicle.get());
//            var mav = new ModelAndView("driver/edit_vehicle");
//            mav.addObject("plateNumber", availabilityId);
//            return mav;
//        } else {
        return new ModelAndView();
//        }
    }


    @RequestMapping(path = "/driver/acceptBooking", method = RequestMethod.POST)
    public ModelAndView acceptBooking(
            @RequestParam("bookingId") long bookingId
    ) {
        ds.acceptBooking(bookingId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path = "/driver/rejectBooking", method = RequestMethod.POST)
    public ModelAndView denyBooking(
            @RequestParam("bookingId") long bookingId
    ) {
        ds.rejectBooking(bookingId);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value = "/driver/pop", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getProofOfPayment(@RequestParam("bookingId") long bookingId, @ModelAttribute("loggedUser") User loggedUser){
        Image pop = is.getPop((int) bookingId);
        if(pop!=null&&pop.getFileName().endsWith(".pdf")){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            return new ResponseEntity<>(pop.getData(),headers,HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}