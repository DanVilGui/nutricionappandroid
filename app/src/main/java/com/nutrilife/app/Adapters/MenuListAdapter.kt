package com.nutrilife.app.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.edit
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.EvaluacionActivity
import com.nutrilife.app.Fragments.EstadisticasFragment
import com.nutrilife.app.LoginActivity
import com.nutrilife.app.R
import com.nutrilife.app.ViewHolders.MenuViewHolder
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuListAdapter(val act : Context, val list: List<String>)
    : RecyclerView.Adapter<MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MenuViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu:String = list[position]
        holder.bind(act,menu)
        holder.nContenedor?.setOnClickListener {
            val activity : AppCompatActivity = act as AppCompatActivity
            when(position ){
                0->{

                }
                1->{

                }
                2->{
                    //Mis Estadisticas
                    Log.e("myerror", "click contenedor menuw " + position.toString())
                    val navHostFragment:NavHostFragment =
                        activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    navHostFragment.navController.navigate(R.id.fragmentEstadisticas)
                    /*
                    activity.supportFragmentManager.commit {
                        replace( R.id.nav_host_fragment,    EstadisticasFragment())
                    }
                     */
                }
                3->{

                    val builder = AlertDialog.Builder(activity)
                    builder.setTitle("Salir")
                    builder.setMessage("Está seguro que desea cerrar sesión?")

                    val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                        when(which){
                            DialogInterface.BUTTON_POSITIVE ->{

                                val sharedPreferences:SharedPreferences = activity.getSharedPreferences(
                                    VAR.PREF_NAME,
                                    VAR.PRIVATE_MODE
                                )
                                sharedPreferences.edit {
                                    putString(VAR.PREF_TOKEN, "")
                                    putString(VAR.PREF_DATA_USUARIO, "")
                                }
                                Toasty.info(activity, "Gracias por usar la aplicación.", Toast.LENGTH_SHORT, false).show()
                                val intent = Intent(activity, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                activity.startActivity(intent)
                                activity.finish()
                            }
                        }
                    }
                    builder.setPositiveButton("SI",dialogClickListener)
                    builder.setNegativeButton("NO",dialogClickListener)
                    val dialog = builder.create()
                    dialog.show()

                }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}