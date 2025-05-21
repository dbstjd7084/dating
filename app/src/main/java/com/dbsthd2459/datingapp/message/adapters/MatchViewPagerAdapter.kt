package com.dbsthd2459.datingapp.message.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dbsthd2459.datingapp.message.tabs.BeLikedFragment
import com.dbsthd2459.datingapp.message.tabs.LikeFragment
import com.dbsthd2459.datingapp.message.tabs.MatchedFragment

class MatchViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3  // 탭 개수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MatchedFragment()     // 매칭된 상대 리스트
            1 -> BeLikedFragment()       // 내가 좋아하는 사람들
            2 -> LikeFragment()     // 나를 좋아하는 사람들
            else -> MatchedFragment()
        }
    }
}
