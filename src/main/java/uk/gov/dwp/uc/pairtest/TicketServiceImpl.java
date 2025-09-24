package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.TicketValidator;
import uk.gov.dwp.uc.pairtest.validation.TicketValidatorImpl;
import uk.gov.dwp.uc.pairtest.calculation.CostCalculator;
import uk.gov.dwp.uc.pairtest.calculation.CostCalculatorImpl;
import uk.gov.dwp.uc.pairtest.calculation.SeatCalculator;
import uk.gov.dwp.uc.pairtest.calculation.SeatCalculatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketServiceImpl implements TicketService {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    
    private final TicketPaymentService paymentService;
    private final SeatReservationService seatService;
    private final TicketValidator validator;
    private final CostCalculator costCalculator;
    private final SeatCalculator seatCalculator;
    
    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatService) {
        this.paymentService = paymentService;
        this.seatService = seatService;
        this.validator = new TicketValidatorImpl();
        this.costCalculator = new CostCalculatorImpl();
        this.seatCalculator = new SeatCalculatorImpl();
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        logger.info("Processing ticket purchase for account: {}", accountId);
        
        validator.validate(accountId, ticketTypeRequests);
        
        int totalCost = costCalculator.calculate(ticketTypeRequests);
        int totalSeats = seatCalculator.calculate(ticketTypeRequests);
        
        logger.debug("Calculated cost: £{}, seats: {}", totalCost, totalSeats);
        
        seatService.reserveSeat(accountId, totalSeats);
        paymentService.makePayment(accountId, totalCost);
        
        logger.info("Successfully processed ticket purchase for account: {}", accountId);
    }

}
