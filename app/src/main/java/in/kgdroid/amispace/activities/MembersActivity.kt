package `in`.kgdroid.amispace.activities


import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.adapters.MemberListItemsAdapter
import `in`.kgdroid.amispace.databinding.ActivityMembersBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.models.User
import `in`.kgdroid.amispace.models.Board
import `in`.kgdroid.amispace.utils.Constants
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {


    private lateinit var mBoardDetails: Board

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean= false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog()
        FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    fun setUpMembersList(list: ArrayList<User>){

        mAssignedMembersList = list

        hideProgressDialog()

        binding.rvMembersList.layoutManager= LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter= MemberListItemsAdapter(this, list)
        binding.rvMembersList.adapter= adapter
    }

    fun memberDetails(user: User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMembersToBoard(this, mBoardDetails, user)
    }

    private fun setupActionBar(){
        val toolbar_members_activity: androidx.appcompat.widget.Toolbar = binding.toolbarMembersActivity
        setSupportActionBar(toolbar_members_activity)
        val actionBar= supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            actionBar.title= resources.getString(R.string.members)
        }

        toolbar_members_activity.setNavigationOnClickListener{ onBackPressed()}

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {

                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        val tv_add: TextView= dialog.findViewById(R.id.tv_add)
        val tv_cancel: TextView= dialog.findViewById(R.id.tv_cancel)
        val et_email_search_member: EditText= dialog.findViewById(R.id.et_email_search_member)
        tv_add.setOnClickListener(View.OnClickListener {

            val email = et_email_search_member.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog()
                FirestoreClass().getMemberDetails(this, email)
            } else {
                showErrorSnackBar("Please enter members email address.")
            }
        })
        tv_cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }

        super.onBackPressed()
    }

    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade= true

        setUpMembersList(mAssignedMembersList)

        SendNotificationToUserAsyncTask(mBoardDetails.name, token = user.fcmToken).execute()
    }

    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String) :
        AsyncTask<Any, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()

            // Show the progress dialog.
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any): String {
            var result: String

            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL) // Base Url
                connection = url.openConnection() as HttpURLConnection

                connection.doOutput = true
                connection.doInput = true

                connection.instanceFollowRedirects = false

                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                // TODO (Step 3: Add the firebase Server Key.)
                // START
                // In order to find your Server Key or authorization key, follow the below steps:
                // 1. Goto Firebase Console.
                // 2. Select your project.
                // 3. Firebase Project Setting
                // 4. Cloud Messaging
                // 5. Finally, the SerkeyKey.
                // For Detail understanding visit the link: https://android.jlelse.eu/android-push-notification-using-firebase-and-advanced-rest-client-3858daff2f50
                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)

                // TODO (Step 4: Create a notification data payload.)
                // START
                // Create JSONObject Request
                val jsonRequest = JSONObject()

                // Create a data object
                val dataObject = JSONObject()
                // Here you can pass the title as per requirement as here we have added some text and board name.
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the new board by ${mAssignedMembersList[0].name}"
                )

                // Here add the data object and the user's token in the jsonRequest object.
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                // END

                wr.writeBytes(jsonRequest.toString())
                wr.flush() // Flushes this data output stream.
                wr.close() // Closes this output stream and releases any system resources associated with the stream

                val httpResult: Int =
                    connection.responseCode // Gets the status code from an HTTP response message.

                if (httpResult == HttpURLConnection.HTTP_OK) {

                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {

                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {

                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {

                    result = connection.responseMessage
                }

            } catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }

            // You can notify with your result to onPostExecute.
            return result
        }

        /**
         * This function will be executed after the background execution is completed.
         */
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            hideProgressDialog()

            // JSON result is printed in the log.
            Log.e("JSON Response Result", result)
        }
    }
}