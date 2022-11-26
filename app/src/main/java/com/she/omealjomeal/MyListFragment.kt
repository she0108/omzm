package com.she.omealjomeal

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.she.omealjomeal.databinding.FragmentMyListBinding
import com.she.omealjomeal.databinding.FragmentRecordBinding


class MyListFragment : Fragment() {

    private lateinit var binding: FragmentMyListBinding
    private val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
    private val playlistRef = database.getReference("playlists")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyListBinding.inflate(inflater, container, false)

        var adapter = PlaylistRecyclerAdapter()

        playlistRef.child("idList").get().addOnSuccessListener {
            adapter.listPlaylistID = it.value.toString().split("/").toMutableList()
            Log.d("playlist", "${adapter.listPlaylistID}")
            binding.playlistRecyclerView2.adapter = adapter
            binding.playlistRecyclerView2.layoutManager = LinearLayoutManager(context)
        }.addOnFailureListener {
            Toast.makeText(context, "네크워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "error=${it.message}")
        }

        return binding.root
    }


}