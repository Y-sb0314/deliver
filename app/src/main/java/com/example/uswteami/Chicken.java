package com.example.uswteami;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Chicken extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private TextToSpeech myTTS;
    private ImageButton mVoiceBtn;

    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS ="address";
    private static String mJsonString;

    private TextView place_layout;
    ArrayList<HashMap<String, String>> mArrayList;
    HashMap<String,String> shops = new HashMap<>();
    HashMap<String,String> shopnames = new HashMap<>();
    List<String> names = new ArrayList<>();
    ListView mlistView;
    ListViewAdapter adapter;

    private static String ad;
    private static String place;
    private static String stt;
    private String sh;
    private int a = 0;
    private int s;

    private Button back;
    private List<Button> btns = new ArrayList<>();
    Geocoder m = new Geocoder(Chicken.this);
    double lat1 = 0, lon1 = 0, lat2 = 0, lon2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chicken_layout);


        place_layout = (TextView) findViewById(R.id.placeLayout);
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        adapter = new ListViewAdapter();
        mArrayList = new ArrayList<>();
        mVoiceBtn = (ImageButton) findViewById(R.id.voiceBtn);

        back = (Button)findViewById(R.id.back);
        btns.add(back);

        final MediaPlayer player_s = MediaPlayer.create(this, R.raw.start);

        GetData task = new GetData();

        Intent get = getIntent();
        if (get.getStringExtra("flag_from_main").equals("y")) {
            place = get.getStringExtra("place");
            stt = get.getStringExtra("sttSwitch");
            ad = get.getStringExtra("address");
            place_layout.setText(place);
        }
        if (!get.getStringExtra("shop").equals("n")) {
            sh = get.getStringExtra("shop");
            switch (sh) {
                case "치킨":
                    task.execute("http://uswteami.dothome.co.kr/my/board/chicken/json.php");
                    break;
                case "피자":
                    task.execute("http://uswteami.dothome.co.kr/my/board/pizza/json.php");
                    break;

            }
        }

        try{
            List<Address> mR = m.getFromLocationName(ad, 1);
            lat1 = mR.get(0).getLatitude();
            lon1 = mR.get(0).getLongitude();
            Log.d(TAG, "onComplete: " + lat1 + lon1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "fail");
        }

        if (stt.equals("y")) {
            myTTS = new TextToSpeech(this, new OnInitListener() {
                @Override
                public void onInit(int status) {
                    s=0;
                    String Text = sh + " 카테고리입니다.배달가능한 " + sh + " 집은";
                    myTTS.speak(Text, TextToSpeech.QUEUE_FLUSH, null);

                    for (String n : names) {
                        myTTS.setSpeechRate(0.95f);
                        myTTS.speak(n, TextToSpeech.QUEUE_ADD, null);
                        s++;
                    }

                    myTTS.setSpeechRate(1f);
                    String text2 = "입니다. 원하시는 가게이름 을 말해주세요.";
                    myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mVoiceBtn.performClick();
                        }
                    }, 6500+2250*s);
                }
            });

            mVoiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player_s.start();
                    speak();
                }
            });


        }

        for(int i=0; i<btns.size(); i++){
            btns.get(i).setOnClickListener(onClick);
        }
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                a = 1;
                Intent i = new Intent(view.getContext(), Menu.class);
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("a", sh);
                i.putExtra("shop", shops.get(adapter.getName(position)));
                i.putExtra("shopname", shopnames.get(adapter.getName(position)));
                i.putExtra("flag_from_chicken", "y");
                i.putExtra("sttSwitch", stt);
                startActivity(i);
            }
        });

    }

    View.OnClickListener onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.back:
                    Intent i = new Intent(Chicken.this, MainActivity.class);
                    i.putExtra("place", place);
                    i.putExtra("flag_from_Payment", "n");
                    startActivity(i);
                    break;
            }
        }
    };


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Chicken.this,
                    "Please Wait", null, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d("a", "response  - " + result);

            if (result == null) {

            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }

    }

    private void showResult(){
        mlistView.setAdapter(adapter);

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            ArrayList<String> commant = new ArrayList<>();
            int k = 1;

            if(place.equals("리뷰 많은 순")) {
                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    commant.add(item.getString("hit"));
                }
                for(int i = 0; i<commant.size(); i++){
                    for(int j = 0; j<commant.size() - 1; j++){
                        if(Integer.parseInt(commant.get(j)) <= Integer.parseInt(commant.get(j + 1))){
                            String tmp = commant.get(j);
                            commant.set(j, commant.get(j+1));
                            commant.set(j+1, tmp);
                        }
                    }
                }
            }

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                Integer num = i + 1;
                String name = item.getString("가게이름");
                String address = item.getString("주소");
                String shop = item.getString("nickname");
                String hit = item.getString("hit");
                Integer a = commant.size();
                Log.d("commnatSize: ", a.toString());

                if(place.equals("리뷰 많은 순")) {
                    if ( (commant.size() != 0) && (commant.get(0).equals(hit)) ) {
                        Integer numb = k;

                        boolean f = true;
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put(TAG_ID, numb.toString());
                        hashMap.put(TAG_NAME, name);
                        hashMap.put(TAG_ADDRESS, address);

                        for(String n : names){
                            if(n.equals(name)) f = false;
                        }
                        if(f) {
                            try{
                                List<Address> mR = m.getFromLocationName("경기 화성시 봉담읍 와우안길 22 ", 1);
                                lat2 = mR.get(0).getLatitude();
                                lon2 = mR.get(0).getLongitude();
                                Log.d(TAG, "onComplete: " + lat2 + lon2);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, "fail");
                            }

                            String rs = calcDistance(lat1, lon1, lat2, lon2);

                            if(Integer.parseInt(rs) <= 20000) {
                                names.add(name);
                                shops.put(name, shop);
                                shopnames.put(shop, name);
                                adapter.addItem(numb.toString(), name, address);

                                k++;
                                i = -1;
                                commant.remove(0);
                            }
                        }else continue;
                    }
                }else {
                    try{
                        List<Address> mR = m.getFromLocationName("경기 화성시 봉담읍 와우안길 22 ", 1);
                        lat2 = mR.get(0).getLatitude();
                        lon2 = mR.get(0).getLongitude();
                        Log.d(TAG, "onComplete: " + lat2 + lon2);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "fail");
                    }

                    String rs = calcDistance(lat1, lon1, lat2, lon2);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(TAG_ID, num.toString());
                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_ADDRESS, address);

                    if(Integer.parseInt(rs) <= 20000) {
                        names.add(name);
                        shops.put(name, shop);
                        shopnames.put(shop, name);
                        adapter.addItem(num.toString(), name, address);
                    }
                }
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    public static String calcDistance(double lat1, double lon1, double lat2, double lon2){
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);

        double rslt = Math.round(Math.round(ret) / 1000);
        //String result = rslt + " km";
        //if(rslt == 0) result = Math.round(ret) +" m";
        String result = Math.round(ret) + "";

        return result;
    }

    private void speak() {
        //intent to show speech to text dialog 텍스트 대화 상자에 음성 표시
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        //start intent 인텐트 시작
        try {
            //in there was no error
            //show dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        } catch (Exception e) {
            //if there was some error
            //get message of error and show
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final MediaPlayer player_f = MediaPlayer.create(this, R.raw.finish);

        player_f.start();


        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    //get text array from voice intent 음성 인텐트에서 텍스트 배열 가져오기
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set to text view 텍스트 보기로 설정
                    for(String n : names){
                        if(result.get(0).equals(n)){
                            a = 1;
                            Intent i = new Intent(Chicken.this,Menu.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("a", sh);
                            i.putExtra("shop", shops.get(n));
                            i.putExtra("sttSwitch", stt);
                            i.putExtra("shopname", shopnames.get(shops.get(n)));
                            i.putExtra("flag_from_chicken", "y");
                            startActivity(i);
                        }
                    }

                    if(a == 1) break;

                    if(result.get(0).equals("이전")){
                        Intent i = new Intent(Chicken.this, MainActivity.class);
                        i.putExtra("place", place);
                        i.putExtra("flag_from_Payment", "n");
                        startActivity(i);
                    }
                    else if(result.get(0).equals("다시") || result.get(0).equals("-")){
                        s=0;
                        String Text = sh + " 카테고리입니다.배달가능한 " + sh + "집은";
                        myTTS.speak(Text, TextToSpeech.QUEUE_FLUSH, null);

                        for(String n : names){
                            myTTS.setSpeechRate(0.95f);
                            myTTS.speak(n, TextToSpeech.QUEUE_ADD, null);
                            s++;
                        }
                        myTTS.setSpeechRate(1f);
                        String text2 = "입니다. 원하시는 가게이름을 말해주세요.";
                        myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 6500+2250*s);
                    }else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 1000);
                    }
                    break;
                }else{
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mVoiceBtn.performClick();
                        }
                    }, 1000);
                }
            }


        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myTTS != null){
            myTTS.stop();
            myTTS.shutdown();
        }
    }



}
