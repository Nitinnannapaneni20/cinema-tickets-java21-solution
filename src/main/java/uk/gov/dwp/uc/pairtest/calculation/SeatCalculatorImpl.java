package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import java.util.Arrays;
import java.util.Set;

public class SeatCalculatorImpl implements SeatCalculator {
    
    private static final Set<TicketTypeRequest.Type> SEAT_REQUIRED_TYPES = Set.of(
        TicketTypeRequest.Type.ADULT,
        TicketTypeRequest.Type.CHILD
    );
    
    @Override
    public int calculate(TicketTypeRequest... requests) {
        return Arrays.stream(requests)
            .filter(request -> SEAT_REQUIRED_TYPES.contains(request.getTicketType()))
            .mapToInt(TicketTypeRequest::getNoOfTickets)
            .sum();
    }
}