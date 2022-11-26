package com.she.omealjomeal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.she.omealjomeal.databinding.FragmentPencilBinding


class PencilFragment : Fragment() {
    lateinit var binding: FragmentPencilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPencilBinding.inflate(inflater, container, false)

        val reviewFragment = ReviewFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayoutPencil, reviewFragment)
        transaction.commit()

        return binding.root
    }


    fun setReviewFragment() {
        val reviewFragment = ReviewFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutPencil, reviewFragment)
        transaction.commit()
    }

    fun setRestaurantFragment() {
        val selectRestaurantFragment = SelectRestaurantFragment()
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutPencil, selectRestaurantFragment)
        transaction.commit()
    }
}