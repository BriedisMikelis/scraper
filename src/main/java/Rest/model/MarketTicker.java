package Rest.model;

/**
 * Created by Mikelis on 2017.12.03..
 */
public class MarketTicker {
    private Boolean success;
    private String message;
    private TickerResults result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TickerResults getResult() {
        return result;
    }

    public void setResult(TickerResults result) {
        this.result = result;
    }
}
