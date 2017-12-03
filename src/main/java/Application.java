import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mikelis.briedis on 11/29/2017.
 */
public class Application {

    public static void main(String[] args) {
        Scraper scraper = new Scraper();
        Runnable scraperRunnable = () -> scraper.run();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(scraperRunnable, 0, 3, TimeUnit.MINUTES);
    }
}