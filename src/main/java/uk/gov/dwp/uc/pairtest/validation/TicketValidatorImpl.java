package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import java.util.Arrays;

public class TicketValidatorImpl implements TicketValidator {
    
    @Override
    public void validate(Long accountId, TicketTypeRequest... requests) {
        validateAccountId(accountId);
        validateTicketRequests(requests);
        validateBusinessRules(requests);
    }
    
    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException();
        }
    }
    
    private void validateTicketRequests(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException();
        }
        
        int totalTickets = Arrays.stream(requests)
            .filter(request -> request != null && request.getNoOfTickets() > 0)
            .mapToInt(TicketTypeRequest::getNoOfTickets)
            .sum();
            
        if (totalTickets == 0 || totalTickets > 25) {
            throw new InvalidPurchaseException();
        }
    }
    
    private void validateBusinessRules(TicketTypeRequest... requests) {
        int adults = countTicketsByType(requests, TicketTypeRequest.Type.ADULT);
        int children = countTicketsByType(requests, TicketTypeRequest.Type.CHILD);
        int infants = countTicketsByType(requests, TicketTypeRequest.Type.INFANT);
        
        if (adults == 0 && (children > 0 || infants > 0)) {
            throw new InvalidPurchaseException();
        }
        
        if (infants > adults) {
            throw new InvalidPurchaseException();
        }
    }
    
    private int countTicketsByType(TicketTypeRequest[] requests, TicketTypeRequest.Type type) {
        return Arrays.stream(requests)
            .filter(request -> request.getTicketType() == type)
            .mapToInt(TicketTypeRequest::getNoOfTickets)
            .sum();
    }
}