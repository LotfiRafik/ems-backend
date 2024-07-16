package net.lotfi.ems.dto;

import net.lotfi.ems.enums.LeaveState;

import java.time.LocalDate;
import java.util.Date;

public class LeaveDto {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveState state;
    private Long employeeId;

    public LeaveDto() {
    }

    public LeaveDto(Long id, LocalDate startDate, LocalDate endDate, LeaveState state, Long employeeId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.employeeId = employeeId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setState(LeaveState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LeaveState getState() {
        return state;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

}
