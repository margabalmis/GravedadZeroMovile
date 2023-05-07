package com.example.gravedadzero.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gravedadzero.R
import com.example.gravedadzero.adapter.AdapterBloques
import com.example.gravedadzero.databinding.FragmentBloquesBinding
import com.example.gravedadzero.model.Bulder
import com.example.gravedadzero.model.Difficulty
import com.example.gravedadzero.network.DataBase
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class BloquesFragment : Fragment(){

    lateinit var binding: FragmentBloquesBinding
    lateinit var filterBloquesBtt: FloatingActionButton
    lateinit var difficultyQuery: Difficulty
    var adapter: AdapterBloques? = null

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBloquesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarRecycler(DataBase().cargarBloquesCompartidos())
    }

    private fun cargarRecycler(query: Query) {
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Bulder>()
            .setQuery(query, Bulder::class.java).build()

        if(adapter==null){
            adapter = AdapterBloques(firestoreRecyclerOptions, "Private")
        }else{
            adapter!!.updateOptions(firestoreRecyclerOptions)
        }

        filterBloquesBtt = binding.filtroBloquesBTT
        filterBloquesBtt.setOnClickListener { filtro() }

        adapter!!.adapterOnClick(::pulsarImagen)

        adapter!!.adapterOnLongClick(){
            val admin = Firebase.auth.currentUser!!.email
            if(admin=="gravedadzero@gmail.com"){
                val docRef = adapter!!.snapshots.getSnapshot(binding.recyclerBloques.getChildAdapterPosition(it)).reference
                docRef.delete()
            }
            true
        }

        binding.recyclerBloques.adapter = adapter
        binding.recyclerBloques.layoutManager = LinearLayoutManager(getActivity())
    }

    fun pulsarImagen(view: View) {
        val idBulder = adapter!!.snapshots.getSnapshot(
            binding.recyclerBloques
                .getChildAdapterPosition(view)
        ).reference.id
        CoroutineScope(Dispatchers.IO).launch {
            val bulderPulsado = async { DataBase().getBulder(idBulder, "SharedBulder") }
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.nav_host_fragment,
                AmpliacionBloqueFragment(bulderPulsado.await())
            )
            transaction.commit()
        }
    }

    private fun filtro() {
        val builder = AlertDialog.Builder(context)
            .create()

        val view = layoutInflater.inflate(R.layout.filter_bulder_dialog,null)
        view.findViewById<Chip>(R.id.dificultad_1).setOnClickListener{
            difficultyQuery = Difficulty.Grey
        }
        view.findViewById<Chip>(R.id.dificultad_2).setOnClickListener{
            difficultyQuery = Difficulty.Blue
        }
        view.findViewById<Chip>(R.id.dificultad_3).setOnClickListener{
            difficultyQuery = Difficulty.Purple
        }
        view.findViewById<Chip>(R.id.dificultad_4).setOnClickListener{
            difficultyQuery = Difficulty.Orange
        }
        view.findViewById<Chip>(R.id.dificultad_5).setOnClickListener{
            difficultyQuery = Difficulty.Green
        }
        view.findViewById<Chip>(R.id.dificultad_6).setOnClickListener{
            difficultyQuery = Difficulty.Yellow
        }

        val autotextView = view.findViewById<AutoCompleteTextView>(R.id.fechasTV)
        val fechas = resources.getStringArray(R.array.Fechas)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, fechas)
        autotextView.setAdapter(adapter)


        val fecha= fechaQuery(view)
        builder.setView(view)
        val  button = view.findViewById<Button>(R.id.buscarBulder)

        button.setOnClickListener {
            cargarRecycler(DataBase()
                .cargarProyectosCompartidosFiltrados(difficultyQuery.name, fecha))
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun fechaQuery(view: View): Date {
        val cal = Calendar.getInstance()
        val periodo  = view.findViewById<MaterialAutoCompleteTextView>(R.id.fechasTV).text.toString()
        val fechaCalculada: Date
        when(periodo){
            "Último mes" -> {
                cal.add(Calendar.MONTH, -1)
                fechaCalculada = cal.time
            }
            "Últimos dos meses" -> {
                cal.add(Calendar.MONTH, -2)
                fechaCalculada = cal.time
            }
            "Últimos tres meses" -> {
                cal.add(Calendar.MONTH, -3)
                fechaCalculada = cal.time
            }
            else -> {
                cal.add(Calendar.YEAR, -3)
                fechaCalculada = cal.time
            }
        }
        return fechaCalculada
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

}