package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.she.omealjomeal.databinding.ActivityPlayBinding

class PlayActivity : AppCompatActivity() {
    private var _viewBinding: ActivityPlayBinding? = null
    private val viewBinding: ActivityPlayBinding get() = requireNotNull(_viewBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}