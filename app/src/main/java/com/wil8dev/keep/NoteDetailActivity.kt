package com.wil8dev.keep

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_note_detail.*
import java.io.Serializable


class NoteDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOTE = "note"
        const val EXTRA_NOTE_INDEX = "noteIndex"
        const val REQUEST_CODE_EDITED_NOTE = 1
        const val ACTION_SAVE_NOTE = "com.wil8dev.keep.actions.ACTION_SAVE_NOTE"
        const val ACTION_DELETE_NOTE = "com.wil8dev.keep.actions.ACTION_DELETE_NOTE"
    }

    private lateinit var note: Note
    private var noteIndex: Int = -1 // if noteIndex == -1 -> new Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        note = intent.getParcelableExtra<Note>(EXTRA_NOTE)
        noteIndex = intent.getIntExtra(EXTRA_NOTE_INDEX, -1)

        title_note_detail.setText(note.title)
        content_note_detail.setText(note.content)

        setSupportActionBar(toolbarNoteDetail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onBackPressed() {
        saveNote()
    }

    private fun saveNote() {
        note.title = title_note_detail.text.toString()
        note.content = content_note_detail.text.toString()
        intent = Intent(ACTION_SAVE_NOTE)
        intent.putExtra(EXTRA_NOTE, note as Serializable)
        intent.putExtra(EXTRA_NOTE_INDEX, noteIndex)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_detail_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.delete_note_detail_menu -> {
            deleteNote()
            true
        }
        android.R.id.home -> {
            saveNote()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun deleteNote() {
        intent = Intent(ACTION_DELETE_NOTE)
        intent.putExtra(EXTRA_NOTE_INDEX, noteIndex)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
