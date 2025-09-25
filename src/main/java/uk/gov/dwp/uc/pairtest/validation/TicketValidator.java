package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import java.util.Map;

@FunctionalInterface
public interface TicketValidator {
    void validate(Long accountId, Map<TicketTypeRequest.Type, Integer> ticketCounts);
}