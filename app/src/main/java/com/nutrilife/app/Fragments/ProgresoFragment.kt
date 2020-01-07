package com.nutrilife.app.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
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
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.DeporteListAdapter
import com.nutrilife.app.Adapters.MedidaListAdapter
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.MainActivity
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.rutina_deportes.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ProgresoFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var adaptador :MedidaListAdapter ? = null
    var listaMedida :LinkedList<ClsMedida> = LinkedList()
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_progreso, container, false)
        Handler().removeCallbacksAndMessages(null)
        val mainActivity = activity as MainActivity
        mainActivity.verificarFinDieta()
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val recyclerView:RecyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarMedidas()
        }
        adaptador = MedidaListAdapter(activity!!,listaMedida)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        buscarMedidas()
        return view
    }

    fun buscarMedidas(){
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_medidas"), null,
            Response.Listener { response ->
                if(response!=null){
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                       // loadingDialog?.dismiss()
                        val medidas = response.getJSONArray("medidas")
                        listaMedida.clear()
                        for (i in 0 until medidas.length()) {
                            val medidaJson = medidas.getJSONObject(i)
                            val medida = ClsMedida( medidaJson.getInt("idmedida"),
                                medidaJson.getInt("idpersona"),medidaJson.getDouble("peso"),
                                medidaJson.getDouble("medida"),medidaJson.getInt("cintura"),
                                medidaJson.getInt("cadera"), medidaJson.getString("fecha"))
                            listaMedida.add(medida)
                        }
                        adaptador?.notifyDataSetChanged()

                    }else{
                        //PROCESAR_AGREGAR = false
                       /// loadingDialog?.dismiss()
                        Toasty.warning(activity!!, message, Toast.LENGTH_SHORT, true).show()
                    }

                    swipeRefreshLayout?.isRefreshing = false
                }

            },
            Response.ErrorListener{
                try {
                    //PROCESAR_AGREGAR = true
                    //loadingDialog?.dismiss()
                    swipeRefreshLayout?.isRefreshing = false
                    Toasty.error(activity!!, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
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


        val requestQueue =  Volley.newRequestQueue(activity!!)
        requestQueue?.add(request)


    }


}