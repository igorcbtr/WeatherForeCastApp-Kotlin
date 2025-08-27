package org.example

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

fun main() {
    val apiKey = "ed82e9a37b8e4dc0b0095948252708"
    val cities = listOf("Chisinau", "Madrid", "Kyiv", "Amsterdam")

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(WeatherService::class.java)

    println("City      | Min Temp (°C) | Max Temp (°C) | Humidity (%) | Wind kph | Wind Dir")
    println("----------------------------------------------------------------------")

    for (city in cities) {
        val response = service.getForecast(city, apiKey, 1).execute()
        if (response.isSuccessful) {
            val forecast = response.body()?.forecast?.forecastday?.first()
            if (forecast != null) {
                val windDir = degreesToCardinal(forecast.day.maxwind_degree)
                println("${city.padEnd(10)} | ${forecast.day.mintemp_c.toString().padEnd(14)} | " +
                        "${forecast.day.maxtemp_c.toString().padEnd(14)} | " +
                        "${forecast.day.avghumidity.toString().padEnd(13)} | " +
                        "${forecast.day.maxwind_kph.toString().padEnd(8)} | " +
                        "$windDir")
            }
        } else {
            println("${city.padEnd(10)} | Error fetching data")
        }
    }
}


fun degreesToCardinal(degrees: Double): String {
    val directions = arrayOf(
        "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
        "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    )
    val index = ((degrees / 22.5) + 0.5).toInt() % 16
    return directions[index]
}


interface WeatherService {
    @GET("forecast.json")
    fun getForecast(
        @Query("q") city: String,
        @Query("key") key: String,
        @Query("days") days: Int
    ): retrofit2.Call<WeatherResponse>
}


data class WeatherResponse(val forecast: Forecast)
data class Forecast(val forecastday: List<ForecastDay>)
data class ForecastDay(val day: Day)
data class Day(
    val mintemp_c: Double,
    val maxtemp_c: Double,
    val avghumidity: Double,
    val maxwind_kph: Double,
    val maxwind_degree: Double
)
