package com.example.gravedadzero.view


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.gravedadzero.R
import com.example.gravedadzero.databinding.FragmentSingUpBinding
import com.example.gravedadzero.model.*
import com.example.gravedadzero.network.DataBase
import com.example.gravedadzero.view.LoginFragment.Companion.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SingUpFragment : Fragment() {

    lateinit var binding: FragmentSingUpBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var etNick: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var bttSingup: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingUpBinding.inflate(inflater, container, false)

        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Enlaces XML
        etNick = binding.nickET
        etEmail = binding.emailET
        etPassword = binding.passwordET
        bttSingup = binding.singupBTT

        bttSingup.setOnClickListener {
            crearUser()
        }

        //Validaciones
        manageButonLogin()
        etEmail.doOnTextChanged { text, start, before, count -> manageButonLogin() }
        etPassword.doOnTextChanged { text,
                                     start, before, count -> manageButonLogin() }
    }

    private fun manageButonLogin() {
        if ((TextUtils.isEmpty(etPassword.text.toString())) || (TextUtils.isEmpty(etNick.text.toString()))
            || !ValidateEmail.isEmail(etEmail.text.toString()
            )
        ) {
            bttSingup.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.secondaryColorDesvilitado
                )
            )
            bttSingup.isEnabled = false
        } else {
            bttSingup.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(), R.color.secondaryColor
                )
            )
            bttSingup.isEnabled = true
        }
    }
    private fun crearUser() {

        mAuth.createUserWithEmailAndPassword(etEmail.text.toString().trim(), etPassword.text.toString().trim())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    user = User(etEmail.text.toString().trim(),etNick.text.toString().trim())
                    DataBase().crearUsuario(user)
                    Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                    goMainActivity()
                } else {
                    Toast.makeText(
                        context,
                        "Error, upps algo salío mal",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun goMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }
}