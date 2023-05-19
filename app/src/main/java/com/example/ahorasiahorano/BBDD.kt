package com.example.ahorasiahorano

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BBDD(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE login (usuario text primary key, password text)")
        db.execSQL("CREATE TABLE parcelas (usuario text primary key, imagen longblob, latitud real, longitud real)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}