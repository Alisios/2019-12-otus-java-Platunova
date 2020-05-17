package ru.otus.backend.model;

import java.io.Serializable;
import java.util.Objects;

public class TicketModel {

    private String type;
    private String amount;
    private String cost;

    private int minCost;
    private int maxCost;

    public TicketModel(String type, String amount, String cost) {
        this.type = type;
        this.amount = amount;
        this.cost = cost;
    }


    @Override
    public String toString() {
        return type + ": " +
                cost;
//                +
//                ", количество: "+
//                + " шт.";
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public int getMinCost() {
        return minCost;
    }

    public void setMinCost(int minCost) {
        this.minCost = minCost;
    }

    public int getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(int maxCost) {
        this.maxCost = maxCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketModel that = (TicketModel) o;
        return minCost == that.minCost &&
                maxCost == that.maxCost &&
                Objects.equals(type, that.type) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(cost, that.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount, cost, minCost, maxCost);
    }
}
