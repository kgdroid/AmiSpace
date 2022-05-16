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
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

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
    }
}