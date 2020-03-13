package com.wil8dev.keep

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.lang.UCharacter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.wil8dev.keep.utils.loadNotes
import com.wil8dev.keep.utils.persistNote
import kotlinx.android.synthetic.main.activity_note_list.*
import java.io.Serializable

//TODO in activiti_note_detail.xml -> android:textCursorDrawable="@drawable/black_cursor" not working

class NoteListActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var notes: MutableList<Note>
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        notes = loadNotes(this)
        /*notes.add(
            Note(
                "EPPDCSI",
                "Etablissement Public du Palais de la Découverte et de la Cité des Sciences et de l'Industrie"
            )
        )
        notes.add(Note("Toto", "Toto en Bretagne"))
        notes.add(Note("Aaaaaaa", getString(R.string.lorem_ipsum)))
        notes.add(Note("Ansdjzndaaaaaa", "njdnjkkm,zmf"))
        notes.add(Note("kzsakddfghjuikol", "fghjkhjk,zmf"))
        notes.add(Note("Toto", "Toto en Bretagne"))
        notes.add(Note("Just a title", ""))
        notes.add(Note("", "Just a note"))
        notes.add(Note("kzsakddfghjuikol", "fghjkhjk,zmf"))
        notes.add(Note("Aaaaaaa", getString(R.string.lorem_ipsum)))
        notes.add(Note("kzsakddfghjuikol", "fghjkhjk,zmf"))
        notes.add(
            Note(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed interdum, velit id eleifend volutpat, est mi tincidunt augue, laoreet posuere nisi nisl in eros. Cras pellentesque eu erat sit amet sollicitudin. Pellentesque",
                "fghjkhjk,zmf"
            )
        )
        notes.add(
            Note(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed interdum, velit id eleifend volutpat, est mi tincidunt augue, laoreet posuere nisi nisl in eros. Cras pellentesque eu erat sit amet sollicitudin. Pellentesque",
                "fghjkhjk,zmf"
            )
        )
        notes.add(
            Note(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed interdum, velit id eleifend volutpat, est mi tincidunt augue, laoreet posuere nisi nisl in eros. Cras pellentesque eu erat sit amet sollicitudin. Pellentesque",
                "fghjkhjk,zmf"
            )
        )*/

        noteAdapter = NoteAdapter(notes, this)
//        var layoutManagerA = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        layoutManagerA.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
//        recyclerView.apply {
////            layoutManager = GridLayoutManager(this.context, 2)
//            layoutManager = layoutManagerA
//            adapter = noteAdapter
//        }

        val layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = noteAdapter


        setSupportActionBar(toolbarNoteListActivity)
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false);
        addNote_fab.setOnClickListener(this)

        swipeRefreshLayout.setOnRefreshListener {
            noteAdapter.notifyDataSetChanged()
            val handler = Handler()
            handler.postDelayed({swipeRefreshLayout.isRefreshing = false}, 1000)
        }
    }

    override fun onClick(view: View) {
        if (view.tag != null) {
            Log.i("NoteListActivity", "clicked on ")
            showNoteDetail(view.tag as Int)
        } else {
            when (view.id) {
                R.id.addNote_fab -> createNote()
            }
        }
    }

    private fun createNote() {
        showNoteDetail(-1)
    }

    private fun deleteSnackbar() {
        val snackbar = Snackbar.make(
            rootLayout,
            getString(R.string.deleted_text_snackbar),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(getString(R.string.undo)) {
            Log.i("MainActivity", "Clicked on Snackbar : undo")
        }
        snackbar.setActionTextColor(Color.YELLOW)
//        snackbar.anchorView = addNote_fab
        snackbar.show()
    }

    fun showNoteDetail(noteIndex: Int) {
        val note = if (noteIndex == -1) {
            Note()
        } else {
            notes[noteIndex]
        }
        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE, note as Serializable)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_INDEX, noteIndex)
        startActivityForResult(intent, NoteDetailActivity.REQUEST_CODE_EDITED_NOTE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        when (requestCode) {
            NoteDetailActivity.REQUEST_CODE_EDITED_NOTE -> editNoteResult(data)
        }
    }

    private fun saveData(note: Note, noteIndex: Int) {
        persistNote(this, note)
        if (noteIndex == -1 && (note.title.isNotEmpty() || note.content.isNotEmpty())) {
            notes.add(0, note)
        } else if (noteIndex == -1 && (note.title.isEmpty() && note.content.isEmpty())) {
            return
        } else {
            notes[noteIndex] = note
        }
        noteAdapter.notifyDataSetChanged()
    }

    private fun editNoteResult(data: Intent) {
        val noteIndex = data.getIntExtra(NoteDetailActivity.EXTRA_NOTE_INDEX, -1)
        when (data.action) {
            NoteDetailActivity.ACTION_SAVE_NOTE -> {
                val note = data.getParcelableExtra<Note>(NoteDetailActivity.EXTRA_NOTE)
                saveData(note, noteIndex)
            }
            NoteDetailActivity.ACTION_DELETE_NOTE -> {
                deleteNote(noteIndex)
            }
        }
    }

    private fun deleteNote(noteIndex: Int) {
        if(noteIndex == -1) {
            return
        } else {
            val note = notes.removeAt(noteIndex)
            com.wil8dev.keep.utils.deleteNote(this, note)
            noteAdapter.notifyDataSetChanged()
            deleteSnackbar()
        }
    }
}
