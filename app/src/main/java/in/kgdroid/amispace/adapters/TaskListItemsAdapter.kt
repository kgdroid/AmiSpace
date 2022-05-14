package `in`.kgdroid.amispace.adapters

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.activities.TaskListActivity
import `in`.kgdroid.amispace.models.Task
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

open class TaskListItemsAdapter(private val context: Context, private var list: ArrayList<Task>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams= LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model= list[position]
        val tv_add_task_list: TextView = holder.itemView.findViewById(R.id.tv_add_task_list)
        val ll_task_item: LinearLayout = holder.itemView.findViewById(R.id.ll_task_item)
        val tv_task_list_title: TextView= holder.itemView.findViewById(R.id.tv_task_list_title)
        val cv_add_task_list_name: CardView= holder.itemView.findViewById(R.id.cv_add_task_list_name)
        val ib_close_list_name: ImageButton= holder.itemView.findViewById(R.id.ib_close_list_name)
        val ib_done_list_name: ImageButton= holder.itemView.findViewById(R.id.ib_done_list_name)
        val et_task_list_name: EditText= holder.itemView.findViewById(R.id.et_task_list_name)
        val ib_edit_list_name: ImageButton= holder.itemView.findViewById(R.id.ib_edit_list_name)
        val et_edit_task_list_name: EditText= holder.itemView.findViewById(R.id.et_edit_task_list_name)
        val ll_title_view: LinearLayout= holder.itemView.findViewById(R.id.ll_title_view)
        val cv_edit_task_list_name: CardView= holder.itemView.findViewById(R.id.cv_edit_task_list_name)
        val ib_close_editatble_view: ImageButton= holder.itemView.findViewById(R.id.ib_close_editable_view)
        val ib_done_edit_list_name: ImageButton= holder.itemView.findViewById(R.id.ib_done_edit_list_name)
        val ib_delete_list: ImageButton= holder.itemView.findViewById(R.id.ib_delete_list)


        if(holder is MyViewHolder){
            if(position == list.size-1){

                tv_add_task_list.visibility= View.VISIBLE
                ll_task_item.visibility= View.GONE
            }else{
                tv_add_task_list.visibility= View.GONE
                ll_task_item.visibility= View.VISIBLE
            }

            tv_task_list_title.text= model.title
            tv_add_task_list.setOnClickListener {
                tv_add_task_list.visibility= View.GONE
                cv_add_task_list_name.visibility= View.VISIBLE
            }

            ib_close_list_name.setOnClickListener {
                tv_add_task_list.visibility= View.VISIBLE
                cv_add_task_list_name.visibility= View.GONE
            }

            ib_done_list_name.setOnClickListener {
                val listName= et_task_list_name.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            ib_edit_list_name.setOnClickListener {
                et_edit_task_list_name.setText(model.title)
                ll_title_view.visibility= View.GONE
                cv_edit_task_list_name.visibility= View.VISIBLE
            }
            ib_close_editatble_view.setOnClickListener {
                ll_title_view.visibility= View.VISIBLE
                cv_edit_task_list_name.visibility= View.GONE
            }

            ib_done_edit_list_name.setOnClickListener {
                val listName= et_edit_task_list_name.text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.updateTaskList(position, listName, model)
                    }
                }else{
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            ib_delete_list.setOnClickListener {
                alertDialogForDeleteList(position, model.title)

            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun Int.toDp(): Int= (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int= (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}