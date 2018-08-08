package ru.com.penza.school58;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import id.zelory.compressor.Compressor;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import ru.com.penza.school58.views.MaskedEditText;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AddCardActivity extends AppCompatActivity {

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
    boolean useNotification;
    private int cardColor = Color.WHITE;
    @BindView(R.id.image_container)
    public RelativeLayout imageContainer;
    private String imageURL;
    private File compressedImage;
    protected int position = -1;
    private File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        postponeEnterTransition();
        unbinder = ButterKnife.bind(this);
        Intent intent = getIntent();
        position = intent.getIntExtra(Constants.KEY_POSITION, -1);
        if (position != -1) {
            setTitle(R.string.edit_label);
        }
        Integer id = intent.getIntExtra(Constants.KEY_ID, -1);
        imageURL = intent.getStringExtra(Constants.KEY_IMAGE);
        cardColor = intent.getIntExtra(Constants.KEY_COLOR, Color.WHITE);
        editName.setText(intent.getStringExtra(Constants.KEY_NAME));
        editCard.setText(intent.getStringExtra(Constants.KEY_CARD));
        if (id != -1) {
            String transitionNameforPhoto = Constants.TRANSITION_PHOTO_NAME + String.valueOf(id);
            String transitionNameforContainer = Constants.TRANSITION_CONTAINER_NAME + String.valueOf(id);
            imageView.setTransitionName(transitionNameforPhoto);
            imageContainer.setTransitionName(transitionNameforContainer);
        }
        imageContainer.setBackgroundColor(cardColor);
        showImage();
        initPreferences(intent);
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        scheduleStartPostponedTransition(imageView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (position == -1) {
            menu.removeItem(0);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showImage() {
        if (imageURL == null) {
            Picasso.with(this).load(R.drawable.student)
                    .transform(new CropCircleTransformation())
                    .into(imageView, myPicassoCallBack);
        } else {
            Picasso.with(this).load(new File(imageURL))
                    .transform(new CropCircleTransformation())
                    .into(imageView, myPicassoCallBack);
        }
    }

    private Callback myPicassoCallBack = new Callback() {
        @Override
        public void onSuccess() {
            scheduleStartPostponedTransition(imageView);
        }

        @Override
        public void onError() {

        }
    };

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }


    private void initPreferences(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        useNotification = sharedPreferences.getBoolean(getString(R.string.key_notification), false);
        if (!useNotification) {
            editAddThreshold.setVisibility(View.INVISIBLE);
            editMainThreshold.setVisibility(View.INVISIBLE);
            labelAddThreshod.setVisibility(View.INVISIBLE);
            labelMainThreshod.setVisibility(View.INVISIBLE);
            editCard.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else {
            int mainThreshold = intent.getIntExtra(Constants.KEY_MAIN_THRESHOLD, -1);
            int addThreshold = intent.getIntExtra(Constants.KEY_ADD_THRESHOLD, -1);
            if (mainThreshold > 0) {
                editMainThreshold.setText(String.valueOf(mainThreshold));
            }
            if (addThreshold > 0) {
                editAddThreshold.setText(String.valueOf(addThreshold));
            }


        }
    }

    @OnClick(R.id.btnSave)
    public void onSave() {

        Intent intent = new Intent();
        intent.putExtra(Constants.KEY_POSITION, position);
        intent.putExtra(Constants.KEY_NAME, editName.getText().toString());
        intent.putExtra(Constants.KEY_CARD, editCard.getText().toString());
        if (useNotification) {
            String strMainThreshold = editMainThreshold.getText().toString();
            if (strMainThreshold != null && !strMainThreshold.isEmpty()) {
                intent.putExtra(Constants.KEY_MAIN_THRESHOLD, Integer.parseInt(strMainThreshold));
            }
            String strAddThreshold = editAddThreshold.getText().toString();
            if (strAddThreshold != null && !strAddThreshold.isEmpty()) {
                intent.putExtra(Constants.KEY_ADD_THRESHOLD, Integer.parseInt(strAddThreshold));
            }
        }
        intent.putExtra(Constants.KEY_IMAGE, imageURL);
        intent.putExtra(Constants.KEY_COLOR, cardColor);
        setResult(RESULT_OK, intent);
        supportFinishAfterTransition();

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
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, cardColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                cardColor = color;
                showImage();
                imageContainer.setBackgroundColor(color);


            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
            }

        });
        dialog.show();
    }

    private void createPhoto() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {


            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ///Do something
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "ru.com.penza.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, CAMERA_RESULT);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageURL = image.getAbsolutePath();
        return image;
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
                setImage(data.getData());
            }
            if (requestCode == CAMERA_RESULT) {
                setImage(null);

            }
        }
    }

    private void setImage(Uri uri) {
        File actualImage = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            if (uri == null) {
//                actualImage = FileUtil.from(this, Uri.parse(imageURL));
                actualImage = photoFile;

            } else {
                actualImage = FileUtil.from(this, uri);
            }
            compressedImage = new Compressor(this).compressToFile(actualImage);
            imageURL = getFilesDir().getPath() + FILE_NAME_BASE + timeStamp + ".jpg";
            compressedImage.renameTo(new File(imageURL));
            File newfile = new File(imageURL);
            Picasso.with(this).load(newfile).transform(new CropCircleTransformation())
                    .into(imageView);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (position != -1) {
            getMenuInflater().inflate(R.menu.menu_add_card, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_delete) {
            Intent intent = new Intent();
            intent.putExtra(Constants.KEY_POSITION, position);
            intent.putExtra(Constants.KEY_NAME, editName.getText().toString());
            intent.putExtra(Constants.KEY_CARD, editCard.getText().toString());
            intent.putExtra(Constants.KEY_CARD_DELETE, true);
            setResult(RESULT_OK, intent);
            supportFinishAfterTransition();

        }
        return super.onOptionsItemSelected(item);
    }
}
