package net.lotfi.ems.service.impl;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.LeaveDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Leave;
import net.lotfi.ems.enums.LeaveState;
import net.lotfi.ems.exception.CustomErrorException;
import net.lotfi.ems.mapper.EmployeeMapper;
import net.lotfi.ems.mapper.LeaveMapper;
import net.lotfi.ems.repository.EmployeeRepository;
import net.lotfi.ems.repository.LeaveRepository;
import net.lotfi.ems.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;
    private LeaveRepository leaveRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, LeaveRepository leaveRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRepository = leaveRepository;
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new CustomErrorException("No employee exist with the given id : "+employeeId));

        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees
                .stream()
                .map(employee -> EmployeeMapper.mapToEmployeeDto(employee))
                .toList();
    }

    @Override
    public List<LeaveDto> getAllLeaves() {
        return List.of();
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new CustomErrorException("Employee not exist with id :" + id));

        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
    }

    // delete employee rest api
    public void deleteEmployee(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new CustomErrorException(HttpStatus.NOT_FOUND, "Employee not exist with id :" + id));

        employeeRepository.delete(employee);
    }

    // Leaves management

    @Override
    public LeaveDto createLeave(LeaveDto leaveDto) {
        // Get concerned employee
        Employee employee = employeeRepository.findById(leaveDto.getEmployeeId())
                .orElseThrow(() -> new CustomErrorException(HttpStatus.NOT_FOUND,
                        "Employee not found with id :" + leaveDto.getEmployeeId()));

        EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee);
        // Check if this leave is valid
        Map<String, Object> result = isLeaveValide(leaveDto, employeeDto);
        if (!Boolean.parseBoolean((String) result.get("status"))){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST ,(String) result.get("error"), result.get("data"));
        }

        // Set default state
        leaveDto.setState(LeaveState.SUBMITED_TO_REVIEW);
        Leave leave = LeaveMapper.mapToLeave(leaveDto);
        leave.setEmployee(employee);
        Leave savedLeave = leaveRepository.save(leave);
        return LeaveMapper.mapToLeaveDto(savedLeave);
    }

    public Map<String, Object> isLeaveValide(LeaveDto leaveDto, EmployeeDto employeeDto){
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = leaveDto.getStartDate();
        LocalDate endDate = leaveDto.getEndDate();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "true");
        result.put("error", null);
        result.put("data", null);

        if (startDate.isBefore(currentDate) || endDate.isBefore(currentDate) || startDate.isAfter(endDate)) {
            result.put("error", "Incoherent leave dates");
            result.put("status", null);
            return result;
        }

        long nb_leave_days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (employeeDto.getAvailableLeaveDays() < nb_leave_days){
            result.put("error", "Exceeded available leave days");
            result.put("status", null);

            // create an object that have these fields (int avaialble days, int requested leave days)
            result.put("data", new Object() {
                int availableLeaveDays = employeeDto.getAvailableLeaveDays();
                long requestedLeaveDays = nb_leave_days;
                public int getAvailableLeaveDays() {
                    return availableLeaveDays;
                }
                public long getRequestedLeaveDays() {
                    return requestedLeaveDays;
                }
            });
            return result;
        }

        // Les conges d’un meme employe ne peuvent pas se chevaucher.

        Integer nbOverlapLeaves = leaveRepository.findOverlappingLeaves(startDate, endDate, employeeDto.getId());
        if (nbOverlapLeaves > 0){
            result.put("error", "Overlapping leaves found");
            result.put("status", null);
            return result;
        }

        return result;
    }



















}
