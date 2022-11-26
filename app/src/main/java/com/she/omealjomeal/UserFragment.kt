package com.she.omealjomeal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.she.omealjomeal.databinding.FragmentUserBinding


class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)

        SaveThings.userFragment = this

        val mypageFragment = MypageFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayoutUser, mypageFragment)
        transaction.commit()

        return binding.root
    }

    fun setMypageFragment() {
        val mypageFragment = MypageFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutUser, mypageFragment)
        transaction.commit()
    }

    fun setPlaylistFragment(playlistID: String) {
        val playlistFragment = PlaylistFragment()
        var bundle = Bundle()
        bundle.putString("playlist", playlistID)
        playlistFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayoutUser, playlistFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun setPlayFragment(soundID: String, restaurantID: String) {
        val playFragment = PlayFragment()
        var bundle = Bundle()
        bundle.putString("sound", soundID)
        bundle.putString("restaurant", restaurantID)
        playFragment.arguments = bundle
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayoutUser, playFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}