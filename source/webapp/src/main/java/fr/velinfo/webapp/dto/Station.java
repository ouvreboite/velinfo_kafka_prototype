package fr.velinfo.webapp.dto;

import lombok.Data;

@Data
public class Station {
    private String stationCode;
    private String stationName;
    private double longitude;
    private double latitude;
    private int totalCapacity;
    private int emptySlots;
    private int electricBikes;
    private int mechanicalBikes;
    private Long lastChangeTimestamp;
    private String status;
}
