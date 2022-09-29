package com.she.omealjomeal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.she.omealjomeal.Sound
import com.she.omealjomeal.databinding.SoundRecyclerBinding

class SoundRecyclerAdapter: RecyclerView.Adapter<Holder>() {

    var listSound = mutableListOf<Sound>()

    // 화면에 보이는 아이템 레이아웃의 바인딩을 생성하는 역할
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = SoundRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    // 생성된 뷰홀더를 화면에 보여주는 & 아이템 레이아웃에 데이터를 출력하는 역할
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val sound = listSound.get(position)     // listSound에서 현재 위치에 해당하는 사운드를 하나 꺼내 sound 변수에 저장 후 홀더에 전달
        holder.setSound(sound)
    }

    // 목록의 개수를 알려주는 역할
    override fun getItemCount(): Int {
        return listSound.size
    }
}

class Holder(val binding: SoundRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
    
    init {
        binding.root.setOnClickListener {
            // 하단 재생바 표시 & 사운드 재생
        }
        
        binding.buttonPlay.setOnClickListener {
            // 해당 사운드의 재생화면으로 이동
        }
    }

    fun setSound(sound: Sound) {
        // <Sound> 타입으로 정의된 사운드에서 프로퍼티(음원파일, 사진, 제목, 가게명)들을 가져와서 화면에 띄움
        binding.run {
            textTitle.text = sound.title
            textRestaurant.text = sound.restaurantName
        }
    }
}