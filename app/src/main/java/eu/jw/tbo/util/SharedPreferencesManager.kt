package eu.jw.tbo.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import eu.jw.tbo.presentation.MainScreenState
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * SharedPreferencesManager is used to write the screen state into persistent storage,
 * so we can display the data right away after app start up and remember which currency
 * was selected last time
 */
object SharedPreferencesManager {
    private const val PREF_NAME = "TboPreferences"
    private const val STATE_KEY = "state"
    private lateinit var sharedPreferences: SharedPreferences

    // Gson needs to be told how to serialize and deserialize LocalDateTime
    private var gson = GsonBuilder().apply {
        registerTypeAdapter(LocalDateTime::class.java, object : JsonSerializer<LocalDateTime> {
            override fun serialize(
                src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?
            ): JsonElement {
                val sec: Long = src?.toInstant(ZoneOffset.UTC)?.epochSecond ?: 0
                return JsonPrimitive(sec)
            }
        })
        registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime> {
            override fun deserialize(
                json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?
            ): LocalDateTime {
                val instant = Instant.ofEpochSecond(json!!.asJsonPrimitive.asLong)
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            }
        })
    }.create()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveState(state: MainScreenState) {
        val string = gson.toJson(state)
        sharedPreferences.edit().putString(STATE_KEY, string).apply()
    }

    fun getState(): MainScreenState {
        val string = sharedPreferences.getString(STATE_KEY, "")
        return gson.fromJson(string, MainScreenState::class.java) ?: MainScreenState()
    }
}