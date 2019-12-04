package com.example.moonwaygravity.Model;

public class Block {
    private String id;
    private String blockName;
    private int floorNo;
    private String parkingLotid;

    public Block() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public String getParkingLotid() {
        return parkingLotid;
    }

    public void setParkingLotid(String parkingLotid) {
        this.parkingLotid = parkingLotid;
    }
}
