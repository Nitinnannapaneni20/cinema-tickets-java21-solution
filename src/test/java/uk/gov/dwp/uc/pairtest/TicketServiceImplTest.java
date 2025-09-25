package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.mockito.Mockito.*;

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

    @Test
    void shouldCallPaymentServiceWithCorrectAmount() {
        // Given
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        
        // When
        ticketService.purchaseTickets(1L, adultRequest);
        
        // Then - verify payment service called with calculated cost (2 * £25 = £50)
        verify(paymentService).makePayment(1L, 50);
    }

    @Test
    void shouldCallSeatServiceWithCorrectSeats() {
        // Given
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        
        // When
        ticketService.purchaseTickets(1L, adultRequest);
        
        // Then - verify seat service called with calculated seats (2 adults = 2 seats)
        verify(seatService).reserveSeat(1L, 2);
    }

    @Test
    void shouldCallServicesInCorrectOrder() {
        // Given
        TicketTypeRequest adultRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        
        // When
        ticketService.purchaseTickets(1L, adultRequest);
        
        // Then - verify order: reserve seats first, then payment
        var inOrder = inOrder(seatService, paymentService);
        inOrder.verify(seatService).reserveSeat(1L, 2);
        inOrder.verify(paymentService).makePayment(1L, 50);
    }

    @Test
    void shouldHandleComplexFamilyScenario() {
        // Given
        TicketTypeRequest adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest children = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        // When
        ticketService.purchaseTickets(5L, adults, children, infants);
        
        // Then - verify correct calculations: 3 seats (adults+children), £65 cost
        verify(seatService).reserveSeat(5L, 3);
        verify(paymentService).makePayment(5L, 65);
    }
}