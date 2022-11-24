package com.vigneshkumarapps.patient.videocall

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.vidyo.VidyoClient.Connector.Connector
import com.vidyo.VidyoClient.Connector.Connector.*
import com.vidyo.VidyoClient.Connector.ConnectorPkg
import com.vidyo.VidyoClient.Device.Device.DeviceState
import com.vidyo.VidyoClient.Device.LocalCamera
import com.vidyo.VidyoClient.Device.LocalMicrophone
import com.vidyo.VidyoClient.Device.LocalSpeaker
import com.vidyo.VidyoClient.Endpoint.LogRecord
import com.vidyo.VidyoClient.Endpoint.Participant
import com.vigneshkumarapps.patient.AppConstant
import com.vigneshkumarapps.patient.Models
import com.vigneshkumarapps.patient.R
import com.vigneshkumarapps.patient.Utils
import com.vigneshkumarapps.patient.Utils.displayToast
import com.vigneshkumarapps.patient.databinding.ActivityVideoCallBinding
import com.vigneshkumarapps.patient.videocall.event.ControlEvent
import com.vigneshkumarapps.patient.videocall.event.IControlEventHandler
import com.vigneshkumarapps.patient.videocall.utils.AppUtils
import com.vigneshkumarapps.patient.videocall.utils.FontsUtils
import com.vigneshkumarapps.patient.videocall.utils.Logger
import com.vigneshkumarapps.patient.videocall.view.ControlView
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class VideoCallActivity : FragmentActivity(),
    IConnect,
    IRegisterLocalCameraEventListener,
    IRegisterLocalMicrophoneEventListener,
    IRegisterLocalSpeakerEventListener,
    IRegisterLogEventListener,
    IRegisterParticipantEventListener,
    IControlEventHandler,
    View.OnLayoutChangeListener {

    var _binding: ActivityVideoCallBinding? = null

    val mBinding get() = _binding!!

    private var connector: Connector? = null

    private var lastSelectedLocalCamera: LocalCamera? = null

    private val isCameraDisabledForBackground = AtomicBoolean(false)

    private val isDisconnectAndQuit = AtomicBoolean(false)

    private var localCameraList: MutableList<LocalCamera> = mutableListOf()

    var roomPin = AppConstant.EMPTYSTRING

    var roomKey = AppConstant.EMPTYSTRING

    override fun onStart() {

        super.onStart()

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        if (connector != null) {

            val state: ControlView.State = mBinding.controlViewMain.controlView.getState()

            connector!!.setMode(ConnectorMode.VIDYO_CONNECTORMODE_Foreground)

            connector!!.setCameraPrivacy(state.isMuteCamera())

        }

        if (connector != null &&
            lastSelectedLocalCamera != null &&
            isCameraDisabledForBackground.getAndSet(false)
        ) {

            connector!!.selectLocalCamera(lastSelectedLocalCamera)

        }

    }

    override fun onStop() {

        super.onStop()

        if (connector != null) {

            connector!!.setMode(ConnectorMode.VIDYO_CONNECTORMODE_Background)

            connector!!.setCameraPrivacy(true)

        }
        if (!isFinishing &&
            connector != null &&
            !mBinding.controlViewMain.controlView.getState().isMuteCamera() &&
            !isCameraDisabledForBackground.getAndSet(true)
        ) {

            connector!!.selectLocalCamera(null)

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        _binding = ActivityVideoCallBinding.inflate(layoutInflater)

        setContentView(mBinding.root)

        ConnectorPkg.setApplicationUIContext(this)

        mBinding.progessBarView.progressBar.setVisibility(View.GONE)

        mBinding.controlViewMain.controlView.registerListener(this)

        if(intent.getBooleanExtra(ISNEWCALL,false))
            mBinding.joinView.visibility = View.VISIBLE
        else mBinding.joinView.visibility = View.GONE

        val logLevel = "warning debug@VidyoClient " +
                "all@LmiPortalSession  all@LmiPortalMembership info@LmiResourceManagerUpdates " +
                "info@LmiPace info@LmiIce all@LmiSignaling info@VidyoCameraEffect"

        connector = Connector(
            mBinding.videoView, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 8,
            logLevel, AppUtils.configLogFile(this), 0
        )
        //Logger.i("Connector instance has been created.")

        val fontsUtils = FontsUtils(this)

        connector!!.setFontFileName(fontsUtils.fontFile().getPath())

        mBinding.controlViewMain.controlView.showVersion(connector!!.getVersion())

        connector!!.registerLocalCameraEventListener(this)

        connector!!.registerLocalMicrophoneEventListener(this)

        connector!!.registerLocalSpeakerEventListener(this)

        connector!!.registerParticipantEventListener(this)

        connector!!.registerLogEventListener(this, logLevel)

        mBinding.videoView.addOnLayoutChangeListener(this)

        addOnClickListener()

    }

    private fun addOnClickListener(){

        mBinding.btnJoin.setOnClickListener {

            if(mBinding.roomKey.text.isEmpty()){

                displayToast(this,getString(R.string.alert_room_key_empty))

            }
            else {

                mBinding.joinView.visibility = View.GONE

                roomKey = mBinding.roomKey.text.toString().trim()

                roomPin = mBinding.roomPin.text.toString().trim()

            }
        }

    }

    override fun onLayoutChange(
        view: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {

        view.removeOnLayoutChangeListener(this)

        val width = view.width

        val height = view.height

        connector!!.showViewAt(view, 0, 0, width, height)

        Logger.i("Show View at: $width, $height")

    }

    override fun onConfigurationChanged(newConfig: Configuration) {

        super.onConfigurationChanged(newConfig)

        Handler().postDelayed({ updateView() }, DateUtils.SECOND_IN_MILLIS * 2)

    }

    override fun onSuccess() {

        runOnUiThread {

            Toast.makeText(
                this@VideoCallActivity, R.string.connected, Toast.LENGTH_SHORT
            ).show()

            mBinding.progessBarView.progressBar.visibility = View.GONE

            mBinding.controlViewMain.controlView.connectedCall(true)

            mBinding.controlViewMain.controlView.updateConnectionState(ControlView.ConnectionState.CONNECTED)

            mBinding.controlViewMain.controlView.disable(false)

        }

    }

    override fun onFailure(connectorFailReason: ConnectorFailReason) {

        Logger.i("onFailure: %s", connectorFailReason.name)

        runOnUiThread {

            Toast.makeText(
                this@VideoCallActivity,
                connectorFailReason.name,
                Toast.LENGTH_SHORT
            ).show()

            mBinding.progessBarView.progressBar.visibility = View.GONE

            mBinding.controlViewMain.controlView.connectedCall(false)

            mBinding.controlViewMain.controlView.updateConnectionState(ControlView.ConnectionState.FAILED)

            mBinding.controlViewMain.controlView.disable(false)

            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        }

    }

    override fun onDisconnected(connectorDisconnectReason: ConnectorDisconnectReason) {

        Logger.i("onDisconnected: %s", connectorDisconnectReason.name)

        runOnUiThread {

            Toast.makeText(
                this@VideoCallActivity, R.string.disconnected, Toast.LENGTH_SHORT
            ).show()

            mBinding.progessBarView.progressBar.visibility = View.GONE

            mBinding.controlViewMain.controlView.connectedCall(false)

            mBinding.controlViewMain.controlView.updateConnectionState(ControlView.ConnectionState.DISCONNECTED)

            mBinding.controlViewMain.controlView.disable(false)

            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            /* Wrap up the conference */
            if (isDisconnectAndQuit.get()) finish()

        }
    }


    override fun onBackPressed() {

        if (connector == null) {

            Logger.e("Connector is null!")

            finish()

            return

        }

        val state = connector!!.state

        if (state == ConnectorState.VIDYO_CONNECTORSTATE_Idle ||
            state == ConnectorState.VIDYO_CONNECTORSTATE_Ready
        ) {

            super.onBackPressed()

        } else {

            Toast.makeText(
                this,
                "You have to disconnect or await connection first",
                Toast.LENGTH_SHORT
            ).show()

            if (state == ConnectorState.VIDYO_CONNECTORSTATE_Connected &&
                !isDisconnectAndQuit.get()) {

                isDisconnectAndQuit.set(true)

                onControlEvent(ControlEvent(ControlEvent.Call.CONNECT_DISCONNECT, false))

            }

        }

    }

    override fun onDestroy() {

        super.onDestroy()

        mBinding.controlViewMain.controlView.unregisterListener()

        if (connector != null) {

            connector!!.hideView(mBinding.videoView)

            connector!!.disable()

            connector = null

        }
        ConnectorPkg.setApplicationUIContext(null)

        Logger.i("Connector instance has been released.")

    }


    override fun onLocalCameraAdded(localCamera: LocalCamera?) {

        if (localCamera != null) {

            Logger.i("onLocalCameraAdded: %s", localCamera.name)

            localCameraList.add(localCamera)

        }

    }

    override fun onLocalCameraSelected(localCamera: LocalCamera?) {

        if (localCamera != null) {

            Logger.i("onLocalCameraSelected: %s", localCamera.name)

            lastSelectedLocalCamera = localCamera

        }

    }

    override fun onLocalCameraStateUpdated(localCamera: LocalCamera?, deviceState: DeviceState?) {}

    override fun onLocalCameraRemoved(localCamera: LocalCamera?) {

        if (localCamera != null) {

            Logger.i("onLocalCameraRemoved: %s", localCamera.name)

        }

    }

    override fun onLog(logRecord: LogRecord?) {}

    private fun updateView() {

        val display = windowManager.defaultDisplay

        val size = Point()

        display.getSize(size)

        val width = size.x

        val height = size.y

        val videoViewParams = FrameLayout.LayoutParams(width, height)

        mBinding.videoView.layoutParams = videoViewParams

        mBinding.videoView.addOnLayoutChangeListener(this)

        mBinding.videoView.requestLayout()

    }

    override fun onLocalMicrophoneAdded(localMicrophone: LocalMicrophone?) {}

    override fun onLocalMicrophoneRemoved(localMicrophone: LocalMicrophone?) {}

    override fun onLocalMicrophoneSelected(localMicrophone: LocalMicrophone?) {}

    override fun onLocalMicrophoneStateUpdated(
        localMicrophone: LocalMicrophone?,
        deviceState: DeviceState?
    ) {
    }

    override fun onLocalSpeakerAdded(localSpeaker: LocalSpeaker?) {}

    override fun onLocalSpeakerRemoved(localSpeaker: LocalSpeaker?) {}

    override fun onLocalSpeakerSelected(localSpeaker: LocalSpeaker?) {}

    override fun onLocalSpeakerStateUpdated(
        localSpeaker: LocalSpeaker?,
        deviceState: DeviceState?
    ) {
    }

    override fun onParticipantJoined(participant: Participant) {

        Logger.i("Participant joined: %s", participant.getUserId())

    }

    override fun onParticipantLeft(participant: Participant) {

        Logger.i("Participant left: %s", participant.getUserId())

    }

    override fun onDynamicParticipantChanged(arrayList: ArrayList<Participant?>?) {}

    override fun onLoudestParticipantChanged(participant: Participant?, b: Boolean) {}

    override fun onControlEvent(event: ControlEvent<*>?) {

        if (connector == null) return

        when (event!!.call) {

            ControlEvent.Call.CONNECT_DISCONNECT -> {

                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                mBinding.progessBarView.progressBar.visibility = View.VISIBLE

                mBinding.controlViewMain.controlView.disable(true)

                val state = event.value as Boolean

                mBinding.controlViewMain.controlView.updateConnectionState(
                    if (state) ControlView.ConnectionState.CONNECTING
                    else ControlView.ConnectionState.DISCONNECTING
                )

                if (state) {

                    val portal = "instapract.platform.vidyo.io"

                    val room =  if (roomKey == AppConstant.EMPTYSTRING) "yiXPf6CTk3" else roomKey

                    val pin = roomPin

                    val name = Models.UserData!!.data.PatientProfile.display_name

                    Logger.i("Start connection: %s, %s, %s, %s", portal, room, pin, name)

                    connector!!.connectToRoomAsGuest(portal, name, room, pin, this)

                } else {

                    Logger.i("Disconnect initiated by user.")

                    if (connector != null) connector!!.disconnect()

                }

            }

            ControlEvent.Call.MUTE_CAMERA -> {

                val cameraPrivacy = event.value as Boolean

                connector!!.setCameraPrivacy(cameraPrivacy)

                if (cameraPrivacy) {

                    connector!!.selectLocalCamera(null)

                } else {

                    if (lastSelectedLocalCamera != null)
                        connector!!.selectLocalCamera(lastSelectedLocalCamera)
                    else connector!!.selectDefaultCamera()
                }

            }

            ControlEvent.Call.MUTE_MIC -> connector!!.setMicrophonePrivacy(event.value as Boolean)

            ControlEvent.Call.MUTE_SPEAKER -> connector!!.setSpeakerPrivacy(event.value as Boolean)

            ControlEvent.Call.CYCLE_CAMERA -> for (localCamera in localCameraList) if (localCamera.position != lastSelectedLocalCamera!!.position) {

                Logger.i("Going to select: %s local camera", localCamera.getName())

                connector!!.selectLocalCamera(localCamera)

                break

            }

            ControlEvent.Call.DEBUG_OPTION -> {

                val value = event.value as Boolean

                if (value) {

                    connector!!.enableDebug(7776, "")

                } else {

                    connector!!.disableDebug()

                }

                Toast.makeText(
                    this@VideoCallActivity,
                    getString(R.string.debug_option) + value,
                    Toast.LENGTH_SHORT
                ).show()
            }

            ControlEvent.Call.SEND_LOGS -> AppUtils.sendLogs(this)

            else -> {}
        }

    }

    companion object{

        private val ISNEWCALL = "NEWCALL"

        fun getIntent(context: Context,isNewCall : Boolean): Intent =
            Intent(context, VideoCallActivity::class.java).apply {
                putExtra(ISNEWCALL,isNewCall)
            }

    }
}