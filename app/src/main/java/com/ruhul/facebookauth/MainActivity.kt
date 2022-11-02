package com.ruhul.facebookauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.ruhul.facebookauth.databinding.ActivityMainBinding
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginManager: LoginManager
    private lateinit var callbackManager: CallbackManager

    private val logMsg = "LogTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initManager()
        logInStatus()
        logInReadPermission()
        logInButtonClick()

    }

    private fun logInButtonClick() {
        // Callback registration
        binding.loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult?> {

                override fun onCancel() {
                    Log.d(logMsg, "onCancel")
                }

                override fun onError(exception: FacebookException) {
                    Log.d(logMsg, "onError")
                }

                override fun onSuccess(result: LoginResult?) {
                    Log.d(logMsg, "onSuccess")

                    val request = GraphRequest.newMeRequest(
                        result?.accessToken
                    ) { `object`, response ->
                        if (`object` != null) {
                            try {
                                val name = `object`.getString("name")
                                binding.userNameTxt.text = name
                                Log.d(logMsg, "name = $name")

                                /*   val email = `object`.getString("email")
                                    val fbUserID = `object`.getString("id")
                                    val gender = response?.getJSONObject()?.getString("gender")*/

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            } catch (e: NullPointerException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    request.executeAsync()

                }
            })

    }

    private fun logInReadPermission() {
        loginManager.logInWithReadPermissions(
            this@MainActivity,
            mutableListOf("email", "public_profile")
        )

    }

    private fun logInStatus() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            Toast.makeText(this@MainActivity, "Log in", Toast.LENGTH_SHORT).show()
        }


    }

    private fun initManager() {
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
    }

    private fun enableExpressLogin() {
        LoginManager.getInstance().retrieveLoginStatus(this, object : LoginStatusCallback {
            override fun onCompleted(accessToken: AccessToken) {

            }

            override fun onFailure() {
                // No access token could be retrieved for the user
            }

            override fun onError(exception: Exception) {
                // An error occurred
            }
        })


    }

    private fun facebookLogin() {
        loginManager
            .registerCallback(
                callbackManager = callbackManager,
                callback = object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        TODO("Not yet implemented")
                    }

                    override fun onError(error: FacebookException) {
                        TODO("Not yet implemented")
                    }

                    override fun onSuccess(result: LoginResult) {
                        val request = GraphRequest.newMeRequest(
                            result?.accessToken
                        ) { `object`, response ->
                            if (`object` != null) {

                                Toast.makeText(
                                    this@MainActivity,
                                    "Response Success",
                                    Toast.LENGTH_SHORT
                                ).show()

                                try {
                                    val name = `object`.getString("name")
                                    val email = `object`.getString("email")
                                    val fbUserID = `object`.getString("id")

                                    binding.userNameTxt.text = name

                                    Log.d(logMsg, "onSuccess = $name")
                                    Log.d(logMsg, "onSuccess = $email")
                                    Log.d(logMsg, "onSuccess = $fbUserID")


                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        request.executeAsync()
                    }

                })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


}

