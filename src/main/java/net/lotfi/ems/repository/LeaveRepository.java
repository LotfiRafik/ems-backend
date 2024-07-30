package net.lotfi.ems.repository;

import net.lotfi.ems.entity.Employee;
import net.lotfi.ems.entity.Leave;
import net.lotfi.ems.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    @Query("SELECT l FROM Leave l " +
            "WHERE " +
            "l.employee.id = ?1 " +
            "AND " +
            "l.state IN (\"APPROVED\", \"SUBMITED_TO_REVIEW\")")
    List<Leave> findEmployeeLeaves(Long employeeId);

    // TODO find algo to get intersection of two int intervals
    // example : [1, 3] and [0, 5] ?
    @Query("SELECT COUNT(l) FROM Leave l WHERE " +
            "( " +
                "l.startDate  BETWEEN ?1 AND ?2 " +
                "OR " +
                "l.endDate  BETWEEN ?1 AND ?2 " +
                "OR " +
                "?1 BETWEEN l.startDate AND l.endDate " +
                "OR " +
                "?2 BETWEEN l.startDate AND l.endDate " +
            ") " +
            "AND " +
                "l.employee.id = ?3 " +
            "AND " +
                "l.state IN (\"APPROVED\", \"SUBMITED_TO_REVIEW\")"
    )
    Integer findOverlappingLeaves(LocalDate startDate, LocalDate endDate, Long employeeId);

}
