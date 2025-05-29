package com.project.back_end.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.project.back_end.service.TokenValidationService;

import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private TokenValidationService tokenValidationService;

    @GetMapping("/adminDashboard/{token}")
    public ModelAndView adminDashboard(@PathVariable String token) {
        Map<String, Object> validationResult = tokenValidationService.validateToken(token, "admin");
        if (validationResult.isEmpty()) {
            return new ModelAndView("admin/adminDashboard");
        } else {
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public ModelAndView doctorDashboard(@PathVariable String token) {
        Map<String, Object> validationResult = tokenValidationService.validateToken(token, "doctor");
        if (validationResult.isEmpty()) {
            return new ModelAndView("doctor/doctorDashboard");
        } else {
            return new ModelAndView("redirect:/");
        }
    }
}
