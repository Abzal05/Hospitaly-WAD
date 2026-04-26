package com.example.demo.controller.mvc;

import com.example.demo.facade.HospitalFacade;
import com.example.demo.service.SystemStatsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final SystemStatsService statsService;
    private final HospitalFacade hospitalFacade;

    @Value("${app.notification.strategy:console}")
    private String notificationStrategy;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    public HomeController(SystemStatsService statsService, HospitalFacade hospitalFacade) {
        this.statsService = statsService;
        this.hospitalFacade = hospitalFacade;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("doctorCount",      statsService.getDoctorCount());
        model.addAttribute("patientCount",     statsService.getPatientCount());
        model.addAttribute("appointmentCount", statsService.getAppointmentCount());
        model.addAttribute("userCount",        statsService.getUserCount());
        model.addAttribute("uptimeSeconds",    statsService.getUptimeSeconds());
        model.addAttribute("appointmentsSinceStartup", statsService.getAppointmentsCreatedSinceStartup());
        model.addAttribute("usersSinceStartup",        statsService.getUsersRegisteredSinceStartup());
        model.addAttribute("notificationStrategy", notificationStrategy);
        model.addAttribute("mailEnabled", mailEnabled);
        return "index";
    }
}
