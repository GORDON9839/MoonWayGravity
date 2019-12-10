package com.example.moonwaygravity.Model;

public class EntryRecords {
    public String authorizeDriverImage,date,licensePlateImage,status,time,vehicleLicensePlateNumber,parkingSlotNumber;
    public String carflowLocation, parkedTime, parkedDate;

    public String getCarflowLocation() {
        return carflowLocation;
    }

    public void setCarflowLocation(String carflowLocation) {
        this.carflowLocation = carflowLocation;
    }

    public String getParkedTime() {
        return parkedTime;
    }

    public void setParkedTime(String parkedTime) {
        this.parkedTime = parkedTime;
    }

    public String getParkedDate() {
        return parkedDate;
    }

    public void setParkedDate(String parkedDate) {
        this.parkedDate = parkedDate;
    }

    public EntryRecords() {
    }

    public String getParkingSlotNumber() {
        return parkingSlotNumber;
    }

    public void setParkingSlotNumber(String parkingSlotNumber) {
        this.parkingSlotNumber = parkingSlotNumber;
    }

    public String getAuthorizeDriverImage() {
        return authorizeDriverImage;
    }

    public void setAuthorizeDriverImage(String authorizeDriverImage) {
        this.authorizeDriverImage = authorizeDriverImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLicensePlateImage() {
        return licensePlateImage;
    }

    public void setLicensePlateImage(String licensePlateImage) {
        this.licensePlateImage = licensePlateImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVehicleLicensePlateNumber() {
        return vehicleLicensePlateNumber;
    }

    public void setVehicleLicensePlateNumber(String vehicleLicensePlateNumber) {
        this.vehicleLicensePlateNumber = vehicleLicensePlateNumber;
    }
}
