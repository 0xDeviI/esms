package com.arminapps.esms.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.arminapps.esms.views.main.fragments.ChatsFragment;
import com.arminapps.esms.views.main.fragments.ContactsFragment;
import com.arminapps.esms.views.main.fragments.SettingsFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ContactsFragment();
            case 2:
                return new SettingsFragment();
            default:
                return new ChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
