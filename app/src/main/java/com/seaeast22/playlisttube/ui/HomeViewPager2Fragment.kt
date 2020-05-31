package com.seaeast22.playlisttube.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.seaeast22.playlisttube.R
import com.seaeast22.playlisttube.adapter.PlaylistPageAdapter
import com.seaeast22.playlisttube.adapter.SAVED_PLAYLIST_PAGE_INDEX
import com.seaeast22.playlisttube.adapter.SEARCH_PLAYLIST_PAGE_INDEX
import com.seaeast22.playlisttube.databinding.FragmentHomeViewPager2Binding
import com.seaeast22.playlisttube.util.BlockableAppBarLayoutBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


/**
 * A simple [Fragment] subclass.
 * Use the [HomeViewPager2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeViewPager2Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHomeViewPager2Binding.inflate(inflater, container, false)
        val tabLayout = binding.tabs
        val viewPager = binding.viewPager2

        viewPager.adapter = PlaylistPageAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //pager.currentItem = tab.position
                when(tab.position) {
                    SEARCH_PLAYLIST_PAGE_INDEX -> {
                        binding.appBarLayout.setExpanded(true)
                        ((binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior as BlockableAppBarLayoutBehavior).isShouldScroll = true
                    }
                    SAVED_PLAYLIST_PAGE_INDEX -> {
                        binding.appBarLayout.setExpanded(false)
                        ((binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior as BlockableAppBarLayoutBehavior).isShouldScroll = false
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // 최초 실행시 스크롤시 검색바 사라지도록 하기 위해
        if (tabLayout.selectedTabPosition == SEARCH_PLAYLIST_PAGE_INDEX)
            ((binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams).behavior as BlockableAppBarLayoutBehavior).isShouldScroll = true


        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when(position) {
            SEARCH_PLAYLIST_PAGE_INDEX -> R.drawable.search_tab_selector
            SAVED_PLAYLIST_PAGE_INDEX -> R.drawable.saved_tab_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when(position) {
            SEARCH_PLAYLIST_PAGE_INDEX -> getString(R.string.search_tab_title)
            SAVED_PLAYLIST_PAGE_INDEX -> getString(R.string.saved_tab_title)
            else -> null
        }
    }

    private fun unlockAppBarExpand() {

    }

    companion object {
    }
}
