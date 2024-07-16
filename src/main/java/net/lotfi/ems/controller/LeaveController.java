package net.lotfi.ems.controller;


import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.LeaveDto;
import net.lotfi.ems.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    private EmployeeService employeeService;

    @Autowired
    public LeaveController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // get all leaves
    @GetMapping
    public ResponseEntity<List<LeaveDto>> getAllLeaves(){
        List<LeaveDto> leaveDtos = employeeService.getAllLeaves();
        return ResponseEntity.ok(leaveDtos);
    }


}
