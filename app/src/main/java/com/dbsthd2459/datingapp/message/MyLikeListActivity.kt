package com.dbsthd2459.datingapp.message

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.dbsthd2459.datingapp.MainActivity
import com.dbsthd2459.datingapp.R
import com.dbsthd2459.datingapp.message.adapters.MatchViewPagerAdapter
import com.dbsthd2459.datingapp.message.tabs.BeLikedFragment
import com.dbsthd2459.datingapp.message.tabs.LikeFragment
import com.dbsthd2459.datingapp.mypage.MyPageActivity
import com.dbsthd2459.datingapp.utils.FirebaseAuthUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MyLikeListActivity : AppCompatActivity(), LikeFragment.FollowingCountListener, BeLikedFragment.FollowerCountListener {

    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        // 네비게이션 바 클릭 시 이벤트 설정
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_chat -> {
                    val intent = Intent(this, MyLikeListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_matching -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        tabLayout = findViewById(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // ViewPager2 어댑터 설정
        val adapter = MatchViewPagerAdapter(this)
        viewPager.adapter = adapter

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "채팅"
                1 -> tab.text = ""
                2 -> tab.text = ""
            }
        }.attach()

        // Firebase에서 가져온 데이터로 탭 업데이트
        updateTabCounts()

    }

    private fun updateTabCounts() {
        lifecycleScope.launch {
            val followingCount = async { FirebaseAuthUtils.getFollowingCount() }
            val followerCount = async { FirebaseAuthUtils.getFollowerCount() }

            tabLayout.getTabAt(1)?.text = "${followerCount.await()} 팔로워"
            tabLayout.getTabAt(2)?.text = "${followingCount.await()} 팔로잉"
        }
    }

    override fun onFollowerCountChanged(count: Int) {
        runOnUiThread {
            tabLayout.getTabAt(1)?.text = "$count 팔로워"
        }
    }

    override fun onFollowingCountChanged(count: Int) {
        runOnUiThread {
            tabLayout.getTabAt(2)?.text = "$count 팔로잉"
        }
    }

}