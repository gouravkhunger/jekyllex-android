package com.github.gouravkhunger.jekyllex.ui.auth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.provider.WebAuthProvider.resume
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.github.gouravkhunger.jekyllex.BuildConfig
import com.github.gouravkhunger.jekyllex.R
import com.github.gouravkhunger.jekyllex.db.userdb.UserDataBase
import com.github.gouravkhunger.jekyllex.repositories.UserRepository
import com.github.gouravkhunger.jekyllex.ui.home.HomeActivity
import com.github.gouravkhunger.jekyllex.util.preActivityStartChecks
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.other_no_internet.*

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var account: Auth0
    private lateinit var apiClient: AuthenticationAPIClient
    private lateinit var manager: SecureCredentialsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (preActivityStartChecks(this)) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
                Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT).show()
                setContentView(R.layout.other_no_internet)
                retry.setOnClickListener {
                    startActivity(
                        Intent(this, HomeActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
                    finish()
                }
                return
            }
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val userRepository = UserRepository(UserDataBase(this))
        val factory = AuthViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        account = Auth0(
            BuildConfig.Auth0ClientId,
            getString(R.string.com_auth0_domain)
        )

        apiClient = AuthenticationAPIClient(account)
        manager = SecureCredentialsManager(this, apiClient, SharedPreferencesStorage(this))
        val isLoggedIn = manager.hasValidCredentials()

        setTheme(R.style.Theme_JekyllEx)
        if (isLoggedIn) goToHome()

        setContentView(R.layout.activity_auth)

        loginBtn.setOnClickListener {

            it.visibility = View.GONE
            loginProgressBar.visibility = View.VISIBLE

            WebAuthProvider.login(account)
                .withScheme(getString(R.string.com_auth0_scheme))
                .withAudience(BuildConfig.API_AUDIENCE)
                .withScope("read:userdata")
                .withConnection("github")
                // Launch the authentication passing the callback where the results will be received
                .start(this, object : Callback<Credentials, AuthenticationException> {

                    // Called when there is an authentication failure
                    override fun onFailure(error: AuthenticationException) {
                        showErrorAlert(error.message)
                        it.visibility = View.VISIBLE
                        loginProgressBar.visibility = View.GONE
                    }

                    // Called when authentication completed successfully
                    override fun onSuccess(result: Credentials) {
                        // Get the access token from the credentials object.
                        // This can be used to call APIs
                        manager.saveCredentials(result)

                        getUserInfo(result)
                    }
                })
        }

        viewModel.userData.observe(this, {
            if (it == null) {
                showErrorAlert("Couldn't load user data")
            } else {
                prefs.edit()
                    .putString("username", it.nickname)
                    .putString("user_id", it.user_id)
                    .putString("pic_url", it.picture)
                    .putString("access_token", it.identities[0].access_token)
                    .apply()
                viewModel.saveUser(it)
            }
        })

        viewModel.saved.observe(this, {
            if (it) {
                loginBtn.visibility = View.VISIBLE
                loginProgressBar.visibility = View.GONE
                goToHome()
            }
        })

    }

    private fun getUserInfo(credentials: Credentials) {
        apiClient.userInfo(credentials.accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                    showErrorAlert(error.message)
                }

                override fun onSuccess(result: UserProfile) {
                    // We have the user's profile!
                    viewModel.getUserData(
                        result.getId().toString(),
                        "Bearer ${credentials.accessToken}"
                    )
                }
            })
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun showErrorAlert(message: String?) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("An Error Occurred")
            .setMessage("Error Log: \n\n$message")
            .setCancelable(false)
            .setPositiveButton("Copy Log") { dialog, _ ->
                val clipboard: ClipboardManager = getSystemService(
                    CLIPBOARD_SERVICE
                ) as ClipboardManager
                val clip = ClipData.newPlainText("error_log", message)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(baseContext, "Copied!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
            }

        val alert: AlertDialog = dialog.create()
        alert.window?.setBackgroundDrawableResource(R.drawable.rounded_corners)
        alert.show()

    }

    override fun onNewIntent(intent: Intent?) {
        if (resume(intent)) {
            return
        }
        super.onNewIntent(intent)
    }
}
