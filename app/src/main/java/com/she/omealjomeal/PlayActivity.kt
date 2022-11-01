package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.she.omealjomeal.databinding.ActivityPlayBinding
import kotlinx.android.synthetic.main.activity_play.view.*

class PlayActivity : AppCompatActivity() {
    private var _viewBinding: ActivityPlayBinding? = null
    private val viewBinding: ActivityPlayBinding get() = requireNotNull(_viewBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val intentSound = getIntent()
        val selectedSound = intentSound.getParcelableExtra<Sound>("sound")      // 리뷰목록(SoundList)에서 선택된 리뷰(Sound) 인스턴스가 selectedSound에 저장됨
        val selectedRestaurant = intentSound.getParcelableExtra<Restaurant>("restaurant")

/*        viewBinding.root.title_text_view.text = selectedSound?.title
        viewBinding.root.textView3.text = selectedRestaurant?.name
        viewBinding.root*/


        // 가게이름 눌렀을 때 가게정보 화면으로 이동
        val context = viewBinding.root.context
        viewBinding.root.textView3.setOnClickListener {
            Log.d("click", "textView3 clicked")
            val intentRestaurant = Intent(context, MapsActivity::class.java)
            intentRestaurant.putExtra("restaurant", selectedRestaurant)
            context.startActivity(intentRestaurant)
        }
    }
}