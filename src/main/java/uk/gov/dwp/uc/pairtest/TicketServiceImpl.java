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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
        
        // Traverse ticket requests once and store counts in map to avoid multiple iterations
        Map<TicketTypeRequest.Type, Integer> ticketCounts = Arrays.stream(ticketTypeRequests)
            .filter(request -> request != null && request.getNoOfTickets() > 0)
            .collect(Collectors.groupingBy(
                TicketTypeRequest::getTicketType,
                Collectors.summingInt(TicketTypeRequest::getNoOfTickets)
            ));
        
        validator.validate(accountId, ticketCounts);
        
        int totalCost = costCalculator.calculate(ticketCounts);
        int totalSeats = seatCalculator.calculate(ticketCounts);
        
        logger.debug("Calculated cost: £{}, seats: {}", totalCost, totalSeats);
        
        seatService.reserveSeat(accountId, totalSeats);
        paymentService.makePayment(accountId, totalCost);
        
        logger.info("Successfully processed ticket purchase for account: {}", accountId);
    }

}
