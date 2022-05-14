package `in`.kgdroid.amispace.activities

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.adapters.TaskListItemsAdapter
import `in`.kgdroid.amispace.databinding.ActivityMyProfileBinding
import `in`.kgdroid.amispace.databinding.ActivityTaskListBinding
import `in`.kgdroid.amispace.firebase.FirestoreClass
import `in`.kgdroid.amispace.models.Board
import `in`.kgdroid.amispace.models.Card
import `in`.kgdroid.amispace.models.Task
import `in`.kgdroid.amispace.utils.Constants
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding

    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentId= ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId= intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog()
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board){
        mBoardDetails= board

        hideProgressDialog()
        setupActionBar()

        val addTaskList= Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.rvTaskList.setHasFixedSize(true)

        val adapter =  TaskListItemsAdapter(this, board.taskList)

        binding.rvTaskList.adapter = adapter
    }

    private fun setupActionBar(){
        val tool_bar_task_list: androidx.appcompat.widget.Toolbar = binding.toolbarTaskListActivity
        setSupportActionBar(tool_bar_task_list)
        val actionBar= supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            actionBar.title= mBoardDetails.name
        }

        tool_bar_task_list.setNavigationOnClickListener{ onBackPressed()}

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog()
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String){
        val task= Task(taskListName, FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task= Task(listName, model.createdBy)

        mBoardDetails.taskList[position]= task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog()
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)

    }

     fun deleteTaskList(position: Int){
         mBoardDetails.taskList.removeAt(position)
         mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
         showProgressDialog()
         FirestoreClass().addUpdateTaskList(this, mBoardDetails)
     }

    fun addCardToTaskList(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(FirestoreClass().getCurrentUserId())

        val card= Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignedUserList)

        val cardsList = mBoardDetails.taskList[position].cards
        cardsList.add(card)

        val task= Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardsList
        )

        mBoardDetails.taskList[position]= task

        showProgressDialog()

        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }
}