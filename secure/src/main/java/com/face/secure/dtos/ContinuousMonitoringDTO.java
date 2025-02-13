package com.face.secure.dtos;

public class ContinuousMonitoringDTO {
    private int minutes;
    private int timeLimit;

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public ContinuousMonitoringDTO(int minutes, int timeLimit) {
        this.minutes = minutes;
        this.timeLimit = timeLimit;
    }

    public ContinuousMonitoringDTO() {
    }
}
