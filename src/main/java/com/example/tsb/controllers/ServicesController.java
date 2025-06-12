package com.example.tsb.controllers;

import com.example.tsb.services.ServiceService;
import com.example.tsb.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;


@Controller
public class ServicesController {
    private final ServiceService serviceService;
    private final UserService userService;

    public ServicesController(ServiceService serviceService, UserService userService) {
        this.serviceService = serviceService;
        this.userService = userService;
    }
    @GetMapping("/services")
    public String getServices(Model model, Principal principal){
        if(principal!=null) {
            model.addAttribute("isAdmin", userService.getUser(principal.getName()).isAdmin());
        }
        else {
            model.addAttribute("isAdmin", false);

        }
        model.addAttribute("services",serviceService.getAll());
        return "services";
    }
    @GetMapping("/services/{id}")
    public String getService(@PathVariable("id") Long id, Model model, Principal principal) {
        if(principal!=null) {
            model.addAttribute("admin", userService.getUser(principal.getName()).isAdmin());
        }
        model.addAttribute("service",serviceService.getById(id));
        return "service";
    }
}
