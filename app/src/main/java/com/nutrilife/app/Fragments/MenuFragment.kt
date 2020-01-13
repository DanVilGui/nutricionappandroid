package com.nutrilife.app.Fragments

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.DeporteListAdapter
import com.nutrilife.app.Adapters.MedidaListAdapter
import com.nutrilife.app.Adapters.MenuListAdapter
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.Clases.ClsPersona
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Clases.VAR.Companion.url
import com.nutrilife.app.MainActivity
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.rutina_deportes.*
import org.json.JSONObject
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class MenuFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var adaptador :MenuListAdapter ? = null
    var listaMenus :LinkedList<String> = LinkedList()
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    var txtNombre:TextView?=null

    var fotoUsuario:ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        Handler().removeCallbacksAndMessages(null)
        val mainActivity = activity as MainActivity
        mainActivity.verificarFinDieta()
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        txtNombre = view.findViewById(R.id.nombre)
        val recyclerView:RecyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
           buscarDatos()
        }
        cargarImagen()
        buscarDatos()
        listaMenus.clear()
        listaMenus.addAll(asList("Modificar mi rutina","Tips de nutrición", "Mis Estadísticas", "Cerrar Sesión"))
        adaptador = MenuListAdapter(activity!!,listaMenus)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }


        fotoUsuario = view.findViewById(R.id.fotousuario)
        fotoUsuario?.setOnClickListener {
            val dialog = SubirFotoDialog()
            dialog.setTargetFragment(this, 130)
            dialog.show(fragmentManager!!, "subirfoto")
        }



        return view
    }

    fun cargarImagen (){
        val request : ImageRequest = object : ImageRequest(
            url("persona_imagen"),
            Response.Listener { response ->

                fotoUsuario?.setImageBitmap(response)
            }, 0, 0, null, Bitmap.Config.RGB_565,
            Response.ErrorListener{
                try {
                    Toasty.error(activity!!, "Error al mostrar la imagen.", Toast.LENGTH_LONG, true).show()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())
                }
            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(activity!!)
        requestQueue.add(request)
    }

    fun buscarDatos(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_datos"), null,
            Response.Listener { response ->
                val success = response.getBoolean("success")
                if(success){
                    val datosPersona = response.getJSONObject("datos")
                    sharedPref?.edit {
                        putString(VAR.PREF_DATA_USUARIO, datosPersona.toString())
                    }
                    val nombreCompleto :String = datosPersona.getString("nombres")+" "+
                            datosPersona.getString("apellidos")
                    txtNombre?.text = nombreCompleto

                }
                swipeRefreshLayout?.isRefreshing = false
            },
            Response.ErrorListener{
                try {
                    swipeRefreshLayout?.isRefreshing = true
                    Toasty.error(activity!!, "Error de conexión.", Toast.LENGTH_LONG, true).show()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())
                }
            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }


        val requestQueue = Volley.newRequestQueue(activity!!)
        requestQueue.add(request)
    }

}