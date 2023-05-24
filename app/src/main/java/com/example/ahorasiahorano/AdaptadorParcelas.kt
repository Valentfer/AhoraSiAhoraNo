package com.example.ahorasiahorano

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorParcelas(
    private val listaParcelas: List<DatosParcela>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<AdaptadorParcelas.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(datosParcela: DatosParcela)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
        // val layoutInflater = LayoutInflater.from(parent.context)
        //   return ViewHolder(layoutInflater.inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount(): Int {
        return listaParcelas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listaParcelas[position])

    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun render(datosParcela: DatosParcela) {

            view.findViewById<TextView>(R.id.tvCardDir).text = datosParcela.dir
            view.findViewById<TextView>(R.id.tvCardRef).text = datosParcela.refeCat
            view.findViewById<TextView>(R.id.tvCardMun).text = datosParcela.municipio
            view.findViewById<CardView>(R.id.Cardview).setOnClickListener {
                listener.onItemClick(datosParcela)
            }
            val bitmap = datosParcela.parcela.imagen
            val bitbytearra = Base64.decode(bitmap, Base64.DEFAULT)
            val imagenbit = BitmapFactory.decodeByteArray(bitbytearra, 0, bitbytearra.size)
            view.findViewById<ImageView>(R.id.image).setImageBitmap(imagenbit)
        }
    }

}
