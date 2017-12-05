package Rest.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Mikelis on 2017.12.03..
 */
public class TickerResults {
    private BigDecimal bid;
    private BigDecimal ask;
    private BigDecimal last;

    @JsonProperty("Bid")
    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    @JsonProperty("Ask")
    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    @JsonProperty("Last")
    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }
}
