package com.example.weatherapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.app.AlertDialog
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.TextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DecimalFormat
import java.time.LocalDateTime

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.SharedPreferences
import android.util.Log
import java.util.Collections.max
import android.os.Handler
import android.os.Looper
import android.view.*
import java.math.RoundingMode
import java.lang.NumberFormatException
import android.widget.*
import java.math.BigDecimal
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.findNavController


private const val url = "https://api.openweathermap.org/data/2.5/weather"
private const val appid = "944cc5d9267435933a89cb239ceee6d3"


class ElderFragment :Fragment(){

    private lateinit var weather: TextView
    private lateinit var place: TextView
    private lateinit var date: TextView
    private lateinit var press: TextView
    private lateinit var temperature: TextView
    private lateinit var asc: TextView
    private lateinit var desc: TextView
    private lateinit var change: Button
    private lateinit var location: EditText
    private lateinit var locate: FloatingActionButton
    private lateinit var df: DecimalFormat
    private lateinit var icon: ImageView
    private val args: ElderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.elderscreen, container,false)
        view.visibility=View.GONE

        weather = view.findViewById(R.id.weather)
        place = view.findViewById(R.id.place)
        date = view.findViewById(R.id.date)
        press = view.findViewById(R.id.press)
        temperature = view.findViewById(R.id.temperature)
        asc = view.findViewById(R.id.asc)
        desc = view.findViewById(R.id.desc)
        change = view.findViewById(R.id.change)
        locate = view.findViewById(R.id.locate)
        icon = view.findViewById(R.id.Icon)
        //przycisk do zmiany widoku na podstawowy
        change.setOnClickListener(){
            val action = ElderFragmentDirections.actionElderfragmentToNormalfragment(place.text.toString())
            findNavController().navigate(action)
        }
        //przycisk służący do wprowadznia nazwy szukanego miasta
        locate.setOnClickListener(){
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Lokalizacja")
            builder.setMessage("Wprowadź nazwę miasta w którym chcesz sprawdzić pogodę")
            location= EditText(requireContext())
            builder.setView(location)
            builder.setPositiveButton("Potwierdź") { _, _ ->
                getWeatherDetails(location.text.toString())
            }
            builder.setNegativeButton("Nie") { _, _ -> }
            builder.create().show()
        }
        try{
            view.visibility=View.GONE
            getWeatherDetails(args.currentCity)
            Handler().postDelayed({
                view.visibility=View.VISIBLE
            }, 200)

        }
        catch(e: Exception){
            view.visibility=View.GONE
            getWeatherDetails("Katowice")
            Handler().postDelayed({
                view.visibility=View.VISIBLE
            }, 200)
        }
        return view
    }
    open fun getWeatherDetails(city:String) {
        var tempUrl:String = ""
        //var city:kotlin.String? = "Katowice"
        if ((city == "")) {
        } else {
            if ((city == "")) {
                //stworzenie url do uzyskania JSONa
                tempUrl = url + "?q=" + city + "," + "&lang=pl&appid=" + appid
            } else {
                tempUrl = url + "?q=" + city + "&lang=pl&appid=" + appid
            }
            var stringRequest: StringRequest? = StringRequest(
                Request.Method.POST,
                tempUrl,
                object : Response.Listener<String?> {
                    override open fun onResponse(response: String?) {
                        var output: String? = ""
                        try {
                            //zebranie danych z JSONa
                            var jsonResponse: JSONObject? = JSONObject(response)
                            var jsonArray: JSONArray? = jsonResponse?.getJSONArray("weather")
                            var jsonObjectWeather: JSONObject? = jsonArray?.getJSONObject(0)
                            var description: String? =
                                jsonObjectWeather?.getString("description")
                            var jsonObjectMain: JSONObject? = jsonResponse?.getJSONObject("main")
                            var temp: Double = jsonObjectMain?.getDouble("temp")!!.toDouble() - 273.15
                            var pressure: Int = jsonObjectMain?.getInt("pressure")!!.toInt()
                            var jsonObjectWind: JSONObject? = jsonResponse?.getJSONObject("wind")
                            var jsonObjectSys: JSONObject? = jsonResponse?.getJSONObject("sys")
                            var countryName: String? = jsonObjectSys?.getString("country")
                            var sunrise: String? = jsonObjectSys?.getString("sunrise")
                            var sunrisedv: Long =
                                java.lang.Long.valueOf(sunrise) * 1000

                            var sunrisedf: java.util.Date? = java.util.Date(sunrisedv)
                            var sunrisetime: kotlin.String? = java.text.SimpleDateFormat("HH:mm").format(sunrisedf)

                            var sunset: String? = jsonObjectSys?.getString("sunset")
                            var sunsetdv: Long =
                                java.lang.Long.valueOf(sunset) * 1000

                            var sunsetdf: java.util.Date? = java.util.Date(sunsetdv)
                            var sunsettime: kotlin.String? = java.text.SimpleDateFormat("HH:mm").format(sunsetdf)

                            var ctime: String? = jsonResponse?.getString("dt")
                            var timedv: Long =
                                java.lang.Long.valueOf(ctime) * 1000

                            var timedf: java.util.Date? = java.util.Date(timedv)
                            var currenttime: kotlin.String? = java.text.SimpleDateFormat("dd MMM yyy | HH:mm").format(timedf)
                            var iconid: String? =
                                jsonObjectWeather?.getString("icon")
                            if(iconid=="01d"){icon.setImageResource(R.drawable.w01d)}
                            else if(iconid=="01n"){icon.setImageResource(R.drawable.w01n)}
                            else if(iconid=="02d"){icon.setImageResource(R.drawable.w02d)}
                            else if(iconid=="02n"){icon.setImageResource(R.drawable.w02n)}
                            else if(iconid=="03d"){icon.setImageResource(R.drawable.w03d)}
                            else if(iconid=="03n"){icon.setImageResource(R.drawable.w03n)}
                            else if(iconid=="04d"){icon.setImageResource(R.drawable.w04d)}
                            else if(iconid=="04n"){icon.setImageResource(R.drawable.w04n)}
                            else if(iconid=="09d"){icon.setImageResource(R.drawable.w09d)}
                            else if(iconid=="09n"){icon.setImageResource(R.drawable.w09n)}
                            else if(iconid=="10d"){icon.setImageResource(R.drawable.w10d)}
                            else if(iconid=="10n"){icon.setImageResource(R.drawable.w10n)}
                            else if(iconid=="11d"){icon.setImageResource(R.drawable.w11d)}
                            else if(iconid=="11n"){icon.setImageResource(R.drawable.w11n)}
                            else if(iconid=="13d"){icon.setImageResource(R.drawable.w13d)}
                            else if(iconid=="13n"){icon.setImageResource(R.drawable.w13n)}
                            else if(iconid=="50d"){icon.setImageResource(R.drawable.w50d)}
                            else if(iconid=="50n"){icon.setImageResource(R.drawable.w50n)}
                            var cityName: String? = jsonResponse?.getString("name")
                            //wprowadzenie zebranych danych do naszych TextView
                            weather.text=description
                            place.text=cityName+", "+countryName
                            press.text=pressure.toString()+" hPa"
                            date.text=currenttime.toString()
                            temperature.text=String.format(" %.2f",temp)+" °C"
                            asc.text=sunrisetime
                            desc.text=sunsettime
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                },
                //informacja wyświetlająca błędną nazwę podanego miasta
                object : Response.ErrorListener {
                    override open fun onErrorResponse(error: VolleyError?) {
                        Toast.makeText(
                            requireContext(),
                            "Podaj poprawną nazwę miasta",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            var requestQueue: RequestQueue? = Volley.newRequestQueue(requireContext())
            requestQueue?.add<kotlin.String?>(stringRequest)
        }
    }
}