package ru.com.penza.school58;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.squareup.picasso.Picasso;

/**
 * Created by Константин on 26.03.2018.
 */

public class MyPreferenceActivity extends PreferenceActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.action_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
//            if (getActivity().getIntent().getBooleanExtra(Constants.KEY_USE_NOTIFICATION, true)) {
//                findPreference(getString(R.string.key_notification)).setDefaultValue(true);
//            } else {
//                findPreference(getString(R.string.key_notification)).setDefaultValue(false);
//            }

        }
    }

}