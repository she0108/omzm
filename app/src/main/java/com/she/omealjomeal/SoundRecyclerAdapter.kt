package com.she.omealjomeal

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.she.omealjomeal.Sound
import com.she.omealjomeal.databinding.SoundRecyclerBinding
import kotlinx.android.synthetic.main.sound_recycler.view.*

class SoundRecyclerAdapter: RecyclerView.Adapter<SoundRecyclerHolder>() {
    var listSoundID = mutableListOf<String>()

    // 화면에 보이는 아이템 레이아웃의 바인딩을 생성하는 역할
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundRecyclerHolder {
        val binding = SoundRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundRecyclerHolder(binding)
    }

    // 생성된 뷰홀더를 화면에 보여주는 & 아이템 레이아웃에 데이터를 출력하는 역할
    override fun onBindViewHolder(holder: SoundRecyclerHolder, position: Int) {
        val soundID = listSoundID.get(position)     // listSound에서 현재 위치에 해당하는 사운드를 하나 꺼내 sound 변수에 저장 후 홀더에 전달
        holder.setSound(soundID)
    }

    // 목록의 개수를 알려주는 역할
    override fun getItemCount(): Int {
        return listSoundID.size
    }
}

class SoundRecyclerHolder(val binding: SoundRecyclerBinding): RecyclerView.ViewHolder(binding.root) {

    var context: Context
    var soundS: Sound? = null

    init {
        context = binding.root.context
        binding.root.setOnClickListener {
            val intentSound = Intent(context, SoundList::class.java)        // 재생화면으로 이동하도록 수정
            intentSound.putExtra("sound", soundS)
            context.startActivity(intentSound)   
        }
    }

    val database = Firebase.database("https://omzm-84564-default-rtdb.asia-southeast1.firebasedatabase.app/")   // (realtime database)
    val soundRef = database.getReference("sounds")
    val storage = Firebase.storage("gs://omzm-84564.appspot.com")   // (storage)

    fun setSound(soundID: String) {
        // soundID 받음. 이걸로 파이어베이스에서 sound 정보(제목, 가게이름, 리뷰, 이미지주소 등) 불러와서 레이아웃에 표시하기     *근데 이거 백그라운드 스레드에서 할 수 있나

        soundRef.child(soundID).get().addOnSuccessListener {
            it.getValue(Sound::class.java)?.let { sound ->
                binding.textTitle2.text = sound.title
                binding.textRestaurant.text = sound.restaurantName
                downloadImage(sound.imagePath)
                soundS = sound
            }
        }.addOnFailureListener {
            Log.d("TAG", "error=${it.message}")
        }
    }

    fun downloadImage(path: String) {
        storage.getReference(path).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context).load(uri).into(binding.root.imageSound)
        }.addOnFailureListener {
            Log.e("storage", "download error => ${it.message}")
        }
    }
}