package com.example.tsb.controllers;

import com.example.tsb.models.Schedule;
import com.example.tsb.models.Service;
import com.example.tsb.services.ScheduleService;
import com.example.tsb.services.ServiceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final ServiceService serviceService;

    public ScheduleController(ScheduleService scheduleService, ServiceService serviceService) {
        this.scheduleService = scheduleService;
        this.serviceService = serviceService;
    }

    @GetMapping("admin/schedule")
    public String getSchedule(Model model){
        model.addAttribute("schedules",scheduleService.getMapSchedules());
        return "schedule";
    }
    @GetMapping("/admin/schedule/new")
    public String getPageWithAddSchedule(Model model){

        model.addAttribute("services",serviceService.getAll());
        model.addAttribute("schedules",scheduleService.getDatesTimesForSchedules());
        return "add-schedule";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/admin/schedule/new")
    public String addSchedule(@RequestParam(name = "dates") ArrayList<LocalDateTime> dates,
                              @RequestParam(name = "ids") ArrayList<Long> ids){

        scheduleService.setTimes(dates,ids);
        return "redirect:/admin/schedule";
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/admin/schedule/delete/{id}")
    public String deleteSchedule(@PathVariable("id") Long id){
//        System.out.println(id);

        scheduleService.deleteById(id);


        return "redirect:/admin/schedule";
    }

}
