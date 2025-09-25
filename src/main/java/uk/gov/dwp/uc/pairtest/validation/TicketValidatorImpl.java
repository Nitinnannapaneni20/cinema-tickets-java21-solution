package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.TicketPurchaseException;
import java.util.Map;

public class TicketValidatorImpl implements TicketValidator {
    
    private static final int MAX_TICKETS = 25;
    
    @Override
    public void validate(Long accountId, Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        ensureAccountIdIsValid(accountId);
        ensureRequestsNotExceedingMaximumTickets(ticketCounts);
        enforceAdultSupervisionBusinessRules(ticketCounts);
    }
    
    private void ensureAccountIdIsValid(Long accountId) {
        if (accountId == null) {
            throw new TicketPurchaseException("Account ID cannot be null");
        }
        if (accountId <= 0) {
            throw new TicketPurchaseException("Account ID must be greater than 0");
        }
    }
    
    private void ensureRequestsNotExceedingMaximumTickets(Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        if (ticketCounts == null || ticketCounts.isEmpty()) {
            throw new TicketPurchaseException("At least one ticket request is required");
        }
        
        int totalTickets = ticketCounts.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
            
        if (totalTickets <= 0) {
            throw new TicketPurchaseException("All ticket requests are invalid or have zero quantity");
        }
            
        if (totalTickets > MAX_TICKETS) {
            throw new TicketPurchaseException(
                String.format("Cannot purchase more than %d tickets at once. Requested: %d", MAX_TICKETS, totalTickets));
        }
    }
    
    private void enforceAdultSupervisionBusinessRules(Map<TicketTypeRequest.Type, Integer> ticketCounts) {
        int adults = ticketCounts.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int children = ticketCounts.getOrDefault(TicketTypeRequest.Type.CHILD, 0);
        int infants = ticketCounts.getOrDefault(TicketTypeRequest.Type.INFANT, 0);
        
        if (adults == 0 && (children > 0 || infants > 0)) {
            throw new TicketPurchaseException(
                "Children and infants must be accompanied by at least one adult");
        }
        
        if (infants > adults) {
            throw new TicketPurchaseException(
                String.format("Cannot have more infants (%d) than adults (%d) - infants sit on adult laps", infants, adults));
        }
    }
    

}