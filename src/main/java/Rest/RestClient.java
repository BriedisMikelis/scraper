package Rest;

import Rest.model.MarketTicker;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Created by Mikelis on 2017.12.03..
 */
public class RestClient {
//    http://www.baeldung.com/jersey-jax-rs-client

    Client client = ClientBuilder.newClient();
    WebTarget bittrexWebTarget = client.target("https://bittrex.com/api/v1.1/public");
    WebTarget tickerWebTarget = bittrexWebTarget.path("getticker");

    public MarketTicker getTicker(String market) {
        WebTarget target = tickerWebTarget.queryParam("market", market);
        return target.request(MediaType.APPLICATION_JSON).get(MarketTicker.class);
    }
}
