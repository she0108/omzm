package com.she.omealjomeal

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.she.omealjomeal.databinding.FragmentMypageBinding
import com.she.omealjomeal.databinding.FragmentSelectRestaurantBinding

// 가게 선택하는 화면 (프래그먼트) - 리뷰 작성 화면이랑 연결해야 함

class SelectRestaurantFragment : Fragment() {
    private lateinit var binding: FragmentSelectRestaurantBinding
    private lateinit var mainActivity: MainActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectRestaurantBinding.inflate(inflater, container, false)

        var adapter = RestaurantRecyclerAdapter()
        adapter.mainActivity = context as MainActivity
        adapter.pencilFragment = parentFragment as PencilFragment

        //리사이클러뷰
        // database, restaurantRef 같은 건 액티비티에 해놓고 가져와서 쓸 것.
        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val restaurantRef = database.getReference("restaurants")
        lateinit var restaurantIdList: MutableList<String>
        restaurantRef.child("idList").get().addOnSuccessListener {
            adapter.listRestaurantID = it.value.toString().split("/").toMutableList()
            adapter.filteredList = adapter.listRestaurantID
            Log.d("select", "adapter.listRestaurantID -> ${adapter.listRestaurantID}")
            binding.restaurantRecyclerView.adapter = adapter
            binding.restaurantRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
            (parentFragment as PencilFragment).setReviewFragment()
        }

        return binding.root
    }

}