package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.databinding.ActivitySignInBinding
import `in`.kgdroid.amispace.databinding.ActivitySignUpBinding
import `in`.kgdroid.amispace.models.User
import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        binding.signInBackBtn.setOnClickListener {
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, IntroActivity::class.java),b)
            finish()
        }

        binding.signInCreateAcc.setOnClickListener {
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, SignUpActivity::class.java),b)
        }

        binding.signInForwardBtn.setOnClickListener {
            val b:Bundle= ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser(){
        val email:String= binding.signInUsername.editText?.text.toString().trim{ it <= ' '}
        val password: String= binding.signInPassword.editText?.text.toString().trim{ it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        Log.d("SignIn", "Success")
                        val user= auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    }else{
                        Log.w("SignIn", "Failed", task.exception)
                        Toast.makeText(baseContext, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun validateForm(email: String, password: String) :Boolean{
        return when {
            TextUtils.isEmpty(email) ->{
                showErrorSnackBar("Please Enter a valid email")
                false
            }
            TextUtils.isEmpty(password) ->{
                showErrorSnackBar("Please Enter a password")
                false
            }else ->{
                true
            }
        }
    }
}