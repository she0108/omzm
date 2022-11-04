package com.she.omealjomeal

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class PlayButton(context: Context, attrs: AttributeSet): AppCompatImageButton(context, attrs) {
    init {
        setImageResource(R.drawable.play2)     //아이콘 여기에 입력
    }

    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.play2)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.pause)
            }
        }
    }
}

