package finder;

/**
 * Created by Antony on 03.10.2016.
 */
public class DataException extends Exception {
    String message;

    DataException(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
