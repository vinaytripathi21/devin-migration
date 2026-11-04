package com.bankz.common;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ControlRepository extends JpaRepository<ControlRecord, String> {

    /** SELECT ... FOR UPDATE replaces EXEC CICS ENQ on the named counter. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ControlRecord c where c.name = :name")
    Optional<ControlRecord> lockByName(@Param("name") String name);
}
