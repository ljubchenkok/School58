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
    @ColumnInfo(name = "card")
    private String card;
    @ColumnInfo(name = "mainthreshold")
    private String mainThreshold;
    @ColumnInfo(name = "addthreshold")
    private String addThreshold;

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
        this.card = card;
        this.mainThreshold = mainThreshold;
        this.addThreshold = addThreshold;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCard(String card) {
        this.card = card;
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

    public String getCard() {
        return card;
    }

    public String getMainThreshold() {
        return mainThreshold;
    }

    public String getAddThreshold() {
        return addThreshold;
    }

}
