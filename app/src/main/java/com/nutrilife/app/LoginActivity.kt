package com.nutrilife.app

import `in`.championswimmer.libsocialbuttons.BtnSocial
import android.R.attr
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.nutrilife.app.Clases.ClsLoginTipo
import com.nutrilife.app.Clases.ClsPersona
import com.nutrilife.app.Clases.VAR
import es.dmoral.toasty.Toasty
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class LoginActivity: AppCompatActivity() {
    var loadingDialog: Dialog? = null
    var sharedPref: SharedPreferences? = null
    var callbackManager: CallbackManager? = null
    var mGoogleSignInClient:GoogleSignInClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        this.supportActionBar?.hide()

        callbackManager = CallbackManager.Factory.create()

        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        /*
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired()
        if(isLoggedIn){
            mostrarMainActivity()
        }
         */
        loginFacebook()
        if(sharedPref?.getString(VAR.PREF_DATA_USUARIO, "") != "") {
            mostrarMainActivity()
        }
        Toasty.Config.getInstance()
            .allowQueue(false)
            .apply()

        val btnLoginFacebook: BtnSocial = findViewById(R.id.btnLoginFacebook)
        btnLoginFacebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        }
        val btnLoginGoogle:BtnSocial = findViewById(R.id.btnLoginGoogle)
        btnLoginGoogle.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient!!.getSignInIntent()
            startActivityForResult(signInIntent, 101)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        if (requestCode === 101) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            loginGoogle(task)
        }else{
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun mostrarEspereDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setView(R.layout.loading_dialog)
        loadingDialog = builder.create()
        loadingDialog?.setCancelable(false)
        loadingDialog?.setCanceledOnTouchOutside(false)
        loadingDialog?.show()
    }

    fun loginFacebook(){
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) { // App code
                mostrarEspereDialog()
                val accessToken = AccessToken.getCurrentAccessToken()
                val request =
                    GraphRequest.newMeRequest(
                        accessToken
                    ) { `object`, response ->
                        //OnCompleted is invoked once the GraphRequest is successful
                        try {
                            Log.e("error", `object`.toString())
                            val nombres = `object`.getString("first_name")
                            val apellidos = `object`.getString("last_name")
                            val correo = `object`.getString("email")
                            val idtipo = ClsLoginTipo.FACEBOOK

                            /*
                            val image =
                                `object`.getJSONObject("picture").getJSONObject("data")
                                    .getString("url")

                             */

                            val persona = ClsPersona(-1,idtipo,nombres, apellidos, correo)
                            registrarPersonaServicio(persona)

                        } catch (e: JSONException) {
                            e.printStackTrace()
                            loadingDialog?.dismiss()
                        }

                    }

                val parameters = Bundle()
                parameters.putString("fields", "id,name,first_name, last_name,email,picture.width(200)")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() { // App code
                loadingDialog?.dismiss()
            }

            override fun onError(exception: FacebookException) { // App code
                Toasty.error(applicationContext, "Error de autenticación con Facebook.", Toast.LENGTH_SHORT, true).show()
                loadingDialog?.dismiss()
            }
        })
    }

    fun loginGoogle(completedTask:Task<GoogleSignInAccount>){

        try {
            mostrarEspereDialog()
            val account = completedTask.getResult(ApiException::class.java)

                val nombres = account?.givenName
            val apellidos = account?.familyName
            val correo = account?.email
            val idtipo = ClsLoginTipo.GOOGLE

            /*
            val image =
                `object`.getJSONObject("picture").getJSONObject("data")
                    .getString("url")

             */

            val persona = ClsPersona(-1,idtipo,nombres!!, apellidos!!, correo!!)
            registrarPersonaServicio(persona)
        } catch ( e:Exception) {
            loadingDialog?.dismiss()
            Toasty.error(applicationContext, "Error de autenticación con Google.", Toast.LENGTH_SHORT, true).show()
            Log.e("myerror", "signInResult:failed code=" + e.message)
        }

    }

    fun mostrarMainActivity(){
        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if(datosPersona!=""){
            val data = JSONObject(datosPersona)
            Log.e("myerror", data.toString())
            if( data.isNull("fecha_nacimiento") ){
                val intent = Intent(applicationContext, SexoEdadActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else if( data.isNull("medidas") ){
                val intent = Intent(applicationContext, ActualizarMedidasActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }else if( data.isNull("rutina") ){
                val intent = Intent(applicationContext, RutinasConocerActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }else if( data.isNull("preferencia") ){
                val intent = Intent(applicationContext, PreferenciasActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else{
                val rutina = data.getJSONObject("rutina")
                if(rutina.getString("recalcular").toInt() == 1){
                    val intent = Intent(applicationContext, EvaluacionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }

            }
        }


    }


    fun registrarPersonaServicio(persona:ClsPersona){

            val parameters = JSONObject(persona.registrarBasico() as Map<String, String>)
            Log.e("myerror", parameters.toString())
            val request = JsonObjectRequest(
                Request.Method.POST, VAR.url("registrar"), parameters,
                Response.Listener { response ->

                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                        val datosPersona = response.getJSONObject("datos")
                        sharedPref?.edit {
                            putString(VAR.PREF_TOKEN, datosPersona.getString("token"))
                            putString(VAR.PREF_DATA_USUARIO, datosPersona.toString())
                        }
                        Toasty.success(applicationContext, message, Toast.LENGTH_LONG, true).show()
                        mostrarMainActivity()
                    }else{
                        Toasty.warning(applicationContext, message, Toast.LENGTH_LONG, true).show()
                    }
                    loadingDialog?.dismiss()
                },
                Response.ErrorListener{
                    try {
                        Toasty.error(applicationContext, "Error de conexión.", Toast.LENGTH_LONG, true).show()
                        loadingDialog?.dismiss()
                        Log.e("myerror",  (it.message))
                        val nr = it.networkResponse
                        val r = String(nr.data)
                    }catch (ex:Exception){
                        Log.e("myerror", ex.message.toString())

                    }
                })


            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(request)
    }
}