package net.lotfi.ems.service;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.EmployeeManagerDto;
import net.lotfi.ems.dto.LeaveDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Leave;
import net.lotfi.ems.entity.Role;
import net.lotfi.ems.enums.LeaveState;
import net.lotfi.ems.enums.RoleEnum;
import net.lotfi.ems.exception.CustomErrorException;
import net.lotfi.ems.mapper.EmployeeMapper;
import net.lotfi.ems.mapper.LeaveMapper;
import net.lotfi.ems.repository.EmployeeRepository;
import net.lotfi.ems.repository.LeaveRepository;
import net.lotfi.ems.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService{

    private EmployeeRepository employeeRepository;
    private LeaveRepository leaveRepository;
    private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, LeaveRepository leaveRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.leaveRepository = leaveRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        employeeDto.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.EMPLOYEE);
        if (optionalRole.isEmpty()) {
            // TODO LOG ERROR
            throw  new CustomErrorException(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        // Set Role
        employee.setRole(optionalRole.get());

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new CustomErrorException("No employee exist with the given id : "+employeeId));

        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        return employees
                .stream()
                .map(employee -> EmployeeMapper.mapToEmployeeDto(employee))
                .toList();
    }

    
    public List<LeaveDto> getAllLeaves() {
        return List.of();
    }

    public EmployeeDto updateEmployeeManager(Long employeeId, EmployeeManagerDto employeeManagerDto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new CustomErrorException("Employee not exist with id :" + employeeId));

        Long ManagerId = employeeManagerDto.getManagerId();
        Employee employeeManager = employeeRepository.findById(ManagerId)
                .orElseThrow(() -> new CustomErrorException("Manager not exist with id :" + ManagerId));

        // TODO check if its possible to set it as a manager (employee hierarchy constraints..)
        employee.setManager(employeeManager);

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeDto(updatedEmployee);
    }


    
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new CustomErrorException("Employee not exist with id :" + id));

        // Get all modified fields (setted fields)
        Map<String, Boolean> isSettedAttributes = employeeDto.getIsSettedAttributes();

        try {
            for (Map.Entry<String, Boolean> entry : isSettedAttributes.entrySet()) {
                String attributeName = entry.getKey();
                Boolean isSetted = entry.getValue();
                if (!isSetted) {
                    continue;
                }
                // Call the getter method dynamically
                String getterMethodName = "get" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
                // TODO handle exception
                Class<?> clazz = employeeDto.getClass();
                Method method = clazz.getMethod(getterMethodName);
                Object getterResult = method.invoke(employeeDto);

                // Call the setter method dynamically
                String setterMethodName = "set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
                Object[] args = {getterResult};
                Class<?>[] parameterTypes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }

                clazz = employee.getClass();


                method = clazz.getMethod(setterMethodName, parameterTypes);
                method.invoke(employee, args);
            }
        } catch (Exception exception) {
            // TODO refactor
            throw new CustomErrorException(exception.getMessage());
        }


//        employee.setFirstName(employeeDto.getFirstName());
//        employee.setLastName(employeeDto.getLastName());
//        employee.setEmail(employeeDto.getEmail());


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
