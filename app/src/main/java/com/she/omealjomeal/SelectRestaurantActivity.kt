package com.she.omealjomeal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.she.omealjomeal.databinding.ActivitySelectRestaurant2Binding
import com.she.omealjomeal.databinding.ActivitySelectRestaurantBinding

class SelectRestaurantActivity : AppCompatActivity() {

    val binding by lazy { ActivitySelectRestaurantBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var adapter = RestaurantRecyclerAdapter()

        //리사이클러뷰
        // database, restaurantRef 같은 건 액티비티에 해놓고 가져와서 쓸 것.
        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val restaurantRef = database.getReference("restaurants")
        lateinit var restaurantIdList: MutableList<String>
        restaurantRef.child("idList").get().addOnSuccessListener {
            Log.d("select", "list -> ${it.value.toString().split("/").toMutableList()}")
            adapter.listRestaurantID = it.value.toString().split("/").toMutableList()
            adapter.filteredList = adapter.listRestaurantID
            Log.d("select", "adapter.listRestaurantID -> ${adapter.listRestaurantID}")
            binding.restaurantRecyclerView.adapter = adapter
            binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(this)
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }


        var searchViewTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                //텍스트 입력/수정시에 호출
                override fun onQueryTextChange(s: String): Boolean {
                    var num = adapter.getFilter(s)
                    binding.textView11.text = "검색결과 총 " + num.toString() + "건"
                    return false
                }
            }

        binding.searchView.setOnQueryTextListener(searchViewTextListener)

        binding.btnClose2.setOnClickListener {
            finish()
        }


                    // 선택 후 돌아가는 코드 -> Adapter에서 돌아가면 되니까 필요 없나...?
//        val returnIntent = Intent()
//        returnIntent.putExtra("returnValue", binding.editMessage.text.toString())
//        setResult(RESULT_OK, returnIntent)
//        finish()

        // 하단 탭 버튼 -> (마이페이지) 플레이리스트 화면으로
        binding.imageButton10.setOnClickListener {
            val intent = Intent(this, PlaylistList::class.java)
            intent.putExtra("from", "other")
            this.startActivity(intent)
        }
    }
}