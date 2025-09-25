package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import java.util.Map;

public class CostCalculatorImpl implements CostCalculator {
    
    private static final Map<TicketTypeRequest.Type, Integer> PRICES = Map.of(
        TicketTypeRequest.Type.ADULT, 25,
        TicketTypeRequest.Type.CHILD, 15,
        TicketTypeRequest.Type.INFANT, 0
    );
    
    @Override
    public int calculate(Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        return ticketCounts.entrySet().stream()
            .mapToInt(entry -> entry.getValue() * PRICES.get(entry.getKey()))
            .sum();
    }
}