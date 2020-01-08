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


class MainActivity : AppCompatActivity() {

    companion object{
        var pinpointManager: PinpointManager? = null

        private fun getPinpointManager( applicationContext: Context): PinpointManager?{
            if (pinpointManager == null){
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

                pinpointManager = PinpointManager(pinpointConfig)
            }
            return pinpointManager
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onStart() {
        super.onStart()
        val pinpointManager = getPinpointManager(applicationContext)
        pinpointManager?.let {
            it.sessionClient.startSession()
        }
    }

    override fun onStop() {
        pinpointManager?.let {
            it.sessionClient.stopSession()
            Log.i("onStop", "EVENTS SUBMITTED")
            it.analyticsClient.submitEvents()
        }
        super.onStop()
    }


//    override fun onDestroy() {
//        pinpointManager?.let {
//            it.sessionClient.stopSession()
//            Log.i("onDestroy", "EVENTS SUBMITTED")
//            it.analyticsClient.submitEvents()
//        }
//        super.onDestroy()
//
//    }
}
