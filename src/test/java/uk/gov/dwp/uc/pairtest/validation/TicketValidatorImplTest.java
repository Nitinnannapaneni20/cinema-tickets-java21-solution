package uk.gov.dwp.uc.pairtest.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;

class TicketValidatorImplTest {

    private TicketValidatorImpl validator;

    @BeforeEach
    void setUp() {
        validator = new TicketValidatorImpl();
    }

    @Test
    void testValidRequestPasses() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        
        assertDoesNotThrow(() -> validator.validate(1L, request));
    }

    @Test
    void testNullAccountIdFails() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(null, request));
    }

    @Test
    void testZeroAccountIdFails() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(0L, request));
    }

    @Test
    void testNegativeAccountIdFails() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(-1L, request));
    }

    @Test
    void testNullTicketRequestsFails() {
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, (TicketTypeRequest[]) null));
    }

    @Test
    void testEmptyTicketRequestsFails() {
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L));
    }

    @Test
    void testOver25TicketsFails() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, request));
    }

    @Test
    void testExactly25TicketsPasses() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25);
        
        assertDoesNotThrow(() -> validator.validate(1L, request));
    }

    @Test
    void testChildAloneFails() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, request));
    }

    @Test
    void testInfantAloneFails() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, request));
    }

    @Test
    void testMoreInfantsThanAdultsFails() {
        TicketTypeRequest adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        
        assertThrows(InvalidPurchaseException.class, () -> validator.validate(1L, adults, infants));
    }

    @Test
    void testOneInfantPerAdultPasses() {
        TicketTypeRequest adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        
        assertDoesNotThrow(() -> validator.validate(1L, adults, infants));
    }
}