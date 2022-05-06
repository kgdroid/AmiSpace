package `in`.kgdroid.amispace.utils

import `in`.kgdroid.amispace.activities.MyProfileActivity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat.startActivityForResult

object Constants{

    const val USERS:String= "users"

    const val BOARDS:String= "boards"

    const val IMAGE:String= "image"
    const val NAME:String= "name"
    const val READ_STORAGE_PERMISSION_CODE=1
    const val  PICK_IMAGE_REQUEST_CODE=2

    fun showImageChooser(activity: Activity){
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}