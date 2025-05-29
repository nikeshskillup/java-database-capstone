package com.smartclinic.controller;

import com.smartclinic.model.Admin;
import com.smartclinic.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    private final Service service;

    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
