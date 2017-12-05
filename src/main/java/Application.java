import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by mikelis.briedis on 11/29/2017.
 */
public class Application {

    public static void main(String[] args) {
        Scraper scraper;
        if (args.length==1 && args[0].equals("linux")){
            scraper = new Scraper(true);
        } else
            scraper = new Scraper(false);
        Runnable scraperRunnable = () -> scraper.run();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(scraperRunnable, 0, 1, TimeUnit.MINUTES);
    }
}