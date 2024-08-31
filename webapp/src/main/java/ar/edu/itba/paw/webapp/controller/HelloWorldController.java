package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class HelloWorldController {

  @Autowired
  private UserService us;

  public HelloWorldController(final UserService us) {}{
    this.us = us;
  }

  @RequestMapping("/")
  public ModelAndView index(@RequestParam(name = "userId", defaultValue = "1") long userId) {
    final ModelAndView mav = new ModelAndView("helloworld/index");
    mav.addObject("username", us.findById(1).get().getUsername());
    mav.addObject("userId", userId);
    return mav;
  }

  @RequestMapping("/{userId:\\d+}")
  public ModelAndView profile(@PathVariable(name = "userId") long userId) {
    final ModelAndView mav = new ModelAndView("helloworld/profile");
    Optional<User> user = us.findById(userId);
    mav.addObject("username", user.get().getUsername());
    mav.addObject("userId", user.get().getId());
    return mav;
  }
}
