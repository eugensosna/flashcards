package edu.znu.sosna.flashcardslearning.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Deck::class, Card::class], version = 1, exportSchema = false)
abstract class FlashcardsDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao

    companion object {
        fun create(context: Context): FlashcardsDatabase =
            Room.databaseBuilder(context, FlashcardsDatabase::class.java, "flashcards.db").build()
    }
}
