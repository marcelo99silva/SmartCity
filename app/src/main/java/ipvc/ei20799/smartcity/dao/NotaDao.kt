package ipvc.ei20799.smartcity.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ipvc.ei20799.smartcity.dataclasses.Nota

@Dao
interface NotaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(nota: Nota)

    @Query(value = "SELECT * FROM nota_table ORDER BY id DESC")
    fun getNotas(): LiveData<List<Nota>>

    @Query("DELETE FROM nota_table WHERE id = :notaId")
    fun deleteNotaById(notaId: Int)

    @Query("DELETE FROM nota_table")
    fun deleteAll()
}