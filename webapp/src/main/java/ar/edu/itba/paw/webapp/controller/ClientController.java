package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.TestUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ClientController {
    @RequestMapping("/posts")
    public ModelAndView viewPosts() {
        final ModelAndView mav = new ModelAndView("client/availability");
        List<TestUser> users = new ArrayList<>();
        TestUser user1 = new TestUser("john_doe", 1, "Delivery specialist, centered in doing stuff with big text", "Toyota Corolla", 500);
        TestUser user2 = new TestUser("jane_smith", 2, "Freight handler", "Ford F-150", 1000);
        TestUser user3 = new TestUser("alice_jones", 3, "Courier", "Honda Civic", 300);
        TestUser user4 = new TestUser("bob_brown", 4, "Warehouse operator", "Chevrolet Silverado", 1200);
        TestUser user5 = new TestUser("charlie_black", 5, "Logistics manager", "Ram 1500", 1500);
        TestUser user6 = new TestUser("dave_white", 6, "Transport coordinator", "Nissan Frontier", 800);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        mav.addObject("users", users);
        return mav;
    }

}