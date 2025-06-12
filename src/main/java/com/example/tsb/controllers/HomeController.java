package com.example.tsb.controllers;

import com.example.tsb.configs.EmailSender;
import com.example.tsb.models.Appointment;
import com.example.tsb.models.Review;
import com.example.tsb.models.Service;
import com.example.tsb.services.AppointmentService;
import com.example.tsb.services.ReviewService;
import com.example.tsb.services.ServiceService;
import com.example.tsb.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
public class HomeController {
    private final UserService userService;
    private final ServiceService serviceService;
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;
    private final EmailSender emailSender;

    public HomeController(UserService userService, ServiceService serviceService, AppointmentService appointmentService, ReviewService reviewService, EmailSender emailSender) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
        this.emailSender = emailSender;
    }
    @PostMapping("/feedback")
    public String getFeedback(String name, String text, String email){
        log.info("Фидбек");
        emailSender.sendMessage("cvlastyuk@mail.ru","имя: "+name+ "\nemail: "+email+"\nтекст: "+text,"фидбек");
        return "redirect:/#feedback";
    }

    @GetMapping("/")
    public String getHome(Model model, Principal principal, @RequestParam(value = "successfully", required = false) Long successfully){
        log.info("Посещение главной страницы");
        if (successfully != null) {
            Appointment appointment=appointmentService.getById(successfully);
            if((appointment!=null)&&(appointment.getRecordingTime().plusSeconds(15).isAfter(LocalDateTime.now()))){
                model.addAttribute("error", "Вы успешно записались");
            }
            else {
                return "redirect:/";
            }
        }
        List<Service> services=serviceService.getAll();

        List<Review> reviews=reviewService.getAllReviewsByConfirm(true);
        if(services.size()>3) {
            model.addAttribute("services", services.subList(0, 3));
        }
        else{
            model.addAttribute("services", services);
        }

        if(reviews.size()>3) {
            model.addAttribute("reviews", reviews.subList(0, 3));
        }
        else{
            model.addAttribute("reviews", reviews);
        }
        if(principal!=null) {
            model.addAttribute("isAdmin", userService.getUser(principal.getName()).isAdmin());
        }
        else {
            model.addAttribute("isAdmin", false);

        }
        return "home";
    }

}
