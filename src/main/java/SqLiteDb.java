import Model.Coins;
import org.apache.commons.dbutils.DbUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private static final String COLUMN_percentageGain = "percentageGain";
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

    public void executeInsertQuery(String query) {
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insertCoin(Coins coinData) {
        String INSERT_SQL = "INSERT INTO TrendingCoins(" + String.join(",",
                COLUMN_title,
                COLUMN_name,
                COLUMN_volumeInBTC,
                COLUMN_percentChange,
                COLUMN_marketName,
                COLUMN_firstTimeAppearing,
                COLUMN_buyPrice,
                COLUMN_maxPrice,
                COLUMN_percentageGain,
                COLUMN_lastTimeAppeared) +
                ") VALUES(?,?,?,?,?,?,?,?,?,?)";

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
            pstmt.setBigDecimal(9, coinData.getPercentageGain());
            pstmt.setObject(10, coinData.getLastTimeAppeared());
            numRowsInserted = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numRowsInserted;
    }

    public List<Coins> getCoinsThatApearedWithinTwoDays() {
//        saving date format in sql strftime('%Y-%m-%dT%H:%M:%f'
        String sql = "SELECT * FROM TrendingCoins " +
                "WHERE " + COLUMN_lastTimeAppeared + " > strftime('%Y-%m-%dT%H:%M:%f','now', '-2 day', '+2 hour')";
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
                coin.setPercentageGain(rs.getBigDecimal(COLUMN_percentageGain));
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
                "SET lastTimeAppeared = strftime('%Y-%m-%dT%H:%M:%f','now', '+2 hour') " +
                "WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAskPriceAndPercetage(Integer id, BigDecimal maxPrice, BigDecimal percentIncrease) {
        String UPDATE_SQL = "UPDATE TrendingCoins SET maxPrice = ?, percentageGain = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setBigDecimal(1, maxPrice);
            pstmt.setBigDecimal(2, percentIncrease);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
