//Donat Salihu
//Nikolaos Lintas
//Memli Restelica
//Philippos Kalatzis

package Payload;

import java.io.Serializable;

public class ErrorMessagePayload implements Serializable {
    //Attributes
    private String errorMessage;

    //Constructors
    public ErrorMessagePayload(String error) {
        this.errorMessage = error;
    }

    //Setters and Getters
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    //Methods
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        ErrorMessagePayload that = (ErrorMessagePayload) object;
        return java.util.Objects.equals(errorMessage, that.errorMessage);
    }

    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), errorMessage);
    }

    @Override
    public String toString() {
        return "ErrorMessagePayload{" +
            "error='" + errorMessage +
            '}';
    }
}
