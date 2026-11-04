package com.bankz.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProctranRepository extends JpaRepository<Proctran, Long> {

    /** INQTRANL TRAN-CURSOR. */
    @Query("""
            select p from Proctran p
            where p.sortCode = :sortCode and p.accountNumber = :accountNumber
              and p.date >= :from and p.date <= :to
            order by p.date desc, p.time desc, p.id desc""")
    List<Proctran> findForAccount(@Param("sortCode") String sortCode, @Param("accountNumber") String accountNumber,
                                  @Param("from") LocalDate from, @Param("to") LocalDate to);

    /** INQTRANL TRAN-COUNT-CURSOR. */
    @Query("""
            select count(p) from Proctran p
            where p.sortCode = :sortCode and p.accountNumber = :accountNumber
              and p.date >= :from and p.date <= :to""")
    long countForAccount(@Param("sortCode") String sortCode, @Param("accountNumber") String accountNumber,
                         @Param("from") LocalDate from, @Param("to") LocalDate to);

    /** INQTRAND TRAND-CURSOR. */
    Optional<Proctran> findFirstBySortCodeAndAccountNumberAndDateAndTimeAndReference(
            String sortCode, String accountNumber, LocalDate date, String time, String reference);

    List<Proctran> findBySortCodeAndAccountNumberOrderByIdAsc(String sortCode, String accountNumber);
}
