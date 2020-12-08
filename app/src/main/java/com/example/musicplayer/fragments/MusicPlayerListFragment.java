package com.example.musicplayer.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.R;
import com.google.android.material.tabs.TabLayout;


public class MusicPlayerListFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private MusicPlayerFragment mFragmentTracks,mFragmentArtists,mFragmentAlbums;

    public MusicPlayerListFragment() {
        // Required empty public constructor
    }

    public static MusicPlayerListFragment newInstance() {
        MusicPlayerListFragment fragment = new MusicPlayerListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentTracks = MusicPlayerFragment.newInstance("Tracks");
        mFragmentArtists = MusicPlayerFragment.newInstance("Artists");
        mFragmentAlbums = MusicPlayerFragment.newInstance("Albums");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_music_player_list, container, false);
        findViews(view);
        updateView();
        initTab();

        return view;
    }

    private void updateView() {
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0){
                    mFragmentTracks.setUserVisibleHint(true);
                }
                else if (position == 1){
                    mFragmentArtists.setUserVisibleHint(true);
                }
                else {
                    mFragmentAlbums.setUserVisibleHint(true);
                }
            }
        });
    }

    private void findViews(View view) {
        mTabLayout = view.findViewById(R.id.tabs);
        mViewPager = view.findViewById(R.id.viewpager);
    }

    private void initTab() {

        addTabs();
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mViewPagerAdapter = new ViewPagerAdapter(getActivity(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTabLayout.setScrollPosition(position, 0f, true);
            }
        });


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void addTabs() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tracks));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.artists));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.albums));
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {
        int mNumOfTabs;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int numOfTabs) {
            super(fragmentActivity);
            mNumOfTabs = numOfTabs;
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return mFragmentTracks;
                case 1:
                    return mFragmentArtists;
                case 2:
                    return mFragmentAlbums;
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return mNumOfTabs;
        }


    }
}