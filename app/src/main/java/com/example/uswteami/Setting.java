package com.example.uswteami;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.util.ArrayList;
import java.util.Locale;

public class Setting extends AppCompatActivity implements OnInitListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    private ArrayList<Button> bs = new ArrayList<>();
    private Button placeLayoutChange, back, save;
    private TextView placeLayout, text;
    private ImageButton mVoiceBtn;

    private TextToSpeech myTTS;
    private static String place;
    private static String stt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Intent getp = getIntent();
        place = getp.getStringExtra("place");
        stt = getp.getStringExtra("sttSwitch");

        placeLayoutChange = (Button)findViewById(R.id.placeLayoutChange);
        text = (TextView) findViewById(R.id.text);
        placeLayout = (TextView) findViewById(R.id.placeLayout);
        placeLayout.setText(place);
        back = (Button)findViewById(R.id.back);
        save = (Button)findViewById(R.id.btn_save);
        mVoiceBtn = findViewById(R.id.voiceBtn);
        bs.add(placeLayoutChange);
        bs.add(back);
        bs.add(save);


        if(stt.equals("y")) {
            myTTS = new TextToSpeech(this, this);

            final MediaPlayer player_s = MediaPlayer.create(this, R.raw.start);

            back.setOnClickListener(onClick);
            save.setOnClickListener(onClick);
            placeLayoutChange.setOnClickListener(onClick);

            mVoiceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player_s.start();
                    speak();
                }
            });

        }
        for(int i = 0; i< bs.size(); i++){
            bs.get(i).setOnClickListener(onClick);
        }
    }

    View.OnClickListener onClick = new View.OnClickListener() {

        @Override

        public void onClick(View v) {

            switch(v.getId()){
                case R.id.placeLayoutChange:
                    if(placeLayout.getText().toString().equals("앱 지정순")) placeLayout.setText("리뷰 많은 순");
                    else placeLayout.setText("앱 지정순");
                    break;
                case R.id.back:
                    Intent i = new Intent(Setting.this, MainActivity.class);
                    i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("place", place);
                    i.putExtra("flag_from_Payment", "n");
                    startActivity(i);
                    break;
                case R.id.btn_save:
                    Intent i_save = new Intent(Setting.this, MainActivity.class);
                    i_save.setFlags(i_save.FLAG_ACTIVITY_CLEAR_TOP);
                    i_save.putExtra("place", placeLayout.getText().toString());
                    i_save.putExtra("flag_from_Payment", "n");
                    Toast.makeText(Setting.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
                    startActivity(i_save);

            }
        }
    };

    @Override
    public void onInit(int status) {
        String myText1 = "설정입니다.";
        String text2 = "매장 나열 방식의 교체를 원하시면 매장 을 말해주세요.";
        myTTS.speak(myText1, TextToSpeech.QUEUE_FLUSH, null);
        myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mVoiceBtn.performClick();
            }
        }, 6000);
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

                    text.setText(result.get(0));

                    btsClick(result.get(0));

                }

                break;

            }

        }



    }

    public void btsClick(String text){

        if(text.equals(bs.get(0).getText().toString())){

            if(placeLayout.getText().toString().equals("앱 지정순")){
                placeLayout.setText("리뷰 많은 순");
                String myText2 = "현재 매장 나열 방식은 " + placeLayout.getText().toString() + "입니다.";
                myTTS.setSpeechRate(0.95f);
                myTTS.speak(myText2, TextToSpeech.QUEUE_ADD, null);
            }
            else {
                placeLayout.setText("앱 지정순");
                String myText3 = "현재 매장 나열 방식은 " + placeLayout.getText().toString() + "입니다.";
                myTTS.setSpeechRate(0.95f);
                myTTS.speak(myText3, TextToSpeech.QUEUE_ADD, null);
            }
            myTTS.setSpeechRate(1f);
            String textOut = "현재 설정의 저장을 원하시면 저장, 원하시지 않으면 뒤로 를 말해주세요.";
            myTTS.speak(textOut, TextToSpeech.QUEUE_ADD, null);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    placeLayout.performClick();
                }
            }, 10000);
        }

        else if(text.equals(bs.get(1).getText().toString())){
            Intent i = new Intent(Setting.this, MainActivity.class);
            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("place", place);
            startActivity(i);
        }else if(text.equals(bs.get(2).getText().toString())){
            final Intent i_save = new Intent(Setting.this, MainActivity.class);
            i_save.setFlags(i_save.FLAG_ACTIVITY_CLEAR_TOP);
            i_save.putExtra("place", placeLayout.getText().toString());
            Toast.makeText(Setting.this, "저장되었습니다", Toast.LENGTH_SHORT).show();
            myTTS.speak("저장되었습니다.", TextToSpeech.QUEUE_ADD, null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i_save);
                }
            }, 1900);
        }

        else {
            new Handler().postDelayed(new Runnable() {

                @Override

                public void run() {
                    mVoiceBtn.performClick();
                }

            }, 1000);
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
