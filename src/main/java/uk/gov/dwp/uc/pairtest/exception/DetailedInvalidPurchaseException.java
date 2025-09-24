package uk.gov.dwp.uc.pairtest.exception;

public class DetailedInvalidPurchaseException extends InvalidPurchaseException {
    
    private final String detailMessage;
    
    public DetailedInvalidPurchaseException(String message) {
        super();
        this.detailMessage = message;
    }
    
    @Override
    public String getMessage() {
        return detailMessage;
    }
}