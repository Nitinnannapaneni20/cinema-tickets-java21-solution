package uk.gov.dwp.uc.pairtest.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeatCalculatorImplTest {

    private SeatCalculatorImpl seatCalculator;

    @BeforeEach
    void setUp() {
        seatCalculator = new SeatCalculatorImpl();
    }

    @Test
    void testAdultsNeedSeats() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 3);
        
        int seats = seatCalculator.calculate(ticketCounts);
        
        assertEquals(3, seats);
    }

    @Test
    void testChildrenNeedSeats() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.CHILD, 2);
        
        int seats = seatCalculator.calculate(ticketCounts);
        
        assertEquals(2, seats);
    }

    @Test
    void testInfantsSitOnLaps() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.INFANT, 2);
        
        int seats = seatCalculator.calculate(ticketCounts);
        
        assertEquals(0, seats); // Infants sit on laps
    }

    @Test
    void testFamilySeating() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(
            TicketTypeRequest.Type.ADULT, 2,
            TicketTypeRequest.Type.CHILD, 1,
            TicketTypeRequest.Type.INFANT, 1
        );
        
        int seats = seatCalculator.calculate(ticketCounts);
        
        assertEquals(3, seats); // 2 adults + 1 child, infant on lap
    }

    @Test
    void testNoTicketsNoSeats() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of();
        
        int seats = seatCalculator.calculate(ticketCounts);
        
        assertEquals(0, seats);
    }
}