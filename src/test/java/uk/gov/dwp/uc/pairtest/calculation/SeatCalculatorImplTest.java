package uk.gov.dwp.uc.pairtest.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeatCalculatorImplTest {

    private SeatCalculatorImpl seatCalculator;

    @BeforeEach
    void setUp() {
        seatCalculator = new SeatCalculatorImpl();
    }

    @Test
    void testAdultsNeedSeats() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);
        
        int seats = seatCalculator.calculate(request);
        
        assertEquals(3, seats);
    }

    @Test
    void testChildrenNeedSeats() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        
        int seats = seatCalculator.calculate(request);
        
        assertEquals(2, seats);
    }

    @Test
    void testInfantsSitOnLaps() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        
        int seats = seatCalculator.calculate(request);
        
        assertEquals(0, seats); // Infants sit on laps
    }

    @Test
    void testFamilySeating() {
        TicketTypeRequest adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest children = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        int seats = seatCalculator.calculate(adults, children, infants);
        
        assertEquals(3, seats); // 2 adults + 1 child, infant on lap
    }

    @Test
    void testNoTicketsNoSeats() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        
        int seats = seatCalculator.calculate(request);
        
        assertEquals(0, seats);
    }
}