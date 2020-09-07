package velibstreaming.repository;

import velibstreaming.avro.record.stream.AvroStationStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class HourlyStationStatsRepository implements Repository<AvroStationStats> {

    public synchronized void persist(AvroStationStats stats) throws RepositoryException {
        String insertSql = "INSERT INTO station_hourly_statistics "+
                "(stationCode, periodStart, periodEnd, "+
                "numberOfMechanicalBikesReturned, numberOfElectricBikesReturned, numberOfMechanicalBikesRented, numberOfElectricBikesRented, "+
                "minimumNumberOfMechanicalBikes, minimumNumberOfElectricBikes, minimumNumberOfEmptySlots, "+
                "lastNumberOfMechanicalBikes, lastNumberOfElectricBikes, lastLoadTimestamp)"+
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
            System.out.println(exception);
            throw new RepositoryException("Exception when persisting "+stats, exception);
        }
    }
}
