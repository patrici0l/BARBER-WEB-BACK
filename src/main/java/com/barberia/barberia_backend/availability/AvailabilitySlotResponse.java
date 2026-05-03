package com.barberia.barberia_backend.availability;

import java.time.LocalTime;

public class AvailabilitySlotResponse {

    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    public AvailabilitySlotResponse() {
    }

    public AvailabilitySlotResponse(LocalTime startTime, LocalTime endTime, boolean available) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}