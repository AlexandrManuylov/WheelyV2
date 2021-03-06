package org.chaynik.wheely;

import android.content.Intent;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.chaynik.wheely.preferences.Profile;
import org.chaynik.wheely.service.WebSocketService;
import org.chaynik.wheely.utils.WheelyUtils;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private String mSelectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (WheelyUtils.isValidProfile(Profile.getUserName()) && WheelyUtils.isValidProfile(Profile.getUserPassword())) {
            mSelectedTag = MapsFragment.TAG;

        } else {
            mSelectedTag = LoginFragment.TAG;
        }
        showFragmentByTag(mSelectedTag);
    }

    private void showFragmentByTag(String tag) {
        showFragmentByTag(tag, 0, 0);
    }

    public void showMapsFragment() {
        showFragmentByTag(MapsFragment.TAG, R.anim.anim_enter_right_to_left, R.anim.fade_out);
    }

    public void showLoginFragment() {
        showFragmentByTag(LoginFragment.TAG, R.anim.anim_enter_left_to_right, R.anim.fade_out);
    }

    private void showFragmentByTag(String tag, @AnimRes int enter, @AnimRes int exit) {
        String oldTag = mSelectedTag;
        mSelectedTag = tag;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final Fragment oldFragment = fm.findFragmentByTag(oldTag);
        final Fragment fragment = fm.findFragmentByTag(tag);
        if (enter != 0 || exit != 0) {
            ft.setCustomAnimations(enter, exit);
        }
        if (oldFragment != null && !tag.equals(oldTag)) {
            ft.detach(oldFragment);
        }

        if (fragment == null) {
            ft.replace(R.id.container, getContentFragment(tag), tag);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();

    }

    private Fragment getContentFragment(String tag) {
        Fragment fragment = null;
        if (LoginFragment.TAG.equals(tag)) {
            fragment = new LoginFragment();
        } else if (MapsFragment.TAG.equals(tag)) {
            fragment = new MapsFragment();
        }
        return fragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MapsFragment.REQUEST_LOCATION) {
            MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentByTag(MapsFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
