package net.lotfi.ems.controller;


import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.LeaveDto;
import net.lotfi.ems.service.EmployeeService;
import net.lotfi.ems.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    private LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    // get all leaves
    @GetMapping
    public ResponseEntity<List<LeaveDto>> getAllLeaves(){
        List<LeaveDto> leaveDtos = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaveDtos);
    }

    @PutMapping("{leaveId}/approve")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<LeaveDto> approveLeave(@PathVariable Long leaveId){
        LeaveDto updatedLeaveDto = leaveService.approveLeave(leaveId);
        return ResponseEntity.ok(updatedLeaveDto);
    }

    @PutMapping("{leaveId}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<LeaveDto> cancelLeave(@PathVariable Long leaveId){
        LeaveDto updatedLeaveDto = leaveService.cancelLeave(leaveId);
        return ResponseEntity.ok(updatedLeaveDto);
    }

    @PutMapping("{leaveId}/reject")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<LeaveDto> rejectLeave(@PathVariable Long leaveId){
        LeaveDto updatedLeaveDto = leaveService.rejectLeave(leaveId);
        return ResponseEntity.ok(updatedLeaveDto);
    }



}
