package com.example.tsb.controllers;

import com.example.tsb.services.ReviewService;
import com.example.tsb.services.ServiceService;
import com.example.tsb.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final ServiceService serviceService;

    public ReviewController(ReviewService reviewService, UserService userService, ServiceService serviceService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.serviceService = serviceService;
    }
    @PostMapping("/reviews/new")
    public String saveReview(String nameUser, String serviceId, byte stars, String textReview, List<MultipartFile> images) throws IOException {
        reviewService.saveReview(stars, nameUser, textReview, images, serviceId);
        return "redirect:/reviews";
    }
    @GetMapping("/reviews")
    public String getReviews(Model model, Principal principal){
        if (principal!=null) {
            model.addAttribute("admin",userService.getUser(principal.getName()).isAdmin());

            model.addAttribute("people",userService.getUser(principal.getName()));
        }
        else {
            model.addAttribute("people",null);

        }
        if(principal!=null) {
            model.addAttribute("isAdmin", userService.getUser(principal.getName()).isAdmin());
        }
        else {
            model.addAttribute("isAdmin", false);

        }
        model.addAttribute("services",serviceService.getAll());
        model.addAttribute("reviews",reviewService.getAllReviewsByConfirm(true));
        return "reviews";
    }
    @GetMapping("/admin/reviews/change")
    public String getNoAcceptReviews(Model model){
        model.addAttribute("reviews",reviewService.getAllReviewsByConfirm(false));
        return "reviews-change";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PatchMapping("/reviews/change/{id}")
    public String acceptReviews(@PathVariable(name = "id") int id){
        reviewService.accept(id);
        return "redirect:/admin/reviews/change";
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/reviews/delete/{id}")
    public String deleteReviews(@PathVariable(name = "id") int id){
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews/change";
    }

}
