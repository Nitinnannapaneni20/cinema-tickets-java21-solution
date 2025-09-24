package uk.gov.dwp.uc.pairtest.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CostCalculatorImplTest {

    private CostCalculatorImpl costCalculator;

    @BeforeEach
    void setUp() {
        costCalculator = new CostCalculatorImpl();
    }

    @Test
    void testAdultTicketsAre25Each() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        
        int cost = costCalculator.calculate(request);
        
        assertEquals(50, cost); // 2 * £25
    }

    @Test
    void testChildTicketsAre15Each() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        
        int cost = costCalculator.calculate(request);
        
        assertEquals(45, cost); // 3 * £15
    }

    @Test
    void testInfantsAreFree() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);
        
        int cost = costCalculator.calculate(request);
        
        assertEquals(0, cost); // 2 * £0
    }

    @Test
    void testFamilyTicketPricing() {
        TicketTypeRequest adults = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest children = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);
        
        int cost = costCalculator.calculate(adults, children, infants);
        
        assertEquals(65, cost); // 2*£25 + 1*£15 + 1*£0
    }

    @Test
    void testZeroTicketsCostNothing() {
        TicketTypeRequest request = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        
        int cost = costCalculator.calculate(request);
        
        assertEquals(0, cost);
    }
}