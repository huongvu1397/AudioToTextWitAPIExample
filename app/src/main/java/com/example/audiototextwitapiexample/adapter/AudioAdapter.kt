package com.example.audiototextwitapiexample.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.audiototextwitapiexample.R
import com.example.audiototextwitapiexample.model.AudioSong
import kotlinx.android.synthetic.main.audio_list_item.view.*

class AudioAdapter(private val items: ArrayList<AudioSong>, private val context: Context) : RecyclerView.Adapter<AudioAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.audio_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myAudio = items[position]
        holder.bind(myAudio,position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAudio = itemView.txt_name_audio!!
        fun bind(myAudio:AudioSong,position: Int){
            tvAudio.text = myAudio.title
            itemView.setOnClickListener {
                listener?.actionAudioToText(position)
            }
        }
    }

    fun setListener(listener:IOnAudioAdapter){
        this.listener = listener
    }

    private var listener :IOnAudioAdapter?=null

    interface IOnAudioAdapter{
        fun actionAudioToText(position:Int)
    }
}

