package ru.com.penza.school58;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.telephony.TelephonyManager;
import android.transition.Explode;
import android.support.v4.util.Pair;
//import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.com.penza.school58.datamodel.Card;
import ru.com.penza.school58.datamodel.DatabaseCallback;
import ru.com.penza.school58.datamodel.LocalCacheManager;
import ru.com.penza.school58.datamodel.Receiver;
import ru.com.penza.school58.views.MyRecycleViewAdapter;


public class MainActivity extends AppCompatActivity implements DatabaseCallback, MyRecycleViewAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 87;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    Unbinder unbinder;
    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;
    List<Card> cards;
    private MyRecycleViewAdapter adapter;
    DatabaseReference receiverRef;
    String token;
    protected static final int SETTINGS = 50;
    boolean useNotification;
    private String deviceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_label);
        unbinder = ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LocalCacheManager.getInstance(this).getCards(this);

        //testing
//        Card card = new Card("Влад", "35-000238", 0,0);
//        card.setId(100);
//        cards = new ArrayList<>();
//        cards.add(card);
//        intitRecyclerView();
        ////
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        useNotification = sharedPreferences.getBoolean(getString(R.string.key_notification), true);
        intitFirebase();


    }

    private void intitFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        receiverRef = database.getReference("receivers");
        if (useNotification) {
            setDeviceId();
            token = FirebaseInstanceId.getInstance().getToken();
        } else {
            if (deviceId!= null && !deviceId.isEmpty()) {
                receiverRef.child(deviceId).removeValue();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, MyPreferenceActivity.class);
            intent.putExtra(Constants.KEY_USE_NOTIFICATION, useNotification);
            startActivityForResult(intent, SETTINGS);
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
                Card card = getCardFromData(data);
                int position = data.getIntExtra(Constants.KEY_POSITION, -1);
                if (position == -1) {
                    LocalCacheManager.getInstance(this).addCard(this, card);
                    cards.add(card);
                    adapter.setCards(cards);
                    adapter.notifyItemInserted(cards.size() - 1);
                } else {
                    boolean cardDelete = data.getBooleanExtra(Constants.KEY_CARD_DELETE, false);
                    if (cardDelete) {
                        deleteCard(cards.get(position));
                    } else {
                        card.setId(cards.get(position).getId());
                        if (!cards.get(position).isEqual(card)) {
                            cards.set(position, card);
                            LocalCacheManager.getInstance(this).updateCard(this, card);
                            adapter.notifyItemChanged(position);
                        }
                    }

                }
            }
        }
        if (requestCode == SETTINGS) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            useNotification = sharedPreferences.getBoolean(getString(R.string.key_notification), false);
            if (!useNotification && deviceId != null && !deviceId.isEmpty()) {
                receiverRef.child(deviceId).removeValue();
            }
            if (useNotification) {
                setDeviceId();

            }

        }
    }

    private Card getCardFromData(Intent data) {
        Card card = new Card();
        card.setName(data.getStringExtra(Constants.KEY_NAME));
        card.setCardNumber(data.getStringExtra(Constants.KEY_CARD));
        card.setMainThreshold(data.getIntExtra(Constants.KEY_MAIN_THRESHOLD, 0));
        card.setAddThreshold(data.getIntExtra(Constants.KEY_ADD_THRESHOLD, 0));
        card.setColor(data.getIntExtra(Constants.KEY_COLOR, Color.WHITE));
        card.setImageURL(data.getStringExtra(Constants.KEY_IMAGE));
        return card;
    }

    private void sendDataToFirebase() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useNotification = sharedPreferences.getBoolean(getString(R.string.key_notification), false);
        if (useNotification && token != null && deviceId != null && !deviceId.isEmpty()) {
            Receiver receiver = new Receiver(token, cards);
            receiverRef.child(deviceId).setValue(receiver);
        }

    }


    @Override
    public void onCardsLoaded(final List<Card> cards) {
        this.cards = cards;
        swipeRefreshLayout.setOnRefreshListener(this);
        intitRecyclerView();
        sendDataToFirebase();

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

    private void deleteCard(Card card) {
        int index = cards.indexOf(card);
        LocalCacheManager.getInstance(this).deleteCard(this, card);
        cards.remove(card);
        adapter.notifyItemRemoved(index);
    }


    private void intitRecyclerView() {
        adapter = new MyRecycleViewAdapter(cards, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onCardDeleted() {
        if (useNotification) {
            if (deviceId !=null && !deviceId.isEmpty()){
                sendDataToFirebase();
            } else {
                setDeviceId();
            }


        }

    }

    @Override
    public void onCardAdded() {
        if (deviceId !=null && !deviceId.isEmpty()){
            sendDataToFirebase();
        } else {
            setDeviceId();
        }
    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onCardUpdated() {
        if (deviceId !=null && !deviceId.isEmpty()){
            sendDataToFirebase();
        } else {
            setDeviceId();
        }

    }

    @Override
    public void onCardLoaded(Card card) {


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onItemClick(Card card, final MyRecycleViewAdapter.CardViewHolder holder) {

        String transitionNameforPhoto = Constants.TRANSITION_PHOTO_NAME + String.valueOf(card.getId());
        String transitionNameforContainer = Constants.TRANSITION_CONTAINER_NAME + String.valueOf(card.getId());
        Pair<View, String> p1 = Pair.create((View) holder.imageView, transitionNameforPhoto);
        Pair<View, String> p2 = Pair.create((View) holder.imageContainer, transitionNameforContainer);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, p1);
        getWindow().setSharedElementEnterTransition(new Explode());
        getWindow().setSharedElementExitTransition(new Explode());
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra(Constants.KEY_ID, card.getId());
        intent.putExtra(Constants.KEY_NAME, card.getName());
        intent.putExtra(Constants.KEY_POSITION, cards.indexOf(card));
        intent.putExtra(Constants.KEY_CARD, card.getCardNumber());
        intent.putExtra(Constants.KEY_MAIN_THRESHOLD, card.getMainThreshold());
        intent.putExtra(Constants.KEY_ADD_THRESHOLD, card.getAddThreshold());
        intent.putExtra(Constants.KEY_COLOR, card.getColor());
        if (card.getImageURL() != null)
            intent.putExtra(Constants.KEY_IMAGE, card.getImageURL());
        startActivityForResult(intent, Constants.ADD_CARD, options.toBundle());
    }


    public void setDeviceId() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            final String tmDevice, tmSerial, androidId;
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(TELEPHONY_SERVICE);
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString();
            if (useNotification) {
                sendDataToFirebase();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               setDeviceId();
            } else {
                disableNotification();
                Toast.makeText(this,getString(R.string.key_notification_disabled),Toast.LENGTH_SHORT).show();


            }
        }
    }

    private void disableNotification() {
        useNotification = false;
        SharedPreferences  pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(getString(R.string.key_notification));
        editor.putBoolean(getString(R.string.key_notification),false);
        editor.commit();
    }

    @Override
    public void onRefresh() {
        for (int i = 0; i < cards.size(); i++) {
            MyRecycleViewAdapter.CardViewHolder holder = (MyRecycleViewAdapter.CardViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            adapter.refreshBallance(holder, i);
//            adapter.notifyItemChanged(i);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
