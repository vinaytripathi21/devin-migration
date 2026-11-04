package com.bankz.customer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

/**
 * Conversion of CRDTAGY1-CRDTAGY5 and of CRECUST's asynchronous credit check. CRECUST started the five
 * agency transactions with EXEC CICS RUN TRANSID, waited 3 seconds and averaged the scores that came back.
 */
@Service
public class CreditAgencyService {

    public static final int AGENCIES = 5;

    private final RandomGenerator random;
    private final Duration minDelay;
    private final Duration maxDelay;
    private final Duration timeout;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public CreditAgencyService(RandomGenerator random,
                               @Value("${bankz.credit-agency.min-delay:1s}") Duration minDelay,
                               @Value("${bankz.credit-agency.max-delay:3s}") Duration maxDelay,
                               @Value("${bankz.credit-agency.timeout:3s}") Duration timeout) {
        this.random = random;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        this.timeout = timeout;
    }

    /** One CRDTAGYn: DELAY for a random time, then COMPUTE score = (998 * FUNCTION RANDOM) + 1. */
    public int agencyScore() {
        long spread = maxDelay.toMillis() - minDelay.toMillis();
        long delay = minDelay.toMillis() + (spread > 0 ? (long) (random.nextDouble() * spread) : 0);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return random.nextInt(998) + 1;
    }

    /** Average of the agencies that answered within the timeout (integer division, as in COBOL), or empty. */
    public OptionalInt averageScore() {
        List<CompletableFuture<Integer>> calls = new ArrayList<>();
        for (int i = 0; i < AGENCIES; i++) {
            calls.add(CompletableFuture.supplyAsync(this::agencyScore, executor)
                    .completeOnTimeout(null, timeout.toMillis(), TimeUnit.MILLISECONDS));
        }
        int total = 0;
        int answered = 0;
        for (CompletableFuture<Integer> call : calls) {
            Integer score = call.join();
            if (score != null) {
                total += score;
                answered++;
            }
        }
        return answered == 0 ? OptionalInt.empty() : OptionalInt.of(total / answered);
    }
}
