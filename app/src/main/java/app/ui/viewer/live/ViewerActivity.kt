package app.ui.viewer.live

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.ObservableArrayList
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import app.model.Chat
import app.model.UserPrefs
import app.ui.liveboradcaster.VideoChatViewActivity
import app.ui.liveboradcaster.VideoViewAdapter
import app.ui.viewer.askquestion.AskQuestionActivity
import app.util.*
import com.github.nitrico.lastadapter.LastAdapter
import com.google.gson.GsonBuilder
import io.agora.AgoraAPIOnlySignal
import io.agora.IAgoraAPI
import io.agora.religionapp.BR
import io.agora.religionapp.R
import io.agora.religionapp.databinding.ItemMessageBinding
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.android.synthetic.main.activity_live_viewer.*
import kotlinx.android.synthetic.main.item_message.view.*
import timber.log.Timber
import java.io.File
import java.util.*

class ViewerActivity : AppCompatActivity(), VideoViewAdapter.VideoSelectedListener {
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
            runOnUiThread {
                mVideoViewAdapter!!.showActiveSpeaker(uid)
            }
        }
    }
    private var mRoomId: String? = null
    private var mVideoViewAdapter: VideoViewAdapter? = null
    private val mGson = GsonBuilder().setPrettyPrinting().create()


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_viewer)
        mRoomId = intent.getStringExtra(CHAT_ROOM_KEY)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mVideoViewAdapter = VideoViewAdapter(this)
        video_list.adapter = mVideoViewAdapter

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.CAMERA, PERMISSION_REQ_ID_CAMERA)) {
            initAgoraEngineAndJoinChannel()
            initSignaling()
        }
        switchToViewerUI()
        mSignaling.channelJoin(mRoomId)

        send_btn.setOnClickListener {

            if (chat_et.getTrimmedText().isNotBlank()) {
                val id = UUID.randomUUID().toString();
                val msg = mGson.encode(Chat(id = id,
                        message = chat_et.getTrimmedText(),
                        userName = UserPrefs.name,
                        userId = UserPrefs.userId,
                        sendDate = Date().time,
                        eventId = mRoomId!!))
                mSignaling.messageChannelSend(mRoomId, msg, id)

                chat_et.text.clear()
                chat_et.clearFocus()
                chat_et.hideKeyboard()
            } else {
                showToastEvent("Cant send empty message")
            }
        }

        chat_list.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        chat_list.adapter = lastAdapter

        expand_btn.setOnClickListener {
            notes.visible(!notes.isShown)
            if (isBroadCaster) {
                switch_camera.visible(!switch_camera.isShown)
                mute.visible(!mute.isShown)
                mute_video.visible(!mute_video.isShown)
            }
        }
    }

    private fun initSignaling() {
        mSignaling
        mSignaling.login2(getString(R.string.agora_app_id), UserPrefs.userId, "_no_need_token", 0, "", 5, 2)
        initSignallingCallbacks()
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
//        setupLocalVideo();           // Tutorial Step 3
        joinChannel()               // Tutorial Step 4
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
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
        val intent = Intent(this, AskQuestionActivity::class.java)
        intent.putExtra(CHAT_ROOM_KEY, mRoomId)
        startActivity(intent)
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
        mRtcEngine!!.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
    }

    // Tutorial Step 3
    private fun setupLocalVideo() {
        val localSurfaceView = RtcEngine.CreateRendererView(baseContext)
        localSurfaceView.tag = 0 // for mark purpose
        mVideoViewAdapter!!.addView(localSurfaceView, userId)
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, userId))
    }

    private fun showInLargeView(remoteSurfaceView: SurfaceView) {
        Handler().postDelayed({
            if (!this@ViewerActivity.isDestroyed) {
                try {
                    val localVideo = video_list.layoutManager.findContainingItemView(remoteSurfaceView)
                    localVideo?.let { onVideoSelected(localVideo) }
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

        showInLargeView(remoteSurfaceView)
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

    private fun initSignallingCallbacks() {
        mSignaling.callbackSet(object : IAgoraAPI.ICallBack {
            override fun onChannelQueryUserIsIn(p0: String?, p1: String?, p2: Int) {

            }

            override fun onMsg(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteFailed(p0: String?, p1: String?, p2: Int, p3: Int, p4: String?) {

            }

            override fun onInviteMsg(p0: String?, p1: String?, p2: Int, p3: String?, p4: String?, p5: String?) {

            }

            override fun onLogout(p0: Int) {

            }

            override fun onChannelUserLeaved(p0: String?, p1: Int) {

            }

            override fun onChannelAttrUpdated(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

            override fun onLoginFailed(p0: Int) {

            }

            override fun onUserAttrResult(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInviteAcceptedByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onMessageSendError(p0: String?, p1: Int) {

            }

            override fun onUserAttrAllResult(p0: String?, p1: String?) {

            }

            override fun onReconnected(p0: Int) {

            }

            override fun onMessageAppReceived(p0: String?) {

            }

            override fun onBCCall_result(p0: String?, p1: String?, p2: String?) {

            }

            override fun onInvokeRet(p0: String?, p1: String?, p2: String?) {

            }

            override fun onChannelUserList(p0: Array<out String>?, p1: IntArray?) {

            }

            override fun onReconnecting(p0: Int) {

            }

            override fun onChannelJoinFailed(p0: String?, p1: Int) {

            }

            override fun onQueryUserStatusResult(p0: String?, p1: String?) {

            }

            override fun onMessageChannelReceive(channelID: String?, account: String?, uid: Int, msg: String?) {
                runOnUiThread {
                    msg?.let {
                        try {
                            val chatMsg = mGson.fromJson(it, Chat::class.java)
                            listOfChat.add(chatMsg)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            override fun onInviteReceived(channelID: String?, account: String?, uid: Int, extra: String?) {
                runOnUiThread {
                    this@ViewerActivity.showDialogWithAction("Guruji would like to join call with you",
                            "Call Request",
                            okText = "Accept",
                            cancelText = "Reject call",
                            onPositiveClick = {
                                mSignaling.channelInviteAccept(channelID, account, uid, extra)
                                onCallAccepted(channelID)
                            },
                            onNegativeClick = {
                                mSignaling.channelInviteRefuse(channelID, account, uid, extra)
                                onCallRejected(account)
                            })
                }
            }

            override fun onLog(p0: String?) {

            }

            override fun onInviteReceivedByPeer(p0: String?, p1: String?, p2: Int) {

            }

            override fun onDbg(p0: String?, p1: ByteArray?) {

            }

            override fun onInviteRefusedByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onInviteEndByPeer(p0: String?, p1: String?, p2: Int, p3: String?) {

            }

            override fun onChannelJoined(p0: String?) {

            }

            override fun onInviteEndByMyself(p0: String?, p1: String?, p2: Int) {

            }

            override fun onLoginSuccess(p0: Int, p1: Int) {

            }

            override fun onMessageSendSuccess(p0: String?) {

            }

            override fun onMessageInstantReceive(p0: String?, p1: Int, p2: String?) {

            }

            override fun onChannelUserJoined(p0: String?, p1: Int) {

            }

            override fun onChannelQueryUserNumResult(p0: String?, p1: Int, p2: Int) {

            }

            override fun onError(p0: String?, p1: Int, p2: String?) {

            }

            override fun onChannelLeaved(p0: String?, p1: Int) {

            }

            override fun onMessageSendProgress(p0: String?, p1: String?, p2: String?, p3: String?) {

            }

        })
    }

    private fun onCallRejected(account: String?) {
        showLongToast("Call Rejected")

    }

    private fun onCallAccepted(channelID: String?) {
        isBroadCaster = true
        showLongToast("Call Accepted")
        switchToBroadcasterUI()
        setupLocalVideo()
        mRtcEngine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)

    }

    var isBroadCaster = false

    private fun switchToBroadcasterUI(boolean: Boolean = true) {
        switch_camera.visible(boolean)
        mute.visible(boolean)
        mute_video.visible(boolean)
    }

    private fun switchToViewerUI() {
        switchToBroadcasterUI(false)
    }


    private val listOfChat = ObservableArrayList<Chat>()

    val lastAdapter: LastAdapter by lazy { initLastAdapter() }

    private fun initLastAdapter() = LastAdapter(listOfChat, BR.chat)
            .map<Chat, ItemMessageBinding>(R.layout.item_message) {
                onBind { item ->
                    item.itemView.time_tv.text = Date(item.binding.chat!!.sendDate).getSimpleTime()
                }
            }


    companion object {

        private val LOG_TAG = VideoChatViewActivity::class.java.simpleName
        const val CHAT_ROOM_KEY = "CHAT_ROOM_KEY"

        private const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
        private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1
    }

    override fun onDestroy() {
        mRtcEngine?.leaveChannel()
        mSignaling.logout()
        super.onDestroy()
    }

}
