package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.databinding.ActivityCreateBoardBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.utils.Constants
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import `in`.kgdroid.amispace.models.Board
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    // Add a binding to Create board activity
    private lateinit var binding: ActivityCreateBoardBinding

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    // A global variable for Username
    private lateinit var mUserName: String

    // A global variable for a board image URL
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }

        binding.ivBoardImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {

                Constants.showImageChooser(this)
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnCreate.setOnClickListener {
            // Here if the image is not selected then update the other details of user.
            if (mSelectedImageFileUri != null) {
                uploadBoardImage()
            } else {
                showProgressDialog()
                // Call a function to update create a board.
                createBoard()
            }
        }
    }

    /**
     * A function to make an entry of a board in the database.
     */
    private fun createBoard() {
        //  A list is created to add the assigned menu_members.
        //  This can be modified later on as of now the user itself will be the member of the board.
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        // Creating the instance of the Board and adding the values as per parameters.
        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUserName,
            assignedUsersArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    /**
     * A function to upload the Board Image to storage and getting the downloadable URL of the image.
     */
    private fun uploadBoardImage() {
        showProgressDialog()

        val sRef: StorageReference = FirebaseStorage.getInstance()
            .reference.child(
                "BOARD_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                    this,
                    mSelectedImageFileUri
                )
            )

        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.i(
                "Board Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mBoardImageURL = uri.toString()

                createBoard()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this,
                exception.message,
                Toast.LENGTH_LONG
            ).show()

            hideProgressDialog()
        }
    }

    /**
     * A function for notifying the board is created successfully.
     */
    fun boardCreatedSuccessfully() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        val tool_bar_create_board: androidx.appcompat.widget.Toolbar =
            binding.toolbarCreateBoardActivity
        setSupportActionBar(tool_bar_create_board)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            actionBar.title = resources.getString(R.string.create_board_title)
        }

        tool_bar_create_board.setNavigationOnClickListener { onBackPressed() }

    }

    /**
     * This function will notify the user after tapping on allow or deny
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(
                this,
                "Oops, just denied the permission for storage",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data

            val iv_board: CircleImageView = binding.ivBoardImage

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(iv_board)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}