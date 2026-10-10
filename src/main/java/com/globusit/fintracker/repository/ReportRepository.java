package com.globusit.fintracker.repository;

import com.globusit.fintracker.domain.Report;
import com.globusit.fintracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByUserOrderByGeneratedAtDesc(User user);
}
