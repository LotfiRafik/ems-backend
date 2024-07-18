package net.lotfi.ems.service;

import net.lotfi.ems.dto.LeaveDto;

import java.util.List;

public interface LeaveService {
    List<LeaveDto> getAllLeaves();
    LeaveDto approveLeave(Long id);
    LeaveDto cancelLeave(Long id);
    LeaveDto rejectLeave(Long id);

}
