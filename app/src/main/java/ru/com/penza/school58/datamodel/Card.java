package ru.com.penza.school58.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Card {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "cardNumber")
    private String cardNumber;
    @ColumnInfo(name = "mainthreshold")
    private String mainThreshold;
    @ColumnInfo(name = "addthreshold")
    private String addThreshold;
    @ColumnInfo(name = "message")
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {

        return message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Card() {
    }

    public Card(String name, String card, String mainThreshold, String addThreshold) {
        this.name = name;
        this.cardNumber = card;
        this.mainThreshold = mainThreshold;
        this.addThreshold = addThreshold;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setMainThreshold(String mainThreshold) {
        this.mainThreshold = mainThreshold;
    }

    public void setAddThreshold(String addThreshold) {
        this.addThreshold = addThreshold;
    }

    public String getName() {

        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMainThreshold() {
        return mainThreshold;
    }

    public String getAddThreshold() {
        return addThreshold;
    }

    public boolean isEqual(Card card){
        boolean result = card.getId() == id;
        result = result && card.getName().equals(name);
        result = result && card.getCardNumber().equals(cardNumber);
        return  result;
    }

}
