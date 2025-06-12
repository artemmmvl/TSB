package com.example.tsb.controllers;

import com.example.tsb.models.Appointment;
import com.example.tsb.models.Service;
import com.example.tsb.services.AppointmentService;
import com.example.tsb.services.ScheduleService;
import com.example.tsb.services.ServiceService;
import com.example.tsb.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final ServiceService serviceService;
    private final ScheduleService scheduleService;
    private final UserService userService;

    public AppointmentController(AppointmentService appointmentService, ServiceService serviceService, ScheduleService scheduleService, UserService userService) {
        this.appointmentService = appointmentService;
        this.serviceService = serviceService;
        this.scheduleService = scheduleService;
        this.userService = userService;
    }
    @GetMapping("/appointments/new")
    public String getPageForNewAppointment(Model model, Principal principal, Long service){
        List<Service> services=serviceService.getAll();
        if (service!=null) {
            model.addAttribute("schedule",serviceService.getMapSchedulesById(service));
            model.addAttribute("serviceId",service);
        }
        else {
            if(!services.isEmpty()) {
                return "redirect:/appointments/new?service="+services.get(0).getId();
            }
        }

        model.addAttribute("services",services);
        return "add-appointment";
    }
    @PostMapping("/appointments/new")
    public String addAppointment(Model model, String email, String inst, String name, String phone, Long schedule, String comment, Long serviceId, boolean send_email){
        Long idAppointment=appointmentService.setAppointment(email, inst, name, phone, schedule, comment,serviceId, send_email);
        if(idAppointment==-1L){
            log.info("Ошибка при записи. ");
            model.addAttribute("error", "Ошибка при записи. Попробуйте снова");
            List<Service> services=serviceService.getAll();
            Service service=serviceService.getById(serviceId);
            if (service!=null) {
                model.addAttribute("schedule",serviceService.getMapSchedulesById(serviceId));
                model.addAttribute("serviceId",serviceId);
            }
            else{
                if(!services.isEmpty()) {
                    model.addAttribute("schedule",serviceService.getMapSchedulesById(services.get(0).getId()));
                    model.addAttribute("serviceId",services.get(0).getId());
                }
            }
            model.addAttribute("services",services);
            model.addAttribute("name",name);
            model.addAttribute("email",email);
            model.addAttribute("phone",phone);
            model.addAttribute("inst",inst);

            model.addAttribute("comment", comment);
            return "add-appointment";
        }
        return "redirect:/?successfully="+idAppointment;
    }
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @DeleteMapping("/admin/appointments/delete/{id}")
    public String deleteAppointment(@PathVariable("id") Long id) {
        appointmentService.delete(id);
        return "redirect:/admin/schedule";
    }



}
