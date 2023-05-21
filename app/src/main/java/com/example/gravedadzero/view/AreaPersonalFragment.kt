package com.example.gravedadzero.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.gravedadzero.databinding.FragmentAreaPersonalBinding
import com.example.gravedadzero.utils.ValidateEmail
import com.example.gravedadzero.view.LoginFragment.Companion.user
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class AreaPersonalFragment : Fragment() {

    lateinit var binding: FragmentAreaPersonalBinding

    lateinit var numBonos: TextView
    lateinit var userName: TextView
    lateinit var userEmail: TextView
    lateinit var userPassword: TextView
    lateinit var userNewPassword: TextView
    lateinit var guardar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAreaPersonalBinding.inflate(inflater, container, false)

        numBonos = binding.numeroBonosTF
        userName = binding.userNick
        userEmail = binding.email
        userPassword = binding.password
        userNewPassword = binding.newPassword
        guardar = binding.actualizarBTT

        //Validaciones
        manageButonGuardar()
        userEmail.doOnTextChanged { text, start, before, count -> manageButonGuardar() }
        userPassword.doOnTextChanged { text, start, before, count -> manageButonGuardar() }
        userNewPassword.doOnTextChanged { text, start, before, count -> manageButonGuardar() }

        guardar.setOnClickListener {
            actualizarUser()
        }
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    private fun manageButonGuardar() {
        guardar.isEnabled = !((TextUtils.isEmpty(userPassword.text.toString())) ||
                (TextUtils.isEmpty(userNewPassword.text.toString()))
                || (!ValidateEmail.isEmail(userEmail.text.toString())))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numBonos.text = user.bonos.toString()
        userName.text = user.nick
        userEmail.text = user.email
    }

    private fun actualizarUser() {
        val u = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider
            .getCredential(userEmail.text.toString(), userPassword.text.toString())

        u?.reauthenticate(credential)?.addOnCompleteListener(OnCompleteListener<Void>() { task ->
            if (task.isSuccessful()) {
                u.updatePassword(userNewPassword.text.toString()).addOnCompleteListener(OnCompleteListener<Void>() { task ->
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                            userPassword.text = ""
                            userNewPassword.text = ""
                        } else {
                            Toast.makeText(requireContext(), "No se ha podido actualizar la contraseña", Toast.LENGTH_SHORT).show()
                        }
                })
            } else {
                Toast.makeText(requireContext(), "No se ha podido actualizar la contraseña", Toast.LENGTH_SHORT).show()
            }
        })
    }


}