package net.lotfi.ems.repository;

import net.lotfi.ems.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;


@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    @Query("SELECT COUNT(l) FROM Leave l WHERE " +
            "( " +
                "l.startDate  BETWEEN ?1 AND ?2 " +
                "OR " +
                "l.endDate  BETWEEN ?1 AND ?2 " +
            ") " +
            "AND " +
                "l.employee.id = ?3 " +
            "AND " +
                "l.state = APPROVED"
    )
    Integer findOverlappingLeaves(LocalDate startDate, LocalDate endDate, Long employeeId);

}
