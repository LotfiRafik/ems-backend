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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new CustomErrorException("Leave with id: " + leaveId + " not found"));

        LeaveState currentState = leave.getState();
        List<LeaveState> allowedLeaveStates = new ArrayList<>();
        allowedLeaveStates.add(LeaveState.SUBMITED_TO_REVIEW);
        allowedLeaveStates.add(LeaveState.REJECTED);
        if (!allowedLeaveStates.contains(currentState)) {
            throw new CustomErrorException("Leave with state : " + currentState + " can not be approved");
        }

        LocalDate startDate = leave.getStartDate();
        LocalDate currentDate = LocalDate.now();
        // if leave already passed
        if (startDate.isBefore(currentDate)){
            throw new CustomErrorException("Leave has with startDate: " + startDate + " can not be approved");
        }

        leave.setState(LeaveState.APPROVED);
        Leave updatedLeave = leaveRepository.save(leave);
        // TODO notify user by email

        return LeaveMapper.mapToLeaveDto(updatedLeave);
    }


    @Override
    public LeaveDto cancelLeave(Long leaveId){
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new CustomErrorException("Leave with id: " + leaveId + " not found"));

        LeaveState currentState = leave.getState();
        List<LeaveState> allowedLeaveStates = new ArrayList<>();
        allowedLeaveStates.add(LeaveState.SUBMITED_TO_REVIEW);
        allowedLeaveStates.add(LeaveState.APPROVED);
        if (!allowedLeaveStates.contains(currentState)) {
            throw new CustomErrorException("Leave with state : " + currentState + " can not be cancelled");
        }

        if (currentState == LeaveState.APPROVED){
            LocalDate startDate = leave.getStartDate();
            LocalDate currentDate = LocalDate.now();
            // if leave already passed
            if (startDate.isBefore(currentDate)){
                throw new CustomErrorException("Leave has with startDate: " + startDate + " can not be cancelled");
            }
        }

        leave.setState(LeaveState.CANCELED);
        Leave updatedLeave = leaveRepository.save(leave);
        // TODO notify user by email

        return LeaveMapper.mapToLeaveDto(updatedLeave);
    }


    @Override
    public LeaveDto rejectLeave(Long leaveId){
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new CustomErrorException("Leave with id: " + leaveId + " not found"));

        LeaveState currentState = leave.getState();
        List<LeaveState> allowedLeaveStates = new ArrayList<>();
        allowedLeaveStates.add(LeaveState.SUBMITED_TO_REVIEW);
        if (!allowedLeaveStates.contains(currentState)) {
            throw new CustomErrorException("Leave with state : " + currentState + " can not be rejected");
        }

        leave.setState(LeaveState.REJECTED);
        Leave updatedLeave = leaveRepository.save(leave);
        // TODO notify user by email

        return LeaveMapper.mapToLeaveDto(updatedLeave);
    }



}
