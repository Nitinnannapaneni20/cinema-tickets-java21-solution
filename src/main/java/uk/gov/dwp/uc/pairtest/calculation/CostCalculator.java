package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

@FunctionalInterface
public interface CostCalculator {
    int calculate(TicketTypeRequest... requests);
}