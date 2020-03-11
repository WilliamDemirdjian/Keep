package com.wil8dev.keep

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_note_list.*

class NoteListActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var notes: MutableList<Note>
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)

        notes = mutableListOf<Note>()
        notes.add(
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
        )

        noteAdapter = NoteAdapter(notes, this)
        var layoutManagerA = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManagerA.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        recyclerView.apply {
//            layoutManager = GridLayoutManager(this.context, 2)
            layoutManager = layoutManagerA
            adapter = noteAdapter
        }
    }

    override fun onClick(view: View) {
        if (view.tag != null) {
            Log.i("NoteListActivity", "clicked on ")
            showNoteDetail(view.tag as Int)
        }
    }

    fun showNoteDetail(noteIndex: Int) {
        val note = notes[noteIndex]
        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE, note)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_INDEX, noteIndex)
        startActivity(intent)
    }
}
