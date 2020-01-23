package com.nutrilife.app.Fragments

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.MedidaListAdapter
import com.nutrilife.app.Adapters.TipsListAdapter
import com.nutrilife.app.Clases.ClsPost
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.item_post_resumen.*
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.util.*

class PostFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var idPost = -1
    var liked = false
    var corazonHeart:ImageView?= null
    var image:String = ""
    var imagenView:ImageView ? = null
    var requestQueue:RequestQueue ? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        activity?.actionBar?.hide()
        val toolbar:Toolbar = view.findViewById(R.id.toolbar)
        requestQueue = Volley.newRequestQueue(activity)

        val compat = activity as AppCompatActivity
        compat.actionBar?.hide()
        imagenView = view.findViewById(R.id.imagen)
        val texto:TextView = view.findViewById(R.id.texto)
        val titulo:TextView = view.findViewById(R.id.titulo)
        corazonHeart = view.findViewById(R.id.likebutton)

        val b = this.arguments
        if(b!=null){
            texto.text = b.getString("texto")
            titulo.text = b.getString("titulo")
             image = b.getString("image","")

            liked = b.getBoolean("like")
            idPost = b.getInt("idpost")
            if(b.getByteArray("imagen")!=null){
                val arr = b.getByteArray("imagen")
                val decoded = BitmapFactory.decodeStream( ByteArrayInputStream( arr))
                imagenView?.setImageBitmap(decoded)
            }else{
                cargarImagen()
            }
        }
        cambioLike(liked)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        corazonHeart?.setOnClickListener {
            cambioLike(!liked)
        }

        return view
    }

    fun cambioLike(b:Boolean){

        if(b){
            corazonHeart?.setImageDrawable(activity!!.resources.getDrawable(R.drawable.corazon_rojo))
        }else{
            corazonHeart?.setImageDrawable(activity!!.resources.getDrawable(R.drawable.corazon))
        }
        liked = b

        val parameters = JSONObject()
        parameters.put("idpost", idPost )
        parameters.put("like", liked)
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("post_like"), parameters,
            Response.Listener { response ->
                val success = response.getBoolean("success")
                val message = response.getString("message")
            },
            Response.ErrorListener{
                try {
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


        requestQueue?.add(request)

    }

    fun cargarImagen(){
        val url =   VAR.urlExt("post_imagen.php","?f=${image}")

        val requestImagen : ImageRequest = object : ImageRequest(
            url, Response.Listener { response ->
                imagenView?.setImageBitmap(response)
            }, 0, 0, null, Bitmap.Config.RGB_565,
            Response.ErrorListener{
                try {
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())
                }
            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                return params
            }
        }
        requestQueue?.add(requestImagen)
    }

}