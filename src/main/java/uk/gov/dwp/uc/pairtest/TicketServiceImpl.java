package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    
    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;
    private static final int INFANT_PRICE = 0;
    
    private final TicketPaymentService paymentService;
    private final SeatReservationService seatService;
    
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatService) {
        this.paymentService = paymentService;
        this.seatService = seatService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        checkAccountId(accountId);
        checkTicketRequests(ticketTypeRequests);
        checkBusinessRules(ticketTypeRequests);
        
        // Calculate total cost and seats, then make payment and reserve seats
        int totalCost = calculateTotalCost(ticketTypeRequests);
        int totalSeats = calculateTotalSeats(ticketTypeRequests);
        
        paymentService.makePayment(accountId, totalCost);
        seatService.reserveSeat(accountId, totalSeats);
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
            if (request == null || request.getNoOfTickets() <= 0) {
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
    
    // Calculate how much money to charge the customer for movie tickets
    private int calculateTotalCost(TicketTypeRequest... requests) {
        int totalCost = 0;
        
        for (TicketTypeRequest request : requests) {
            switch (request.getTicketType()) {
                case ADULT:
                    totalCost += request.getNoOfTickets() * ADULT_PRICE;
                    break;
                case CHILD:
                    totalCost += request.getNoOfTickets() * CHILD_PRICE;
                    break;
                case INFANT:
                    totalCost += request.getNoOfTickets() * INFANT_PRICE;
                    break;
            }
        }
        
        return totalCost;
    }
    
    // Calculate how many seats to reserve (adults + children, infants sit on adults lap)
    private int calculateTotalSeats(TicketTypeRequest... requests) {
        int totalSeats = 0;
        
        for (TicketTypeRequest request : requests) {
            switch (request.getTicketType()) {
                case ADULT:
                case CHILD:
                    totalSeats += request.getNoOfTickets();
                    break;
                case INFANT:
                    // Infants don't need seats
                    break;
            }
        }
        
        return totalSeats;
    }

}
