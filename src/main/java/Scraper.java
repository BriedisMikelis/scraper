import Model.Coins;
import Rest.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mikelis on 2017.12.04..
 */
public class Scraper {
    RestClient restClient;
    WebScraper webScraper;
    SqLiteDb db;
    public void run() {
        restClient = new RestClient();
        webScraper = new WebScraper();
        db = new SqLiteDb();

        List<Coins> listOfTrendingCoins = webScraper.scrapeBittrexFrontpage();
        webScraper.driver.quit();

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
        System.out.println("done");
        db.closeConnection();
    }

    private void updateLastTimeSeen(List<Coins> coinsToUpdateForLastTimeSeen) {
        coinsToUpdateForLastTimeSeen.forEach(coin -> {
            db.updateLastTimeSeen(coin.getId());
        });
    }

    private void addNewCoins(List<Coins> newCoinsToAdd) {
        if (!newCoinsToAdd.isEmpty()) {
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
            for (Coins coin : listOfCoinsThatAppearedWithinTwoDays) {
                BigDecimal askPrice = restClient.getTicker(coin.getMarketName()).getResult().getAsk();
                if (coin.getMaxPrice().compareTo(askPrice) < 0) {
                    BigDecimal percentIncrease = askPrice
                            .subtract(coin.getBuyPrice())
                            .divide(coin.getBuyPrice(), 8, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal(100));
                    db.updateAskPriceAndPercetage(coin.getId(), askPrice, percentIncrease);
                }
            }
        }
    }
}
