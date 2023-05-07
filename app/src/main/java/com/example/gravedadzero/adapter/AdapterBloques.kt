package com.example.gravedadzero.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gravedadzero.R
import com.example.gravedadzero.model.Bulder
import com.example.gravedadzero.network.DataBase
import com.example.gravedadzero.utils.ImagenUtilidad
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class AdapterBloques(options: FirestoreRecyclerOptions<Bulder>, val tipoElemento: String) :
    FirestoreRecyclerAdapter<Bulder, AdapterBloques.Holder>(options),
    View.OnClickListener, View.OnLongClickListener{

    lateinit var listener: View.OnClickListener
    var listenerLong: View.OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: View
        if (tipoElemento == "Shared") {
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.elemento_bloque_shared, parent, false
            )
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.elemento_bloque, parent, false
            )
        }
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
        return Holder(itemView, tipoElemento)
    }

    override fun onBindViewHolder(holder: Holder, position: Int, bloque: Bulder) {
        return holder.bind(bloque)
    }

    fun adapterOnClick(listener: View.OnClickListener?) {
        this.listener = listener!!
    }

    fun adapterOnLongClick(listenerLong: View.OnLongClickListener?) {
        this.listenerLong = listenerLong!!
    }

    override fun onClick(v: View) {
        listener.onClick(v)
    }

    override fun onLongClick(v: View): Boolean {
        listenerLong!!.onLongClick(v)
        return true
    }

    
    inner class Holder(
        itemView: View,
        val tipoElemento: String
    ) : RecyclerView.ViewHolder(itemView) {

        var  bloquePulsado =  Bulder()
        var date : TextView
        var  name: TextView
        var  difficulty: ImageView
        private var  done: ImageView
        var imagen : ImageView

        @SuppressLint("ResourceAsColor")
        fun bind(bulder: Bulder) {
            bloquePulsado = bulder
            date.text = bloquePulsado.getDateBulder()
            name.text = bloquePulsado.name
            difficulty.backgroundTintList = ColorStateList.valueOf(bloquePulsado.difficulty!!.color)
            imagen.setImageBitmap(ImagenUtilidad.convertirStringBitmap(bloquePulsado.image))
            if(tipoElemento == "Shared"){
                estaEnMisBloquesColor(bulder)
            }else{
                done.backgroundTintList = ColorStateList.valueOf(bloquePulsado.getColorDone(bloquePulsado.done!!))
            }
        }
        init {
            date = itemView.findViewById(R.id.fechaDetalleTV)
            name= itemView.findViewById(R.id.nombreDetalleTV)
            difficulty= itemView.findViewById(R.id.difDetalleBtt)
            done= itemView.findViewById(R.id.hechoCheck)
            imagen = itemView.findViewById(R.id.bloqueImagenDetalle)
        }
        private fun estaEnMisBloquesColor(bloquePulsado: Bulder){
            CoroutineScope(Dispatchers.IO).launch {
                val existe = async { DataBase().existeBulder(bloquePulsado) }
                done.backgroundTintList = ColorStateList.valueOf(bloquePulsado.
                getColorDone(existe.await()))
            }
        }
    }
}


