package bg.sofia.uni.fmi.mjt.cache;


public class CapacityExceededException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public CapacityExceededException() {
    }
    public CapacityExceededException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CapacityExceededException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
