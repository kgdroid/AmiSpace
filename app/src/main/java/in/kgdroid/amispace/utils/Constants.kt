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

    const val ASSIGNED_TO: String= "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE=1
    const val  PICK_IMAGE_REQUEST_CODE=2
    const val DOCUMENT_ID: String= "documentId"
    const val TASK_LIST: String= "taskList"
    const val BOARD_DETAIL: String= "board_detail"
    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"
    const val ID: String= "id"
    const val EMAIL: String="email"
    const val TASK_LIST_ITEM_POSITION: String= "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION: String= "card_list_item_position"

    const val BOARD_MEMBERS_LIST: String= "board_members_list"

    const val AMISPACE_PREFERENCES = "AmiSpacePrefs"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAA5NsZOvs:APA91bFEc5R0E8o5iWLnpIov3c73tLxCAJj0WUCk6HBGYkawHIoJKhrdKQ_mVjLCZJmWt2iExsDwCn6YI2BMqpegvnGMm6yam9WWaWAM7ZhsMQmbRi9BRsZuYgi6z2i7rW-1epFtBetF"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun showImageChooser(activity: Activity){
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?):String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}