package com.bankz.customer.service;

import com.bankz.customer.domain.Customer;
import com.bankz.customer.domain.CustomerKey;
import com.bankz.customer.domain.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerInquiryServiceTest {

    private static final long SORT_CODE = 987654;

    private final CustomerRepository repository = mock(CustomerRepository.class);
    private final RandomGenerator random = mock(RandomGenerator.class);
    private CustomerInquiryService service;

    @BeforeEach
    void setUp() {
        service = new CustomerInquiryService(repository, random, SORT_CODE);
    }

    private static Customer customer(long number) {
        return new Customer(CustomerKey.of(SORT_CODE, number), "Mr", "John", "Smith", 19850615, "", "", "", "",
                "", "", "ACTIVE", 20200115, (short) 650, 20260115);
    }

    @Test
    void returnsCustomerForDefaultSortCode() {
        when(repository.findById(new CustomerKey("987654", "0000000042"))).thenReturn(Optional.of(customer(42)));

        assertThat(service.inquire(42).getKey().number()).isEqualTo("0000000042");
    }

    @Test
    void missingCustomerFailsWithCode1() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.inquire(7))
                .isInstanceOf(CustomerNotFoundException.class)
                .extracting("failCode").isEqualTo("1");
    }

    @Test
    void customerNumber9999999999ReturnsHighestCustomer() {
        when(repository.findFirstByKeySortCodeOrderByKeyNumberDesc("987654")).thenReturn(Optional.of(customer(42)));
        when(repository.findById(CustomerKey.of(SORT_CODE, 42))).thenReturn(Optional.of(customer(42)));

        assertThat(service.inquire(CustomerInquiryService.LAST_CUSTOMER).getKey().number()).isEqualTo("0000000042");
    }

    @Test
    void lastCustomerLookupRetriesOnceWhenLastCustomerDeleted() {
        when(repository.findFirstByKeySortCodeOrderByKeyNumberDesc("987654"))
                .thenReturn(Optional.of(customer(42)), Optional.of(customer(41)));
        when(repository.findById(CustomerKey.of(SORT_CODE, 42))).thenReturn(Optional.empty());
        when(repository.findById(CustomerKey.of(SORT_CODE, 41))).thenReturn(Optional.of(customer(41)));

        assertThat(service.inquire(CustomerInquiryService.LAST_CUSTOMER).getKey().number()).isEqualTo("0000000041");
    }

    @Test
    void noCustomersForSortCodeFailsWithCode9() {
        when(repository.findFirstByKeySortCodeOrderByKeyNumberDesc("987654")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.inquire(CustomerInquiryService.LAST_CUSTOMER))
                .isInstanceOf(CustomerNotFoundException.class)
                .extracting("failCode").isEqualTo("9");
    }

    @Test
    void customerNumber0PicksRandomCustomerWithCobolFormula() {
        when(repository.findFirstByKeySortCodeOrderByKeyNumberDesc("987654")).thenReturn(Optional.of(customer(43)));
        when(random.nextDouble()).thenReturn(0.5);
        when(repository.findById(CustomerKey.of(SORT_CODE, 22))).thenReturn(Optional.of(customer(22)));

        // ((43 - 1) * 0.5) + 1 = 22
        assertThat(service.inquire(CustomerInquiryService.RANDOM_CUSTOMER).getKey().number()).isEqualTo("0000000022");
    }

    @Test
    void randomCustomerGivesUpAfter1000Retries() {
        when(repository.findFirstByKeySortCodeOrderByKeyNumberDesc("987654")).thenReturn(Optional.of(customer(43)));
        when(random.nextDouble()).thenReturn(0.1);
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.inquire(CustomerInquiryService.RANDOM_CUSTOMER))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(repository, times(CustomerInquiryService.MAX_RANDOM_RETRIES + 1)).findById(any());
    }
}
