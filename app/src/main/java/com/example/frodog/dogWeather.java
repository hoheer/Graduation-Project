package com.example.frodog;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class dogWeather extends AppCompatActivity {
    String rs = "";
    String rs2 = "";
    String[] wrs = null;



    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dogweather);


        //LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsTracker = new GpsTracker(dogWeather.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        Geocoder gCoder = new Geocoder(this, Locale.getDefault());
        List<Address> addr = null;

        try {
            addr = gCoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address a = addr.get(0);
        String add =  a.getAdminArea()+" "+a.getSubLocality()+" "+a.getThoroughfare();
        // add에 현재 위치 저장

        try {
            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=ARIEwNB4jCcVbsbYD8nmaEF%2FI7kzcl8NIclBLC0VSz4SzEW2bdNCk1veYA20vsPmjafWnLDxyDP5jzXItm6HqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(a.getSubLocality(), "UTF-8")); /*측정소 이름*/
            urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간 (하루 : DAILY, 한달 : MONTH, 3달 : 3MONTH)*/
            urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고문서 참조*/
            URL url2 = new URL(urlBuilder.toString());

            String url = url2.toString();

            try {
                api_caller dust = new api_caller(url);
                rs = dust.execute().get();
            }
            catch (Exception e) {}
        }
           catch (IOException k) {}





            Date today = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyyMMdd");

        String lat = Integer.toString((int)latitude); //60
        String lon = Integer.toString((int)longitude); //127
        //String lat1 = Integer.toString((int)latitude);
        //String lon1 = Integer.toString((int)longitude);
         lat ="59";
         lon ="127";
        Calendar cal = Calendar.getInstance();
        String base_date = format1.format(cal.getTime());
        //base_date = "20200922";
            String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=ARIEwNB4jCcVbsbYD8nmaEF%2FI7kzcl8NIclBLC0VSz4SzEW2bdNCk1veYA20vsPmjafWnLDxyDP5jzXItm6HqA%3D%3D&numOfRows=10&pageNo=1&base_date="+base_date+"&base_time=0800&nx="+lat+"&ny="+lon;

            try {
                api_caller2 weather = new api_caller2(url);
                rs2 = weather.execute().get();
                 wrs = rs2.split(" ");
                    Log.d("OPEN_API", "lom : " + lon);
                Log.d("OPEN_API", "lat : " + lat);
            Log.d("OPEN_API", "rs2  : " + rs2);
            Log.d("OPEN_API", "rs2  : " + wrs[7]);


            }
            catch (Exception e) {}



















        TextView dust = findViewById(R.id.fine_dust);
        TextView fine_dust = findViewById(R.id.fime_dust_matter);
        TextView gps = findViewById(R.id.gps);
        String[] arrword = rs.split("");

        TextView humid = findViewById(R.id.hum1);
        TextView temp = findViewById(R.id.temp1);

        humid.setText(wrs[7]+"%");
        temp.setText(wrs[13]+"℃");

        gps.setText(add);
        fine_dust.setText(arrword[0]+arrword[1]);
        dust.setText(arrword[2]+arrword[3]);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("외출");
    }



}