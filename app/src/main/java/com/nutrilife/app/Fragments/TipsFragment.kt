package com.nutrilife.app.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.MedidaListAdapter
import com.nutrilife.app.Adapters.TipsListAdapter
import com.nutrilife.app.Clases.ClsPost
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.util.*

class TipsFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    var listaTips :LinkedList<ClsPost> = LinkedList()
    var adaptador :TipsListAdapter ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tips, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )


        val toolbar:Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarTips()
        }
        adaptador = TipsListAdapter(activity!!,listaTips)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }
        buscarTips()

        return view
    }

    fun buscarTips(){

        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("post_listar"), null,
            Response.Listener { response ->

                val success = response.getBoolean("success")
                val message = response.getString("message")
                listaTips.clear()
                if(success){
                    val tips = response.getJSONArray("posts")
                    for (i in 0 until tips.length()) {
                        val json = tips.getJSONObject(i)
                        Log.e("myerror", "liked"+ json.getInt("liked").toString())
                        val ti = ClsPost(json.getInt("idpost"), json.getString("titulo"),
                            json.getString("texto"), json.getString("resumen"), json.getString("imagen"),
                            json.getInt("likes"))
                        ti.liked = json.getInt("liked") == 1
                        listaTips.add(ti)
                    }
                }
                adaptador?.notifyDataSetChanged()
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