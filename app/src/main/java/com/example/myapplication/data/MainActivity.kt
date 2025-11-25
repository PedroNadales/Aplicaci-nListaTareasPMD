package com.example.myapplication.data


import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.NoteAdapter
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var db: DatabaseHelper
    private lateinit var adapter: NoteAdapter
    private var selectedNote: Note? = null
    private val prefsName = "app_prefs"
    private val keyLastId = "last_note_id"


    // Vistas
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var btnPickDate: Button
    private lateinit var spinnerSort: Spinner
    private lateinit var cbImportant: CheckBox
    private lateinit var rating: RatingBar
    private lateinit var seekPriority: SeekBar
    private lateinit var rvNotes: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DatabaseHelper(this)


        // findViewById para todas las vistas
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnAdd = findViewById(R.id.btnAdd)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDelete = findViewById(R.id.btnDelete)
        btnPickDate = findViewById(R.id.btnPickDate)
        spinnerSort = findViewById(R.id.spinnerSort)
        cbImportant = findViewById(R.id.cbImportant)
        rating = findViewById(R.id.rating)
        seekPriority = findViewById(R.id.seekPriority)
        rvNotes = findViewById(R.id.rvNotes)


        // RecyclerView: pasar también onLongClick para alternar importancia
        adapter = NoteAdapter(listOf(), { note -> onNoteSelected(note) }, { note -> toggleImportant(note) })
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter


        // Spinner: opciones de orden/filtro combinadas (ahora sólo ordenamientos solicitados)
        val sortOptions = listOf("Más reciente", "Más antiguo", "Más prioridad", "Menos prioridad")
        spinnerSort.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                loadNotes()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        // Load data (usa la ordenación actual)
        loadNotes()


        // Restore last selected ID from prefs
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val lastId = prefs.getLong(keyLastId, -1)
        if (lastId != -1L) {
            db.listAll().firstOrNull { it.id == lastId }?.let { onNoteSelected(it) }
        }


        // SeekBar: mostrar valor en Toast al soltar (ya existía) y se usa su progress en add/update
        seekPriority.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) { Toast.makeText(this@MainActivity, "Prioridad ${p0?.progress}", Toast.LENGTH_SHORT).show() }
        })


        btnAdd.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()
            if (title.isEmpty()) { Toast.makeText(this, "Añade un título", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val n = Note(
                title = title,
                content = content,
                date = date,
                important = cbImportant.isChecked,
                rating = rating.rating,
                priority = seekPriority.progress // <-- usar SeekBar
            )
            val id = db.insert(n)
            if (id > 0) {
                Toast.makeText(this, "Nota añadida", Toast.LENGTH_SHORT).show()
                saveLastId(id)
                clearFields()
                loadNotes() // actualizar con la orden actual
            }
        }


        btnUpdate.setOnClickListener {
            val sel = selectedNote
            if (sel == null) { Toast.makeText(this, "Selecciona una nota para actualizar", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            sel.title = etTitle.text.toString()
            sel.content = etContent.text.toString()
            sel.important = cbImportant.isChecked
            sel.rating = rating.rating
            sel.priority = seekPriority.progress // <-- actualizar prioridad
            db.update(sel)
            Toast.makeText(this, "Nota actualizada", Toast.LENGTH_SHORT).show()
            saveLastId(sel.id)
            clearFields()
            loadNotes() // actualizar con la orden actual
        }


        btnDelete.setOnClickListener {
            val sel = selectedNote
            if (sel == null) { Toast.makeText(this, "Selecciona una nota para borrar", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Borrar nota '${sel.title}'?")
                .setPositiveButton("Borrar") { _, _ ->
                    db.delete(sel.id)
                    Toast.makeText(this, "Borrada", Toast.LENGTH_SHORT).show()
                    clearFields()
                    loadNotes()
                    saveLastId(-1)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


        btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                val s = String.format("%04d-%02d-%02d", y, m+1, d)
                etContent.append("\nFecha: $s")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }


        // Controles adicionales
        cbImportant.setOnCheckedChangeListener { _, isChecked ->
            // Si hay una nota seleccionada, actualizar su estado visualmente (no persistir hasta Update/LongPress)
            selectedNote?.important = isChecked
        }
        rating.setOnRatingBarChangeListener { _, ratingValue, _ ->

        }
    }


    private fun onNoteSelected(note: Note) {
        selectedNote = note
        etTitle.setText(note.title)
        etContent.setText(note.content)
        cbImportant.isChecked = note.important
        rating.rating = note.rating
        seekPriority.progress = note.priority // <-- sincronizar SeekBar con la nota
        saveLastId(note.id)
    }


    private fun toggleImportant(note: Note) {
        note.important = !note.important
        db.update(note)
        Toast.makeText(this, if (note.important) "Marcada como importante" else "Desmarcada como importante", Toast.LENGTH_SHORT).show()
        // Si la nota era la actualmente seleccionada, sincronizar checkbox
        if (selectedNote?.id == note.id) {
            selectedNote = note
            cbImportant.isChecked = note.important
        }
        loadNotes()
    }


    private fun saveLastId(id: Long) {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        prefs.edit().putLong(keyLastId, id).apply()
    }


    private fun clearFields() {
        selectedNote = null
        etTitle.setText("")
        etContent.setText("")
        cbImportant.isChecked = false
        rating.rating = 0f
        seekPriority.progress = 0
    }


    // Modificado: ahora ordena según la selección del spinner (id o priority)
    private fun loadNotes() {
        val list = db.listAll()
        val sorted = when (spinnerSort.selectedItemPosition) {
            0 -> list.sortedByDescending { it.id }        // Más reciente
            1 -> list.sortedBy { it.id }                 // Más antiguo
            2 -> list.sortedByDescending { it.priority } // Más prioridad
            3 -> list.sortedBy { it.priority }           // Menos prioridad
            else -> list.sortedByDescending { it.id }
        }
        adapter.update(sorted)
    }
}
