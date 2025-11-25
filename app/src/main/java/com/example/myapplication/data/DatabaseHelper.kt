package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "notes.db"
        const val DB_VERSION = 4 // <-- incrementado para añadir priority
        const val TABLE = "notes"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_CONTENT = "content"
        const val COL_DATE = "date"
        const val COL_IMPORTANT = "important"
        const val COL_RATING = "rating"
        const val COL_PRIORITY = "priority" // <-- nueva columna (INTEGER)
    }

    override fun onCreate(db: SQLiteDatabase) {
        val create = """
            CREATE TABLE $TABLE (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT,
                $COL_CONTENT TEXT,
                $COL_DATE TEXT,
                $COL_IMPORTANT INTEGER DEFAULT 0,
                $COL_RATING REAL DEFAULT 0,
                $COL_PRIORITY INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLE ADD COLUMN $COL_IMPORTANT INTEGER DEFAULT 0")
            } catch (e: Exception) {

            }
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE $TABLE ADD COLUMN $COL_RATING REAL DEFAULT 0")
            } catch (e: Exception) {

            }
        }
        // migración para priority
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE $TABLE ADD COLUMN $COL_PRIORITY INTEGER DEFAULT 0")
            } catch (e: Exception) {
                // si falla, ignorar 
            }
        }
    }

    fun insert(note: Note): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, note.title)
            put(COL_CONTENT, note.content)
            put(COL_DATE, note.date)
            put(COL_IMPORTANT, if (note.important) 1 else 0)
            put(COL_RATING, note.rating)
            put(COL_PRIORITY, note.priority)
        }
        return db.insert(TABLE, null, values)
    }

    fun update(note: Note): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, note.title)
            put(COL_CONTENT, note.content)
            put(COL_DATE, note.date)
            put(COL_IMPORTANT, if (note.important) 1 else 0)
            put(COL_RATING, note.rating)
            put(COL_PRIORITY, note.priority)
        }
        return db.update(TABLE, values, "$COL_ID = ?", arrayOf(note.id.toString()))
    }

    fun delete(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun listAll(): List<Note> {
        val db = readableDatabase
        val cursor = db.query(TABLE, null, null, null, null, null, "$COL_ID DESC")
        val list = mutableListOf<Note>()
        cursor.use {
            while (it.moveToNext()) {
                val n = Note(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_TITLE)),
                    content = it.getString(it.getColumnIndexOrThrow(COL_CONTENT)),
                    date = it.getString(it.getColumnIndexOrThrow(COL_DATE)),
                    important = try { it.getInt(it.getColumnIndexOrThrow(COL_IMPORTANT)) != 0 } catch (e: Exception) { false },
                    rating = try { it.getFloat(it.getColumnIndexOrThrow(COL_RATING)) } catch (e: Exception) {
                        try { it.getDouble(it.getColumnIndexOrThrow(COL_RATING)).toFloat() } catch (ex: Exception) { 0f }
                    },
                    priority = try { it.getInt(it.getColumnIndexOrThrow(COL_PRIORITY)) } catch (e: Exception) { 0 }
                )
                list.add(n)
            }
        }
        return list
    }
}
