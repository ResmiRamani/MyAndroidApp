package com.example.myandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class FiveDayActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_day);
        progressBar = findViewById(R.id.progressBar);

        // Start the JSON download task
        new RetrieveFeedTask(this).execute();
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception excepion;
        Context my_context;

        public RetrieveFeedTask(Context context) {
            my_context = context;
        }

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("https://dataservice.accuweather.com/forecasts/v1/daily/5day/49564?apikey=Ar2LOa5GlS7jSyokXGx3rYr0yv857n9j&metric=true");
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
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }

        /*  function, avoid switch case/if...else...if*/

        private int convert_AccuIconCode_to_Id(int iconCode) {
            String codeString = String.format("%02d", iconCode);
            String iconResource = "accu" + codeString + "s";
            Resources r = getResources();
            int drawableId = r.getIdentifier(iconResource, "drawable", "com.example.myandroidapp");
            return drawableId;
        }

        /* function to convert EpochDate to days */

        private String convert_epochTime_to_dayOfWeek(long epochDate) {
            Date date = new Date(epochDate * 1000);
            SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
            simpleDateformat.setTimeZone(TimeZone.getDefault());
            String dayName = simpleDateformat.format(date);
            return dayName;
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);


            try {
                JSONTokener tokenizer = new JSONTokener(response);
                JSONObject topObject = (JSONObject) tokenizer.nextValue();
                /*  Extract JSON Array */
                JSONArray dailyForecasts = topObject.getJSONArray("DailyForecasts");

                /*  extract forecast values*/

                double[] minimumTemperature_array = new double[5];
                double[] maximumTemperature_array = new double[5];
                int[] iconCode_array = new int[5];
                long[] epochDate_array = new long[5];

                for (int counter = 0; counter < 5; counter++) {
                    JSONObject day = dailyForecasts.getJSONObject(counter);

                    JSONObject temperatureObject = day.getJSONObject("Temperature");
                    JSONObject minimumObject = temperatureObject.getJSONObject("Minimum");
                    minimumTemperature_array[counter] = minimumObject.getDouble("Value");
                    JSONObject maximumObject = temperatureObject.getJSONObject("Maximum");
                    maximumTemperature_array[counter] = maximumObject.getDouble("Value");

                    JSONObject dayObject = day.getJSONObject("Day");
                    iconCode_array[counter] = dayObject.getInt("Icon");

                    epochDate_array[counter] = day.getInt("EpochDate");
                }

                /*  prepare for ListView */

                List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

                for (int counter = 0; counter < 5; counter++) {
                    String day_Name = convert_epochTime_to_dayOfWeek(epochDate_array[counter]);
                    String high_Temp = String.format("%d", Math.round(maximumTemperature_array[counter]));
                    String low_Temp = String.format("%d", Math.round(minimumTemperature_array[counter]));
                    int iconId = convert_AccuIconCode_to_Id(iconCode_array[counter]);
                    String iconIdString = Integer.toString(iconId);  // the HashMap is String to String

                    //  ListView adapter

                    HashMap<String, String> row_hash_map = new HashMap< >();
                    row_hash_map.put("list_view_dayName", day_Name);
                    row_hash_map.put("list_view_icon", iconIdString);
                    row_hash_map.put("list_view_highTemp", high_Temp);
                    row_hash_map.put("list_view_lowTemp", low_Temp);
                    // To add  row's hash map to the list
                    aList.add(row_hash_map);
                }

                String[] from = {"list_view_dayName", "list_view_icon", "list_view_highTemp", "list_view_lowTemp"};
                int[] to = {R.id.dayName, R.id.dayIcon, R.id.highTemp, R.id.lowTemp};

                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.forecast_row, from, to);
                ListView listView = findViewById(R.id.forecast_list);
                listView.setAdapter(adapter);

            } catch (JSONException e) {
                // error handling code
                Log.i("JSONException...", "Here is the raw JSON string:" + response);
            }
        }
    }

}


