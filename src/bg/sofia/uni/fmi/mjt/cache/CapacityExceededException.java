package bg.sofia.uni.fmi.mjt.cache;


public class CapacityExceededException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public CapacityExceededException(String string, Throwable throwable) {
        super(string, throwable);
    }
    public CapacityExceededException(String message) {
        super(message);
    }
}
