package com.globusit.fintracker.repository;

import com.globusit.fintracker.domain.Transaction;
import com.globusit.fintracker.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t join fetch t.category where t.user = :user order by t.date desc, t.id desc")
    List<Transaction> findByUserOrderByDateDescIdDesc(@Param("user") User user);

    List<Transaction> findByUserAndDateBetweenOrderByDateDescIdDesc(User user, LocalDate start, LocalDate end);

    @Query("select t from Transaction t join fetch t.category where t.id = :id and t.user = :user")
    Optional<Transaction> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("select t from Transaction t join fetch t.category where t.user = :user and t.date between :start and :end order by t.date desc, t.id desc")
    List<Transaction> findWithCategoryForPeriod(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
