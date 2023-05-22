package com.vitorthemyth.persistence.models



import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vitorthemyth.domain.models.Resultado

@Entity(tableName = "times")
data class TimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val ultimasPartidas: List<PartidaEntity>,
    val proximaPartida: PartidaEntity? = null
)


data class PartidaEntity(
    val matchToken : String,
    val adversario: EquipeEntity?= null,
    val dificuldade: Int,
    val importancia: Int,
    val estatisticas: EstatisticasEntity,
    val date : String
)


data class EquipeEntity(
    val nome: String,
    val ultimasPartidas: List<EstatisticasEntity>?
)

data class EstatisticasEntity(
    val golsMarcados: Int,
    val golsSofridos: Int,
    val posseDeBola: Int,
    val chutesAoGol: Int,
    val chutesForaDoGol: Int,
    val escanteios: Int,
    val faltas: Int,
    val cartoesAmarelos: Int,
    val cartoesVermelhos: Int,
    val jogouEmCasa: Boolean,
    val resultado : Resultado
)

