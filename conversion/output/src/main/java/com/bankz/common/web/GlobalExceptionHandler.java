package com.bankz.common.web;

import com.bankz.common.AbendException;
import com.bankz.common.AbendService;
import com.bankz.common.BusinessFailureException;
import com.bankz.common.InvalidRequestException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    private final AbendService abendService;

    GlobalExceptionHandler(AbendService abendService) {
        this.abendService = abendService;
    }

    @ExceptionHandler(BusinessFailureException.class)
    ResponseEntity<Map<String, String>> businessFailure(BusinessFailureException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.getMessage(), "message", String.valueOf(e.getMessage()), "failCode", e.getFailCode()));
    }

    @ExceptionHandler({InvalidRequestException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, String>> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AbendException.class)
    ResponseEntity<Map<String, String>> abend(AbendException e) {
        abendService.record(e.getProgram(), e.getAbendCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage(), "message", String.valueOf(e.getMessage()), "abendCode", e.getAbendCode()));
    }

    /** Any SQLCODE other than 0 or 100: the COBOL programs abend (for example INQCUST abends CVR1). */
    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<Map<String, String>> databaseError(DataAccessException e) {
        abendService.record("DB2", "SQLE", e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Database request failed", "abendCode", "SQLE"));
    }
}
