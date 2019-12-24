package com.nutrilife.app.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.DeporteListAdapter
import com.nutrilife.app.Adapters.DietaBlockListAdapter
import com.nutrilife.app.Adapters.MedidaListAdapter
import com.nutrilife.app.Adapters.MenuListAdapter
import com.nutrilife.app.Clases.*
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.rutina_deportes.*
import org.json.JSONObject
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class HoyFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var adaptador :DietaBlockListAdapter ? = null
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    var listaDietas :LinkedList<ClsDietaBlock> = LinkedList()
    var txtNombre:TextView?=null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hoy, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )

      //  txtNombre = view.findViewById(R.id.nombre)
        val recyclerView:RecyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
         buscarDatos()
        }
       buscarDatos()

        //listaMenus.addAll(asList("Modificar mi rutina","Tips de nutrición", "Mis Estadísticas", "Cerrar Sesión"))

        adaptador = DietaBlockListAdapter(activity!!,listaDietas)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }

        /*
        val recyclerView:RecyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarMedidas()
        }
        adaptador = MedidaListAdapter(activity!!,listaMedida)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }/*


         */
         */
     //   buscarMedidas()
        return view
    }

    fun buscarDatos(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_dieta_fecha"), null,
            Response.Listener { response ->

                val success = response.getBoolean("success")
                if(success){
                    val dietas = response.getJSONArray("dieta")
                    val horarios = response.getJSONArray("horarios")
                    val listaDietaHorario = LinkedList<ClsDietaHorario>()
                    for ( j in 0 until  dietas.length()){
                        val dieta  = dietas.getJSONObject(j)
                            listaDietaHorario.add(ClsDietaHorario(dieta.getString("producto"),
                                dieta.getInt("idhorario"), dieta.getDouble("cantidad"),
                                dieta.getString("medida"), dieta.getString("fecha")))
                    }

                    listaDietas.clear()
                    for (i in 0 until horarios.length()) {
                        val horarioStr = horarios.getJSONObject(i)
                        val idhorario = horarioStr.getInt("id")
                        val filtro = listaDietaHorario.filter{ it.idhorario == idhorario}
                        val horario = ClsHorario(idhorario, horarioStr.getString("nombre"))
                        listaDietas.add(ClsDietaBlock(horario, filtro))
                    }

                    adaptador?.notifyDataSetChanged()

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