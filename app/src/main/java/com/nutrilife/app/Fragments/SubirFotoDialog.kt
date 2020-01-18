package com.nutrilife.app.Fragments

import android.Manifest
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import id.zelory.compressor.Compressor
import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File


class SubirFotoDialog: DialogFragment()  , EasyPermissions.PermissionCallbacks{
    var btnSubir:Button? = null
    var imagenPerfil:ImageView? = null
    var encodedImage:String? = null
    var sharedPref: SharedPreferences? = null
    var lastClick:Long = 0
    var lastClickBuscar:Long = 0

    var loadingDialog: Dialog? = null
    var imagenReferencial:Bitmap? = null
    var buscarFoto = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view =  inflater.inflate(R.layout.dialog_subirimagen, container)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        val btnClose:ImageView = view.findViewById(R.id.close)
        btnClose.setOnClickListener{
            dismiss()
        }


        imagenPerfil = view.findViewById(R.id.imagenPerfil)
        if(imagenReferencial!=null){
            imagenPerfil?.setImageBitmap(imagenReferencial)
        }
        btnSubir = view.findViewById(R.id.btnSubir)
        btnSubir?.visibility  = View.GONE
        val btnSeleccionar: Button = view.findViewById(R.id.btnSeleccionar)
        btnSeleccionar.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickBuscar >= 1000){
                if(buscarFoto){
                    buscarFoto = false
                    storageTask()

                }
            }
            lastClickBuscar = SystemClock.elapsedRealtime()
        }
        val btnSubir: Button = view.findViewById(R.id.btnSubir)
        btnSubir.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick >= 1000){
               subirFotoWs()
            }
            lastClick = SystemClock.elapsedRealtime()
        }


        return view
    }

    fun subirFotoWs(){
        if(encodedImage == null){
            Toasty.warning(activity!!, "Seleccione una Imagen", Toast.LENGTH_SHORT, true).show()
            return
        }
        Log.e("myerror", encodedImage.toString())
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(R.layout.loading_dialog)
        loadingDialog = builder.create()
        loadingDialog?.setCancelable(false)
        loadingDialog?.setCanceledOnTouchOutside(false)
        loadingDialog?.show()

        val parameters = JSONObject()
        parameters.put("imagen", encodedImage)

        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_cambiarimagen"),parameters,
            Response.Listener { response ->
                if(response!=null){
                    val success = response.getBoolean("success")
                    val message = response.getString("message")
                    if(success){
                        Toasty.success(activity!!, message, Toast.LENGTH_LONG, true).show()
                        val parent = targetFragment as MenuFragment
                        parent.cargarImagen()
                        this.dismiss()
                    }else{
                        Toasty.warning(activity!!, message, Toast.LENGTH_LONG, true).show()
                    }
                    loadingDialog?.dismiss()

                }

            },
            Response.ErrorListener{
                try {
                    loadingDialog?.dismiss()
                    Toasty.error(activity!!, "Error de conexión.", Toast.LENGTH_LONG, true).show()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    loadingDialog?.dismiss()
                    Log.e("myerror", ex.message.toString())

                }

            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> =HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }
        val socketTimeout = 25000

        val policy: RetryPolicy = DefaultRetryPolicy(
            socketTimeout,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        request.retryPolicy = policy

        val requestQueue = Volley.newRequestQueue(activity!!)
        requestQueue.add(request)
    }

    private fun getRealPathFromURI( contentURI:Uri):Long {
       val scheme = contentURI.scheme
        var size:Long = 0
        System.out.println("Scheme type " + scheme);
        if(scheme.equals(ContentResolver.SCHEME_CONTENT))
        {
            try {
                val fileInputStream= activity?.contentResolver?.openInputStream(contentURI)
                size = fileInputStream?.available()!!.toLong()
                return size
            } catch (e:Exception) {
                e.printStackTrace()
                return 0
            }
        }
        else if(scheme.equals(ContentResolver.SCHEME_FILE))
        {
            val path = contentURI.path
            try {
               val f =  File(path)
                return f.length()
            } catch ( e:Exception) {
                e.printStackTrace()
                return 0
            }

        }

        return 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC_SELECCION_IMAGEN){
            val uri = data!!.data
            if(uri !=null){
                try {
                    var valid = true
                    val size = getRealPathFromURI(uri)

                    if(size>0){
                        val nsize = size/(1024.0*1024)
                        Log.e("myerror", "tamanio $nsize")

                        if(nsize>2.0){
                            Toasty.warning(activity!!, "El archivo supera 2mb!!", Toast.LENGTH_LONG, true).show()
                            valid = false
                        }
                    }

                    if(valid){


                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                        val baos =  ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos)
                        val decoded = BitmapFactory.decodeStream( ByteArrayInputStream(baos.toByteArray()))

                        imagenPerfil?.setImageBitmap(decoded)

                        val imageBytes = baos.toByteArray()
                        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                        btnSubir?.visibility  = View.VISIBLE
                    }


                }catch (ex:Exception){
                    btnSubir?.visibility  = View.GONE
                    encodedImage = null
                }

            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

      @AfterPermissionGranted(RC_CAMERA)
    private fun cameraTask() {
        if (EasyPermissions.hasPermissions(
                context!!,
                Manifest.permission.CAMERA
            )
        ) { // Have permission, do the thing!
            //Toast.makeText(activity, "TODO: SMS things", Toast.LENGTH_LONG).show()
        } else { // Request one permission
            EasyPermissions.requestPermissions(
                this, getString( R.string.pedir_camera),
                RC_CAMERA, Manifest.permission.CAMERA
            )
        }
    }
    @AfterPermissionGranted(RC_STORAGE)
    private fun storageTask() {
        if (EasyPermissions.hasPermissions(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) { // Have permission, do the thing!


            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mimeTypes =
                    arrayOf("image/jpeg", "image/png")
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
             startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.label_seleccione_imagen)
                ), RC_SELECCION_IMAGEN
            )

            buscarFoto = true




        } else { // Request one permission
            EasyPermissions.requestPermissions(
                this, getString( R.string.pedir_camera),
                RC_CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //Log.e("myerror", "onPermissionsDenied:" + requestCode + ":" + perms.size)

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //Log.e("myerror", "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }




    companion object {
        const val RC_CAMERA = 101
        const val RC_STORAGE = 102
        const val RC_SELECCION_IMAGEN= 201
    }
}