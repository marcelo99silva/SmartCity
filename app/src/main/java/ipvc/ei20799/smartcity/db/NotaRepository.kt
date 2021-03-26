package ipvc.ei20799.smartcity.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ipvc.ei20799.smartcity.dao.NotaDao
import ipvc.ei20799.smartcity.dataclasses.Nota

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class NotaRepository(private val notaDao: NotaDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allNotas: LiveData<List<Nota>> = notaDao.getNotas()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(nota: Nota) {
        notaDao.insert(nota)
    }

    suspend fun deleteNotaById(notaId: Int){
        notaDao.deleteNotaById(notaId)
    }
}