package `in`.kgdroid.amispace.firebase

import `in`.kgdroid.amispace.activities.SignInActivity
import `in`.kgdroid.amispace.activities.SignUpActivity
import `in`.kgdroid.amispace.models.User
import `in`.kgdroid.amispace.utils.Constants
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore= FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName, "Error")
            }
    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if(loggedInUser !=null)
                    activity.signInSuccess(loggedInUser)
            }.addOnFailureListener{
                    e->
                Log.e("SignInUser", "Error")
            }
    }

    fun getCurrentUserId(): String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId= ""
        if(currentUser != null){
            currentUserId= currentUser.uid
        }
        return currentUserId
    }
}