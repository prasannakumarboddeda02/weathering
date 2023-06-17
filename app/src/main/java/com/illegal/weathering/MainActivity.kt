package com.illegal.weathering

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.illegal.weathering.retrofit.ResultData
import com.illegal.weathering.retrofit.RetrofitInstance
import com.illegal.weathering.retrofit.WeatherApi
import com.illegal.weathering.ui.theme.WeatheringTheme
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatheringTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                ) {
                    MyApp()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MyApp() {
        var response by remember {
            mutableStateOf(getTemperatureData())
        }
        var text by remember {
            mutableStateOf("City")
        }
        //getTemperatureData()
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            /*Image(painter = painterResource(id = R.drawable.background), contentDescription = "background",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight())*/
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFieldCity(
                    value = text,
                    onValueChanged = { text = it },
                    onSearchClicked = { response = getTemperatureData(text) })
                TemperatureData(response)
                Column(modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
                    .padding(start = 35.dp,end = 25.dp)) {
                    MaxMinTemp(response)
                    PressureHumidity(response)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TextFieldCity(
        value: String,
        onValueChanged: (String) -> Unit,
        onSearchClicked: () -> Unit
    ) {
        TextField(
            value = value,
            onValueChange = onValueChanged,
            trailingIcon = {
                IconButton(onClick = onSearchClicked) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_search_24),
                        contentDescription = "search"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun TemperatureData(response: Response<ResultData>) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(120.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
                Text(
                    text = convertDate(response.body()?.dt!!.toLong()),
                    fontSize = 17.sp,
                    color = Color.White
                )
                Text(
                    text = "${convert(response.body()?.main?.temp).substring(0, 4)}°C",
                    fontSize = 60.sp,
                    color = Color.White
                )
                Text(
                    text = "${response.body()?.name}",
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
            SetImage(
                code = response.body()!!.weather[0].icon,
                name = "weatherImage",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                alignment = Alignment.Center
            )

        }
    }

    @Composable
    fun MaxMinTemp(response: Response<ResultData>) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 20.dp, bottom = 5.dp)
        ) {
            Text(
                text = "Min:${convert(response.body()?.main?.temp_min).substring(0, 3)}°C",
                color = Color.White,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )
            Text(
                text = "Max:${convert(response.body()?.main?.temp_max).substring(0, 3)}°C",
                color = Color.White,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )
        }
    }

    @Composable
    fun PressureHumidity(response: Response<ResultData>){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 5.dp, bottom = 20.dp)
        ) {
            Text(text = "Pressure:${response.body()?.main?.pressure}",
                color = Color.White,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )


            Text(text = "Humidity:${response.body()?.main?.humidity}",
            color = Color.White,
            modifier = Modifier.weight(1f),
            fontSize = 20.sp)

        }
    }


    @Composable
    fun SetImage(code : String, name: String, modifier: Modifier = Modifier,alignment: Alignment) {
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${code}@2x.png",
            contentDescription = name,
            modifier = modifier,
            alignment = alignment)
    }

    private fun getTemperatureData(text: String = "London"): Response<ResultData> {
        val weatherApi = RetrofitInstance.getInstance().create(WeatherApi::class.java)
        var response1: Response<ResultData>
        runBlocking {
            response1 = weatherApi.weatherApi(city = text, key = "8cf7d9c8797866cb1d34d33db50d2bae")
            Log.d("pk : ", response1.body().toString())
        }
        return response1
    }
}


