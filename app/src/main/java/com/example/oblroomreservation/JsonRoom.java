package com.example.oblroomreservation;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

class JsonRoom implements Serializable {

    /**
     * id : 0
     * name : string
     * description : string
     * capacity : 0
     * remarks : string
     */

    private int id;
    private String name;
    private String description;
    private int capacity;
    private String remarks;
    /**
     * fromTime : 0
     * toTime : 0
     * userId : string
     * purpose : string
     * roomId : 0
     */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @NotNull
    @Override
    public String toString() {
        return "Room name=" + name + "\n" +
                "Room type: " + description + "\n" +
                "Holds " + capacity + " people \n" +
                "remarks=" + remarks + "\n";
    }


}
