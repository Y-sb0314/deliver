package com.example.uswteami;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Payment extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private ImageButton mVoiceBtn;
    private TextToSpeech myTTS;
    private TextView name;
    private TextView price;
    private TextView content;

    private ArrayList<String> pay_name = new ArrayList<>();
    private ArrayList<String> pay_price = new ArrayList<>();
    private ArrayList<String> pay_content = new ArrayList<>();
    private String menu_name;
    private String menu_price;
    private String menu_content;
    private static int k = 0;

    ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();
    ListView payment;
    ListViewAdapterPayment adapter;
    int f = 0;

    private int a=0;
    private String stt;
    private Button back;
    private Button var;
    private ArrayList<Button> btns = new ArrayList<>();
    private TextView payPrice;
    private String m;
    private int s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent get = getIntent();
        menu_name = get.getStringExtra("name");
        if(menu_name.equals("장바구니")){
            setContentView(R.layout.payment);
        }else {
            setContentView(R.layout.payment_layout);
        }
        menu_name = get.getStringExtra("name");
        menu_price = get.getStringExtra("price");
        menu_content = get.getStringExtra("content");
        stt = get.getStringExtra("sttSwitch");
        m = get.getStringExtra("where");

        payPrice = (TextView)findViewById(R.id.payPrice);
        mVoiceBtn = findViewById(R.id.voiceBtn);
        back = (Button)findViewById(R.id.back);
        var = (Button)findViewById(R.id.var);
        btns.add(back);
        btns.add(var);

        final MediaPlayer player_s = MediaPlayer.create(this, R.raw.start);

        showResult(menu_name);


        if(menu_name.equals("장바구니")){
            var.setText("결제");
            Integer p = 0;
            for(int i = 0; i<pay_price.size(); i++){
                p += Integer.parseInt(pay_price.get(i));
            }
            payPrice.setText(p.toString());
        }

        if(stt.equals("y")) {
            myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    String text1 = menu_name + " 를 선택하셨 습니다.";
                    myTTS.speak(text1, TextToSpeech.QUEUE_FLUSH, null);

                    if (menu_name.equals("장바구니")) {
                        if (pay_name == null) {
                            String t = "현재 장바구니에는";
                            myTTS.speak(t + "메뉴가 없습니다.주문 카테고리 이동은 뒤로 입니다.", TextToSpeech.QUEUE_ADD, null);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVoiceBtn.performClick();
                                }
                            }, 5500);
                        } else {
                            String text2 = "결제는 결제, 메뉴확인은 확인 을 말해주세요.";
                            myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVoiceBtn.performClick();
                                }
                            }, 6800);
                        }
                    } else {
                        if(k == 0) {
                            String text2 = "장바구니에 저장은 저장, ";
                            String text3 = "메뉴의 설명은 설 명 , 가격 확인은 가격 을 말해주세요.";

                            myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);
                            myTTS.speak(text3, TextToSpeech.QUEUE_ADD, null);
                            k++;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVoiceBtn.performClick();
                                }
                            }, 9500);
                        }else{
                            String text2 = "명령어를 말해주세요.";
                            myTTS.speak(text2, TextToSpeech.QUEUE_ADD, null);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVoiceBtn.performClick();
                                }
                            }, 5500);
                        }

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
        for(int i=0; i<btns.size(); i++){
            btns.get(0).setOnClickListener(onClick);
        }

        var.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menu_name.equals("장바구니")){
                    Intent i = new Intent(Payment.this, Menu.class);
                    i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("flag_from_chicken", "no");
                    i.putExtra("flag_delete", "n");
                    i.putExtra("name", menu_name);
                    i.putExtra("price", menu_price);
                    i.putExtra("content", menu_content);
                    startActivity(i);
                    Toast.makeText(Payment.this, "장바구니에 저장되었습니다.", Toast.LENGTH_LONG).show();
                }else{
                    if(payPrice.getText().equals("0")){
                        Toast.makeText(Payment.this, "결제할 메뉴가 없습니다.", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(Payment.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(Payment.this, MainActivity.class);
                        i.putExtra("name", pay_name);
                        i.putExtra("price", pay_price);
                        i.putExtra("content", pay_content);
                        i.putExtra("k", 1);
                        i.putExtra("flag_from_Payment", "y");
                        startActivity(i);
                    }
                }
            }
        });

    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.back:
                    if(m.equals("main")){
                        Intent i = new Intent(Payment.this, MainActivity.class);
                        i.putExtra("name", pay_name);
                        i.putExtra("price", pay_price);
                        i.putExtra("content", pay_content);
                        i.putExtra("k", 1);
                        i.putExtra("flag_from_Payment", "y");
                        startActivity(i);
                        break;
                    }
                    if(menu_name.equals("장바구니")){
                        if(f != 0){
                            a=1;
                            Intent i = new Intent(Payment.this, Menu.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("flag_from_chicken", "no");
                            i.putExtra("name", "no");
                            i.putExtra("flag_delete", "y");
                            i.putExtra("pay_name", pay_name);
                            i.putExtra("pay_price", pay_price);
                            i.putExtra("pay_content", pay_content);
                            startActivity(i);
                            if(a==1) break;
                        }else {
                            a = 1;
                            Intent i = new Intent(Payment.this, Menu.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("flag_from_chicken", "no");
                            i.putExtra("flag_delete", "n");
                            i.putExtra("name", "no");
                            startActivity(i);
                            if (a == 1) break;
                        }
                    }else{
                        if(f != 0){
                            a=1;
                            Intent i = new Intent(Payment.this, Menu.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("flag_from_chicken", "no");
                            i.putExtra("name", "no");
                            i.putExtra("flag_delete", "y");
                            i.putExtra("pay_name", pay_name);
                            i.putExtra("pay_price", pay_price);
                            i.putExtra("pay_content", pay_content);
                            startActivity(i);
                            if(a==1) break;
                        }else {
                            a=1;
                            Intent i = new Intent(Payment.this, Menu.class);
                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.putExtra("flag_from_chicken", "no");
                            i.putExtra("flag_delete", "n");
                            i.putExtra("sttSwitch", stt);
                            i.putExtra("name", "no");
                            startActivity(i);
                            if(a==1) break;
                        }
                    }
                    break;
                case R.id.var:
                    break;
            }
        }
    };

    private void showResult(String text){
        payment = (ListView)findViewById(R.id.payment);
        adapter = new ListViewAdapterPayment();
        payment.setAdapter(adapter);

        if(text.equals("장바구니")) {
            if(f == 0) {
                Intent g = getIntent();
                pay_name = (ArrayList<String>) g.getSerializableExtra("pay_name");
                pay_price = (ArrayList<String>) g.getSerializableExtra("pay_price");
                pay_content = (ArrayList<String>) g.getSerializableExtra("pay_content");
            }

            for (int i = 0; i < pay_name.size(); i++) {

                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("pay_name", pay_name.get(i));
                hashMap.put("pay_price", pay_price.get(i));
                hashMap.put("pay_content", pay_content.get(i));
                adapter.addItem(pay_name.get(i), pay_price.get(i), pay_content.get(i));

            }


        }else{

            name = (TextView)findViewById(R.id.name);
            price = (TextView)findViewById(R.id.price);
            content = (TextView)findViewById(R.id.content);

            name.setText(menu_name);
            price.setText(menu_price);
            content.setText(menu_content);
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

                    String res = result.get(0).replace(" ", "");
                    int k = -1;


                    if (menu_name.equals("장바구니")) {
                        if (res.equals("이전")) {
                            if (m.equals("main")) {
                                Intent i = new Intent(Payment.this, MainActivity.class);
                                i.putExtra("name", pay_name);
                                i.putExtra("price", pay_price);
                                i.putExtra("content", pay_content);
                                i.putExtra("k", 1);
                                i.putExtra("flag_from_Payment", "y");
                                startActivity(i);
                            } else if (f != 0) {
                                a = 1;
                                Intent i = new Intent(Payment.this, Menu.class);
                                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("flag_from_chicken", "no");
                                i.putExtra("name", "no");
                                i.putExtra("flag_delete", "y");
                                i.putExtra("pay_name", pay_name);
                                i.putExtra("pay_price", pay_price);
                                i.putExtra("pay_content", pay_content);
                                i.putExtra("sttSwitch", stt);
                                startActivity(i);
                                if (a == 1) break;
                            } else {
                                a = 1;
                                Intent i = new Intent(Payment.this, Menu.class);
                                i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("flag_from_chicken", "no");
                                i.putExtra("flag_delete", "n");
                                i.putExtra("name", "no");
                                i.putExtra("sttSwitch", stt);
                                startActivity(i);
                                if (a == 1) break;
                            }
                        } else if (res.equals("확인")) {
                            int cnt = 0, pay = 0;
                            String text = "현재 장바구니에는";
                            if (pay_name.size() == 0) {
                                myTTS.speak(text + "메뉴가 없습니다.주문 카테고리 이동은 뒤로 입니다.", TextToSpeech.QUEUE_ADD, null);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVoiceBtn.performClick();
                                    }
                                }, 5500);
                            } else {
                                s = 0;
                                myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
                                for (String n : pay_name) {
                                    pay += Integer.parseInt(pay_price.get(cnt));
                                    myTTS.speak(n + " ", TextToSpeech.QUEUE_ADD, null);
                                    cnt++;
                                    s++;
                                }
                                Integer c = cnt;
                                Integer p = pay;
                                myTTS.setSpeechRate(0.95f);
                                myTTS.speak("의 " + c.toString() + " 개가 있으며, .", TextToSpeech.QUEUE_ADD, null);
                                myTTS.speak("총합 " + p.toString() + " 원 입니다.", TextToSpeech.QUEUE_ADD, null);
                                myTTS.speak("삭제할 메뉴가 있으시다면, 메뉴의 이름을 말해주세요.", TextToSpeech.QUEUE_ADD, null);
                                myTTS.setSpeechRate(1f);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVoiceBtn.performClick();
                                    }
                                }, 11000 + 2500 * s);
                            }
                        } else if (res.equals("결재") || res.equals("결제")) {
                            if (pay_name.size() == 0) {
                                myTTS.speak("장바구니에 결제 할 메뉴가 없습니다.주문 카테고리 이동은 뒤로 입니다.", TextToSpeech.QUEUE_ADD, null);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVoiceBtn.performClick();
                                    }
                                }, 4500);
                            } else {
                                int pay = 0;
                                int cnt = 0;
                                for (int i = 0; i < pay_price.size(); i++) {
                                    pay += Integer.parseInt(pay_price.get(i));
                                    cnt++;
                                }
                                Integer c = cnt;
                                Integer p = pay;
                                myTTS.speak(c.toString() + " 개의 메뉴 ", TextToSpeech.QUEUE_ADD, null);
                                myTTS.speak("총합 " + p.toString() + " 원 입니다.", TextToSpeech.QUEUE_ADD, null);
                                myTTS.speak("결제를 원하시면 결제승인 을 말해주세요.", TextToSpeech.QUEUE_ADD, null);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVoiceBtn.performClick();
                                    }
                                }, 6800);
                            }
                        } else if (res.equals("결제승인") || res.equals("결재승인")) {
                            myTTS.speak("결제가 정상적으로 완료 되었 습니다.", TextToSpeech.QUEUE_ADD, null);
                            //android.os.Process.killProcess(android.os.Process.myPid());
                            //System.exit(1);
                            Intent i = new Intent(Payment.this, MainActivity.class);
                            i.putExtra("name", menu_name);
                            i.putExtra("price", menu_price);
                            i.putExtra("content", menu_content);
                            i.putExtra("flag_from_Payment", "y");
                            startActivity(i);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mVoiceBtn.performClick();
                                }
                            }, 1000);
                        }

                        for (String n : pay_name) {
                            k++;
                            if (res.equals(n)) {
                                f++;
                                myTTS.speak(n + " 메뉴 가 장바구니 에서 삭제 되었습니다.", TextToSpeech.QUEUE_ADD, null);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mVoiceBtn.performClick();
                                    }
                                }, 4000);

                                pay_name.remove(k);
                                pay_content.remove(k);
                                pay_price.remove(k);
                                mArrayList.remove(k);
                                k = -1;
                                showResult("장바구니");
                                break;

                            }
                        }
                    } else if (res.equals("이전")) {
//                        if (f != 0) {
//                            a = 1;
//                            Intent i = new Intent(Payment.this, Menu.class);
//                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
//                            i.putExtra("flag_from_chicken", "no");
//                            i.putExtra("name", "no");
//                            i.putExtra("flag_delete", "y");
//                            i.putExtra("a", "aaa");
//                            i.putExtra("pay_name", pay_name);
//                            i.putExtra("pay_price", pay_price);
//                            i.putExtra("pay_content", pay_content);
//                            startActivity(i);
//                            if (a == 1) break;
//                        } else {
//                            a = 1;
//                            final Intent i = new Intent(Payment.this, Menu.class);
//                            i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
//                            i.putExtra("flag_from_chicken", "no");
//                            i.putExtra("flag_delete", "n");
//                            i.putExtra("name", "no");
//                            i.putExtra("name", menu_name);
//                            i.putExtra("price", menu_price);
//                            i.putExtra("content", menu_content);
//                            if (a == 1) break;
//                        }
                    } else if (res.equals("설명")) {
                        String text = menu_name + " 메뉴 는  " + menu_content;
                        myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 3500);
                    } else if (res.equals("가격")) {
                        myTTS.setSpeechRate(0.95f);
                        String text = menu_name + " 메뉴 의 가격 은    " + menu_price + "    원 입니다.";
                        myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
                        myTTS.setSpeechRate(1f);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mVoiceBtn.performClick();
                            }
                        }, 4600);
                    }
                 else if (res.equals("저장")) {
                        myTTS.speak("장바구니에 저장 되었 습니다.", TextToSpeech.QUEUE_ADD, null);
                        a = 1;
                        final Intent i = new Intent(Payment.this, Menu.class);
                        i.setFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("flag_from_chicken", "no");
                        i.putExtra("flag_delete", "n");
                        i.putExtra("name", menu_name);
                        i.putExtra("price", menu_price);
                        i.putExtra("content", menu_content);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(i);
                            }
                        }, 1800);

                        if (a == 1) break;
                    } else if (res.equals("설명")) {
                        String text = menu_name + " 메뉴 는  " + menu_content;
                        myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
                    } else if (res.equals("가격")) {
                        myTTS.setSpeechRate(0.95f);
                        String text = menu_name + " 메뉴 의 가격 은    " + menu_price + "    원 입니다.";
                        myTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
                        myTTS.setSpeechRate(1f);
                    }

                    break;
                } else{
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
