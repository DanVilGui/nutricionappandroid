package com.nutrilife.app.Adapters

import android.R.attr.button
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Clases.ClsPost
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Fragments.EstadisticasFragment
import com.nutrilife.app.MainActivity
import com.nutrilife.app.R
import com.nutrilife.app.ViewHolders.TipViewHolder
import com.sackcentury.shinebuttonlib.ShineButton
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*


class TipsListAdapter(val act : Context, val list: List<ClsPost>)
    : RecyclerView.Adapter<TipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TipViewHolder(inflater, parent)
    }



    private fun makeTextLink(textView: TextView, str: String, underlined: Boolean, color: Int?, action: (() -> Unit)? = null) {
        val spannableString = SpannableString(textView.text)
        val textColor = color ?: textView.currentTextColor
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                action?.invoke()
            }
            override fun updateDrawState(drawState: TextPaint) {
                super.updateDrawState(drawState)
                drawState.isUnderlineText = underlined
                drawState.color = textColor
            }
        }
        val index = spannableString.indexOf(str)
        spannableString.setSpan(clickableSpan, index, index + str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT
    }


    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val requestQueue = Volley.newRequestQueue(act)

        val tip = list[position]
        holder.nTitulo?.text = tip.titulo
        val frase = "Continuar leyendo"
        val resumen = tip.resumen +frase
        var spanned:Spanned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            spanned = Html.fromHtml(resumen, Html.FROM_HTML_MODE_LEGACY)
        } else {
            spanned = Html.fromHtml(resumen)
        }
        holder.nTexto?.text = spanned

        makeTextLink(holder.nTexto!! ,frase , false, Color.BLUE,
            action = {
                val activity = act as AppCompatActivity
                val navHostFragment: NavHostFragment =
                    activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val b = Bundle()
                b.putInt("idpost", tip.idpost)
                b.putString("texto", tip.texto)
                b.putString("titulo", tip.titulo)
                b.putBoolean("like", tip.liked)
                b.putString("image", tip.imagen)

                if(tip.cargoImagen){
                    val bmap = holder.nImagen?.drawable!!.toBitmap()
                    val baos =  ByteArrayOutputStream()
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                    b.putByteArray("imagen", baos.toByteArray())
                }

                navHostFragment.navController.navigate(R.id.fragmentPost,b)
            }
        )


        holder.likeButton?.isChecked = tip.liked
        holder.nContador?.text = holder.cambiarContador(tip.likes)

        holder.likeButton?.setOnCheckStateChangeListener { view, checked ->
            var likes = tip.likes
            if(checked){
                tip.liked = true
                likes += 1
            }else{
                tip.liked= false
                likes -=1
            }
            tip.likes = likes
            holder.nContador?.text = holder.cambiarContador(likes)
            //holder.likeButton?.isChecked = !checked
            val sharedPreferences: SharedPreferences = act.getSharedPreferences(
                VAR.PREF_NAME,
                VAR.PRIVATE_MODE
            )

            val parameters = JSONObject()
            parameters.put("idpost", tip.idpost )
            parameters.put("like", checked)
            val request : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, VAR.url("post_like"), parameters,
                Response.Listener { response ->
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    tip.cargoImagen = true
                },
                Response.ErrorListener{
                    try {
                        Toasty.error(act, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
                        Log.e("myerror",  (it.message))
                        val nr = it.networkResponse
                        val r = String(nr.data)
                    }catch (ex:Exception){
                        Log.e("myerror", ex.message.toString())
                    }
                }) {
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String> = HashMap()
                    params["TOKEN"] =  sharedPreferences.getString("token", "")!!
                    return params
                }
            }


            requestQueue.add(request)

        }



        val url =   VAR.urlExt("post_imagen.php","?f=${tip.imagen}")
        Log.e("myerror",  url)

        val request : ImageRequest = object : ImageRequest(
        url, Response.Listener { response ->
                holder.nImagen?.setImageBitmap(response)
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
        requestQueue.add(request)
    }

    override fun getItemCount(): Int = list.size

}