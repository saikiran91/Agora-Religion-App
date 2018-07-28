package app.ui.liveboradcaster

import android.Manifest
import android.content.pm.PackageManager
import android.databinding.ObservableArrayList
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import app.model.Question
import app.model.UserPrefs
import app.util.clearAndAddAll
import app.util.visible
import com.github.nitrico.lastadapter.LastAdapter
import com.google.firebase.firestore.FirebaseFirestore
import io.agora.AgoraAPIOnlySignal
import io.agora.religionapp.BR
import io.agora.religionapp.R
import io.agora.religionapp.databinding.ItemQuestionBinding
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_live_broadcaster.*
import kotlinx.android.synthetic.main.item_question.view.*
import timber.log.Timber
import java.io.File

class VideoChatViewActivity : AppCompatActivity(), VideoViewAdapter.VideoSelectedListener {

    val mSignaling by lazy { AgoraAPIOnlySignal.getInstance(this, getString(R.string.agora_app_id)); }
    private val userId = UserPrefs.userId.hashCode()
    private var mRtcEngine: RtcEngine? = null// Tutorial Step 1
    private val mRtcEventHandler = object : IRtcEngineEventHandler() { // Tutorial Step 1
        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) { // Tutorial Step 5
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onUserOffline(uid: Int, reason: Int) { // Tutorial Step 7
            runOnUiThread { onRemoteUserLeft(uid) }
        }

        override fun onUserMuteVideo(uid: Int, muted: Boolean) { // Tutorial Step 10
            runOnUiThread { onRemoteUserVideoMuted(uid, muted) }
        }

        override fun onActiveSpeaker(uid: Int) {
            super.onActiveSpeaker(uid)
            mVideoViewAdapter!!.showActiveSpeaker(uid)
        }

        override fun onStreamMessage(uid: Int, streamId: Int, data: ByteArray?) {
            super.onStreamMessage(uid, streamId, data)
            Timber.d("onStreamMessage Received")
        }

        override fun onStreamMessageError(uid: Int, streamId: Int, error: Int, missed: Int, cached: Int) {
            super.onStreamMessageError(uid, streamId, error, missed, cached)
            Timber.d("onStreamMessageError error = %s", error)
        }
    }

    private var mRoomId: String? = null
    private var mVideoViewAdapter: VideoViewAdapter? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_broadcaster)
        mRoomId = intent.getStringExtra(CHAT_ROOM_KEY)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mVideoViewAdapter = VideoViewAdapter(this)
        val mListView = findViewById<RecyclerView>(R.id.video_list)
        mListView.adapter = mVideoViewAdapter

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel()
        }


        listenToNewQuestion()
        question_list.adapter = lastAdapter

        initSignaling()


        expand_btn.setOnClickListener {
            questions.visible(!questions.isShown)
            switch_camera.visible(!switch_camera.isShown)
            mute.visible(!mute.isShown)
            mute_video.visible(!mute_video.isShown)
        }
    }

    private fun initSignaling() {
        mSignaling
        mSignaling.login2(getString(R.string.agora_app_id), UserPrefs.userId, "_no_need_token", 0, "", 5, 2)
    }

    override fun onVideoSelected(view: View) {
        Log.d(LOG_TAG, "onVideoSelected")

        //
        val listView = findViewById<RecyclerView>(R.id.video_list)
        val itemPosition = listView.getChildAdapterPosition(view)
        val agSurfaceView = mVideoViewAdapter!!.getItem(itemPosition)
        if (agSurfaceView == null || agSurfaceView.isSelected) return

        //
        agSurfaceView.isSelected = true
        mVideoViewAdapter!!.notifyItemChanged(itemPosition)

        //
        val largeContainer = findViewById<FrameLayout>(R.id.large_video_container)

        //Checking for previous surface view
        val childViewOfLargeContainer = largeContainer.getChildAt(0)
        if (childViewOfLargeContainer != null) {
            moveTheViewToVideoList(childViewOfLargeContainer)
            largeContainer.removeAllViews()
        }

        if (agSurfaceView.surfaceView != null) {
            val parent = agSurfaceView.surfaceView.parent as ViewGroup
            parent.removeAllViews()
            agSurfaceView.surfaceView.setZOrderMediaOverlay(false)
            largeContainer.addView(agSurfaceView.surfaceView)
        }
    }

    private fun moveTheViewToVideoList(childViewOfLargeContainer: View) {
        mVideoViewAdapter!!.putViewBack(childViewOfLargeContainer)
    }


    private fun initAgoraEngineAndJoinChannel() {

        initializeAgoraEngine()     // Tutorial Step 1
        setupVideoProfile()         // Tutorial Step 2
        setupLocalVideo()           // Tutorial Step 3
        joinChannel()               // Tutorial Step 4

        //            findViewById(R.id.mute).performClick();
        showLocalCameraInLargeView()

    }

    fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(LOG_TAG, "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(this,
                        permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(permission),
                    requestCode)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode)

        when (requestCode) {
            PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO)
                    finish()
                }
            }
            PERMISSION_REQ_ID_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel()
                } else {
                    showLongToast("No permission for " + Manifest.permission.CAMERA)
                    finish()
                }
            }
        }
    }

    fun showLongToast(msg: String) {
        this.runOnUiThread { Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show() }
    }

    // Tutorial Step 10
    fun onLocalVideoMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        mRtcEngine!!.muteLocalVideoStream(iv.isSelected)

        val container = findViewById<FrameLayout>(R.id.large_video_container)
        val surfaceView = container.getChildAt(0) as SurfaceView
        surfaceView.visibility = if (iv.isSelected) View.GONE else View.VISIBLE
    }

    // Tutorial Step 9
    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    // Tutorial Step 8
    fun onSwitchCameraClicked(view: View) {
        mRtcEngine!!.switchCamera()
    }

    // Tutorial Step 6
    fun onEncCallClicked(view: View) {
        leaveChannel()
        finish()
    }

    fun onNotesClicked(view: View) {
        question_list.visible(!question_list.isShown)
    }

    // Tutorial Step 1
    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, getString(R.string.agora_app_id), mRtcEventHandler)
            mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine!!.setLogFile(Environment.getExternalStorageDirectory().toString()
                    + File.separator + packageName + "/log/agora-rtc.log")
        } catch (e: Exception) {
            Timber.e(Log.getStackTraceString(e))
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }

    }

    // Tutorial Step 2
    private fun setupVideoProfile() {
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setVideoProfile(Constants.VIDEO_PROFILE_480P, false)
        mRtcEngine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
    }

    // Tutorial Step 3
    private fun setupLocalVideo() {
        val localSurfaceView = RtcEngine.CreateRendererView(baseContext)
        localSurfaceView.tag = 0 // for mark purpose
        mVideoViewAdapter!!.addView(localSurfaceView, 0)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, userId))

    }

    private fun showLocalCameraInLargeView() {
        Handler().postDelayed({
            if (!this@VideoChatViewActivity.isDestroyed) {
                try {
                    val listView = findViewById<RecyclerView>(R.id.video_list)
                    val localVideo = listView.layoutManager.findViewByPosition(0)
                    onVideoSelected(localVideo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }, 800)
    }


    // Tutorial Step 4
    private fun joinChannel() {
        mRtcEngine!!.joinChannel(null, mRoomId, null, userId) // if you do not specify the uid, we will generate the uid for you
    }


    // Tutorial Step 5
    fun setupRemoteVideo(uid: Int) {
        val remoteSurfaceView = RtcEngine.CreateRendererView(baseContext)
        remoteSurfaceView.tag = uid // for mark purpose
        val result = mVideoViewAdapter!!.addView(remoteSurfaceView, uid)
        if (result) {
            mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
        }
    }


    // Tutorial Step 6
    private fun leaveChannel() {
        mRtcEngine!!.leaveChannel()
    }

    // Tutorial Step 7
    fun onRemoteUserLeft(uid: Int) {
        mVideoViewAdapter!!.removeView(uid)
    }

    // Tutorial Step 10
    fun onRemoteUserVideoMuted(uid: Int, muted: Boolean) {
        mVideoViewAdapter!!.onUserVideoMuted(uid, muted)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (question_list.isShown) {
            question_list.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    fun hideQuestionList() {
        question_list.visibility = View.GONE
    }

    private val listOfQuestions = ObservableArrayList<Question>()

    val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    private fun initLastAdapter() = LastAdapter(listOfQuestions, BR.question)
            .map<Question, ItemQuestionBinding>(R.layout.item_question) {
                onBind {
                    val view = it.itemView
                    val question = it.binding.question!!
                    //Update actions according to isBroadcasting
                    view.answer_bt.setOnClickListener {
                        answerQuestion(question)
                        hideQuestionList()
                    }
                }
            }

    private fun answerQuestion(question: Question) {
        mSignaling.channelInviteUser2(mRoomId, question.userId, null)
    }


    private fun listenToNewQuestion() {
        Observable.create<List<Question>> { emitter ->
            FirebaseFirestore.getInstance().collection("question")
                    .whereEqualTo("eventId", mRoomId)
                    .addSnapshotListener { result, exception ->
                        when {
                            exception != null -> emitter.onError(exception)
                            result != null -> emitter.onNext(result.toObjects(Question::class.java))
                            else -> emitter.onError(Exception("listenToNewQuestion result is null or empty"))
                        }

                    }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            listOfQuestions.clearAndAddAll(it)
        }, {})
    }

    override fun onDestroy() {
        mRtcEngine?.leaveChannel()
        mSignaling.logout()
        super.onDestroy()
    }

    companion object {

        private val LOG_TAG = VideoChatViewActivity::class.java.simpleName
        const val CHAT_ROOM_KEY = "CHAT_ROOM_KEY"
        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1
    }
}
