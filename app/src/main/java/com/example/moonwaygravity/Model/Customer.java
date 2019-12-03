package com.example.moonwaygravity.Model;

public class Customer {
    private String id;
    private String name;
    private String contact;
    private String imageUrl;
    private String accountBalance;

    public Customer() {

    }

    public Customer(String id, String name, String contact, String imageUrl, String accountBalance) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.imageUrl = imageUrl;
        this.accountBalance = accountBalance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }
}
