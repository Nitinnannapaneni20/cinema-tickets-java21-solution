package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.TicketPurchaseException;
import java.util.Arrays;

public class TicketValidatorImpl implements TicketValidator {
    
    private static final int MAX_TICKETS = 25;
    
    @Override
    public void validate(Long accountId, TicketTypeRequest... requests) {
        ensureAccountIdIsValid(accountId);
        ensureRequestsNotExceedingMaximumTickets(requests);
        enforceAdultSupervisionBusinessRules(requests);
    }
    
    private void ensureAccountIdIsValid(Long accountId) {
        if (accountId == null) {
            throw new TicketPurchaseException("Account ID cannot be null");
        }
        if (accountId <= 0) {
            throw new TicketPurchaseException("Account ID must be greater than 0");
        }
    }
    
    private void ensureRequestsNotExceedingMaximumTickets(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new TicketPurchaseException("At least one ticket request is required");
        }
        
        int totalTickets = Arrays.stream(requests)
            .filter(request -> request != null && request.getNoOfTickets() > 0)
            .mapToInt(TicketTypeRequest::getNoOfTickets)
            .sum();
            
        if (totalTickets <= 0) {
            throw new TicketPurchaseException("All ticket requests are invalid or have zero quantity");
        }
            
        if (totalTickets > MAX_TICKETS) {
            throw new TicketPurchaseException(
                String.format("Cannot purchase more than %d tickets at once. Requested: %d", MAX_TICKETS, totalTickets));
        }
    }
    
    private void enforceAdultSupervisionBusinessRules(TicketTypeRequest... requests) {
        int adults = calculateTotalTicketsByType(requests, TicketTypeRequest.Type.ADULT);
        int children = calculateTotalTicketsByType(requests, TicketTypeRequest.Type.CHILD);
        int infants = calculateTotalTicketsByType(requests, TicketTypeRequest.Type.INFANT);
        
        if (adults == 0 && (children > 0 || infants > 0)) {
            throw new TicketPurchaseException(
                "Children and infants must be accompanied by at least one adult");
        }
        
        if (infants > adults) {
            throw new TicketPurchaseException(
                String.format("Cannot have more infants (%d) than adults (%d) - infants sit on adult laps", infants, adults));
        }
    }
    
    private int calculateTotalTicketsByType(TicketTypeRequest[] requests, TicketTypeRequest.Type type) {
        return Arrays.stream(requests)
            .filter(request -> request != null && request.getTicketType() == type)
            .mapToInt(TicketTypeRequest::getNoOfTickets)
            .sum();
    }
}