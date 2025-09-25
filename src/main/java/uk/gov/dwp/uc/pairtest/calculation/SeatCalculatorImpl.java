package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import java.util.Map;
import java.util.Set;

public class SeatCalculatorImpl implements SeatCalculator {
    
    private static final Set<TicketTypeRequest.Type> SEAT_REQUIRED_TYPES = Set.of(
        TicketTypeRequest.Type.ADULT,
        TicketTypeRequest.Type.CHILD
    );
    
    @Override
    public int calculate(Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        return ticketCounts.entrySet().stream()
            .filter(entry -> SEAT_REQUIRED_TYPES.contains(entry.getKey()))
            .mapToInt(Map.Entry::getValue)
            .sum();
    }
}