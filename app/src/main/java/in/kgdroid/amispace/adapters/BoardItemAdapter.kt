package `in`.kgdroid.amispace.adapters

import `in`.kgdroid.amispace.R
import `in`.kgdroid.amispace.databinding.ActivityMainBinding
import `in`.kgdroid.amispace.databinding.ItemBoardBinding
import `in`.kgdroid.amispace.models.Board
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

open class BoardItemAdapter(private val context: Context, private var list: ArrayList<Board>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListner: OnClickListner?= null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        val iv_board_image:CircleImageView
        val tv_name:TextView
        val tv_created_by:TextView
        iv_board_image= holder.itemView.findViewById(R.id.iv_board_image)
        tv_name= holder.itemView.findViewById(R.id.tv_name)
        tv_created_by= holder.itemView.findViewById(R.id.tv_created_by)
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(iv_board_image)

            tv_name.text= model.name
            tv_created_by.text= "Created by: ${model.createBy}"

            holder.itemView.setOnClickListener {
                if(onClickListner != null){
                    onClickListner!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListner{
        fun onClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)


}