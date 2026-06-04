package com.example.casemanagement.casefile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseRepository extends JpaRepository<CaseEntity, Long> {

    List<CaseEntity> findAllByOrderByUpdatedAtDesc();

    List<CaseEntity> findByStatusOrderByUpdatedAtDesc(CaseStatus status);

    List<CaseEntity> findByPriorityOrderByUpdatedAtDesc(Priority priority);

    List<CaseEntity> findByStatusAndPriorityOrderByUpdatedAtDesc(CaseStatus status, Priority priority);
}
