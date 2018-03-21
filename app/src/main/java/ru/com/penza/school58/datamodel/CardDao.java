package ru.com.penza.school58.datamodel;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface CardDao {
    @Query("SELECT * FROM card")
    Maybe<List<Card>> getAll();

    @Query("SELECT * FROM card WHERE id IN (:cardIds)")
    Flowable<List<Card>> loadAllByIds(int[] cardIds);

    @Query("SELECT * FROM card WHERE name LIKE :name AND "
            + "card LIKE :card LIMIT 1")
    Maybe<Card> findByName(String name, String card);

    @Insert
    void insertAll(Card... cards);

    @Delete
    void delete(Card card);

    @Update
    public void updateCards(Card... cards);
}
