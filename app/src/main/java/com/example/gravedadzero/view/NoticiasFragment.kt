package com.example.gravedadzero.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gravedadzero.adapter.AdapterNews
import com.example.gravedadzero.databinding.FragmentNoticiasBinding
import com.example.gravedadzero.model.New
import com.example.gravedadzero.network.DataBase
import com.example.gravedadzero.view.LoginFragment.Companion.user
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query



class NoticiasFragment : Fragment(){

    lateinit var binding: FragmentNoticiasBinding
    var adapter: AdapterNews? = null

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoticiasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarRecycler(DataBase().cargarNoticias())
    }

    private fun cargarRecycler(query: Query) {
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<New>()
            .setQuery(query, New::class.java).build()

        if(adapter==null){
            adapter = AdapterNews(firestoreRecyclerOptions)
        }else{
            adapter!!.updateOptions(firestoreRecyclerOptions)
        }

        adapter!!.adapterOnLongClick{
            val isAdmin = user.email
            val docRef = adapter!!.snapshots.getSnapshot(binding.recyclerNew.getChildAdapterPosition(it)).reference
            if(isAdmin == "gravedadzero@gmail.com") {
                docRef.delete().addOnSuccessListener {
                    Toast.makeText(requireActivity(),"Noticia eliminada",Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        binding.recyclerNew.adapter = adapter
        binding.recyclerNew.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

}