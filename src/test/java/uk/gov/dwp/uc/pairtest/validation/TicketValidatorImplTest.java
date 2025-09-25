package uk.gov.dwp.uc.pairtest.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TicketValidatorImplTest {

    private TicketValidatorImpl validator;

    @BeforeEach
    void setUp() {
        validator = new TicketValidatorImpl();
    }

    // Can parameterized tests be used here to pass multiple valid requests?
    @ParameterizedTest
    @MethodSource("validTicketRequests")
    void testValidRequestPasses(Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        assertDoesNotThrow(() -> validator.validate(1L, ticketCounts));
    }
    
    static Stream<Arguments> validTicketRequests() {
        return Stream.of(
            Arguments.of(Map.of(TicketTypeRequest.Type.ADULT, 2)),
            Arguments.of(Map.of(TicketTypeRequest.Type.ADULT, 1, TicketTypeRequest.Type.CHILD, 1)),
            Arguments.of(Map.of(TicketTypeRequest.Type.ADULT, 2, TicketTypeRequest.Type.INFANT, 1)),
            Arguments.of(Map.of(TicketTypeRequest.Type.ADULT, 25)),
            Arguments.of(Map.of(TicketTypeRequest.Type.ADULT, 3, TicketTypeRequest.Type.CHILD, 2, TicketTypeRequest.Type.INFANT, 1))
        );
    }

    @Test
    void testNullAccountIdFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(null, ticketCounts));
    }

    @Test
    void testZeroAccountIdFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(0L, ticketCounts));
    }

    @Test
    void testNegativeAccountIdFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(-1L, ticketCounts));
    }

    @Test
    void testNullTicketRequestsFails() {
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, null));
    }

    @Test
    void testEmptyTicketRequestsFails() {
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, Map.of()));
    }

    @Test
    void testOver25TicketsFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 26);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, ticketCounts));
    }

    @Test
    void testExactly25TicketsPasses() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 25);
        
        assertDoesNotThrow(() -> validator.validate(1L, ticketCounts));
    }

    @Test
    void testChildAloneFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.CHILD, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, ticketCounts));
    }

    @Test
    void testInfantAloneFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.INFANT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, ticketCounts));
    }

    @Test
    void testMoreInfantsThanAdultsFails() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(
            TicketTypeRequest.Type.ADULT, 1,
            TicketTypeRequest.Type.INFANT, 2
        );
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, ticketCounts));
    }

    @Test
    void testOneInfantPerAdultPasses() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(
            TicketTypeRequest.Type.ADULT, 2,
            TicketTypeRequest.Type.INFANT, 2
        );
        
        assertDoesNotThrow(() -> validator.validate(1L, ticketCounts));
    }
}