package fr.velinfo.repository;

import fr.velinfo.avro.record.stream.AvroStationStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class HourlyStationStatsRepository implements Repository<AvroStationStats> {
    private static final String INSERT_SQL = "INSERT INTO station_hourly_statistics \n" +
            "(stationCode, periodStart, periodEnd, \n" +
            "numberOfMechanicalBikesReturned, numberOfElectricBikesReturned, numberOfMechanicalBikesRented, numberOfElectricBikesRented, \n" +
            "minimumNumberOfMechanicalBikes, minimumNumberOfElectricBikes, minimumNumberOfEmptySlots, \n" +
            "lastNumberOfMechanicalBikes, lastNumberOfElectricBikes, lastLoadTimestamp)\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String QUERY_PAST_DAYS_SQL = "SELECT  \n" +
            "stationCode, periodStart, periodEnd, \n" +
            "numberOfMechanicalBikesReturned, numberOfElectricBikesReturned, numberOfMechanicalBikesRented, numberOfElectricBikesRented, \n" +
            "minimumNumberOfMechanicalBikes, minimumNumberOfElectricBikes, minimumNumberOfEmptySlots, \n" +
            "lastNumberOfMechanicalBikes, lastNumberOfElectricBikes, lastLoadTimestamp \n" +
            "FROM station_hourly_statistics \n" +
            "WHERE stationCode = ? and periodStart > current_date - interval '%days%' day;";

    @Override
    public void persist(Collection<AvroStationStats> stats) throws RepositoryException {
        if(stats.isEmpty())
            return;

        try (
                Connection connection = ConnectionUtils.getConnection();
                PreparedStatement insertStats = connection.prepareStatement(INSERT_SQL)
        ) {
            connection.setAutoCommit(false);
            for (AvroStationStats stat : stats) {
                insertStats.setString(1, stat.getStationCode());
                insertStats.setTimestamp(2, new Timestamp(stat.getPeriodStart()));
                insertStats.setTimestamp(3, new Timestamp(stat.getPeriodEnd()));
                insertStats.setInt(4, stat.getNumberOfMechanicalBikesReturned());
                insertStats.setInt(5, stat.getNumberOfElectricBikesReturned());
                insertStats.setInt(6, stat.getNumberOfMechanicalBikesRented());
                insertStats.setInt(7, stat.getNumberOfElectricBikesRented());
                insertStats.setInt(8, stat.getMinimumNumberOfMechanicalBikes());
                insertStats.setInt(9, stat.getMinimumNumberOfElectricBikes());
                insertStats.setInt(10, stat.getMinimumNumberOfEmptySlots());
                insertStats.setInt(11, stat.getLastNumberOfMechanicalBikes());
                insertStats.setInt(12, stat.getLastNumberOfElectricBikes());
                insertStats.setTimestamp(13, new Timestamp(stat.getLastLoadTimestamp()));
                insertStats.addBatch();
            }
            insertStats.executeBatch();
            connection.commit();
        } catch (SQLException exception ) {
            throw new RepositoryException("Exception when persisting "+stats, exception);
        }
    }

    public Collection<AvroStationStats> getStatsForPastDays(String stationCode, int days) throws RepositoryException {
        String query = QUERY_PAST_DAYS_SQL.replace("%days%", days+"");
        try (
                Connection connection = ConnectionUtils.getConnection();
                PreparedStatement insertStats = connection.prepareStatement(query)
        ) {
            insertStats.setString(1, stationCode);
            var resultSet = insertStats.executeQuery();
            var stats = new ArrayList<AvroStationStats>();
            while (resultSet.next()) {
                AvroStationStats stat = AvroStationStats.newBuilder()
                        .setStationCode(resultSet.getString("stationCode"))
                        .setPeriodStart(resultSet.getTimestamp("periodStart").getTime())
                        .setPeriodEnd(resultSet.getTimestamp("periodEnd").getTime())
                        .setNumberOfMechanicalBikesReturned(resultSet.getInt("numberOfMechanicalBikesReturned"))
                        .setNumberOfElectricBikesReturned(resultSet.getInt("numberOfElectricBikesReturned"))
                        .setNumberOfMechanicalBikesRented(resultSet.getInt("numberOfMechanicalBikesRented"))
                        .setNumberOfElectricBikesRented(resultSet.getInt("numberOfElectricBikesRented"))
                        .setMinimumNumberOfMechanicalBikes(resultSet.getInt("minimumNumberOfMechanicalBikes"))
                        .setMinimumNumberOfElectricBikes(resultSet.getInt("minimumNumberOfElectricBikes"))
                        .setMinimumNumberOfEmptySlots(resultSet.getInt("minimumNumberOfEmptySlots"))
                        .setLastNumberOfMechanicalBikes(resultSet.getInt("lastNumberOfMechanicalBikes"))
                        .setLastNumberOfElectricBikes(resultSet.getInt("lastNumberOfElectricBikes"))
                        .setLastLoadTimestamp(resultSet.getTimestamp("lastLoadTimestamp").getTime())
                        .build();
                stats.add(stat);
            }
            return stats;
        } catch (SQLException exception ) {
            throw new RepositoryException("Exception when loading 30 days stats for "+stationCode, exception);
        }
    }
}
