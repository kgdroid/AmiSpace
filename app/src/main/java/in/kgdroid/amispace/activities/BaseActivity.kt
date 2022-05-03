package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseActivity : AppCompatActivity() {

    private var doubleBackRoExitPressedOnce= false

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(){
        mProgressDialog= Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)

        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doubleBackRoExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackRoExitPressedOnce= true
        Toast.makeText(
            this,
            "Please click back again to exit.",
            Toast.LENGTH_SHORT
        ).show()

        Handler().postDelayed({
            doubleBackRoExitPressedOnce= false
        }, 2000)
    }

    fun showErrorSnackBar(message: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView= snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackbar.show()
    }

}