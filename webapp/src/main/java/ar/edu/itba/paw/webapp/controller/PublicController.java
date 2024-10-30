package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.ClientService;
import ar.edu.itba.paw.services.DriverService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class PublicController {
    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    @Autowired
    private ClientService cs;
    @Autowired
    private DriverService ds;
    @Autowired
    private ImageService is;

    public PublicController(ClientService cs, DriverService ds, ImageService is) {
        this.cs = cs;
        this.ds = ds;
        this.is = is;
    }

    @RequestMapping(path = {"/", "/home"})
    public ModelAndView index(@ModelAttribute("loggedUser") User loggedUser,
                              @RequestParam(value = "page", defaultValue = "0") int page) {
        if (loggedUser == null || !loggedUser.getIsDriver()) {
            return new ModelAndView("public/home");
        } else {
            // This should probably not be here but in the DriverController.
            // Yeah, no shit
            List<Booking> bookings = ds.getBookings(loggedUser.getId(),page);
            int totalBookings =  ds.getTotalBookingCount(loggedUser.getId());
            int totalPages = (int) Math.ceil((double) totalBookings / Pagination.BOOKINGS_PAGE_SIZE);
            final ModelAndView mav = new ModelAndView("driver/home");
            mav.addObject("currentPage", page);
            mav.addObject("currentDate", LocalDate.now());
            mav.addObject("totalPages", totalPages);
            mav.addObject("bookings",bookings);
            return mav;
        }
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return createForm(userForm);
        }
        final User user;
        if (userForm.getUserType().equals(UserRole.DRIVER.name()))
            user = ds.create(userForm.getUsername(), userForm.getMail(), userForm.getPassword(), "", LocaleContextHolder.getLocale());
        else
            user = cs.create(userForm.getUsername(), userForm.getMail(),userForm.getPassword(), LocaleContextHolder.getLocale());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), userForm.getPassword());
        SecurityContextHolder.getContext().setAuthentication(token);
        if(user.isDriver())
            return new ModelAndView("redirect:/driver/vehicles");
        return new ModelAndView("redirect:/client/search");
    }

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute("userForm") UserForm userForm) {
        return new ModelAndView("public/register");
    }

    @RequestMapping(path = "/login")
    public ModelAndView login() {
        return new ModelAndView("public/login");
    }

    @RequestMapping(path = "/upload/pfp", method = RequestMethod.POST)
    public String submit(@RequestParam("profilePicture") MultipartFile file, @ModelAttribute("loggedUser") User loggedUser) {
        if (file != null && !file.isEmpty()) {
            try {
                loggedUser.setPfp(is.uploadPfp(file.getBytes(), file.getOriginalFilename(), loggedUser.getId()));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return "redirect:/profile";
    }

    @RequestMapping(path = "/profile")
    public ModelAndView profile(@ModelAttribute("loggedUser") User loggedUser) {
        ModelAndView mav = new ModelAndView("public/profile");
        Optional<Driver> test = ds.findById(loggedUser.getId());
        test.ifPresent(driver -> mav.addObject("loggedDriver", driver));
        mav.addObject("loggedUser", loggedUser);
        return mav;
    }


    @RequestMapping(value = "/user/pfp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getUserPicture(@RequestParam("userPfp") int userPfp ,@ModelAttribute("loggedUser") User loggedUser) {
        return getValidatedPfp(is.getImage(userPfp));
    }

    @RequestMapping(value = "/profile/picture", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@ModelAttribute("loggedUser") User loggedUser) {
        return getValidatedPfp(is.getImage(loggedUser.getPfp()));
    }

    private ResponseEntity<byte[]> getValidatedPfp(Image pfp) {
        if (pfp != null && pfp.getData() != null) {
            String fileName = pfp.getFileName();
            String contentType;
            if(fileName == null)
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            if (fileName.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".jpg")) {
                contentType = "image/jpeg";
            } else {
                return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(pfp.getData().length);
            return new ResponseEntity<>(pfp.getData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/booking/pop", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> getProofOfPayment(@RequestParam("popId") long popId, @ModelAttribute("loggedUser") User loggedUser) {
        Image pop = is.getImage(popId);
        if (pop != null && pop.getFileName().endsWith(".pdf")) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            return new ResponseEntity<>(pop.getData(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}