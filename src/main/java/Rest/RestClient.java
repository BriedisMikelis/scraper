package Rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import Rest.model.MarketTicker;

/**
 * Created by Mikelis on 2017.12.03..
 */
public class RestClient {
//    http://www.baeldung.com/jersey-jax-rs-client

    Client client = ClientBuilder.newClient();
    WebTarget bittrexWebTarget = client.target("https://bittrex.com/api/v1.1/public");
    WebTarget tickerWebTarget = bittrexWebTarget.path("getticker");

    public MarketTicker getTicker(String market) {
        System.out.println("getTicker(" + market + ")");
        WebTarget target = tickerWebTarget.queryParam("market", market);
        System.out.println("1");
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        System.out.println("2");
        MarketTicker ticker = invocationBuilder.get(MarketTicker.class);
        System.out.println("3");
        return ticker;
    }
}
