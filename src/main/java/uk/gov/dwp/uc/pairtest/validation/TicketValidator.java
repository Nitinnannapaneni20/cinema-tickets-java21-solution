package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

@FunctionalInterface
public interface TicketValidator {
    void validate(Long accountId, TicketTypeRequest... requests);
}