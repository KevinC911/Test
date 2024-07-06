package com.uca.project.controllers;

import com.uca.project.domain.DTOs.AddRoleDTO;
import com.uca.project.domain.DTOs.AddRoleToUserDTO;
import com.uca.project.services.RoleService;
import com.uca.project.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.uca.project.domain.entities.User;
import com.uca.project.domain.entities.Role;
import org.springframework.web.bind.annotation.*;

// Controller para roles sin autenticacion, quitar posteriormente en prod
@RestController
@RequestMapping("/auth")
public class RoleController {
    private final RoleService roleService;

    private final UserService userService;

    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @PostMapping("/role")
    public ResponseEntity<?> createRole(@RequestBody AddRoleDTO info){
        roleService.addRole(info.getCode(), info.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/user-role")
    public ResponseEntity<?> createUserRole(@RequestBody AddRoleToUserDTO info){
        User user = userService.findByIdentifier(info.getIdentifier());
        Role role = roleService.getRole(info.getRole_name());
        roleService.addRoleToUser(role, user);

        return new ResponseEntity<>(HttpStatus.CREATED);

    }
}
