package com.she.omealjomeal

import android.content.Intent
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
import com.she.omealjomeal.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment() {
    lateinit var binding: FragmentPlaylistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        var adapter = SoundRecyclerAdapter()

        val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
        val playlistRef = database.getReference("playlists")

        playlistRef.child(arguments?.getString("playlist")?:"").get().addOnSuccessListener {
            it.getValue(Playlist::class.java)?.let { playlist ->
                adapter.listSoundID = playlist.soundIdList.split("/").toMutableList()
                setFragment(playlist)   //AlbumFragment
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "네크워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "error=${it.message}")
        }

        binding.soundRecyclerView.adapter = adapter
        binding.soundRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // 레이아웃 매니저: 리사이클러뷰를 화면에 보여주는 형태 결정


        return binding.root
    }

    fun setFragment(playlist: Playlist) {
        val albumFragment = AlbumFragment()
        var bundle = Bundle()
        bundle.putString("title", playlist.title)
        bundle.putString("userName", playlist.userName)
        bundle.putString("imagePath", playlist.imagePath)
        albumFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentView, albumFragment)
        transaction.commit()
    }
}