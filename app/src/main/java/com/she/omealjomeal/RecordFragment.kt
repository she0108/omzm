package com.she.omealjomeal

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.service.voice.VoiceInteractionSession
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.she.omealjomeal.databinding.FragmentRecordBinding
import kotlinx.android.synthetic.main.fragment_record.*
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

        if (SaveThings.saveAudioFile?.isFile?:false) {  // 녹음 후
            state_ = State.AFTER_RECORDING
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                setDataSource(requireContext().applicationContext, Uri.fromFile(recordedFile))  // 재생에 필요한 uri
                setWakeMode(requireContext().applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
                prepareAsync()
                var preparedListener: MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener { player ->
                    Log.d("Record", "OnPreparedListener called")
                }
                setOnPreparedListener(preparedListener)
            }
        }

        if (PostReview2.saveThings.audioFile?.isFile()?:false) {  // 체크 후
            check = Check.AFTER_CHECK
            PostReview2.saveThings.audioRecorded = true
        }

        binding.btnRecord2.setOnClickListener {
            when (state_) {
                State.BEFORE_RECORDING -> { // 녹음 시작.
                    permissionLauncher_record.launch(permissions)    // 권한 요청 띄우는 거 -> 수락하면 startRecording -> startCountup, state = ON_RECORDING
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

            object : Thread() {
                var timeFormat = SimpleDateFormat("mm:ss")  //"분:초"를 나타낼 수 있도록 포멧팅
                override fun run() {
                    super.run()
                    if (player == null)
                        return
                    binding.seekBar2.max = player!!.duration  // player.duration : 음악 총 시간
                    while (player!!.isPlaying) {
                        run {
                            binding.seekBar2.progress = player!!.currentPosition
                            binding.root.post {
                                binding.textViewStart.text = timeFormat.format(player!!.currentPosition)
                                binding.textViewTotal.text = timeFormat.format(player!!.duration)
                            }
                        }
                        SystemClock.sleep(200)
                    }

                    //1. 음악이 종료되면 자동으로 초기상태로 전환
                    if(player!!.isPlaying){
                        player!!.stop()      //음악 정지
                        player!!.reset()
                        seekBar2.progress = 0
                        state_ = com.she.omealjomeal.State.AFTER_RECORDING
                    }
                }
            }.start()
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

    // 외부저장소 쓰는 권한은 필요없으면 뺄 것
    val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    val permissionLauncher_record = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultMap ->
        val isAllGranted = permissions.all { e -> resultMap[e] == true }

        if (isAllGranted) {
            startRecording()
        } else {
            Toast.makeText(requireContext(), "음성녹음 기능을 사용하려면 모든 권한을 승인해야 합니다.", Toast.LENGTH_LONG).show()
        }
    }

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingFilePath: String by lazy { "${requireContext().filesDir.absolutePath}/recording.3gp"}  //외부저장소-고유영역-캐시
    val recordedFile by lazy { File(requireContext().filesDir, "recording.3gp") }

    private var check = Check.BEFORE_CHECK      // 녹음파일 확정 전/후
    var state_ = State.BEFORE_RECORDING  // 녹음전/녹음중/녹음후/재생중
        set(value) {            // state 변화에 따라 실행할 코드들 여기에 작성
            field = value
            binding.btnRecord2.updateIconWithState(value)    //아이콘 모양 변경
            seekBarVisible(value == State.AFTER_RECORDING || value == State.ON_PLAYING)     // seekBar + time textViews
            sideButtonsVisible((value == State.AFTER_RECORDING || value == State.ON_PLAYING) && (check == Check.BEFORE_CHECK))  // clear button + check button


        }



/*    override fun onRequestPermissionsResult(
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
    }*/

    fun startRecording() {
        recordedFile.delete()
        recorder = MediaRecorder(requireContext()).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)   // 포멧?
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)      // 엔코더
            setOutputFile(recordingFilePath)
            Log.d("Record", "${requireContext().filesDir}/recording.3gp") // 앞에가 파일 경로, recording.3gp가 파일명
            prepare()
        }
        recorder?.start()       // 녹음기 실행
        binding.textCountup.startCountup()       // 녹음시간 시작
        state_ = State.ON_RECORDING      // state 변경
    }

    fun stopRecording() {
        recorder?.run {
            stop()
            reset()
            release()
        }
        recorder = null
        binding.textCountup.stopCountup()    // 녹음 시간 저장해야 됨
        state_ = State.AFTER_RECORDING
        Log.d("Record", "파일 존재 -> ${recordedFile.isFile}")
        SaveThings.saveAudioFile = recordedFile
        player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            setDataSource(recordingFilePath)
//            setDataSource(requireContext().applicationContext, Uri.fromFile(File(recordingFilePath)))  // 재생에 필요한 uri
            prepareAsync()
            var preparedListener: MediaPlayer.OnPreparedListener = MediaPlayer.OnPreparedListener { player ->
                Log.d("Record", "OnPreparedListener called")
            }
            setOnPreparedListener(preparedListener)
        }
    }

    fun playRecordedFile() {    // prepare 완료되면 실행
        state_ = State.ON_PLAYING
        player?.start()
        Log.d("Record", "playRecordedFile() called, player.isPlaying -> ${player?.isPlaying}")
    }

    fun pauseRecordedFile() {
        state_ = State.AFTER_RECORDING
        player?.pause()
        Log.d("Record", "pauseRecordedFile() called, player.isPlaying -> ${player?.isPlaying}")
    }

    fun clearRecordedFile() {
        state_ = State.BEFORE_RECORDING
        recordedFile.delete()
        Log.d("Record", "clearRecordedFile() called, 외부저장소 캐시에 저장된 파일 삭제 -> 파일 존재 여부 ${recordedFile.isFile}")
//        SaveThings.saveAudio = null
        player?.stop()
        player?.release()
        player = null
        Log.d("Record", "player -> ${player}")
    }

    fun checkRecordedFile() {
        check = Check.AFTER_CHECK
        state_ = state_
        Log.d("Record", "checkRecordedFile() called")
        PostReview2.saveThings.audioFile = recordedFile
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