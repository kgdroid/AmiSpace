package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.databinding.ActivitySignInBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.models.User
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
                    if (task.isSuccessful) {
                        // Calling the FirestoreClass signInUser function to get the data of user from database.
                        FirestoreClass().loadUserData(this@SignInActivity)
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
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