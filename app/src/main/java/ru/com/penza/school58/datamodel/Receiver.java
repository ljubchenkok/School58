package ru.com.penza.school58.datamodel;

import java.util.List;

/**
 * Created by Константин on 26.03.2018.
 */

public class Receiver {
    private String token;
    private List<Card> cards;

    public void setToken(String token) {
        this.token = token;
    }

    public Receiver() {
    }

    public String getToken() {

        return token;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {

        return cards;
    }

    public Receiver(String token, List<Card> cards) {

        this.token = token;
        this.cards = cards;
    }
}
