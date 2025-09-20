package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountId(accountId);
        checkTicketRequests(ticketTypeRequests);
        checkBusinessRules(ticketTypeRequests);
    }

    // Making sure account ID is valid
    private void checkAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException();
        }
    }

    // Making sure ticket requests are valid
    private void checkTicketRequests(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException();
        }
        
        int totalTickets = 0;
        for (TicketTypeRequest request : requests) {
            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException();
            }
            totalTickets += request.getNoOfTickets();
        }
        
        // Max 25 tickets rule
        if (totalTickets > 25) {
            throw new InvalidPurchaseException();
        }
    }
    
    // Validating business rules
    private void checkBusinessRules(TicketTypeRequest... requests) {
        int adults = 0;
        int children = 0;
        int infants = 0;
        
        // Count each ticket type
        for (TicketTypeRequest request : requests) {
            switch (request.getTicketType()) {
                case ADULT:
                    adults += request.getNoOfTickets();
                    break;
                case CHILD:
                    children += request.getNoOfTickets();
                    break;
                case INFANT:
                    infants += request.getNoOfTickets();
                    break;
            }
        }
        
        if (adults == 0 && (children > 0 || infants > 0)) {
            throw new InvalidPurchaseException();
        }
        
        // Infants sit on adult laps so can't have more infants than adults
        if (infants > adults) {
            throw new InvalidPurchaseException();
        }
    }

}
