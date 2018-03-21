package ru.com.penza.school58.datamodel;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;




public class LocalCacheManager {
    private static final String DB_NAME = "database-name";
    private Context context;
    private static LocalCacheManager _instance;
    private AppDatabase db;

    public static LocalCacheManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new LocalCacheManager(context);
        }
        return _instance;
    }

    public LocalCacheManager(Context context) {
        this.context = context;
        db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
    }

    public void getCards(final DatabaseCallback databaseCallback) {
        db.cardDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Card>>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull List<Card> Cards) throws Exception {
                databaseCallback.onCardsLoaded(Cards);

            }
        });
    }

    public void addCard(final DatabaseCallback databaseCallback, final Card card) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.cardDao().insertAll(card);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
                databaseCallback.onCardAdded();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }

    public void deleteCard(final DatabaseCallback databaseCallback, final Card Card) {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.cardDao().delete(Card);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                databaseCallback.onCardDeleted();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }


    public void updateCard(final DatabaseCallback databaseCallback, final Card card) {
        card.setName("Max");
        card.setCard("11-2222222");
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                db.cardDao().updateCards(card);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                databaseCallback.onCardUpdated();
            }

            @Override
            public void onError(Throwable e) {
                databaseCallback.onDataNotAvailable();
            }
        });
    }
}
