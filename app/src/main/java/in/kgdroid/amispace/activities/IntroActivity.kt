package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class IntroActivity : BaseActivity() {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val introSignUp: LinearLayout = findViewById(R.id.intro_sign_up_ll)
        val introSignIn: LinearLayout = findViewById(R.id.intro_sign_in_ll)

        introSignUp.setOnClickListener {
            val b: Bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            // Launch the sign in screen.
            startActivity(Intent(this, SignUpActivity::class.java), b)
        }

        introSignIn.setOnClickListener {
            val b: Bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            // Launch the sign up screen.
            startActivity(Intent(this, SignInActivity::class.java), b)
        }

    }
}