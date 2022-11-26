package com.she.omealjomeal

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.she.omealjomeal.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.nav_view).setupWithNavController(navController)


    }

    private val selectRestaurantFragment = SelectRestaurantFragment()
    private val reviewFragment = ReviewFragment()


    // 리뷰작성하기 -> 가게선택하기
    fun setReviewFragment() {
        val reviewFragment = ReviewFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutPencil, reviewFragment)
        transaction.commit()
    }

    fun setRestaurantFragment() {
        val reviewFragment = ReviewFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutPencil, reviewFragment)
        transaction.commit()
    }

    fun selected() {
    }
}