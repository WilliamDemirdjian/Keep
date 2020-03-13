package com.wil8dev.keep

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wil8dev.keep.model.Note
import com.wil8dev.keep.recyclerview.NoteAdapter
import com.wil8dev.keep.utils.loadNotes
import com.wil8dev.keep.utils.persistNote
import kotlinx.android.synthetic.main.activity_note_list.*
import java.io.Serializable


class NoteListActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var notes: MutableList<Note>
    private lateinit var noteAdapter: NoteAdapter
    private var gridLayoutManager = GridLayoutManager(this, GRID_LAYOUT_SPAN_COUNT)

    companion object {
        var GRID_LAYOUT_SPAN_COUNT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        loadData()
        setRecyclerView()
        setActionBar()
        setFab()
        setSwipeRefreshLayout()
    }

    private fun loadData() {
        notes = loadNotes(this)
    }

    private fun setSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener {
            noteAdapter.notifyDataSetChanged()
            val handler = Handler()
            handler.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
        }
    }

    private fun setFab() {
        addNote_fab.setOnClickListener(this)
    }

    private fun setActionBar() {
        setSupportActionBar(toolbarNoteListActivity)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun setRecyclerView() {
        setAdapter()
        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = noteAdapter
        }
    }

    private fun changeVue() {
        if (gridLayoutManager.spanCount == 2) {
            gridLayoutManager.spanCount = 1
        } else {
            gridLayoutManager.spanCount = 2
        }
        noteAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_vue -> {
            changeVue()
            refreshIcon(item)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun refreshIcon(item: MenuItem) {
        if(gridLayoutManager.spanCount == 1) {
            item.icon = getDrawable(R.drawable.ic_view_row_grey_24dp)
        } else {
            item.icon = getDrawable(R.drawable.ic_view_cards_grey_24dp)
        }
    }

    private fun setAdapter() {
        noteAdapter = NoteAdapter(notes, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_list_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
            10000
        )
        snackbar.setAction(getString(R.string.undo)) {
            Log.i("MainActivity", "Clicked on Snackbar : undo")
        }
        snackbar.setActionTextColor(Color.YELLOW)
//        snackbar.anchorView = addNote_fab
        snackbar.show()
    }

    private fun showNoteDetail(noteIndex: Int) {
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
                saveData(note!!, noteIndex)
            }
            NoteDetailActivity.ACTION_DELETE_NOTE -> {
                deleteNote(noteIndex)
            }
        }
    }

    private fun deleteNote(noteIndex: Int) {
        if (noteIndex == -1) {
            return
        } else {
            val note = notes.removeAt(noteIndex)
            com.wil8dev.keep.utils.deleteNote(this, note)
            noteAdapter.notifyDataSetChanged()
            deleteSnackbar()
        }
    }
}
