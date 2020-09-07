package velibstreaming.repository;

import velibstreaming.avro.record.stream.AvroNearbyTraffic;
import velibstreaming.avro.record.stream.AvroStationStats;

import java.sql.*;

public class DailyNearbyTrafficRepository implements Repository<AvroNearbyTraffic> {

    public synchronized void persist(AvroNearbyTraffic traffic) throws RepositoryException {
        String insertSql = "INSERT INTO station_daily_nearby_traffic "+
                "(stationCode, periodStart, periodEnd, totalTraffic)"+
                "VALUES (?, ?, ?, ?);";

        try (
                Connection connection = ConnectionUtils.getConnection();
                PreparedStatement insertStats = connection.prepareStatement(insertSql)
        ) {
            insertStats.setString(1, traffic.getStationCode());
            insertStats.setTimestamp(2, new Timestamp(traffic.getPeriodStart()));
            insertStats.setTimestamp(3, new Timestamp(traffic.getPeriodEnd()));
            insertStats.setInt(4, traffic.getTotalTraffic());

            insertStats.execute();
        } catch (SQLException exception ) {
            System.out.println(exception);
            throw new RepositoryException("Exception when persisting "+traffic, exception);
        }
    }
}
