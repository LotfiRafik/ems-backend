package net.lotfi.ems.mapper;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee){
        EmployeeDto employeeDto = new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail()
        );

        employeeDto.setAvailableLeaveDays(employee.getAvailableLeaveDays());

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

        return employee;
    }

}
