package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.adapters.BoardItemAdapter
import `in`.kgdroid.amispace.databinding.ActivityMainBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.models.Board
import `in`.kgdroid.amispace.models.User
import `in`.kgdroid.amispace.utils.Constants
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.google.firebase.iid.FirebaseInstanceId
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    private lateinit var mUserName: String

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        binding.toolBar.fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }


    }

    /**
     * A function to populate the result of BOARDS list in the UI i.e in the recyclerView.
     */
    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()
        val rv_boards_list: RecyclerView = findViewById(R.id.rv_boards_list)
        val ll_no_view: LinearLayout = findViewById(R.id.ll_no_view)
        if (boardsList.size > 0) {
            rv_boards_list.visibility = View.VISIBLE
            ll_no_view.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)

            val adapter = BoardItemAdapter(this, boardsList)
            rv_boards_list.adapter = adapter

            adapter.setOnClickListener(object : BoardItemAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)

                    startActivity(intent)
                }
            })

        } else {
            rv_boards_list.visibility = View.GONE
            ll_no_view.visibility = View.VISIBLE
        }
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {
        val tool_bar_main: androidx.appcompat.widget.Toolbar = binding.toolBar.toolbarMainActivity
        setSupportActionBar(tool_bar_main)
        tool_bar_main.setNavigationIcon(R.drawable.ic_action_navigation)
        tool_bar_main.setNavigationOnClickListener {
            toggleDrawer()
        }

    }


    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {
        val nav_user_image: CircleImageView = binding.navView.findViewById(R.id.nav_user_image)
        val tv_username: TextView = binding.navView.findViewById(R.id.tv_username)
        mUserName = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_image)
            .into(nav_user_image)

        tv_username.text = user.name

        if (readBoardsList) {
            showProgressDialog()
            FirestoreClass().getBoardsList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            FirestoreClass().getBoardsList(this)
        } else {
            Log.e("Canceled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                    Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}