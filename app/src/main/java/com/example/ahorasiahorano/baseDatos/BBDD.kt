package com.example.ahorasiahorano.baseDatos

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
/*
* Base de datos para guardar usuarios y otra tabla donde se almacena las parcelas
* */
class BBDD(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE login (usuario text primary key, password text)")
        db.execSQL("CREATE TABLE parcelas (usuario text, imagen text, latitud real, longitud real)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}