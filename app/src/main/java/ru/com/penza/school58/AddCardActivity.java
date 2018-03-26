package ru.com.penza.school58;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.media.session.IMediaSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import ru.com.penza.school58.datamodel.AppDatabase;
import ru.com.penza.school58.datamodel.Card;
import ru.com.penza.school58.datamodel.DatabaseCallback;
import ru.com.penza.school58.datamodel.LocalCacheManager;
import ru.com.penza.school58.views.MaskedEditText;

public class AddCardActivity extends AppCompatActivity{

    @BindView(R.id.name)
    EditText editName;
    @BindView(R.id.cardNumber)
    MaskedEditText editCard;
    @BindView(R.id.mainThreshold)
    EditText editMainThreshold;
    @BindView(R.id.addThreshold)
    EditText editAddThreshold;
    @BindView(R.id.labelMainThreshold)
    TextView labelMainThreshod;
    @BindView(R.id.labelAddThreshold)
    TextView labelAddThreshod;
    @BindView(R.id.image)
    ImageView imageView;
    Unbinder unbinder;
    private int position;
    boolean useNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        unbinder = ButterKnife.bind(this);
        Intent intent = getIntent();
        position = intent.getIntExtra(Constants.KEY_POSITION, -1);
        if (position != -1){
            setTitle(R.string.edit_label);
        }
        String imageURL = intent.getStringExtra(Constants.KEY_IMAGE);
        editName.setText(intent.getStringExtra(Constants.KEY_NAME));
        editCard.setText(intent.getStringExtra(Constants.KEY_CARD));
        showImage(imageURL, 0xffffff);
        initPreferences(intent);

    }

    private void showImage(String imageURL, int color) {
        if (imageURL == null) {
            Picasso.with(this).load(R.drawable.student)
                    .transform(new CropCircleTransformation())
                    .into(imageView);
        } else {
            Picasso.with(this).load(Uri.parse(imageURL))
                    .transform(new CropCircleTransformation())
                    .into(imageView);
        }
    }

    private void initPreferences(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        useNotification = sharedPreferences.getBoolean(getString(R.string.key_notification), false);
        if(!useNotification){
            editAddThreshold.setVisibility(View.INVISIBLE);
            editMainThreshold.setVisibility(View.INVISIBLE);
            labelAddThreshod.setVisibility(View.INVISIBLE);
            labelMainThreshod.setVisibility(View.INVISIBLE);
            editCard.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }else {
            editMainThreshold.setText(String.valueOf(intent.getIntExtra(Constants.KEY_MAIN_THRESHOLD,0)));
            editAddThreshold.setText(String.valueOf(intent.getIntExtra(Constants.KEY_ADD_THRESHOLD,0)));

        }
    }

    @OnClick(R.id.btnSave)
    public void onSave() {

        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_POSITION, position);
        intent.putExtra(Constants.KEY_NAME, editName.getText().toString());
        intent.putExtra(Constants.KEY_CARD, editCard.getText().toString());
        if(useNotification) {
            intent.putExtra(Constants.KEY_MAIN_THRESHOLD, Integer.parseInt(editMainThreshold.getText().toString()));
            intent.putExtra(Constants.KEY_ADD_THRESHOLD, Integer.parseInt(editAddThreshold.getText().toString()));
        }
        setResult(RESULT_OK,intent);
        finish();


    }
}
