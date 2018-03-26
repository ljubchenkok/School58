package ru.com.penza.school58.datamodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Intent;
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
    private Integer mainThreshold = 0;
    @ColumnInfo(name = "addthreshold")
    private Integer addThreshold = 0;
    @ColumnInfo(name = "message")
    private String message;
    @ColumnInfo(name = "color")
    private Integer color;
    @ColumnInfo(name = "imageURL")
    private String imageURL;

    public void setColor(Integer color) {
        this.color = color;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getColor() {

        return color;
    }

    public String getImageURL() {
        return imageURL;
    }

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

    public Integer getMainThreshold() {
        return mainThreshold;
    }

    public Integer getAddThreshold() {
        return addThreshold;
    }

    public void setMainThreshold(Integer mainThreshold) {

        this.mainThreshold = mainThreshold;
    }

    public void setAddThreshold(Integer addThreshold) {
        this.addThreshold = addThreshold;
    }

    public Card(String name, String cardNumber, Integer mainThreshold, Integer addThreshold) {

        this.name = name;
        this.cardNumber = cardNumber;
        this.mainThreshold = mainThreshold;
        this.addThreshold = addThreshold;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }


    public String getName() {

        return name;
    }

    public String getCardNumber() {
        return cardNumber;
    }


    public boolean isEqual(Card card) {
        boolean result = card.getId() == id;
        result = result && card.getName().equals(name);
        result = result && card.getCardNumber().equals(cardNumber);
        result = result && card.getMainThreshold() == mainThreshold;
        result = result && card.getAddThreshold() == addThreshold;
        return result;
    }

}
