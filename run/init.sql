CREATE TABLE station_hourly_statistics
(
	stationCode varchar(256) NOT NULL,
	periodStart TIMESTAMP NOT NULL,
	periodEnd TIMESTAMP NOT NULL,
	numberOfMechanicalBikesReturned INT NOT NULL,
	numberOfElectricBikesReturned INT NOT NULL,
	numberOfMechanicalBikesRented INT NOT NULL,
	numberOfElectricBikesRented INT NOT NULL,
	minimumNumberOfMechanicalBikes INT NOT NULL,
	minimumNumberOfElectricBikes INT NOT NULL,
	minimumNumberOfEmptySlots INT NOT NULL,
	lastNumberOfMechanicalBikes INT NOT NULL,
	lastNumberOfElectricBikes INT NOT NULL,
	lastLoadTimestamp TIMESTAMP NOT NULL,
	PRIMARY KEY (stationCode, periodStart, periodEnd)
);

CREATE TABLE station_daily_nearby_traffic
(
	stationCode varchar(256) NOT NULL,
	periodStart TIMESTAMP NOT NULL,
	periodEnd TIMESTAMP NOT NULL,
	totalTraffic INT NOT NULL,
	PRIMARY KEY (stationCode, periodStart, periodEnd)
);