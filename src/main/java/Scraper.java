import Model.Coins;
import Rest.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mikelis on 2017.12.04..
 */
public class Scraper {
    RestClient restClient;
    WebScraper webScraper = new WebScraper();
    SqLiteDb db;
    public void run() {
        System.out.println("STARTING RUN -----------------------------------------");
        restClient = new RestClient();
        db = new SqLiteDb();

        List<Coins> listOfTrendingCoins = webScraper.scrapeBittrexFrontpage();
//        webScraper.driver.quit();

        List<Coins> listOfCoinsThatAppearedWithinTwoDays = db.getCoinsThatApearedWithinTwoDays();

        List<Coins> newCoinsToAdd = new ArrayList<>();
        List<Coins> coinsToUpdateForLastTimeSeen = new ArrayList<>();
        listOfTrendingCoins.stream().forEach(trendingCoin -> {
            Optional<Coins> result = listOfCoinsThatAppearedWithinTwoDays.stream().filter(o -> o.equals(trendingCoin)).findFirst();
            if (result.isPresent()) {
                coinsToUpdateForLastTimeSeen.add(result.get());
            } else {
                newCoinsToAdd.add(trendingCoin);
            }
        });

        addNewCoins(newCoinsToAdd);
        updateLastTimeSeen(coinsToUpdateForLastTimeSeen);
        updatePrices(listOfCoinsThatAppearedWithinTwoDays);
        db.closeConnection();
        System.out.println("done");
    }

    private void updateLastTimeSeen(List<Coins> coinsToUpdateForLastTimeSeen) {
        System.out.println("Updating last time seen");
        coinsToUpdateForLastTimeSeen.forEach(coin -> {
            db.updateLastTimeSeen(coin.getId());
        });
    }

    private void addNewCoins(List<Coins> newCoinsToAdd) {
        if (!newCoinsToAdd.isEmpty()) {
            System.out.println("Adding new coins");
            for (Coins coin : newCoinsToAdd) {
                BigDecimal askPrice = restClient.getTicker(coin.getMarketName()).getResult().getAsk();
                coin.setBuyPrice(askPrice);
                coin.setMaxPrice(askPrice);
                db.insertCoin(coin);
            }
        }
    }

    private void updatePrices(List<Coins> listOfCoinsThatAppearedWithinTwoDays) {
        if (!listOfCoinsThatAppearedWithinTwoDays.isEmpty()) {
            System.out.println("Updating asked price and %");
            for (Coins coin : listOfCoinsThatAppearedWithinTwoDays) {
                BigDecimal lastPrice = restClient.getTicker(coin.getMarketName()).getResult().getLast();
                if (coin.getMaxPrice().compareTo(lastPrice) < 0) {
                    BigDecimal percentIncrease = Utils.calculatePercentIncrease(coin.getBuyPrice(), lastPrice);
                    int minutesAfterMaxWasReached = (int)Duration.between(LocalDateTime.now(), coin.getFirstTimeAppearing()).toMinutes();
                    db.updateCoin(coin.getId(), lastPrice, percentIncrease, minutesAfterMaxWasReached);
                }

            }
        }
    }
}
