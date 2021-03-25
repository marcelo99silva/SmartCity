package ipvc.ei20799.smartcity.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.ei20799.smartcity.dao.NotaDao
import ipvc.ei20799.smartcity.dataclasses.Nota
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Nota class
@Database(entities = arrayOf(Nota::class), version = 1, exportSchema = false)
public abstract class NotaDB : RoomDatabase() {

    abstract fun notaDao(): NotaDao

    private class NotaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.notaDao())
                }
            }
        }

        suspend fun populateDatabase(notaDao: NotaDao) {
            // Delete all content here.
           /* notaDao.deleteAll()

            // Add sample words.
            var nota = Nota(1, "Nota Uno", "Teste lalala descricao de nota", "25/03/2021 18:20")
            notaDao.insert(nota)
            nota = Nota(2, "Nota Dos", "Teste lalala descricao de nota", "25/03/2021 18:50")
            notaDao.insert(nota)
            nota = Nota(3, "Nota Tres", "Teste lalala descricao de nota", "22/03/2021 15:00")
            notaDao.insert(nota)*/

        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NotaDB? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): NotaDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotaDB::class.java,
                    "nota_database"
                )
                // estratégia de destruição para alteração de versão db
                // .fallbackToDestructiveMigration()
                .addCallback(NotaDatabaseCallback(scope))
                .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}