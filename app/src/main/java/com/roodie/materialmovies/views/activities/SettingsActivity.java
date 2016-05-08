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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.roodie.materialmovies.MMoviesApp;
import com.roodie.materialmovies.R;
import com.roodie.materialmovies.mvp.presenters.SettingsPresenter;
import com.roodie.materialmovies.mvp.views.SettingsView;
import com.roodie.materialmovies.util.AboutUtils;
import com.roodie.materialmovies.util.MMoviesPreferences;
import com.roodie.materialmovies.util.Utils;
import com.roodie.model.Display;
import com.roodie.model.util.StringFetcher;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Roodie on 13.07.2015.
 */
public class SettingsActivity extends BaseNavigationActivity implements SettingsView {

    @InjectPresenter
    SettingsPresenter mPresenter;

    @Inject
    StringFetcher mStringFetcher;

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    private static final String KEY_CLEAR_CACHE = "com.roodie.materialmovies.clear_cache";

    private static final String KEY_CLEAR_DATABASE = "com.roodie.materialmovies.local_database";

    public static final String KEY_THEME = "com.roodie.materialmovies.theme";

    public static final String KEY_LICENSES = "com.roodie.materialmovies.licenses";

    public static final String KEY_VERSION = "com.roodie.materialmovies.build_version";

    public static @StyleRes int THEME;

    public static void setTheme(Context context) {
        int theme = MMoviesPreferences.getApplicationTheme(context);
        switch (theme) {
            case 0 :
                THEME = R.style.Theme_MMovies_Light;
                break;
            case 1 :
                THEME = R.style.Theme_MMovies_Dark;
                break;
            case 2 :
                THEME = R.style.Theme_MMovies_Green;
                break;
            }
        }


    public static boolean hasTheme() {
        return THEME != 0;
    }

    @Override
    protected void handleIntent(Intent intent, Display display) {

    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_singlepane;
    }

    @Override
    public void onWatchedCleared() {
        Toast.makeText(this, getResources().getString(R.string.action_cleared_watched), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        if ( getDisplay() != null) {
            getDisplay().showUpNavigation(true);
            //TODO
            getDisplay().setActionBarTitle(MMoviesApp.get().getStringFetcher().getString(R.string.settings_title));
        }

        if (savedInstanceState == null) {
            Fragment fragment = new SettingsHeadersFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, fragment);
            ft.commit();
        }
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

    protected void setupBasicSettings(final Activity activity, final Intent intent, Preference themePreference,
                                             Preference onlyWiFiPreference, Preference animationsPreference) {
        themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (KEY_THEME.equals(preference.getKey())) {
                    Utils.updateTheme(activity.getApplicationContext(), (String) newValue);

                    TaskStackBuilder.create(activity)
                            .addNextIntent(new Intent(activity, WatchlistActivity.class))
                            .addNextIntent(intent)
                            .startActivities();
                }
                return true;
            }
        });
        setListPreferenceSummary((ListPreference) themePreference);

        ((CheckBoxPreference)onlyWiFiPreference).setChecked(MMoviesPreferences.isLargeDataOverWifiOnly(activity.getApplicationContext()));

        ((CheckBoxPreference)animationsPreference).setChecked(MMoviesPreferences.areAnimationsEnabled(activity.getApplicationContext()));
    }

    protected void setupAdvancedSettings(final Context context, Preference clearCachePreference, Preference clearDatabasePreference) {

        clearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Display display = getDisplay();
                if (display != null) {
                    // try to open app info where user can clear app cache folders
                    Intent intent = new Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    if (!display.tryStartActivity(intent, false)) {
                        // try to open all apps view if detail view not available
                        intent = new Intent(
                                android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        display.tryStartActivity(intent, true);
                    }
                }
                return true;
            }
        });

        clearDatabasePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mPresenter != null) {
                    mPresenter.clearWatched(SettingsActivity.this);
                }
                return true;
            }
        });
    }

    protected void setupAboutSettings(final Context context, Preference licensesPreference, Preference buildVersionPreference) {

        licensesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AboutUtils.showOpenSourceLicensesDialog(context);
                return true;
            }
        });

        buildVersionPreference.setSummary(Utils.getVersion(context));
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
            headers.add(new Header(R.string.prefs_category_cache, "cache"));
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

        private SettingsActivity mActivity;

        @Override
        public void onAttach(Activity activity) {
            if (activity instanceof SettingsActivity)
            {
                mActivity = (SettingsActivity) activity;
            }
            super.onAttach(activity);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String settings = getArguments().getString("settings");
            assert settings != null;
            switch (settings) {
                case "basic":
                    addPreferencesFromResource(R.xml.settings_basic);
                    mActivity.setupBasicSettings(getActivity(), getActivity().getIntent(),
                            findPreference(KEY_THEME),
                            findPreference(MMoviesPreferences.KEY_ONLYWIFI),
                            findPreference(MMoviesPreferences.KEY_ANIMATIONS));
                    break;
                case "cache":
                    addPreferencesFromResource(R.xml.settings_advanced);
                    mActivity.setupAdvancedSettings(getActivity(),
                            findPreference(KEY_CLEAR_CACHE), findPreference(KEY_CLEAR_DATABASE));
                    break;
                case "about":
                    addPreferencesFromResource(R.xml.settings_about);
                    mActivity.setupAboutSettings(getActivity(), findPreference(KEY_LICENSES), findPreference(KEY_VERSION));
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

            if (MMoviesPreferences.KEY_ANIMATIONS.equals(key)) {
                if (preference != null) {
                    CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                    MMoviesPreferences.setAnimationsEnabled(getActivity().getApplicationContext(), checkBoxPreference.isChecked());
                }
            }
        }
    }
}
