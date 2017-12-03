package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Mikelis on 2017.12.01..
 */
public class Coins {
    private Integer id;
    private String title;
    private String name;
    private BigDecimal volumeInBTC;
    private BigDecimal percentChange;
    private String marketName;
    private LocalDateTime firstTimeAppearing;
    private BigDecimal buyPrice;
    private BigDecimal maxPrice;
    private BigDecimal percentageGain;
    private LocalDateTime lastTimeAppeared;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getVolumeInBTC() {
        return volumeInBTC;
    }

    public void setVolumeInBTC(BigDecimal volumeInBTC) {
        this.volumeInBTC = volumeInBTC;
    }

    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public LocalDateTime getFirstTimeAppearing() {
        return firstTimeAppearing;
    }

    public void setFirstTimeAppearing(LocalDateTime firstTimeAppearing) {
        this.firstTimeAppearing = firstTimeAppearing;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getPercentageGain() {
        return percentageGain;
    }

    public void setPercentageGain(BigDecimal percentageGain) {
        this.percentageGain = percentageGain;
    }

    public LocalDateTime getLastTimeAppeared() {
        return lastTimeAppeared;
    }

    public void setLastTimeAppeared(LocalDateTime lastTimeAppeared) {
        this.lastTimeAppeared = lastTimeAppeared;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coins coins = (Coins) o;

        if (title != null ? !title.equals(coins.title) : coins.title != null) return false;
        return marketName != null ? marketName.equals(coins.marketName) : coins.marketName == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (marketName != null ? marketName.hashCode() : 0);
        return result;
    }
}
