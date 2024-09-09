package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(method = RequestMethod.POST)
@Controller
public class MailController {
    @Autowired
    private MailService mailService;


}
