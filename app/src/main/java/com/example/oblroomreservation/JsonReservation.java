package com.example.oblroomreservation;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

class JsonReservation implements Serializable {
    /**
     * id : 0
     * fromTime : 0
     * toTime : 0
     * userId : string
     * purpose : string
     * roomId : 0
     */

    private int id;
    private int fromTime;
    private int toTime;
    private String userId;
    private String purpose;
    private int roomId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    private String getFromTime() {
        Date date = new Date();
        date.setTime((long)fromTime*1000);
        return date.toString();
    }
    public void setFromTime(int fromTime) { this.fromTime = fromTime; }

    private String getToTime() {
        Date date = new Date();
        date.setTime((long)toTime*1000);
        return date.toString();
    }
    public void setToTime(int toTime) { this.toTime = toTime; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    @NotNull
    @Override
    public String toString() {
        return "From: " + getFromTime() + "\n" +
                "To: " + getToTime() + "\n" +
                "Reserved by: " + userId + "\n" +
                "Reserved for: " + purpose + "\n";
    }
}
