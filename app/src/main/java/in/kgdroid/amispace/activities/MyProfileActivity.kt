package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.databinding.ActivityMainBinding
import `in`.kgdroid.amispace.databinding.ActivityMyProfileBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.models.User
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val  PICK_IMAGE_REQUEST_CODE=2
    }

    private var mSelectedImageFileUri: Uri?= null
    private var mProfileImageURL:String= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        binding.ivProfileUserImage.setOnClickListener{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

                showImageChooser()
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding.btnUpdate.setOnClickListener{
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImageChooser()
            }
        }else{
            Toast.makeText(
                this,
                "Oops, just denied the permission for storage",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showImageChooser(){
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode ==  Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data!!.data != null){
            mSelectedImageFileUri= data.data

            val iv_user_image: CircleImageView = binding.ivProfileUserImage

            try{
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_image)
                    .into(iv_user_image)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }

    private fun setupActionBar(){
        val tool_bar_my_profile: androidx.appcompat.widget.Toolbar = binding.toolbarMyProfileActivity
        setSupportActionBar(tool_bar_my_profile)
        val actionBar= supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            actionBar.title= resources.getString(R.string.nav_my_profile)
        }

        tool_bar_my_profile.setNavigationOnClickListener{ onBackPressed()}

    }

    fun setUserDataInUI(user: User){
        val iv_user_image: CircleImageView = binding.ivProfileUserImage
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_image)
            .into(iv_user_image)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
    }

    private fun uploadUserImage(){
        showProgressDialog()

        if(mSelectedImageFileUri != null){

            val sRef: StorageReference= FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageURL = uri.toString()

                    hideProgressDialog()

                    // TODO update user profile data
                }
            }.addOnFailureListener{
                exception ->
                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
        }
    }

    private fun getFileExtension(uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}