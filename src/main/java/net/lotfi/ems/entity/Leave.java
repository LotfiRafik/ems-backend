package net.lotfi.ems.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import net.lotfi.ems.enums.LeaveState;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "leaves")
public class Leave {

//    Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    @NotNull
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull
    private LocalDate endDate;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private LeaveState state;

    // Owning side
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    private Employee employee;


//  Constructors

    public Leave(Long id, LocalDate startDate, LocalDate endDate, LeaveState state, Employee employee) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
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

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setState(LeaveState state) {
        this.state = state;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}