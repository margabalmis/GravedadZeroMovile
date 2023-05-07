package com.example.gravedadzero.view

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.gravedadzero.R
import com.example.gravedadzero.utils.ImagenUtilidad
import com.example.gravedadzero.utils.ImagenUtilidad.convertirStringBitmap
import com.example.gravedadzero.databinding.FragmentDetalleBloqueBinding
import com.example.gravedadzero.model.Bulder
import com.example.gravedadzero.network.DataBase
import com.google.android.material.button.MaterialButton


class DetalleBloqueFragment( val bulder: Bulder) : Fragment(),View.OnClickListener{

    lateinit var binding: FragmentDetalleBloqueBinding
    lateinit var date : TextView
    lateinit var  name: TextView
    lateinit var  difficulty: ImageView
    lateinit var  share: Button
    lateinit var  done: MaterialButton
    lateinit var imagen : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetalleBloqueBinding.inflate(inflater, container,false)

        date = binding.fechaTV
        name = binding.BulderNameTV
        difficulty = binding.dificultadBtt
        share = binding.compartirBulder
        done = binding.completadoMB
        imagen = binding.bloqueImagenDetalle

        imagen.setOnClickListener(this)
        done.setOnClickListener (this)
        share.setOnClickListener(this)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        date.text = bulder.getDateBulder()
        name.text = bulder.name
        imagen.setImageBitmap(convertirStringBitmap(bulder.image))
        difficulty.backgroundTintList = ColorStateList.valueOf(bulder.difficulty!!.color)
        done.iconTint = ColorStateList.valueOf(bulder.getColorDone(bulder.done!!))
        share.isEnabled = !bulder.shared!!

    }
    override fun onClick(v: View?) {
        var fieldToUpdate = "done"

        when (v!!.id) {
            R.id.completadoMB -> bulder.done = bulder.done != true
            R.id.compartirBulder -> {
                bulder.shared = bulder.shared != true
                fieldToUpdate = "shared"
            }
        }
        if (v.id != R.id.bloqueImagenDetalle){
            DataBase().updateBulder(bulder, fieldToUpdate)
            share.isEnabled = !bulder.shared!!
            done.iconTint = ColorStateList.valueOf(bulder.getColorDone(bulder.done!!))
        }

    }

}