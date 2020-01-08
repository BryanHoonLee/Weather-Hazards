package com.example.weatherapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager
import java.lang.Exception

object Pinpoint{
    var pinpointManager: PinpointManager? = null

    /** Sends Event Log for Funnel Analytics to Pinpoint */
    fun logFunnel(eventType: String) {
        Log.i("Funnel", "$eventType")
        val event = pinpointManager?.let {
            it.analyticsClient.createEvent("Funnel")
                .withAttribute("Screen", "$eventType")
        }
        pinpointManager?.analyticsClient?.recordEvent(event)
    }
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPinpointManager(applicationContext)
    }

    private fun initPinpointManager(applicationContext: Context): PinpointManager?{
        if (Pinpoint.pinpointManager == null){
            val awsConfig = AWSConfiguration(applicationContext)

            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, object: Callback<UserStateDetails>{
                override fun onResult(result: UserStateDetails?) {
                    Log.i("INIT", result?.getUserState().toString())
                }

                override fun onError(e: Exception?) {
                    Log.e("INIT", "Initialization error.", e)
                }
            })

            var pinpointConfig = PinpointConfiguration(
                applicationContext,
                AWSMobileClient.getInstance(),
                awsConfig
            )

            Pinpoint.pinpointManager = PinpointManager(pinpointConfig)
        }
        return Pinpoint.pinpointManager
    }

    override fun onStart() {
        super.onStart()
        Pinpoint.pinpointManager?.let {
            it.sessionClient.startSession()
        }
    }

    override fun onStop() {
        Pinpoint.pinpointManager?.let {
            it.sessionClient.stopSession()
            it.analyticsClient.submitEvents()
        }
        super.onStop()
    }
}
