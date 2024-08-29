package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloWorldController {

  @Autowired
  private UserService us;

  public HelloWorldController(final UserService us) {}{
    this.us = us;
  }

  @RequestMapping("/")
  public ModelAndView index() {
    final ModelAndView mav = new ModelAndView("index");
    mav.addObject("username", us.findById(1).get().getUsername());

    return mav;
  }
}
