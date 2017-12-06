package Rest;

import javax.ws.rs.ProcessingException;
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

    public MarketTicker getTicker(String market) throws ProcessingException {
        System.out.println("getTicker(" + market + ")");
        WebTarget target = tickerWebTarget.queryParam("market", market);
        Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
        int count = 0;
        int maxTries = 3;
        while (true) {
            try {
                return invocationBuilder.get(MarketTicker.class);
            } catch (ProcessingException e) {
                System.out.println("a fucking anticipated exceptin------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
                System.out.println(e.toString());
                if (++count == maxTries) throw e;
            }
        }
    }
}
