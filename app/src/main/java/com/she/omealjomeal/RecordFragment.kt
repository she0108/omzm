package com.she.omealjomeal

import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.service.voice.VoiceInteractionSession
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.she.omealjomeal.databinding.FragmentRecordBinding
import java.io.File


// 리뷰 작성에서 하단 녹음하는 부분


class RecordFragment : Fragment() {
//    var postReview2Activity: PostReview2? = null
    private lateinit var binding: FragmentRecordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecordBinding.inflate(inflater, container, false)   //프래그먼트 바인딩


        binding.btnRecord2.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> { // 녹음 시작.
                    requestAudioPermission()    // 권한 요청 띄우는 거 -> 수락하면 startRecording -> startCountup, state = ON_RECORDING
                }
                State.ON_RECORDING -> { // 녹음 완료
                    stopRecording()
                }
                State.AFTER_RECORDING -> { // 녹음한 거 재생
                    playRecordedFile()
                }
                State.ON_PLAYING -> { // 재생 일시정지
                    pauseRecordedFile()
                }
            }
        }

        binding.btnClear2.setOnClickListener {
            clearRecordedFile()
        }

        binding.btnCheck2.setOnClickListener {
            // check 버튼 누른 뒤에는 clear, check 버튼을 없애야 하는데 그럼 state 외에 다른 변수 하나 더 필요함.
            checkRecordedFile()
        }

        return binding.root
    }


    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingFilePath: String by lazy { "${requireContext().externalCacheDir?.absolutePath}/recording.3gp"}  //외부저장소-고유영역-캐시
    val externalCacheFile by lazy { File(requireContext().externalCacheDir, "recording.3gp") }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var check = Check.BEFORE_CHECK      // 녹음파일 확정 전/후
    private var state = State.BEFORE_RECORDING  // 녹음전/녹음중/녹음후/재생중
        set(value) {            // state 변화에 따라 실행할 코드들 여기에 작성
            field = value
            binding.btnRecord2.updateIconWithState(value)    //아이콘 모양 변경
            seekBarVisible(value == State.AFTER_RECORDING || value == State.ON_PLAYING)     // seekBar + time textViews
            sideButtonsVisible((value == State.AFTER_RECORDING || value == State.ON_PLAYING) && (check == Check.BEFORE_CHECK))  // clear button + check button
        }

    companion object { const val REQUEST_RECORD_AUDIO_PERMISSION = 201 }

    fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)    // deprecated???
        val audioRecordPermissionGranted = (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) && (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)
        if (!audioRecordPermissionGranted) {
            Toast.makeText(requireContext(), "녹음 권한을 승인해야 이 기능을 사용할 수 있습니다", Toast.LENGTH_LONG).show()
        }     // 권한 거부하면 앱 종료 -> Toast 메시지 띄운다거나 그런 걸로 수정할 것.
        else {
            startRecording()    // 권한 요청 -> 한번만 수락하면 오류 나는데 그냥 수락하니까 오류 안 남 (?)
        }
    }

    fun startRecording() {
        recorder = MediaRecorder(requireContext()).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)   // 포멧?
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)      // 엔코더
            setOutputFile(recordingFilePath)    //캐시에 저장?
            Log.d("Record", "${requireContext().externalCacheDir?.absolutePath}/recording.3gp") // 앞에가 파일 경로, recording.3gp가 파일명
            prepare()
        }
        recorder?.start()       // 녹음기 실행
        binding.textCountup.startCountup()       // 녹음시간 시작
        state = State.ON_RECORDING      // state 변경
    }

    fun stopRecording() {
        recorder?.run {
            stop()
            reset()
            release()
        }
        recorder = null
        binding.textCountup.stopCountup()    // 녹음 시간 저장해야 됨
        state = State.AFTER_RECORDING
        Log.d("Record", "외부저장소 캐시에 파일 존재 -> ${externalCacheFile.isFile}")
        PostReview2.saveThings.audioURI = Uri.fromFile(File(recordingFilePath))
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(requireContext().applicationContext, PostReview2.saveThings.audioURI)
            setWakeMode(requireContext().applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            prepareAsync()
            var preparedListener: MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener { player ->
                Log.d("Record", "OnPreparedListener called")
            }
            setOnPreparedListener(preparedListener)
        }
    }

    fun playRecordedFile() {    // prepare 완료되면 실행
        state = State.ON_PLAYING
        player?.start()
        Log.d("Record", "playRecordedFile() called, player.isPlaying -> ${player?.isPlaying}")
    }

    fun pauseRecordedFile() {
        state = State.AFTER_RECORDING
        player?.pause()
        Log.d("Record", "pauseRecordedFile() called, player.isPlaying -> ${player?.isPlaying}")
    }

    fun clearRecordedFile() {
        state = State.BEFORE_RECORDING
        externalCacheFile.delete()
        Log.d("Record", "clearRecordedFile() called, 외부저장소 캐시에 저장된 파일 삭제 -> 파일 존재 여부 ${externalCacheFile.isFile}")
        player?.stop()
        player?.release()
        player = null
        Log.d("Record", "player -> ${player}")
    }

    fun checkRecordedFile() {
        check = Check.AFTER_CHECK
        state = state
        Log.d("Record", "checkRecordedFile() called")
        PostReview2.saveThings.audioRecorded = true
    }

    fun seekBarVisible(flag: Boolean) {
        if (flag) {
            binding.seekBar2.visibility = VISIBLE
            binding.textViewStart.visibility = VISIBLE
            binding.textViewTotal.visibility = VISIBLE
        } else {
            binding.seekBar2.visibility = INVISIBLE
            binding.textViewStart.visibility = INVISIBLE
            binding.textViewTotal.visibility = INVISIBLE
        }
    }

    fun sideButtonsVisible(flag: Boolean) {
        if (flag) {
            binding.btnClear2.isEnabled = true
            binding.btnCheck2.isEnabled = true
            binding.btnClear2.visibility = VISIBLE
            binding.btnCheck2.visibility = VISIBLE
        } else {
            binding.btnClear2.isEnabled = false
            binding.btnCheck2.isEnabled = false
            binding.btnClear2.visibility = INVISIBLE
            binding.btnCheck2.visibility = INVISIBLE
        }
    }

}