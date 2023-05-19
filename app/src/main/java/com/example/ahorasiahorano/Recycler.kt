package com.example.ahorasiahorano

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class Recycler : AppCompatActivity() {

    private val parcelas: List<Parcela> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler)
        init()

    }
    private fun init(){
        val adapter = AdaptadorParcelas(parcelas)
        val recycler = findViewById<RecyclerView>(R.id.rcRecycler)
        recycler.adapter = adapter
    }
}