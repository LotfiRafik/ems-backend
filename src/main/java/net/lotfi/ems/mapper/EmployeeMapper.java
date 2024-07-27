package net.lotfi.ems.mapper;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.enums.RoleEnum;

import java.util.Date;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee){
        EmployeeDto employeeDto = new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail()
        );

        employeeDto.setAvailableLeaveDays(employee.getAvailableLeaveDays());
        if (employee.getManager() != null){
            employeeDto.setManagerId(employee.getManager().getId());
        }

        return employeeDto;
    }

    public static Employee mapToEmployee(EmployeeDto employeeDto){
        Employee employee = new Employee(
                employeeDto.getId(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                employeeDto.getEmail()
        );
        employee.setAvailableLeaveDays(employeeDto.getAvailableLeaveDays());
        employee.setPassword(employeeDto.getPassword());
        employee.setRole(employeeDto.getRole());

        return employee;
    }

}
