package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountId(accountId);
        checkTicketRequests(ticketTypeRequests);
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
        
        for (TicketTypeRequest request : requests) {
            if (request.getNoOfTickets() <= 0) {
                throw new InvalidPurchaseException();
            }
        }
    }

}
