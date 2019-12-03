package com.example.moonwaygravity.Model;

public class Vehicle {
    public String vehicleLicensePlateNumber;
    public String customerId;
    public String name;

    public Vehicle(){

    }

    public Vehicle(String vehicleLicensePlateNumber, String customerId, String name) {
        this.vehicleLicensePlateNumber = vehicleLicensePlateNumber;
        this.customerId = customerId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vehicle(String vehicleLicensePlateNumber) {
        this.vehicleLicensePlateNumber = vehicleLicensePlateNumber;
    }

    public Vehicle(String vehicleLicensePlateNumber, String customerId) {
        this.vehicleLicensePlateNumber = vehicleLicensePlateNumber;
        this.customerId = customerId;
    }

    public String getVehicleLicensePlateNumber() {
        return vehicleLicensePlateNumber;
    }

    public void setVehicleLicensePlateNumber(String vehicleLicensePlateNumber) {
        this.vehicleLicensePlateNumber = vehicleLicensePlateNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
