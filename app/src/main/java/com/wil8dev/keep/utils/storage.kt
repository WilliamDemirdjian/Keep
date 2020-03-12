package com.wil8dev.keep.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.wil8dev.keep.Note
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

private val TAG = "storage.kt"

fun persistNote(context: Context, note: Note) {
//    if(note.fileName.isEmpty()) {
    if(TextUtils.isEmpty(note.fileName)) {
        note.fileName = UUID.randomUUID().toString() + ".keep"
    }

    val fileOutput = context.openFileOutput(note.fileName, Context.MODE_PRIVATE)
    val outputStream = ObjectOutputStream(fileOutput)
    outputStream.writeObject(note)
    outputStream.close()
}

fun deleteNote(context: Context, note:Note) {
    context.deleteFile(note.fileName)
}

fun loadNotes(context: Context): MutableList<Note> {
    val notes = mutableListOf<Note>()
    val notesDirectory = context.filesDir
    for(fileName in notesDirectory.list()) {
        val note = loadNote(context, fileName)
        Log.i(TAG, "Loaded note $note")
        notes.add(note)
    }
    return notes
}

private fun loadNote(context: Context, filename: String): Note {
    val fileInput = context.openFileInput(filename)
    val inputStream = ObjectInputStream(fileInput)
    val note = inputStream.readObject() as Note
    inputStream.close()
    return note
}