package ru.com.penza.school58.datamodel;

import java.util.List;

/**
 * Created by Константин on 21.03.2018.
 */

public interface DatabaseCallback {
    void onCardsLoaded(List<Card> cards);

    void onCardDeleted();

    void onCardAdded();

    void onDataNotAvailable();

    void onCardUpdated();
}
