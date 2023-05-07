package com.example.gravedadzero.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gravedadzero.R
import com.example.gravedadzero.model.New
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdapterNews(options: FirestoreRecyclerOptions<New>) :
    FirestoreRecyclerAdapter<New, AdapterNews.Holder>(options),
    View.OnLongClickListener{

    var listenerLong: View.OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.ampliacion_noticia, parent, false)

        itemView.setOnLongClickListener(this)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int, new: New) {
        return holder.bind(new)
    }

    fun adapterOnLongClick(listenerLong: View.OnLongClickListener) {
        this.listenerLong = listenerLong
    }

    override fun onLongClick(v: View): Boolean {
        listenerLong!!.onLongClick(v)
        return true
    }

    inner class Holder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        var  noticiaPulsada =  New()
        var date : TextView
        var  titulo: TextView
        var  texto: TextView

        @SuppressLint("ResourceAsColor")
        fun bind(new: New) {
            noticiaPulsada = new

            date.text = noticiaPulsada.getDateNew()
            titulo.text = noticiaPulsada.titulo
            texto.text = noticiaPulsada.texto

        }
        init {
            date = itemView.findViewById(R.id.dateNew)
            titulo= itemView.findViewById(R.id.tituloNew)
            texto= itemView.findViewById(R.id.textoNew)
        }
    }

}


