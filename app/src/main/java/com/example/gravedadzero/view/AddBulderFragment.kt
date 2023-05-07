package com.example.gravedadzero.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.AlarmClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.gravedadzero.R
import com.example.gravedadzero.utils.ImagenUtilidad
import com.example.gravedadzero.databinding.AddBulderBinding
import com.example.gravedadzero.model.Bulder
import com.example.gravedadzero.model.Difficulty
import com.example.gravedadzero.network.DataBase
import com.example.gravedadzero.view.LoginFragment.Companion.user
import java.util.*


class AddBulderFragment : Fragment(), View.OnClickListener{

    lateinit var binding: AddBulderBinding

    private var fotoBulder: Bitmap? = null
    private lateinit var dificultad : Difficulty

    companion object {
        var fotoEditada: Bitmap? = null
    }
    var bulderToAdd = Bulder()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddBulderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (fotoEditada != null){
            fotoBulder = fotoEditada
            binding.fotoBulder.setImageBitmap(fotoBulder)
        }


        binding.dificultad1.setOnClickListener(this)
        binding.dificultad2.setOnClickListener(this)
        binding.dificultad3.setOnClickListener(this)
        binding.dificultad4.setOnClickListener(this)
        binding.dificultad5.setOnClickListener(this)
        binding.dificultad6.setOnClickListener(this)

        binding.guardarBulder.setOnClickListener {
            addBulder()
        }
        binding.fotoBulder.setOnClickListener {
            val intent = Intent(context, EditImageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addBulder() {

        if(datosOK()){

            val foto = ImagenUtilidad.ConvertirImagenString(fotoBulder as Bitmap)

            bulderToAdd.name = binding.nombreBulderTI.text.toString().trim()
            bulderToAdd.user_email = user.email
            bulderToAdd.difficulty = dificultad
            bulderToAdd.date =Date()
            bulderToAdd.image = foto
            bulderToAdd.done =false
            bulderToAdd.shared =false

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, DetalleBloqueFragment(bulderToAdd))
            transaction.addToBackStack(null)
            transaction.commit()

            DataBase().setBulder(bulderToAdd)
            Toast.makeText(requireContext(), "Bloque creado", Toast.LENGTH_SHORT).show()

        } else{
            Toast.makeText(requireContext(), "Debes añadir una foto, seleccionar la dificultad y un nombre",
                Toast.LENGTH_SHORT).show()
        }
        fotoEditada = null
    }


    private fun datosOK(): Boolean {
        val chipSelected = this::dificultad.isInitialized
        val datosOK = binding.nombreBulderTI.text.toString().isNotEmpty()
        val imagenOK = binding.fotoBulder.drawable != null

        return datosOK && chipSelected && imagenOK
    }

    override fun onClick(v: View?) {
        when(v){
            binding.dificultad1 -> {
                dificultad = Difficulty.Grey
                binding.dificultad1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            binding.dificultad2 -> dificultad = Difficulty.Blue
            binding.dificultad3-> dificultad = Difficulty.Purple
            binding.dificultad4 -> dificultad = Difficulty.Orange
            binding.dificultad5-> dificultad = Difficulty.Green
            binding.dificultad6 -> dificultad = Difficulty.Yellow
        }
    }

}