package ru.com.penza.school58;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.media.session.IMediaSession;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import yuku.ambilwarna.AmbilWarnaDialog;

public class AddCardActivity extends AppCompatActivity{

    private static final String FILE_NAME_BASE = "SchoolCard-";
    private final int CAMERA_RESULT = 57;
    private static final int RESULT_LOAD_IMG = 52;
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
    private int cardColor = Color.WHITE;
    @BindView(R.id.image_container)
    public RelativeLayout imageContainer;
    private String imageURL;


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
        imageURL = intent.getStringExtra(Constants.KEY_IMAGE);
        cardColor = intent.getIntExtra(Constants.KEY_COLOR, Color.WHITE);
        editName.setText(intent.getStringExtra(Constants.KEY_NAME));
        editCard.setText(intent.getStringExtra(Constants.KEY_CARD));
        showImage();
        initPreferences(intent);

    }

    private void showImage() {
        if (imageURL == null) {
            Picasso.with(this).load(R.drawable.student)
                    .transform(new CropCircleTransformation())
                    .into(imageView);
        } else {
            Picasso.with(this).load(Uri.parse(imageURL))
                    .transform(new CropCircleTransformation())
                    .into(imageView);
        }
        imageContainer.setBackgroundColor(cardColor);
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
        intent.putExtra(Constants.KEY_IMAGE, imageURL);
        intent.putExtra(Constants.KEY_COLOR, cardColor);
        setResult(RESULT_OK,intent);
        finish();
    }

    @OnClick(R.id.image)
    public void showImageDialog() {        
        if (!isCameraAvailable(this)) {
            changeImage();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.image_dialog_title));
        builder.setMessage(getString(R.string.image_dialog_message));
        builder.setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeImage();
            }
        });
        builder.setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createPhoto();

            }
        });
        builder.setNeutralButton(R.string.change_bkg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseColor();
            }
        });
        builder.show();
    }

    private void chooseColor() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.WHITE, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                cardColor= color;
                imageContainer.setBackgroundColor(color);
                showImage();

            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

        });
        dialog.show();
    }

    private void createPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = FILE_NAME_BASE + timeStamp + ".jpg";
        File file = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                fileName
        );
        Uri myPhotoUri = Uri.fromFile(file);
        imageURL = myPhotoUri.toString();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, myPhotoUri);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    private boolean isCameraAvailable(AddCardActivity addCardActivity) {
        final PackageManager packageManager = getPackageManager();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    private void changeImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMG) {
                imageURL = data.getData().toString();
                Picasso.with(this).load(data.getData()).transform(new CropCircleTransformation())
                        .into(imageView);
            }
            if (requestCode == CAMERA_RESULT) {
                Picasso.with(this).load(imageURL).transform(new CropCircleTransformation())
                        .into(imageView);

            }
        }
    }
}
