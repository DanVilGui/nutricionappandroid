package com.nutrilife.app.Fragments

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.DeporteListAdapter
import com.nutrilife.app.Adapters.MedidaListAdapter
import com.nutrilife.app.Adapters.MenuListAdapter
import com.nutrilife.app.Clases.ClsMedida
import com.nutrilife.app.Clases.ClsPersona
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.rutina_deportes.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import org.json.JSONObject
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class TipsFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val toolbar:Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarEstadisticas()
        }
        buscarEstadisticas()

        return view
    }

    fun buscarEstadisticas(){


        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_estadistica"), null,
            Response.Listener { response ->

                val success = response.getBoolean("success")
                val message = response.getString("message")

                if(success){
                    val estadistica = response.getJSONObject("estadistica")

                }
                swipeRefreshLayout?.isRefreshing = false
            },
            Response.ErrorListener{
                try {
                    swipeRefreshLayout?.isRefreshing = true
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


        val requestQueue = Volley.newRequestQueue(activity!!)
        requestQueue.add(request)
    }

}