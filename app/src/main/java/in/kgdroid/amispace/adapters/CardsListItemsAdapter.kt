package `in`.kgdroid.amispace.adapters

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.activities.TaskListActivity
import `in`.kgdroid.amispace.models.Card
import `in`.kgdroid.amispace.models.SelectedMembers
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        val tv_card_name:TextView= holder.itemView.findViewById(R.id.tv_card_name)
        val view_label_color:View= holder.itemView.findViewById(R.id.view_label_color)
        val rv_card_selected_members_list:RecyclerView= holder.itemView.findViewById(R.id.rv_card_selected_members_list)

        if (holder is MyViewHolder) {

            if (model.labelColor.isNotEmpty()) {
                view_label_color.visibility = View.VISIBLE
                view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                view_label_color.visibility = View.GONE
            }

            tv_card_name.text = model.name

            if ((context as TaskListActivity).mAssignedMembersDetailList.size > 0) {
                // A instance of selected members list.
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                // Here we got the detail list of members and add it to the selected members list as required.
                for (i in context.mAssignedMembersDetailList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMembersDetailList[i].id == j) {
                            val selectedMember = SelectedMembers(
                                context.mAssignedMembersDetailList[i].id,
                                context.mAssignedMembersDetailList[i].image
                            )

                            selectedMembersList.add(selectedMember)
                        }
                    }
                }

                if (selectedMembersList.size > 0) {

                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                        rv_card_selected_members_list.visibility = View.GONE
                    } else {
                        rv_card_selected_members_list.visibility = View.VISIBLE

                        rv_card_selected_members_list.layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                        rv_card_selected_members_list.adapter = adapter
                        adapter.setOnClickListener(object :
                            CardMemberListItemsAdapter.OnClickListener {
                            override fun onClick() {
                                if (onClickListener != null) {
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })
                    }
                } else {
                    rv_card_selected_members_list.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}