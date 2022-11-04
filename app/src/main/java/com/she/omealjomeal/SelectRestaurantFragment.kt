package com.she.omealjomeal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.she.omealjomeal.databinding.FragmentSelectRestaurantBinding

// 가게 선택하는 화면 (프래그먼트) - 리뷰 작성 화면이랑 연결해야 함

class SelectRestaurantFragment : Fragment() {
    var postReviewActivity: PostReview? = null      // 리뷰작성화면 activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSelectRestaurantBinding.inflate(inflater, container, false)   //프래그먼트 바인딩

        //리사이클러뷰
        // database, restaurantRef 같은 건 액티비티에 해놓고 가져와서 쓸 것.
        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val restaurantRef = database.getReference("restaurants")
        lateinit var restaurantIdList: MutableList<String>
        restaurantRef.child("idList").get().addOnSuccessListener {
            val restaurantIdList = it.value.toString().split("/")
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }

        var adapter = RestaurantRecyclerAdapter()
        adapter.listRestaurantID = restaurantIdList    // 어댑터의 listSound에 방금 불러온 data
        binding.restaurantRecyclerView.adapter = adapter
        binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(postReviewActivity?.baseContext) // 레이아웃 매니저: 리사이클러뷰를 화면에 보여주는 형태 결정

        return binding.root
    }


}