package uk.gov.dwp.uc.pairtest.exception;

public class TicketPurchaseException extends InvalidPurchaseException {
    
    private final String detailMessage;
    
    public TicketPurchaseException(String message) {
        super();
        this.detailMessage = message;
    }
    
    @Override
    public String getMessage() {
        return detailMessage;
    }
}