package net.lotfi.ems.repository;

import net.lotfi.ems.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;


@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

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
                "l.state = APPROVED"
    )
    Integer findOverlappingLeaves(LocalDate startDate, LocalDate endDate, Long employeeId);

}
