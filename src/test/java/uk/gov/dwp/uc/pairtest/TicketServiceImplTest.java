package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

class TicketServiceImplTest {

    @Mock
    private TicketPaymentService paymentService;
    
    @Mock
    private SeatReservationService seatService;
    
    private TicketServiceImpl ticketService;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        ticketService = new TicketServiceImpl(paymentService, seatService);
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    // Basic scenario - just adults buying tickets
    @Test
    void testSimpleAdultPurchase() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        
        ticketService.purchaseTickets(1L, adultRequest);
        
        // Should reserve 2 seats and charge £50 (2 * £25)
        verify(seatService).reserveSeat(1L, 2);
        verify(paymentService).makePayment(1L, 50);
    }

    // Real family scenario - parents with child and baby
    @Test
    void testFamilyTicketPurchase() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        ticketService.purchaseTickets(5L, adultRequest, childRequest, infantRequest);
        
        // 3 seats (2 adults + 1 child), infant sits on lap
        // £65 total (2*£25 + 1*£15 + 1*£0)
        verify(seatService).reserveSeat(5L, 3);
        verify(paymentService).makePayment(5L, 65);
    }
    
    // Edge case what about exactly at the boundary?
    @Test
    void testMaxTicketBoundary() {
        TicketTypeRequest maxRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25);
        
        ticketService.purchaseTickets(1L, maxRequest);
        
        verify(seatService).reserveSeat(1L, 25);
        verify(paymentService).makePayment(1L, 625); // 25 * £25
    }
    
    // Null account should be rejected immediately
    @Test
    void testNullAccountRejection() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(null, adultRequest));
    }

    // Account ID 0 doesn't exist in real systems
    @Test
    void testInvalidAccountId() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(0L, adultRequest));
    }

    // Negative account IDs
    @Test
    void testNegativeAccountId() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(-1L, adultRequest));
    }

    // Can't buy zero tickets - that makes no sense
    @Test
    void testEmptyTicketRequest() {
        assertThrows(InvalidPurchaseException.class, () ->
            ticketService.purchaseTickets(1L));
    }

    // Individual request is null - could cause NPE
    @Test
    void testNullIndividualRequest() {
        assertThrows(InvalidPurchaseException.class, () ->
            ticketService.purchaseTickets(1L, (TicketTypeRequest) null));
    }

    // Asking for 0 tickets doesn't make sense
    @Test
    void testZeroTicketQuantity() {
        TicketTypeRequest zeroRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, zeroRequest));
    }

    // Negative tickets - someone trying to break the system?
    @Test
    void testNegativeTicketQuantity() {
        TicketTypeRequest negativeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, negativeRequest));
    }

    // 26 tickets exceeds the 25 limit - business rule violation
    @Test
    void testExceedingTicketLimit() {
        TicketTypeRequest tooManyRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, tooManyRequest));
    }

    // Kids can't go to cinema alone - need adult supervision
    @Test
    void testChildWithoutAdultSupervision() {
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, childRequest));
    }

    // Baby definitely can't go alone!
    @Test
    void testInfantWithoutAdult() {
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, infantRequest));
    }

    // 1 adult can't handle 2 babies on their lap - physical limitation
    @Test
    void testTooManyBabiesPerAdult() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, adultRequest, infantRequest));
    }

    // 2 adults with 2 babies - each adult holds one baby, makes sense
    @Test
    void testValidInfantToAdultRatio() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        
        ticketService.purchaseTickets(1L, adultRequest, infantRequest);
        
        // Only adults get seats, babies sit on laps
        verify(seatService).reserveSeat(1L, 2);
        verify(paymentService).makePayment(1L, 50); // Only adults pay
    }
    
    // Edge case - mixed scenarios with multiple ticket types
    @Test
    void testComplexFamilyScenario() {
        // Large family: 3 adults, 2 children, 1 infant - realistic scenario
        TicketTypeRequest adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        TicketTypeRequest children = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        ticketService.purchaseTickets(10L, adults, children, infant);
        
        // 5 seats needed (3 adults + 2 children), infant on lap
        // Cost: 3*£25 + 2*£15 + 1*£0 = £105
        verify(seatService).reserveSeat(10L, 5);
        verify(paymentService).makePayment(10L, 105);
    }
    
    // Edge case - 26 infants should fail (exceeds 25 ticket limit)
    @Test
    void testTwentySixInfantsFails() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26); // Need adults for infants
        TicketTypeRequest infantRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 26);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, adultRequest, infantRequest)); // 52 total tickets > 25 limit
    }
    
    // Edge case - 26 children should fail (exceeds 25 ticket limit)
    @Test
    void testTwentySixChildrenFails() {
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1); // Need adult for children
        TicketTypeRequest childRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 26);
        
        assertThrows(InvalidPurchaseException.class, () -> 
            ticketService.purchaseTickets(1L, adultRequest, childRequest)); // 27 total tickets > 25 limit
    }
}