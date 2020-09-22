package fr.velinfo.webapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StationStat {
    private String stationCode;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private int mechanicalBikesReturned;
    private int electricBikesReturned;
    private int mechanicalBikesRented;
    private int electricBikesRented;
    private int minimumMechanicalBikes;
    private int minimumElectricBikes;
    private int minimumEmptySlots;
}