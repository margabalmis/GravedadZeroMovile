package com.example.gravedadzero.view

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.gravedadzero.R
import com.example.gravedadzero.utils.ImagenUtilidad.convertirStringBitmap
import com.example.gravedadzero.databinding.AmpliacioinBloqueBinding
import com.example.gravedadzero.model.Bulder
import com.example.gravedadzero.network.DataBase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*


class AmpliacionBloqueFragment(val bulder: Bulder) : Fragment(),View.OnClickListener{

    lateinit var binding: AmpliacioinBloqueBinding
    lateinit var date : TextView
    lateinit var nombre : TextView
    lateinit var  difficulty: ImageView
    lateinit var  bajar: MaterialButton
    lateinit var imagen : ImageView
    var existe = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = AmpliacioinBloqueBinding.inflate(inflater, container,false)

        existeBulder(bulder)

        date = binding.fechaTV
        nombre = binding.nombreTV
        difficulty = binding.dificultadBtt
        bajar = binding.bajarProyectoMB
        imagen = binding.bloqueImagen

        bajar.setOnClickListener (this)

        return binding.root
    }

    private fun existeBulder(bulder: Bulder) {
        CoroutineScope(Dispatchers.IO).launch {
            val e = async { DataBase().existeBulder(bulder) }
            existe = e.await()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nombre.text = bulder.name
        date.text = bulder.getDateBulder()
        imagen.setImageBitmap(convertirStringBitmap(bulder.image))
        difficulty.backgroundTintList = ColorStateList.valueOf(bulder.difficulty!!.color)
        bajar.iconTint = ColorStateList.valueOf(bulder.getColorDone(!existe))

    }
    override fun onClick(v: View?) {
        DataBase().añadirListaBulder(bulder)
        existe = true
        bajar.isEnabled = !existe
        bajar.iconTint = ColorStateList.valueOf(bulder.getColorDone(!existe))
        Toast.makeText(context, "Bulder añadido", Toast.LENGTH_SHORT).show()
        navegarAlDetalle()
    }

    private fun navegarAlDetalle() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.nav_host_fragment,BloquesFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }
}