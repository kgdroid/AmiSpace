package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.databinding.ActivitySignUpBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.models.User
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.get
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding
    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpBackBtn.setOnClickListener {
            val b: Bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            startActivity(Intent(this, IntroActivity::class.java),b)
            finish()
        }

        binding.signUpForwardBtn.setOnClickListener {
            registerUser()
        }

    }

    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser(){
        val name:String= binding.signUpUsername.editText?.text.toString().trim{ it <= ' '}
        val email:String= binding.signUpEmail.editText?.text.toString().trim{ it <= ' '}
        val password:String= binding.signUpPassword.editText?.text.toString().trim{ it <= ' '}

        if(validateForm(name, email, password)){
            showProgressDialog()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    val user = User(firebaseUser.uid, name, registeredEmail)
                    FirestoreClass().registerUser(this, user)
                } else {
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess(){
        Toast.makeText(
            this,
            "you have successfully registered",
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this, MainActivity::class.java))
        hideProgressDialog()
//        FirebaseAuth.getInstance().signOut()
        finish()
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String) :Boolean{
        return when {
            TextUtils.isEmpty(name) ->{
                showErrorSnackBar("Please Enter a name")
                false
            }
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

