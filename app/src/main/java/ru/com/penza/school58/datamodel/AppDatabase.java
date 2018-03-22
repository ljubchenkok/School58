package ru.com.penza.school58.datamodel;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Card.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CardDao cardDao();


}