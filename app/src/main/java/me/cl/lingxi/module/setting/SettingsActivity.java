package me.cl.lingxi.module.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.databinding.SettingsActivityBinding;

/**
 * 设置
 * https://github.com/shellhub/blog/issues/29
 */
public class SettingsActivity extends BaseActivity {

    private SettingsActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        init();
    }

    private void init() {
        ToolbarUtil.init(mBinding.includeTb.toolbar, this)
                .setTitle(R.string.action_settings)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private static final String OPEN_TU_PICS = "open_tu_pics";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);
            initPreferences();
        }

        private void initPreferences() {
            Preference openTuPics = findPreference(OPEN_TU_PICS);
            openTuPics.setOnPreferenceChangeListener((preference, newValue) -> {
                SPUtil.build().putBoolean(OPEN_TU_PICS, (Boolean) newValue);
                return true;
            });
        }
    }

}
