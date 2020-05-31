package com.seaeast22.playlisttube.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.seaeast22.playlisttube.ui.SavedPlaylistFragment
import com.seaeast22.playlisttube.ui.SearchedPlaylistsFragment

const val SEARCH_PLAYLIST_PAGE_INDEX = 0
const val SAVED_PLAYLIST_PAGE_INDEX = 1

class PlaylistPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        SEARCH_PLAYLIST_PAGE_INDEX to { SearchedPlaylistsFragment() },
        SAVED_PLAYLIST_PAGE_INDEX to { SavedPlaylistFragment() }
    )

    val stringPlus: (String, String) -> String = String::plus

    override fun getItemCount(): Int = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        // Function can be call by invoke() operator.
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}