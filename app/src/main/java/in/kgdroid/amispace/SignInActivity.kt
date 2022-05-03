package `in`.kgdroid.amispace

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val backButtonSignIn:ImageButton= findViewById(R.id.sign_in_back_btn)
        val forwardButtonSignIn:ImageButton= findViewById(R.id.sign_in_forward_btn)
        val signInForgotPass:TextView= findViewById(R.id.sign_in_forgot_pass)
        val signInCreateAcc:TextView= findViewById(R.id.sign_in_create_acc)

        backButtonSignIn.setOnClickListener {
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, IntroActivity::class.java),b)
            finish()
        }

        signInCreateAcc.setOnClickListener {
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, SignUpActivity::class.java),b)
        }
    }
}