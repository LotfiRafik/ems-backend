package net.lotfi.ems.controller;


import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.EmployeeManagerDto;
import net.lotfi.ems.dto.LeaveDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.service.EmployeeService;
import net.lotfi.ems.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private EmployeeService employeeService;
    private LeaveService leaveService;

    @Autowired
    public EmployeeController(EmployeeService employeeService, LeaveService leaveService) {
        this.employeeService = employeeService;
        this.leaveService = leaveService;
    }

    // Build add employee REST API
    @PostMapping
    // TODO add user fields (password...etc)
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody  EmployeeDto employeeDto){
        EmployeeDto savedEmployeeDto = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployeeDto, HttpStatus.CREATED);
    }

    // Build Get Employee endpoint
    @GetMapping("{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId) {
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employeeDto);
    }

    // Get all employees
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(){
        List<EmployeeDto> employeeDtos = employeeService.getAllEmployees();
        return ResponseEntity.ok(employeeDtos);
    }

	// update employee rest api
	@PutMapping("{id}")
	public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto){
		EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(id, employeeDto);
		return ResponseEntity.ok(updatedEmployeeDto);
	}


    // delete employee rest api
    @DeleteMapping("{id}")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }

    // **************************** EMPLOYEES LEAVES MANAGEMENT *******************************

    @PostMapping("{employeeId}/leaves")
    public ResponseEntity<LeaveDto> createLeave(@RequestBody LeaveDto leaveDto, @PathVariable Long employeeId){
        // TODO input validation
        leaveDto.setEmployeeId(employeeId);
        LeaveDto savedLeaveDto = employeeService.createLeave(leaveDto);
        return new ResponseEntity<>(savedLeaveDto, HttpStatus.CREATED);
    }

    @GetMapping("{employeeId}/leaves")
    public ResponseEntity<List<LeaveDto>> getEmployeeLeaves(@PathVariable Long employeeId){
        List<LeaveDto> leaveDtos = leaveService.getEmployeeLeaves(employeeId);
        return ResponseEntity.ok(leaveDtos);
    }

    // *************************** EMPLOYEE MANAGER *****************************
    // update employee rest api
    @PutMapping("{employeeId}/manager")
    public ResponseEntity<EmployeeDto> updateEmployeeManager(@PathVariable Long employeeId, @RequestBody EmployeeManagerDto employeeManagerDto){
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployeeManager(employeeId, employeeManagerDto);
        return ResponseEntity.ok(updatedEmployeeDto);
    }




}
