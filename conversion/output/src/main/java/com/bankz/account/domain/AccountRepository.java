package com.bankz.account.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, AccountKey> {

    /** INQACCCU ACC-CURSOR; the commarea holds at most 20 accounts. */
    List<Account> findTop20ByKeySortCodeAndCustomerNumberOrderByKeyNumberAsc(String sortCode, String customerNumber);

    /** INQACC with account number 99999999. */
    Optional<Account> findFirstByKeySortCodeOrderByKeyNumberDesc(String sortCode);

    /** INQACCS ACC-ALL-CURSOR. */
    List<Account> findByKeySortCodeOrderByKeyNumberAsc(String sortCode);

    /** SELECT ... FOR UPDATE, so concurrent debits and transfers serialize on the row as under DB2. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.key = :key")
    Optional<Account> lockById(@Param("key") AccountKey key);
}
