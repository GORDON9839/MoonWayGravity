package com.example.moonwaygravity.Model;

public class Floor {
    private String id;
    private String blockid;
    private int totalSlot;
    private int flowNo;
    private String floorName;

    public Floor() {
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockid() {
        return blockid;
    }

    public void setBlockid(String blockid) {
        this.blockid = blockid;
    }

    public int getTotalSlot() {
        return totalSlot;
    }

    public void setTotalSlot(int totalSlot) {
        this.totalSlot = totalSlot;
    }

    public int getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(int flowNo) {
        this.flowNo = flowNo;
    }
}
