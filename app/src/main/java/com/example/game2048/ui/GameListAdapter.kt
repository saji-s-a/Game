package com.example.game2048.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.game2048.R
import java.util.*

class GameListAdapter(
    gameDataArray: ArrayList<Int>?, context: Context
) : RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    var mContext: Context
    var mGameDataArray: ArrayList<Int>? = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.game_tale_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.gameTaleTv.text = mGameDataArray!![position].toString()
        setBackgrount(mGameDataArray!![position], holder.gameTaleTv)
    }

    override fun getItemCount(): Int {
        return if (mGameDataArray == null) {
            0
        } else mGameDataArray!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var gameTaleTv: TextView

        init {
            gameTaleTv = itemView.findViewById(R.id.gameTV)
        }
    }

    fun updateGameData(gameDataList: ArrayList<Int>?) {
        mGameDataArray = gameDataList
    }

    fun setBackgrount(count: Int, view: View) {
        var color = Color.argb(255, 255, 255, 255)
        if (count != 0) {
            color = Color.argb(
                90,
                120,
                (255 - count * 30) / 2,
                (255 - count * 10) / 2
            )
        }
        view.setBackgroundColor(color)
    }

    init {
        mGameDataArray = gameDataArray
        mContext = context
    }
}