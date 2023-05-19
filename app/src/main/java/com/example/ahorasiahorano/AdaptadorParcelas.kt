package com.example.ahorasiahorano

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorParcelas(val listaParcelas: List<Parcela>): RecyclerView.Adapter<AdaptadorParcelas.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_card,parent, false))
    }

    override fun getItemCount(): Int {
        return listaParcelas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listaParcelas[position])
    }
    class ViewHolder(val view: View):RecyclerView.ViewHolder(view){

        lateinit var codPostaCard: String
        lateinit var ccaaCard: String
        lateinit var municipioCard: String
        lateinit var referenciaCard: String
        lateinit var dirCard: String

        fun render(parcela: Parcela){

            val datos = ObtenerRefCat(parcela.Longitud, parcela.latitud)
            datos.getRefCatastral {ref ->
                referenciaCard = ref
            }
            datos.getRefCatastral {dir ->
                dirCard = dir
            }
            datos.getRefCatastral {ccaa ->
                ccaaCard = ccaa
            }
            datos.getRefCatastral {municipio ->
                municipioCard = municipio
            }
            datos.getRefCatastral {cp ->
                codPostaCard = cp
            }

            view.findViewById<TextView>(R.id.tvCardCP).text = codPostaCard
            view.findViewById<TextView>(R.id.tvCardRef).text = referenciaCard
            view.findViewById<TextView>(R.id.tvCardCcaa).text = ccaaCard
            view.findViewById<ImageView>(R.id.image).setImageBitmap(parcela.imagen)
        }
    }

}
