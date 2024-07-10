package net.lotfi.ems.mapper;

import net.lotfi.ems.dto.EmployeeDto;
import net.lotfi.ems.dto.LeaveDto;
import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Leave;

public class LeaveMapper {

    public static LeaveDto mapToLeaveDto(Leave leave){
        return new LeaveDto(
                leave.getId(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getState(),
                leave.getEmployee().getId()
        );
    }

    public static Leave mapToLeave(LeaveDto leaveDto){
        return new Leave(
                leaveDto.getId(),
                leaveDto.getStartDate(),
                leaveDto.getEndDate(),
                leaveDto.getState(),
                null
        );
    }

}
