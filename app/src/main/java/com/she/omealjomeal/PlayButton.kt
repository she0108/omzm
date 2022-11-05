package com.she.omealjomeal

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class PlayButton(context: Context, attrs: AttributeSet): AppCompatImageButton(context, attrs) {
    init {
        setImageResource(R.drawable.play2)     //아이콘 여기에 입력
    }

    fun updateIconWithState(state: State2) {
        when (state) {
            State2.PLAY -> {
                setImageResource(R.drawable.record)
            }
            State2.PAUSE -> {
                setImageResource(R.drawable.stop)
            }
        }
    }
}

