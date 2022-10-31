package com.android.statussavvy.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.android.statussavvy.R
import com.android.statussavvy.base.BaseActivity
import com.android.statussavvy.data.RegisterResponse
import com.android.statussavvy.databinding.LoginBinding
import com.android.statussavvy.network.ApiClient
import com.android.statussavvy.network.ApiRequest
import com.android.statussavvy.network.NetworkHelper
import com.android.statussavvy.utils.MyPreference
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Login : BaseActivity<LoginBinding>(), InstallReferrerStateListener {
    private var myGid: String? = null
    private lateinit var responseRef: ReferrerDetails
    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor()
    private lateinit var mReferrerClient: InstallReferrerClient
    private var account: GoogleSignInAccount? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    val prefInstallReferrer = "installReferrer"
    private var referrerUrl: String? = null

    private var utm_medium: String? = ""
    private var utm_term: String? = ""
    private var utm_source: String? = ""
    private var utm_campaign: String = ""
    override fun setLayoutId(): Int {
        return R.layout.login
    }

    override fun initM() {
        firebaseAuth()
        setUTM()
        setTerms()
        getAdvertisingId()


    }

    private fun setTerms() {

        // binding.terms.text = "By continuing You indicate that you have read and agree to our"
        binding.tvPrivacy.setOnClickListener {
            setPrivacy("Privacy Policy", "https://statussavvy.app/privacy-policy.html")

        }
        binding.tvTerms.setOnClickListener {
            setPrivacy("Terms of Service", "https://statussavvy.app/terms_conditions.html")

        }
    }

    private fun setUTM() {
        mReferrerClient = InstallReferrerClient.newBuilder(this).build()
        mReferrerClient.startConnection(this)
        checkInstallReferrer()
    }

    private fun firebaseAuth() {
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.loginGoogle.setOnClickListener {
            signIn()
        }

    }


    private fun setPrivacy(title: String, url: String) {
        val dialog = Dialog(this)
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.privacy_dialog)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        val title_tv = dialog.findViewById<TextView>(R.id.p_dialog_title)
        val webView = dialog.findViewById<WebView>(R.id.policy_wev)
        val accept_bt = dialog.findViewById<TextView>(R.id.done_bt)
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        title_tv.text = title
        accept_bt.setOnClickListener { v: View? -> dialog.dismiss() }
        if (!(this as Activity).isFinishing) {
            //show dialog
            dialog.show()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, CredentialPickerConfig.Prompt.SIGN_IN)
        //launcher.launch(signInIntent)
    }

    private fun checkInstallReferrer() {
        val prefs: SharedPreferences = MyPreference.getPreference(this)
        if (prefs.getBoolean(prefInstallReferrer, false)) {
            return
        }
        val referrerClient = InstallReferrerClient.newBuilder(this).build()
        backgroundExecutor.execute(Runnable { getInstallReferrerFromClient(referrerClient) })
    }

    private fun getInstallReferrerFromClient(referrerClient: InstallReferrerClient?) {
        referrerClient!!.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            responseRef = referrerClient.installReferrer
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                            return
                        }
                        referrerUrl = responseRef.installReferrer.toString()
                        // Toast.makeText(LoginActivity.this, ""+responseRef.getInstallReferrer(), Toast.LENGTH_SHORT).show();
                        // trackInstallReferrer(referrerUrl);

                        // // Only check this once.
                        // getPreferences(MODE_PRIVATE).edit().putBoolean(prefInstallReferrer, true).commit();

                        // End the connection
                        referrerClient.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {}
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {}
                }
            }

            override fun onInstallReferrerServiceDisconnected() {}
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CredentialPickerConfig.Prompt.SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d("TAG", "handleSignInResult: " + account)
            if (account != null) {
                FirebaseGoogleAuth(account)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
            //  Log.w((String) TAG, "signInResult:failed code=" + e.getStatusCode());
            showMsg("Sign In Failed")
        }
    }

    private fun FirebaseGoogleAuth(account: GoogleSignInAccount) {
        val dialog = ProgressDialog(this)
        dialog.setMessage("Please wait...")
        if (!(this as Activity).isFinishing) {
            dialog.show()
        }

        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dialog.dismiss()
                    val user = auth.currentUser
                    updateUi(user)
                } else {
                    dialog.dismiss()
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    //  updateUi(null)
                }
            }
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                registerUser(account!!)
            }
        }
    }

    fun getAdvertisingId() {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val adInfo =
                    AdvertisingIdClient.getAdvertisingIdInfo(this)
                myGid = if (adInfo != null) adInfo.id else null
                //myGid[0] = adInfo != null ? adInfo.getId() : null;
                // Log.i("UIDMY", myGid)

            } catch (e: Exception) {
                Log.e("error", e.toString())
            }

        }
        /* AsyncTask.execute {
             try {
                 val adInfo =
                     AdvertisingIdClient.getAdvertisingIdInfo(this)
                 myGid = if (adInfo != null) adInfo.id else null
                 //myGid[0] = adInfo != null ? adInfo.getId() : null;
                 // Log.i("UIDMY", myGid)

             } catch (e: Exception) {
                 Log.e("error", e.toString())
             }
         }*/
        //return myGid;
    }

    override fun onInstallReferrerSetupFinished(responseCode: Int) {
        when (responseCode) {
            InstallReferrerClient.InstallReferrerResponse.OK ->                 // Connection established
                try {
                    getReferralUser()
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {}
            InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {}
            InstallReferrerClient.InstallReferrerResponse.DEVELOPER_ERROR -> {}
            InstallReferrerClient.InstallReferrerResponse.SERVICE_DISCONNECTED -> {}
        }
    }


    override fun onInstallReferrerServiceDisconnected() {

    }

    @Throws(RemoteException::class)
    private fun getReferralUser() {

        val response: ReferrerDetails = mReferrerClient!!.getInstallReferrer()
        val referrerData = response.installReferrer
        Log.e("TAG", "Install referrer:" + response.installReferrer)


        // for utm terms
        val values = HashMap<String, String>()
        //if (values.containsKey("utm_medium") && values.containsKey("utm_term")) {
        try {
            if (referrerData != null) {
                val referrers = referrerData.split("&").toTypedArray()
                //  Log.d(TAG, "getReferralUser: " + referrerData);
                for (referrerValue in referrers) {
                    val keyValue = referrerValue.split("=").toTypedArray()
                    values[URLDecoder.decode(keyValue[0], "UTF-8")] =
                        URLDecoder.decode(keyValue[1], "UTF-8")
                }
                Log.e("TAG", "UTM medium:" + values["utm_medium"])
                Log.e("TAG", "UTM term:" + values["utm_term"])
                utm_medium = values["utm_medium"].toString()
                // utm_term = values.get("utm_term");
                utm_source = values["utm_source"].toString()
                utm_campaign = values["utm_campaign"].toString()
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun registerUser(account: GoogleSignInAccount) {
        val isNetwork = NetworkHelper.isNetworkAvailable(this)
        if (isNetwork) {
            val call = ApiClient.getApi().register(
                ApiRequest.registerRequest(
                    this,
                    account,
                    utm_source!!, utm_medium!!, utm_campaign, referrerUrl!!, myGid!!
                )
            )

            call.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val data = response.body()
                    if (data != null) {
                        MyPreference.saveData(
                            this@Login,
                            data.userId.toString(),
                            data.securityToken
                        )
                        MyPreference.saveUserDetails(
                            this@Login,
                            account.displayName,
                            account.email,
                            data.socialImgurl
                        )
                        Log.d("TAG", "onResponse: " + account.photoUrl)
                        Splash.appOpen(this@Login)


                    }


                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                }

            })
        } else makeText(this, "Network Not Available", Toast.LENGTH_SHORT).show()


    }

}