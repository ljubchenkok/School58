package ru.com.penza.school58;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;

import java.util.ArrayList;
import 	java.util.regex.Pattern;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.com.penza.school58.datamodel.Card;
import ru.com.penza.school58.datamodel.DatabaseCallback;
import ru.com.penza.school58.datamodel.LocalCacheManager;
import ru.com.penza.school58.datamodel.Message;
import ru.com.penza.school58.views.MyRecycleViewAdapter;
import ru.com.penza.school58.web.ApiUtils;
import ru.com.penza.school58.web.SOService;


public class MainActivity extends AppCompatActivity implements DatabaseCallback {
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Unbinder unbinder;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    List<Card> cards;
    private MyRecycleViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LocalCacheManager.getInstance(this).getCards(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        Intent intent = new Intent(this, AddCardActivity.class);
        startActivityForResult(intent, Constants.ADD_CARD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_CARD) {
            if (resultCode == RESULT_OK) {
                Card card = new Card();
                card.setName(data.getStringExtra(Constants.KEY_NAME));
                card.setCard(data.getStringExtra(Constants.KEY_CARD));
                card.setMainThreshold(data.getStringExtra(Constants.KEY_MAIN_THRESHOLD));
                card.setAddThreshold(data.getStringExtra(Constants.KEY_ADD_THRESHOLD));
                LocalCacheManager.getInstance(this).addCard(this,card);
                LocalCacheManager.getInstance(this).findCardbyName(this,card.getName(),card.getCard());




            }
        }
    }

    @Override
    public void onCardsLoaded(final List<Card> cards) {
        this.cards = cards;
        intitRecyclerView();

    }

    ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            deleteCard(cards.get(position));
        }
    };

    private void deleteCard(Card card){
        LocalCacheManager.getInstance(this).deleteCard(this,card);
        int index = cards.indexOf(card);
        cards.remove(card);
        adapter.notifyItemRemoved(index);


    }



    private void intitRecyclerView() {
        adapter = new MyRecycleViewAdapter(cards, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCardDeleted() {

    }

    @Override
    public void onCardAdded() {


    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onCardUpdated() {

    }

    @Override
    public void onCardLoaded(Card card) {
        cards.add(card);
        adapter.setCards(cards);
        adapter.notifyItemInserted(cards.size()-1);

    }
}
