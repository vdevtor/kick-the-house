package com.vitorthemyth.persistence

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vitorthemyth.persistence.models.PartidaEntity

class PartidaListConverter {
    // ...

    @TypeConverter
    fun fromPartidaList(partidas: List<PartidaEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<PartidaEntity>>() {}.type
        return gson.toJson(partidas, type)
    }

    @TypeConverter
    fun toPartidaList(partidasString: String): List<PartidaEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<PartidaEntity>>() {}.type
        return gson.fromJson(partidasString, type)
    }


    @TypeConverter
    fun fromPartida(partida: PartidaEntity?): String? {
        val gson = Gson()
        val type = object : TypeToken<PartidaEntity>() {}.type
        return gson.toJson(partida, type)
    }

    @TypeConverter
    fun toPartida(partidaString: String?): PartidaEntity? {
        val gson = Gson()
        val type = object : TypeToken<PartidaEntity>() {}.type
        return gson.fromJson(partidaString, type)
    }
}