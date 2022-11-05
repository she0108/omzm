package com.she.omealjomeal

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.databinding.RestaurantRecyclerBinding
import kotlinx.android.synthetic.main.restaurant_recycler.view.*
import kotlinx.android.synthetic.main.sound_recycler.view.*

class RestaurantRecyclerAdapter: RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantRecyclerHolder>() {
    var listRestaurantID = mutableListOf<String>()

    // 화면에 보이는 아이템 레이아웃의 바인딩을 생성하는 역할
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantRecyclerHolder {
        val binding = RestaurantRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantRecyclerHolder(binding)
    }

    // 생성된 뷰홀더를 화면에 보여주는 & 아이템 레이아웃에 데이터를 출력하는 역할
    override fun onBindViewHolder(holder: RestaurantRecyclerHolder, position: Int) {
        val restaurantID = listRestaurantID.get(position)     // listSound에서 현재 위치에 해당하는 사운드를 하나 꺼내 sound 변수에 저장 후 홀더에 전달
        holder.setRestaurant(restaurantID)
    }

    // 목록의 개수를 알려주는 역할
    override fun getItemCount(): Int {
        return listRestaurantID.size
    }

    class RestaurantRecyclerHolder(val binding: RestaurantRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

        var context: Context
        var restaurantS_id: String = ""

        init {
            context = binding.root.context
            binding.root.setOnClickListener {
                // 선택한 restaurant id 인텐트로 액티비티에 전달
                val intentRestaurant = Intent(context, PostReview2::class.java)
                intentRestaurant.putExtra("restaurant", restaurantS_id)
                intentRestaurant.putExtra("from", "SelectRestaurant")
                context.startActivity(intentRestaurant)
            }
        }

        private val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
        private val restaurantRef = database.getReference("restaurants")
        private val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // 이거 전역변수로 할까?

        fun setRestaurant(restaurantID: String) {
            restaurantRef.child(restaurantID).get().addOnSuccessListener {
                it.getValue(Restaurant::class.java)?.let { restaurant ->    // Restaurant 클래스로 가져오는 거 해보고 안되면 일일이 String으로...
                    binding.textRestaurantName.text = restaurant.name
                    binding.textAddress.text = restaurant.address
                    downloadImage(restaurant.imagePath)
                    restaurantS_id = restaurant.id
                    Log.d("TAG", "restaurantS_id -> $restaurantS_id")
                }
            }.addOnFailureListener {
                Log.d("TAG", "error=${it.message}")
            }
        }

        fun downloadImage(path: String) {
            storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context).load(uri).into(binding.root.imageRestaurant)
            }.addOnFailureListener {
                Log.e("storage", "download error => ${it.message}")
            }
        }
    }
}

