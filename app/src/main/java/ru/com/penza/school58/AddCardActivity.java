package ru.com.penza.school58;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.com.penza.school58.datamodel.AppDatabase;
import ru.com.penza.school58.datamodel.Card;
import ru.com.penza.school58.datamodel.DatabaseCallback;
import ru.com.penza.school58.datamodel.LocalCacheManager;
import ru.com.penza.school58.views.MaskedEditText;

public class AddCardActivity extends AppCompatActivity{

    @BindView(R.id.name)
    EditText editName;
    @BindView(R.id.card)
    MaskedEditText editCard;
    @BindView(R.id.mainThreshold)
    EditText editMainThreshold;
    @BindView(R.id.addThreshold)
    EditText editAddThreshold;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        unbinder = ButterKnife.bind(this);
    }

    @OnClick(R.id.btnSave)
    public void onSave() {
        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_NAME, editName.getText().toString());
        intent.putExtra(Constants.KEY_CARD, editCard.getText().toString());
        intent.putExtra(Constants.KEY_MAIN_THRESHOLD, editMainThreshold.getText().toString());
        intent.putExtra(Constants.KEY_ADD_THRESHOLD, editAddThreshold.getText().toString());
        setResult(RESULT_OK,intent);
        finish();


    }
}
