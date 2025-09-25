package uk.gov.dwp.uc.pairtest.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CostCalculatorImplTest {

    private CostCalculatorImpl costCalculator;

    @BeforeEach
    void setUp() {
        costCalculator = new CostCalculatorImpl();
    }

    @Test
    void testAdultTicketsAre25Each() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.ADULT, 2);
        
        int cost = costCalculator.calculate(ticketCounts);
        
        assertEquals(50, cost); // 2 * £25
    }

    @Test
    void testChildTicketsAre15Each() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.CHILD, 3);
        
        int cost = costCalculator.calculate(ticketCounts);
        
        assertEquals(45, cost); // 3 * £15
    }

    @Test
    void testInfantsAreFree() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(TicketTypeRequest.Type.INFANT, 2);
        
        int cost = costCalculator.calculate(ticketCounts);
        
        assertEquals(0, cost); // 2 * £0
    }

    @Test
    void testFamilyTicketPricing() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of(
            TicketTypeRequest.Type.ADULT, 2,
            TicketTypeRequest.Type.CHILD, 1,
            TicketTypeRequest.Type.INFANT, 1
        );
        
        int cost = costCalculator.calculate(ticketCounts);
        
        assertEquals(65, cost); // 2*£25 + 1*£15 + 1*£0
    }

    @Test
    void testZeroTicketsCostNothing() {
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Map.of();
        
        int cost = costCalculator.calculate(ticketCounts);
        
        assertEquals(0, cost);
    }
}