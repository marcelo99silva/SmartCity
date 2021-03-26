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

    @Query("DELETE FROM nota_table WHERE id == :notaId")
    suspend fun deleteNotaById(notaId: Int)

    @Query("UPDATE nota_table SET titulo = :nTitulo, texto = :nDescricao, data = :nData WHERE id == :id")
    suspend fun updateNotaById(id: Int, nTitulo: String, nDescricao: String, nData: String)

    @Query("DELETE FROM nota_table")
    fun deleteAll()
}