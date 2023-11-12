package com.dooo.android.list;

public class SubscriptionPlanList {
    private int ID;
    private String Name;
    private int Time;
    private int Amount;
    private int Currency;
    private String Background;
    private int Status;

    public SubscriptionPlanList(int ID, String name, int time, int amount, int currency, String background, int status) {
        this.ID = ID;
        Name = name;
        Time = time;
        Amount = amount;
        Currency = currency;
        Background = background;
        Status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public int getCurrency() {
        return Currency;
    }

    public void setCurrency(int currency) {
        Currency = currency;
    }

    public String getBackground() {
        return Background;
    }

    public void setBackground(String background) {
        Background = background;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
