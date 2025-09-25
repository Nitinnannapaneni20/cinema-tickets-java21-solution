package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import java.util.Map;

@FunctionalInterface
public interface CostCalculator {
    int calculate(Map<TicketTypeRequest.Type, Integer> ticketCounts);
}