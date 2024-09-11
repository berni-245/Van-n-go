package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping(method = RequestMethod.POST)
@Controller
public class MailController {
    @Autowired
    private MailService mailService;

    @RequestMapping(path = "/availability/contact", method = RequestMethod.POST)
    public ModelAndView sendRequestServiceMail(@RequestParam("clientMail") String clientMail,
                                       @RequestParam("jobDescription") String jobDescription,
                                       @RequestParam("driverMail") String driverMail,
                                       @RequestParam("driverName") String driverName,
                                       @RequestParam("clientName") String clientName) {
        mailService.sendRequestedHauler(clientMail, driverMail, clientName, driverName, jobDescription);
        return new ModelAndView("redirect:/availability");
    }

}
