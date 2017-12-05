import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Model.Coins;
import org.apache.commons.dbutils.DbUtils;

/**
 * Created by Mikelis on 2017.12.02..
 */
public class SqLiteDb {
    private static final String COLUMN_id = "id";
    private static final String COLUMN_title = "title";
    private static final String COLUMN_name = "name";
    private static final String COLUMN_volumeInBTC = "volumeInBTC";
    private static final String COLUMN_percentChange = "percentChange";
    private static final String COLUMN_marketName = "marketName";
    private static final String COLUMN_firstTimeAppearing = "firstTimeAppearing";
    private static final String COLUMN_buyPrice = "buyPrice";
    private static final String COLUMN_maxPrice = "maxPrice";
    private static final String COLUMN_maxPrctGain = "maxPrctGain";
    private static final String COLUMN_minutesAfterMaxReached = "minutesAfterMaxReached";
    private static final String COLUMN_minutesMinus10PrcReached = "minutesMinus10PrcReached";
    private static final String COLUMN_currentPercentage = "currentPercentage";
    private static final String COLUMN_lastTimeAppeared = "lastTimeAppeared";

    Connection conn = null;

    SqLiteDb() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:coinsDB.db");
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int insertCoin(Coins coinData) {
        String INSERT_SQL = "INSERT INTO TrendingCoins (" + String.join(",",
                COLUMN_title,
                COLUMN_name,
                COLUMN_volumeInBTC,
                COLUMN_percentChange,
                COLUMN_marketName,
                COLUMN_firstTimeAppearing,
                COLUMN_buyPrice,
                COLUMN_maxPrice,
                COLUMN_lastTimeAppeared) +
                ") VALUES (?,?,?,?,?,?,?,?,?)";

        int numRowsInserted = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, coinData.getTitle());
            pstmt.setString(2, coinData.getName());
            pstmt.setBigDecimal(3, coinData.getVolumeInBTC());
            pstmt.setBigDecimal(4, coinData.getPercentChange());
            pstmt.setString(5, coinData.getMarketName());
            pstmt.setObject(6, coinData.getFirstTimeAppearing());
            pstmt.setBigDecimal(7, coinData.getBuyPrice());
            pstmt.setBigDecimal(8, coinData.getMaxPrice());
            pstmt.setObject(9, coinData.getLastTimeAppeared());
            numRowsInserted = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numRowsInserted;
    }

    public List<Coins> getCoinsThatApearedWithinTwoDays() {
        String sql = "SELECT * FROM TrendingCoins " +
                "WHERE " + COLUMN_lastTimeAppeared + " > strftime('%Y-%m-%dT%H:%M:%f','now', '-1 day', '+2 hour')";
        List<Coins> resultList = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()) {
                Coins coin = new Coins();
                coin.setId(rs.getInt(COLUMN_id));
                coin.setTitle(rs.getString(COLUMN_title));
                coin.setName(rs.getString(COLUMN_name));
                coin.setVolumeInBTC(rs.getBigDecimal(COLUMN_volumeInBTC));
                coin.setPercentChange(rs.getBigDecimal(COLUMN_percentChange));
                coin.setMarketName(rs.getString(COLUMN_marketName));
                coin.setFirstTimeAppearing(LocalDateTime.parse(rs.getString(COLUMN_firstTimeAppearing)));
                coin.setBuyPrice(rs.getBigDecimal(COLUMN_buyPrice));
                coin.setMaxPrice(rs.getBigDecimal(COLUMN_maxPrice));
                coin.setMaxPrctGain(rs.getBigDecimal(COLUMN_maxPrctGain));
                coin.setMinutesAfterMaxWasReached(rs.getInt(COLUMN_minutesAfterMaxReached));
                coin.setMinutesMinus10PrcReached(rs.getInt(COLUMN_minutesMinus10PrcReached));
                coin.setCurrentPercentage(rs.getBigDecimal(COLUMN_currentPercentage));
                coin.setLastTimeAppeared(LocalDateTime.parse(rs.getString(COLUMN_lastTimeAppeared)));
                resultList.add(coin);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultList;
    }

    public void closeConnection() {
        try {
            DbUtils.close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLastTimeSeen(Integer id) {
        String UPDATE_SQL = "UPDATE TrendingCoins " +
                "SET " + COLUMN_lastTimeAppeared + " = strftime('%Y-%m-%dT%H:%M:%f','now', '+2 hour') " +
                "WHERE " + COLUMN_id + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCoin(Integer id, BigDecimal maxPrice, BigDecimal percentIncrease, int minutesAfterMaxWasReached) {
        String UPDATE_SQL = "UPDATE TrendingCoins " +
                "SET " + COLUMN_maxPrice + " = ?" +
                ", " + COLUMN_maxPrctGain + " = ?" +
                ", " + COLUMN_minutesAfterMaxReached + " = ? " +
                ", " + COLUMN_currentPercentage + " = ? " +
                "WHERE " + COLUMN_id + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setBigDecimal(1, maxPrice);
            pstmt.setBigDecimal(2, percentIncrease);
            pstmt.setInt(3, minutesAfterMaxWasReached);
            pstmt.setBigDecimal(4, percentIncrease);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentPercentage(Integer id, BigDecimal currentPercentage) {
        String UPDATE_SQL = "UPDATE TrendingCoins " +
                "SET " + COLUMN_currentPercentage + " = ?" +
                "WHERE " + COLUMN_id + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setBigDecimal(1, currentPercentage);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMinutesMinus10PercReached(Integer id, int minutesMinus10PercReached) {
        String UPDATE_SQL = "UPDATE TrendingCoins " +
                "SET " + COLUMN_minutesMinus10PrcReached + " = ?" +
                "WHERE " + COLUMN_id + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setInt(1, minutesMinus10PercReached);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
