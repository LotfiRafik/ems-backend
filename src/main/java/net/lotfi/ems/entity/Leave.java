package net.lotfi.ems.entity;
import jakarta.persistence.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.lotfi.ems.enums.LeaveState;

import java.util.Date;

@Entity
@Table(name = "leaves")
public class Leave {

//    Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private LeaveState state;

    // Owning side
    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @NotNull
    private Employee employee;


//  Constructors

    public Leave(Long id, Date startDate, Date endDate, LeaveState state, Employee employee) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.state = state;
        this.employee = employee;
    }

    public Leave() {
    }


//  Getters/Setters

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

    public Employee getEmployee() {
        return employee;
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

    public void setState(String state) {
        this.state = state;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}