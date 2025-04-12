package es.ibm.usermanagement.exception.custom;

public class InvalidParamException extends RuntimeException{

    String parameter;

    public InvalidParamException(String message, String parameter) {
        super(message);
        this.parameter = parameter;
    }

    public String getParameter() {
        return this.parameter;
    }
}
