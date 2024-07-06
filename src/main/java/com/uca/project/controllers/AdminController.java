package com.uca.project.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PreAuthorize("hasAuthority('RESIDENTE')")
    @PostMapping("/test")
    public ResponseEntity<?> test(){
        return new ResponseEntity<>("Test Ok", HttpStatus.OK);
    }

    @PostMapping("/test2")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> test2(){
        return new ResponseEntity<>("Test Ok", HttpStatus.OK);
    }

}
