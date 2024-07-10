package net.lotfi.ems.dto;

import net.lotfi.ems.enums.LeaveState;

import java.util.Date;

public class LeaveDto {

    private Long id;
    private Date startDate;
    private Date endDate;
    private LeaveState state;
    private Long employeeId;

    public LeaveDto() {
    }

    public LeaveDto(Long id, Date startDate, Date endDate, LeaveState state, Long employeeId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.employeeId = employeeId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setState(LeaveState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
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
