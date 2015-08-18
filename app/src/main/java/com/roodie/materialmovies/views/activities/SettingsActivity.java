package com.roodie.materialmovies.views.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.roodie.materialmovies.R;
import com.roodie.materialmovies.util.MMoviesPreferences;
import com.roodie.materialmovies.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Roodie on 13.07.2015.
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    private static final String KEY_CLEAR_CACHE = "com.roodie.materialmovies.clear_cache";

    public static final String KEY_THEME = "com.roodie.materialmovies.theme";

    private static final String KEY_ABOUT = "about";

    public static @StyleRes int THEME = R.style.Theme_MMovies_Light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(SettingsActivity.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlepane);
        setupActionBar();

        if (savedInstanceState == null) {
            Fragment fragment = new SettingsHeadersFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, fragment);
            ft.commit();
        }
    }


    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        // Because we use the platform fragment manager we need to pop fragments on our own
        if (!getFragmentManager().popBackStackImmediate()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchToSettings(String settingsId) {
        Bundle args = new Bundle();
        args.putString("settings", settingsId);
        Fragment f = new SettingsFragment();
        f.setArguments(args);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, f);
        ft.addToBackStack(null);
        ft.commit();
    }

    protected static void setupBasicSettings(final Activity activity, final Intent intent, Preference themePreference,
                                             Preference onlyWiFiPreference) {
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (KEY_THEME.equals(preference.getKey())) {
                    Utils.updateTheme((String) newValue);

                    TaskStackBuilder.create(activity)
                            .addNextIntent(new Intent(activity, MainActivity.class))
                            .addNextIntent(intent)
                            .startActivities();
                }
                return true;
            }
        });
        setListPreferenceSummary((ListPreference) themePreference);

        ((CheckBoxPreference)onlyWiFiPreference).setChecked(MMoviesPreferences.isLargeDataOverWifiOnly(activity.getApplicationContext()));
    }

    protected static void setupAdvancedSettings(final Context context, Preference clearCachPreference) {

        clearCachPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // try to open app info where user can clear app cache folders
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                return true;
            }
        });
    }

    public static void setListPreferenceSummary(ListPreference listPref) {
        // Set summary to be the user-description for the selected value
        listPref.setSummary(listPref.getEntry().toString().replaceAll("%", "%%"));
    }

    public static class SettingsHeadersFragment extends Fragment {
        private HeaderAdapter adapter;
        private ListView listView;

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_settings, container, false);

            listView = (ListView) v.findViewById(R.id.listViewSettingsHeaders);

            return v;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            adapter = new HeaderAdapter(getActivity(), buildHeaders());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Header item = adapter.getItem(position);
                    ((SettingsActivity) getActivity()).switchToSettings(item.settingsId);
                }
            });
        }

        private List<Header> buildHeaders() {
            List<Header> headers = new LinkedList<>();

            headers.add(new Header(R.string.prefs_category_basic, "basic"));
            headers.add(new Header(R.string.prefs_category_advanced, "advanced"));
            headers.add(new Header(R.string.prefs_category_about, "about"));

            return headers;
        }

        private static class HeaderAdapter extends ArrayAdapter<Header> {
            private final LayoutInflater mInflater;

            private static class HeaderViewHolder {
                TextView title;

                public HeaderViewHolder(View view) {
                    title = (TextView) view.findViewById(R.id.textViewSettingsHeader);
                }
            }

            public HeaderAdapter(Context context, List<Header> headers) {
                super(context, 0, headers);
                mInflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                HeaderViewHolder viewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_settings_header, parent, false);
                    viewHolder = new HeaderViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (HeaderViewHolder) convertView.getTag();
                }

                viewHolder.title.setText(getContext().getString(getItem(position).titleRes));

                return convertView;
            }
        }

        public static final class Header {
            public int titleRes;
            public String settingsId;

            public Header(int titleResId, String settingsId) {
                this.titleRes = titleResId;
                this.settingsId = settingsId;
            }
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String settings = getArguments().getString("settings");
            switch (settings) {
                case "basic":
                    addPreferencesFromResource(R.xml.settings_basic);
                    setupBasicSettings(getActivity(), getActivity().getIntent(),
                            findPreference(KEY_THEME),
                            findPreference(MMoviesPreferences.KEY_ONLYWIFI));
                    break;
                case "advanced":
                    addPreferencesFromResource(R.xml.settings_advanced);
                    setupAdvancedSettings(getActivity(),
                            findPreference(KEY_CLEAR_CACHE));
                    break;
                case "about":
                    addPreferencesFromResource(R.xml.settings_about);
                    break;

            }
        }

        @Override
        public void onStart() {
            super.onStart();
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop() {
            super.onStop();
            final SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);

            if (preference != null) {
            }

            if (KEY_THEME.equals(key)) {
                if (preference != null){
                    setListPreferenceSummary((ListPreference) preference);
                }
            }

            if (MMoviesPreferences.KEY_ONLYWIFI.equals(key)) {
                if (preference != null) {
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    MMoviesPreferences.seLargeDataOverWifiOnly(getActivity().getApplicationContext(), checkBoxPreference.isChecked());
                }
            }
        }
    }
}
