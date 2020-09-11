package velibstreaming.repository;

import velibstreaming.avro.record.stream.AvroStationStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class HourlyStationStatsRepository implements Repository<AvroStationStats> {

    public synchronized void persist(AvroStationStats stats) throws RepositoryException {
        String insertSql = "INSERT INTO station_hourly_statistics \n" +
                "(stationCode, periodStart, periodEnd, \n" +
                "numberOfMechanicalBikesReturned, numberOfElectricBikesReturned, numberOfMechanicalBikesRented, numberOfElectricBikesRented, \n" +
                "minimumNumberOfMechanicalBikes, minimumNumberOfElectricBikes, minimumNumberOfEmptySlots, \n" +
                "lastNumberOfMechanicalBikes, lastNumberOfElectricBikes, lastLoadTimestamp)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (
                Connection connection = ConnectionUtils.getConnection();
                PreparedStatement insertStats = connection.prepareStatement(insertSql)
        ) {
            insertStats.setString(1, stats.getStationCode());
            insertStats.setTimestamp(2, new Timestamp(stats.getPeriodStart()));
            insertStats.setTimestamp(3, new Timestamp(stats.getPeriodEnd()));
            insertStats.setInt(4, stats.getNumberOfMechanicalBikesReturned());
            insertStats.setInt(5, stats.getNumberOfElectricBikesReturned());
            insertStats.setInt(6, stats.getNumberOfMechanicalBikesRented());
            insertStats.setInt(7, stats.getNumberOfElectricBikesRented());
            insertStats.setInt(8, stats.getMinimumNumberOfMechanicalBikes());
            insertStats.setInt(9, stats.getMinimumNumberOfElectricBikes());
            insertStats.setInt(10, stats.getMinimumNumberOfEmptySlots());
            insertStats.setInt(11, stats.getLastNumberOfMechanicalBikes());
            insertStats.setInt(12, stats.getLastNumberOfElectricBikes());
            insertStats.setTimestamp(13, new Timestamp(stats.getLastLoadTimestamp()));

            insertStats.execute();
        } catch (SQLException exception ) {
            throw new RepositoryException("Exception when persisting "+stats, exception);
        }
    }

    public Collection<AvroStationStats> getStatsForPast30Days(String stationCode) throws RepositoryException {
        String querySql = "SELECT  \n" +
                "stationCode, periodStart, periodEnd, \n" +
                "numberOfMechanicalBikesReturned, numberOfElectricBikesReturned, numberOfMechanicalBikesRented, numberOfElectricBikesRented, \n" +
                "minimumNumberOfMechanicalBikes, minimumNumberOfElectricBikes, minimumNumberOfEmptySlots, \n" +
                "lastNumberOfMechanicalBikes, lastNumberOfElectricBikes, lastLoadTimestamp \n" +
                "FROM station_hourly_statistics \n" +
                "WHERE stationCode = ? and periodStart > current_date - interval '30' day;";
        try (
                Connection connection = ConnectionUtils.getConnection();
                PreparedStatement insertStats = connection.prepareStatement(querySql)
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
