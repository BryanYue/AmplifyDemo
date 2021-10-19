package com.example.amplifydemo.app

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin

class App : MultiDexApplication(){


    override fun onCreate() {
        super.onCreate()

        initAmplify()
    }


    fun initAmplify(){
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())

            Amplify.configure(applicationContext)
            Log.e("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }
}