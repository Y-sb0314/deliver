package com.example.uswteami;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Menu extends AppCompatActivity {

    // stt, tts 변수
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private TextToSpeech myTTS;
    private ImageButton mVoiceBtn;

    // json 변수
    private static String TAG = "phptest_MainActivity";
    private static final String TAG_JSON="webnautes";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ADDRESS ="address";

    // Intent로 받아오는 변수(Chicken.java로부터)
    private static String shop;
    private static String shopname;
    private String flag_from_chicken;
    private String sh;
    private static String stt;

    // Intent로 받아오는 변수(Menu.java로부터)
    private static ArrayList<String> pay_name = new ArrayList<>();
    private static ArrayList<String> pay_price = new ArrayList<>();
    private static ArrayList<String> pay_content = new ArrayList<>();
    private static int flag = 0;
    private Integer num = flag;

    // json pasing 변수
    int num_main = 1;
    int num_side = 1;
    int num_soda = 1;
    ArrayList<HashMap<String, String>> mArrayList_main;
    ArrayList<HashMap<String, String>> mArrayList_side;
    ArrayList<HashMap<String, String>> mArrayList_soda;
    List<String> names_main = new ArrayList<>();
    List<String> names_side = new ArrayList<>();
    List<String> names_soda = new ArrayList<>();
    ListView mlistView;
    ListViewAdapter adapter;
    String mJsonString_main;
    String mJsonString_side;
    String mJsonString_soda;

    // Intent 전달 변수
    HashMap<String, String> price_main = new HashMap<>();
    HashMap<String, String> price_side = new HashMap<>();
    HashMap<String, String> price_soda = new HashMap<>();
    HashMap<String, String> content_main = new HashMap<>();
    HashMap<String, String> content_side = new HashMap<>();
    HashMap<String, String> content_soda = new HashMap<>();

    private int a=0;
    private Button pay;
    private int s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        pay = (Button)findViewById(R.id.pay);
        mVoiceBtn = (ImageButton)findViewById(R.id.voiceBtn);

        final MediaPlayer player_s = MediaPlayer.create(this, R.raw.start);


        Intent get = getIntent();
        flag_from_chicken = get.getStringExtra("flag_from_chicken");
        if(flag_from_chicken.equals("y")) {
            shop = get.getStringExtra("shop");
            shopname = get.getStringExtra("shopname");
            sh = get.getStringExtra("a");
            stt = get.getStringExtra("sttSwitch");
        }


        if(flag != 0){
            Intent g = getIntent();
            if(!g.getStringExtra("name").equals("no")){
                pay_name.add(g.getStringExtra("name"));
                pay_price.add(g.getStringExtra("price"));
                pay_content.add(g.getStringExtra("content"));
            }
            if(g.getStringExtra("flag_delete").equals("y")){
                pay_name = (ArrayList<String>) g.getSerializableExtra("pay_name");
                pay_price = (ArrayList<String>) g.getSerializableExtra("pay_price");
                pay_content = (ArrayList<String>) g.getSerializableExtra("pay_content");
            }


        }



        mlistView = (ListView) findViewById(R.id.listView);
        adapter = new ListViewAdapter();
        mArrayList_main = new ArrayList<>();
        mArrayList_side = new ArrayList<>();
        mArrayList_soda = new ArrayList<>();

        GetData_main task1 = new GetData_main();
        task1.execute("http://uswteami.dothome.co.kr/my/board/chicken/menu/json_main.php");

        GetData_side task2 = new GetData_side();
        task2.execute("http://uswteami.dothome.co.kr/my/board/chicken/menu/json_side.php");

        GetData_soda task3 = new GetData_soda();
        task3.execute("http://uswteami.dothome.co.kr/my/board/chicken/menu/json_soda.php");

        if(stt.equals("y")) {
            myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (flag == 0) {
                        String text1 = shopname + " 입니다.";
                        String text2 = "메인메뉴는 메인, 사이드메뉴는 사이드, 음료는 음료, 또는 메뉴 이름 을 말해주세요.";
                        String text3 = "장바구니 이동은 구매 입니다.";

                        myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
                        myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);
                        myTTS.speak(text3, TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 10000);

                    } else {
                        String text1 = shopname + " 입니다.";
                        String text2 = "명령어를 말해주세요.";

                        myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
                        myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 3500);
                    }
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

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.getName(position).equals("메인메뉴") || adapter.getName(position).equals("사이드메뉴") || adapter.getName(position).equals("음료"))
                {}
                else if(position <= price_main.size() + 1) {
                    a = 1;
                    Intent i = new Intent(Menu.this, Payment.class);
                    i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("name", adapter.getName(position));
                    i.putExtra("price", price_main.get(adapter.getName(position)));
                    i.putExtra("content", content_main.get(adapter.getName(position)));
                    i.putExtra("where", "menu");
                    i.putExtra("sttSwitch", stt);
                    flag++;
                    startActivity(i);
                }else if(position <= price_main.size() + price_side.size() + 2){
                    a = 1;
                    Intent i = new Intent(Menu.this, Payment.class);
                    i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("name", adapter.getName(position));
                    i.putExtra("price", price_side.get(adapter.getName(position)));
                    i.putExtra("content", content_side.get(adapter.getName(position)));
                    i.putExtra("where", "menu");
                    i.putExtra("sttSwitch", stt);
                    flag++;
                    startActivity(i);
                }else{
                    a = 1;
                    Intent i = new Intent(Menu.this, Payment.class);
                    i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("name", adapter.getName(position));
                    i.putExtra("price", price_soda.get(adapter.getName(position)));
                    i.putExtra("content", content_soda.get(adapter.getName(position)));
                    i.putExtra("where", "menu");
                    i.putExtra("sttSwitch", stt);
                    flag++;
                    startActivity(i);
                }
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, Payment.class);
                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("name", "장바구니");
                i.putExtra("pay_name", pay_name);
                i.putExtra("pay_price", pay_price);
                i.putExtra("pay_content", pay_content);
                i.putExtra("sttSwitch", stt);
                i.putExtra("where", "menu");

                startActivity(i);
            }
        });
    }

    private class GetData_main extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Menu.this,
                    "Please Wait", null, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null) {

            } else {

                mJsonString_main = result;
                showResult_main();
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


    private void showResult_main() {
        mlistView.setAdapter(adapter);
        adapter.addItem("", "메인메뉴", "");
        try {
            JSONObject jsonObject = new JSONObject(mJsonString_main);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString("메뉴이름");
                String address = item.getString("가격");
                String where = item.getString("shop");
                String content = item.getString("설명");

                HashMap<String, String> hashMap = new HashMap<>();

                if(shop.equals(where)) {
                    Integer num = num_main++;
                    hashMap.put(TAG_ID, num.toString());
                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_ADDRESS, address);

                    names_main.add(name);
                    price_main.put(name, address);
                    content_main.put(name, content);
                    adapter.addItem(num.toString(), name, address);
                }
                else continue;
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    //------------------------------------------------------------------------------------------------------------

    private class GetData_side extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Menu.this,
                    "Please Wait", null, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null) {
            } else {
                mJsonString_side = result;
                showResult_side();
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


    private void showResult_side() {
        mlistView.setAdapter(adapter);
        adapter.addItem("", "사이드메뉴", "");
        try {
            JSONObject jsonObject = new JSONObject(mJsonString_side);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString("메뉴이름");
                String address = item.getString("가격");
                String where = item.getString("shop");
                String content = item.getString("설명");

                HashMap<String, String> hashMap = new HashMap<>();

                if(shop.equals(where)) {
                    Integer num = num_side++;
                    hashMap.put(TAG_ID, num.toString());
                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_ADDRESS, address);

                    names_side.add(name);
                    price_side.put(name,address);
                    content_side.put(name,content);
                    adapter.addItem(num.toString(), name, address);
                }
                else continue;
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    //------------------------------------------------------------------------------------------------------

    private class GetData_soda extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Menu.this,
                    "Please Wait", null, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null) {
            } else {

                mJsonString_soda = result;
                showResult_soda();
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


    private void showResult_soda() {
        mlistView.setAdapter(adapter);
        adapter.addItem("", "음료", "");
        try {
            JSONObject jsonObject = new JSONObject(mJsonString_soda);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString("메뉴이름");
                String address = item.getString("가격");
                String where = item.getString("shop");
                String content = item.getString("설명");

                HashMap<String, String> hashMap = new HashMap<>();

                if(shop.equals(where)) {
                    Integer num = num_soda++;
                    hashMap.put(TAG_ID, num.toString());
                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_ADDRESS, address);

                    names_soda.add(name);
                    price_soda.put(name, address);
                    content_soda.put(name, content);
                    adapter.addItem(num.toString(), name, address);
                }
                else continue;
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

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
                    String  res = result.get(0).replace(" ", "");

                    for (String n : names_main) {
                        if (res.equals(n)){
                            a=1;
                            Intent i = new Intent(Menu.this, Payment.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("name", n);
                            i.putExtra("price", price_main.get(n));
                            i.putExtra("content", content_main.get(n));
                            i.putExtra("sttSwitch", stt);
                            i.putExtra("where", "menu");
                            flag++;
                            startActivity(i);
                        }
                    }

                    for (String n : names_side) {
                        if (res.equals(n)){
                            a=1;
                            Intent i = new Intent(Menu.this, Payment.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("name", n);
                            i.putExtra("price", price_side.get(n));
                            i.putExtra("content", content_side.get(n));
                            i.putExtra("sttSwitch", stt);
                            i.putExtra("where", "menu");
                            flag++;
                            startActivity(i);
                        }
                    }

                    for (String n : names_soda) {
                        if (res.equals(n)){
                            a=1;
                            Intent i = new Intent(Menu.this, Payment.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("name", n);
                            i.putExtra("price", price_soda.get(n));
                            i.putExtra("content", content_soda.get(n));
                            i.putExtra("sttSwitch", stt);
                            i.putExtra("where", "menu");
                            flag++;
                            startActivity(i);
                        }
                    }
                    if (a==1) break;

                    if(res.equals("메인")){
                        s=0;
                        String text1 = "메인메뉴에는";
                        myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
                        for(String n : names_main){
                            myTTS.setSpeechRate(0.95f);
                            myTTS.speak(n + "  ", TextToSpeech.QUEUE_ADD, null);
                            s++;
                        }
                        myTTS.setSpeechRate(1f);
                        myTTS.speak("가 있습니다.원하시는 메뉴 이름을 말해주세요.", TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 4000+2500*s);

                    }else if(res.equals("사이드") || res.equals("싸이드")){
                        s=0;
                        String text1 = "사이드메뉴에는";
                        myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
                        for(String n : names_side){
                            myTTS.setSpeechRate(0.95f);
                            myTTS.speak(n + "  ", TextToSpeech.QUEUE_ADD, null);
                            s++;
                        }
                        myTTS.setSpeechRate(1f);
                        myTTS.speak("가 있습니다.원하시는 메뉴 이름을 말해주세요.", TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 4500+2500*s);

                    }else if(res.equals("음료")){
                        s=0;
                        String text1 = "음료에는";
                        myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
                        for(String n : names_soda){
                            myTTS.setSpeechRate(0.95f);
                            myTTS.speak(n + "  ", TextToSpeech.QUEUE_ADD, null);
                            s++;
                        }
                        myTTS.setSpeechRate(1f);
                        myTTS.speak("가 있습니다.원하시는 메뉴 이름을 말해주세요.", TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 4000+2000*s);

                    }else if(res.equals("다시") || res.equals("-")){
                        String text1 = shopname + " 입니다.";
                        String text2 = "메인메뉴는 메인, 사이드메뉴는 사이드, 음료는 음료, 또는 메뉴 이름 을 말해주세요.";
                        String text3 = "장바구니 이동은 구매 입니다.";

                        myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
                        myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);
                        myTTS.speak(text3, TextToSpeech.QUEUE_ADD, null);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 10000);
                    }else if(res.equals("구매")){
                        a=1;
                        Intent i = new Intent(Menu.this, Payment.class);
                        i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("name", "장바구니");
                        i.putExtra("pay_name", pay_name);
                        i.putExtra("pay_price", pay_price);
                        i.putExtra("pay_content", pay_content);
                        i.putExtra("sttSwitch", stt);
                        i.putExtra("where", "menu");

                        startActivity(i);
                        if(a==1) break;
                    }else if(res.equals("이전")){
                        a=1;
                        Intent i = new Intent(Menu.this, Chicken.class);
                        i.putExtra("flag_from_main", "n");
                        i.putExtra("shop",sh);
                        startActivity(i);
                        if(a==1) break;
                    }

                    else {
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
