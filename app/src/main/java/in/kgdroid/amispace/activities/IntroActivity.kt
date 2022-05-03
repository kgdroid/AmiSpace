package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class IntroActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val introSignUp:LinearLayout= findViewById(R.id.intro_sign_up_ll)
        val introSignIn:LinearLayout= findViewById(R.id.intro_sign_in_ll)

        introSignUp.setOnClickListener{
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, SignUpActivity::class.java),b)
        }

        introSignIn.setOnClickListener {
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, SignInActivity::class.java), b)
        }

    }
}