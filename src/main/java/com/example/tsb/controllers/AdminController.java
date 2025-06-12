package com.example.tsb.controllers;

import com.example.tsb.models.Service;
import com.example.tsb.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class AdminController {
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final ServiceService serviceService;
    private final ReviewService reviewService;

    private final UserService userService;

    public AdminController(ScheduleService scheduleService, AppointmentService appointmentService, ServiceService serviceService, ReviewService reviewService, UserService userService) {
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
        this.serviceService = serviceService;
        this.reviewService = reviewService;
        this.userService = userService;
    }
    @GetMapping("/admin/services/new")
    public String getPageForAddService(Model model,@RequestParam(value = "id", required = false) Long id){
        if(id!=null){
            Service service=serviceService.getById(id);
            if(service!=null){
                model.addAttribute("service", service);
            }
        }



        return "add-service";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/admin/services/new")
    public String newService(String name, int price, List<MultipartFile> file, String description,
                             Long id, boolean saveSchedule) throws IOException {
        if(id==null) {
            serviceService.save(name, price, file, description, saveSchedule);
        }
        else {
            serviceService.change(name, price, file, description,  id);
        }
        return "redirect:/services";
    }
    @GetMapping("/superadmin")
    public String getSuperAdminPage(Model model, Principal principal){
        log.info("вход в суперадминку");
        model.addAttribute("users",userService.getUsers());
        return "superadmin";
    }
    @PostMapping("/superadmin/users/delete")
    public String deleteUser(Long id){
        userService.delete(id);
        return "redirect:/superadmin";
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    @PostMapping("/superadmin/users/change")
    public String changeUser(Long id, boolean active, String email,
                             String firstname, String lastname, String password, String phoneNumber, String roles){
        userService.changeUser(id, active, email, firstname, lastname, password, phoneNumber,roles);
        return "redirect:/superadmin";
    }
    @GetMapping("/admin")
    public String getAdminPage(Model model, Principal principal){
        log.info("вход в админку");
        model.addAttribute("people",userService.getUser(principal.getName()));
        return "admin";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/services/delete/{id}")
    public String deleteService(@PathVariable("id") Long id) {

        serviceService.deleteService(id);
        return "redirect:/";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteReview(id);
        return "redirect:/reviews";
    }
}
