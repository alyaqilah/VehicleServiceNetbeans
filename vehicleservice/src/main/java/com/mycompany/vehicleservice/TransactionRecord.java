package com.mycompany.vehicleservice;

public class TransactionRecord {
    private int id;
    private String customerName;
    private String vehicleReg;
    private String vehicleType;
    private String serviceType;
    private String addons;
    private String serviceDate;
    private double finalAmount;

    public TransactionRecord(int id, String customerName, String vehicleReg, String vehicleType, 
                             String serviceType, String addons, String serviceDate, double finalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.vehicleReg = vehicleReg;
        this.vehicleType = vehicleType;
        this.serviceType = serviceType;
        this.addons = addons;
        this.serviceDate = serviceDate;
        this.finalAmount = finalAmount;
    }

    public int getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getVehicleReg() { return vehicleReg; }
    public String getVehicleType() { return vehicleType; }
    public String getServiceType() { return serviceType; }
    public String getAddons() { return addons; }
    public String getServiceDate() { return serviceDate; }
    public double getFinalAmount() { return finalAmount; }
}