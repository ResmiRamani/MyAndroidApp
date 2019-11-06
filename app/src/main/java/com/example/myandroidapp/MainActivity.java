package com.example.myandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    TextView max_temperature_field,min_temperature_field,currentDate;
    Button fiveDays;
    ImageView weatherIcon;
    ProgressBar progressBar;

    //second Activity......FiveDayActivity

    public void SecondActivity(View view) {
        Intent intent = new Intent(this, FiveDayActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherIcon = findViewById(R.id.imageView);
        max_temperature_field=findViewById(R.id.max_temperature_field);
        min_temperature_field=findViewById(R.id.min_temperature_field);
        fiveDays=findViewById(R.id.five_button);
        currentDate=findViewById((R.id.currentDate));
        progressBar = findViewById(R.id.progressBar);

        new RetrieveFeedTask().execute();

    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }


        protected String doInBackground(Void... urls) {
            String email = "https://api.openweathermap.org/data/2.5/weather?q=kitchener,Canada&appid=deeca45352ba2570bec75fb9812f6c52&units=metric"; // emailText.getText().toString();

            // Do some validation here

            try {
                URL url = new URL(email);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("Response-->", response);

//          USED TO SHOW THE RETURNED JSON DATA

            try {

                JSONTokener jsonTokener = new JSONTokener(response);
                JSONObject topObject = (JSONObject) jsonTokener.nextValue();

                long current_Date=topObject.getInt("dt");
                Date date=new Date(current_Date*1000);
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
                simpleDateformat.setTimeZone(TimeZone.getDefault());
                String dayName = simpleDateformat.format(date);

                JSONObject mainObject = topObject.getJSONObject("main");
                double temp_max = mainObject.getDouble("temp_max");
                double temp_min = mainObject.getDouble("temp_min");

                JSONArray weatherArray = topObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String iconString = weatherObject.getString("icon");

                max_temperature_field.setText(String.format("%d", Math.round(temp_max ))+"°");
                min_temperature_field.setText(String.format("%d", Math.round(temp_min ))+"°");
                currentDate.setText(dayName);

                if(iconString.equals("01d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_01d);
                }
                else if(iconString.equals("01n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_01n);
                }
                else if(iconString.equals("02d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_02d);
                }
                else if(iconString.equals("02n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_02n);
                }
                else if(iconString.equals("03d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_03d);
                }
                else if(iconString.equals("03n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_03n);
                }
                else if(iconString.equals("04d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_04d);
                }
                else if(iconString.equals("04n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_04n);
                }
                else if(iconString.equals("09d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_09d);
                }
                else if(iconString.equals("09n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_09n);
                }
                else if(iconString.equals("10d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_10d);
                }
                else if(iconString.equals("10n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_10n);
                }
                else if(iconString.equals("11d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_11d);
                }
                else if(iconString.equals("11n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_11n);
                }
                else if(iconString.equals("13d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_13n);
                }
                else if(iconString.equals("13n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_13n);
                }
                else if(iconString.equals("50d"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_50d);
                }
                else if(iconString.equals("50n"))
                {
                    weatherIcon.setImageResource(R.drawable.icon_50n);
                }
                else
                {
                     weatherIcon.setImageResource(R.drawable.alert);
                }

            } catch (JSONException e) {
                String errorMessage = e.toString();
                Log.e("ERROR----->", errorMessage);

            }
        }
    }
}