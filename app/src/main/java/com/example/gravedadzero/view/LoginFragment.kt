package com.example.gravedadzero.view

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
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
import com.example.gravedadzero.databinding.FragmentLoginBinding
import com.example.gravedadzero.model.*
import com.example.gravedadzero.network.DataBase
import com.example.gravedadzero.utils.ValidateEmail
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentLoginBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var bttLoging: Button
    private lateinit var bttForgotPassword: Button
    private lateinit var bttSingup: Button
    private lateinit var bttAnonymousUser: Button

    companion object{
        var user = User()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Enlaces XML
        etEmail = binding.emailET
        etPassword = binding.passwordET
        bttLoging = binding.loginBTT
        bttForgotPassword = binding.forgotPasswordBTT
        bttSingup = binding.singupBTT
        bttAnonymousUser = binding.anonymousUserBTT

        //Listener
        bttLoging.setOnClickListener(this)
        bttForgotPassword.setOnClickListener(this)
        bttSingup.setOnClickListener(this)
        bttAnonymousUser.setOnClickListener(this)

        //Validaciones
        manageButonLogin()
        etEmail.doOnTextChanged { text, start, before, count -> manageButonLogin() }
        etPassword.doOnTextChanged { text, start, before, count -> manageButonLogin() }

    }
    private fun manageButonLogin() {
        bttLoging = binding.loginBTT
        etEmail = binding.emailET
        etPassword = binding.passwordET
        if ((TextUtils.isEmpty(etPassword.text.toString())) ||
            !ValidateEmail.isEmail(etEmail.text.toString())) {
            bttLoging.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondaryColorDesvilitado
                )
            )
            bttLoging.isEnabled = false
        } else {
            bttLoging.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondaryColor
                )
            )
            bttLoging.isEnabled = true
        }
    }
    override fun onClick(view: View?) {
        when (view) {
            bttSingup -> navegarSingUp()
            bttLoging -> login("conDatos", view)
            bttForgotPassword -> resetPassword()
            bttAnonymousUser -> login("anonimo", view)
        }
    }
    private fun navegarSingUp() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, SingUpFragment())
        transaction.addToBackStack(null)
        transaction.commit()

    }
    private fun resetPassword() {
        val email = etEmail.text.toString()
        if (email.isNotBlank()) {
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            context, "Contraseña enviada a $email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context, "No se ha encontrado ningún usuario con este email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(context, "Introduce un correo", Toast.LENGTH_SHORT).show()
        }
    }
    private fun login(tipodeinicio: String, view: View) {
        when (tipodeinicio) {
            "conDatos" -> {
                mAuth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString().trim())
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            DataBase().getUserDocuments(etEmail.text.toString())
                            user.email = etEmail.text.toString()
                            goMainActivity(user.email!!)
                            etPassword.setText("")
                        } else {
                            Toast.makeText(
                                context, "El usuario no existe o usuario o contraseña " +
                                        "equivocados ", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            "anonimo" -> {
                view.isEnabled = false
                mAuth.signInAnonymously()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.email = "anonimo"
                            goMainActivity(user.email!!)
                        }
                    }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            if(!currentUser.isAnonymous){
                DataBase().getUserDocuments(it.email!!)
                goMainActivity("noAnonimo")
            }else{
                goMainActivity("anonimo")
            }
        }
    }
    private fun goMainActivity(s: String) {

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, s)
        }
        startActivity(intent)
    }


}