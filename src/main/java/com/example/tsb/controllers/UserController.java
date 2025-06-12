package com.example.tsb.controllers;

import com.example.tsb.models.User;
import com.example.tsb.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@Slf4j
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout){
        if(error!=null){
            log.info("Попытка входа");
            model.addAttribute("error", "Неверный логин или пароль");
        }
        if(logout!=null){
            log.info("Выход");
            model.addAttribute("error", "Выполнен выход");
        }

        return "login";
    }

    @PostMapping("/registration")
    public String createUser(@RequestParam String firstname, @RequestParam String lastname,
                             @RequestParam String email, @RequestParam String password,@RequestParam String phone, Model model) {
        User user=new User();
        user.setPassword(password);
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhoneNumber(phone);
        if (!userService.createUser(user)) {
            model.addAttribute("errorMessage", "Такой пользователь уже существует");
            model.addAttribute("name", firstname);
            model.addAttribute("lastname", lastname);
            model.addAttribute("phone", phone);
            model.addAttribute("email", email);
            model.addAttribute("password", password);
            return "registration";
        }
        return "redirect:/login";
    }
    @GetMapping("/registration")
    public String registration(){
        return "registration";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/admin/change")
    public String changeUser(Principal principal, @RequestParam String firstname, @RequestParam String lastname,
                             @RequestParam String email, @RequestParam String phone, Model model){

        if(userService.changeUser(principal.getName(), firstname, lastname, phone, email)){
            return "redirect:/admin";
        }{
            model.addAttribute("people",userService.getUser(principal.getName()));
            model.addAttribute("error", "Такой email уже есть");
            return "admin";
        }

    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/admin/change/password")
    public String changePassword(String passwordOld, Principal principal, String passwordNew, String passwordNew2, Model model) {

        if(!passwordNew.equals(passwordNew2)){
            model.addAttribute("people",userService.getUser(principal.getName()));
            model.addAttribute("errorPasswordNew", "Пароли не совпадают");
            model.addAttribute("passwordOld", passwordOld);
            model.addAttribute("passwordNew", passwordNew);
            model.addAttribute("passwordNew2", passwordNew2);
            return "admin";
        }
        if(userService.changePassword(passwordOld, passwordNew, principal.getName())){
            return "redirect:/admin";
        }
        else {
            model.addAttribute("people",userService.getUser(principal.getName()));
            model.addAttribute("errorPasswordOld", "Пароль не совпадет");
            model.addAttribute("passwordOld", passwordOld);
            model.addAttribute("passwordNew", passwordNew);
            model.addAttribute("passwordNew2", passwordNew2);
            return "admin.html";
        }

    }





}
