package ipvc.ei20799.smartcity.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "nota_table")

class Nota (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "texto") val texto: String,
    @ColumnInfo(name = "data") val data: Date
)