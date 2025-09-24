package uk.gov.dwp.uc.pairtest.calculation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

@FunctionalInterface
public interface SeatCalculator {
    int calculate(TicketTypeRequest... requests);
}