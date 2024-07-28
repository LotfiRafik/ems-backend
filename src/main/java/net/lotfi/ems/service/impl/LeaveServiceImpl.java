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
import net.lotfi.ems.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LeaveServiceImpl implements LeaveService {

    private EmployeeRepository employeeRepository;
    private LeaveRepository leaveRepository;

    @Autowired
    public LeaveServiceImpl(EmployeeRepository employeeRepository, LeaveRepository leaveRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRepository = leaveRepository;
    }



    @Override
    public List<LeaveDto> getAllLeaves() {
        List<Leave> leaves = leaveRepository.findAll();
        return leaves
                .stream()
                .map(leave -> LeaveMapper.mapToLeaveDto(leave))
                .toList();
    }

    @Override
    public LeaveDto approveLeave(Long leaveId){
        // Check externel input
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave with id: " + leaveId + " not found"));

        // Check state constraints
        LeaveState currentState = leave.getState();
        List<LeaveState> allowedLeaveStates = new ArrayList<>();
        allowedLeaveStates.add(LeaveState.SUBMITED_TO_REVIEW);
        allowedLeaveStates.add(LeaveState.REJECTED);
        if (!allowedLeaveStates.contains(currentState)) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave with state : " + currentState + " can not be approved");
        }

        // Check dates constraints
        LocalDate startDate = leave.getStartDate();
        LocalDate currentDate = LocalDate.now();
        if (startDate.isBefore(currentDate)){
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave has with startDate: " + startDate + " can not be approved");
        }

        // Check authorization
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee userDetails = (Employee) authentication.getPrincipal();
        System.out.println("User has authorities: " + userDetails.getAuthorities());

        Employee manager = leave.getEmployee().getManager();
        if (manager == null){
            // if the leave's employee doesn't have a direct manager, only root employees can approve the leave
            if(userDetails.getManager() != null){
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Only root employee or direct manager can approve leave requests");
            }
        }
        else{
            // Only direct manager can approve leave
            if (!Objects.equals(userDetails.getId(), manager.getId())){
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Only direct manager can approve leave requests");
            }
        }

        // ACT
        leave.setState(LeaveState.APPROVED);
        Leave updatedLeave = leaveRepository.save(leave);
        // TODO notify user by email

        return LeaveMapper.mapToLeaveDto(updatedLeave);
    }


    @Override
    public LeaveDto cancelLeave(Long leaveId){
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new CustomErrorException("Leave with id: " + leaveId + " not found"));

        // Check state constraints
        LeaveState currentState = leave.getState();
        List<LeaveState> allowedLeaveStates = new ArrayList<>();
        allowedLeaveStates.add(LeaveState.SUBMITED_TO_REVIEW);
        allowedLeaveStates.add(LeaveState.APPROVED);
        if (!allowedLeaveStates.contains(currentState)) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave with state : " + currentState + " can not be cancelled");
        }

        // Check date constraints
        if (currentState == LeaveState.APPROVED){
            LocalDate startDate = leave.getStartDate();
            LocalDate currentDate = LocalDate.now();
            // if leave already passed
            if (startDate.isBefore(currentDate)){
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave has with startDate: " + startDate + " can not be cancelled");
            }
        }

        // Check authorization
        // Only the user who requested the leave that can cancel a leave
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee userDetails = (Employee) authentication.getPrincipal();
        if (!Objects.equals(userDetails.getId(), leave.getEmployee().getId())){
            throw new CustomErrorException(HttpStatus.FORBIDDEN, "Only the owner of the leave can cancel it");
        }


        leave.setState(LeaveState.CANCELED);
        Leave updatedLeave = leaveRepository.save(leave);
        // TODO notify user by email

        return LeaveMapper.mapToLeaveDto(updatedLeave);
    }


    @Override
    public LeaveDto rejectLeave(Long leaveId){
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave with id: " + leaveId + " not found"));


        // *********** Check state constraints (respect state diagram) ***********
        LeaveState currentState = leave.getState();
        List<LeaveState> allowedLeaveStates = new ArrayList<>();
        allowedLeaveStates.add(LeaveState.SUBMITED_TO_REVIEW);
        if (!allowedLeaveStates.contains(currentState)) {
            throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Leave with state : " + currentState + " can not be rejected");
        }

        // *********** Check authorization ***********
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee userDetails = (Employee) authentication.getPrincipal();
        Employee manager = leave.getEmployee().getManager();
        if (manager == null){
            // if the leave's employee doesn't have a direct manager, only root employees can reject the leave
            if(userDetails.getManager() != null){
                throw new CustomErrorException(HttpStatus.FORBIDDEN, "Only root employee or direct manager can reject leave requests");
            }
        }
        else{
            // Only direct manager can reject leave
            if (!Objects.equals(userDetails.getId(), manager.getId())){
                throw new CustomErrorException(HttpStatus.BAD_REQUEST, "Only direct manager can reject leave requests");
            }
        }

        // ACT
        leave.setState(LeaveState.REJECTED);
        Leave updatedLeave = leaveRepository.save(leave);
        // TODO notify user by email

        return LeaveMapper.mapToLeaveDto(updatedLeave);
    }



}
