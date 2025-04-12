package es.ibm.usermanagement.exception.custom;




public class AsyncTaskFailureException extends RuntimeException {
    public AsyncTaskFailureException(String message) {
        super(message);
    }

    public AsyncTaskFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}