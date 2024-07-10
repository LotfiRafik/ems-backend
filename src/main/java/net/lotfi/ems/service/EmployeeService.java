package net.lotfi.ems.service;

import net.lotfi.ems.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    List<EmployeeDto> getAllEmployees();
    
    void deleteEmployee(Long id);

    EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto);
}
