package edu.cmu.pocketsphinx.demo;

import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class TabFragmentListener<T extends Fragment> implements TabListener {
    private final Activity activity;
    private final String tag;
    private final Class<T> cls;
    private Fragment fragment;
    private Bundle state;

    public TabFragmentListener(Activity a, String t, Class<T> c, Bundle state) {
        activity = a;
        tag = t;
        cls = c;
        this.state = state;

        fragment = activity.getFragmentManager().findFragmentByTag(tag);
        if (fragment != null && !fragment.isDetached()) {
            FragmentManager fm = activity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.detach(fragment);
            ft.commit();
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, cls.getName(), state);
            ft.add(android.R.id.content, fragment, tag);
        } else {
            ft.attach(fragment);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (fragment != null)
            ft.detach(fragment);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
}