package com.she.omealjomeal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.she.omealjomeal.databinding.ActivitySelectRestaurantBinding

class SelectRestaurantActivity : AppCompatActivity() {

    val binding by lazy { ActivitySelectRestaurantBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //리사이클러뷰
        // database, restaurantRef 같은 건 액티비티에 해놓고 가져와서 쓸 것.
        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val restaurantRef = database.getReference("restaurants")
        lateinit var restaurantIdList: MutableList<String>
        restaurantRef.child("idList").get().addOnSuccessListener {
//            restaurantIdList = it.value.toString().split("/")   // 이거 mutable list로 바꿔야 함
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }

        var adapter = RestaurantRecyclerAdapter()
        adapter.listRestaurantID = restaurantIdList    // 어댑터의 listSound에 방금 불러온 data
        binding.restaurantRecyclerView.adapter = adapter
        binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(this) // 레이아웃 매니저: 리사이클러뷰를 화면에 보여주는 형태 결정

        // 선택 후 돌아가는 코드 -> Adapter에서 돌아가면 되니까 필요 없나...?
//        val returnIntent = Intent()
//        returnIntent.putExtra("returnValue", binding.editMessage.text.toString())
//        setResult(RESULT_OK, returnIntent)
//        finish()
    }
}