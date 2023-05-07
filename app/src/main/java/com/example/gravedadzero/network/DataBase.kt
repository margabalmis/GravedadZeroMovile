package com.example.gravedadzero.network

import android.content.ContentValues.TAG
import android.util.Log
import com.example.gravedadzero.model.*
import com.example.gravedadzero.view.LoginFragment.Companion.user
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import java.util.*


class DataBase {

    private val db = Firebase.firestore


    fun getUserDocuments(userID: String) {
        val docRef = db.collection("Users").document(userID)
        docRef.get().addOnSuccessListener { document ->
            if (document.data?.size != null) {
                user = document.toObject(User::class.java)!!
            }
        }
    }
    fun crearUsuario(user: User) {
        db.collection("Users").document(user.email!!).set(
            hashMapOf(
                "email" to user.email,
                "nick" to user.nick,
                "bonos" to user.bonos))
    }
    fun cargarMisproyectos(): Query {
        val query: Query = db.collection("Bulder").whereEqualTo("user_email", user.email)
        query.get()
        return query
    }
    fun cargarBloquesCompartidos(): Query {
        val query: Query = db.collection("SharedBulder")
        query.get()
        return query
    }
    fun setBulder(bulderToAdd: Bulder) {

        CoroutineScope(Dispatchers.IO).launch {
            val existe = async { DataBase().existeBulder(bulderToAdd) }

            if (!existe.await()) {
                val bulder = hashMapOf(
                    "shared" to bulderToAdd.shared,
                    "date" to bulderToAdd.date,
                    "difficulty" to bulderToAdd.difficulty.toString(),
                    "done" to bulderToAdd.done,
                    "name" to bulderToAdd.name,
                    "user_email" to bulderToAdd.user_email,
                    "image" to bulderToAdd.image,
                )

                db.collection("Bulder").document(bulderToAdd.user_email + bulderToAdd.name)
                    .set(bulder)
            }
        }
    }

    suspend fun existeBulder(bulderToAdd: Bulder): Boolean {
        val task = db.collection("Bulder")
            .whereEqualTo(FieldPath.documentId(), bulderToAdd.user_email + bulderToAdd.name).get()
            .asDeferred().await()
        return task.size() != 0
    }

    fun cargarMisproyectosFiltrados(difficulty: String, fecha: Date, done: Boolean): Query {
        val query = db.collection("Bulder")
            .whereEqualTo("user_email", user.email)
            .whereEqualTo("difficulty", difficulty)
            .whereEqualTo("done", done)
            .whereGreaterThan("date", fecha)

        query.get()
            .addOnSuccessListener { querySnapshot->
                for (document in querySnapshot) {
                    Log.d(TAG, document.id + " => " + document.data)
                }
                            }
            .addOnFailureListener{
                Log.d("db-fire","${it.message}")
            }
        return query
    }

    fun cargarProyectosCompartidosFiltrados(difficulty: String, fecha: Date): Query {
        val query = db.collection("SharedBulder")
            .whereEqualTo("difficulty", difficulty)
            .whereGreaterThan("date", fecha)

        query.get()
            .addOnSuccessListener { querySnapshot->
                for (document in querySnapshot) {
                    Log.d(TAG, document.id + " => " + document.data)
                }
            }
            .addOnFailureListener{
                Log.d("db-fire","${it.message}")
            }
        return query
    }

    fun updateBulder(bulder: Bulder, fieldToUpdate: String): Boolean {
        var updateSuccessfull = false
        val bulderRef = db.collection("Bulder").document(bulder.user_email + bulder.name!!.trim())
        val valueToUpdate: Boolean

        if (fieldToUpdate == "done") {
                valueToUpdate= bulder.done!!
        } else{
                valueToUpdate= bulder.shared!!
            añadirShared(bulder)
        }
        bulderRef.update(fieldToUpdate, valueToUpdate)
            .addOnSuccessListener {
                updateSuccessfull = true
            }
        return updateSuccessfull
    }
    fun añadirListaBulder(bulder: Bulder) {

        var sharedBulder = bulder

        sharedBulder.user_email = user.email
        sharedBulder.shared = true
        sharedBulder.done = false

        CoroutineScope(Dispatchers.IO).launch {
            val existe = async { DataBase().existeBulder(sharedBulder) }

            if (!existe.await()) {
                val bulder = hashMapOf(
                    "shared" to sharedBulder.shared,
                    "date" to sharedBulder.date,
                    "difficulty" to sharedBulder.difficulty.toString(),
                    "done" to sharedBulder.done,
                    "name" to sharedBulder.name,
                    "user_email" to sharedBulder.user_email,
                    "image" to sharedBulder.image,
                )

                db.collection("Bulder").document(user.email + sharedBulder.name)
                    .set(bulder)
            }
        }
    }
    fun añadirShared(sharedBulder: Bulder) {
        CoroutineScope(Dispatchers.IO).launch {

                val bulder = hashMapOf(
                    "shared" to sharedBulder.shared,
                    "date" to sharedBulder.date,
                    "difficulty" to sharedBulder.difficulty.toString(),
                    "done" to sharedBulder.done,
                    "name" to sharedBulder.name,
                    "user_email" to sharedBulder.user_email,
                    "image" to sharedBulder.image,
                )

                db.collection("SharedBulder").document(user.email + sharedBulder.name)
                    .set(bulder)

        }
    }
    suspend fun getBulder(idBulder: String, collection: String): Bulder {
        return db.collection(collection).document(idBulder).get().asDeferred()
            .await().toObject(Bulder::class.java)!!
    }

    suspend fun getNew(idNew: String, collection: String): New {
        return db.collection(collection).document(idNew).get().asDeferred()
            .await().toObject(New::class.java)!!
    }

    fun cargarNoticias(): Query {
        val query: Query = db.collection("News")
        query.get()
        return query
    }


}