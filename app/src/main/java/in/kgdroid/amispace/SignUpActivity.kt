package `in`.kgdroid.amispace

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val backButtonSignUp: ImageButton = findViewById(R.id.sign_up_back_btn)
        val forwardButton: ImageButton = findViewById(R.id.sign_up_forward_btn)
        val phoneButton: ImageButton = findViewById(R.id.sign_up_phone_btn)
        val gmailButton: ImageButton = findViewById(R.id.sign_up_gmail_btn)

        backButtonSignUp.setOnClickListener {
            val b: Bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, IntroActivity::class.java),b)
            finish()
        }


    }

}

