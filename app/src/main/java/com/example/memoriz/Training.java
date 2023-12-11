package com.example.memoriz;


//import static com.example.memoriz.MainActivity.BROADCAST_ACTION_CONTROL;
//import static com.example.memory.MainActivity.MESSAGEOUTPUTCONTROL;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.security.SecureRandom;

public class Training extends Activity implements OnCompletionListener {
    String fragment;
    String netchoice="3";
    String hostIP;
    String port_srv;
    int intport_srv;
    String sending_command;
    String password;

    String ipaddress;
    String macaddress="1122.3344.5566";
    String factory="amx";
    String name="panel";
    String username;
    String pinganswer;
    String row;
    String satz;
    String translate;
    String filename_satz;
    String filename_translate;
    String thema;
    String themen[];
    String Current_theme;
    public String themax;
    final public String[][] training_data = new String[100][3];
    int count;
    int counter;
    String deutschtext="";
    String ourtext;
    String rating;
    int duration;
    int my_lesson;
    int index_lesson;
    int timepause1;
    int timepause2;
    int themaposition;
    boolean revers;
    boolean stop;

    DBHelper dbHelper;

//    BroadcastReceiver br;
    Context ctx;

    private static final String LOG_TAG = "===Training===" ;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    //  private MediaPlayer mediaPlayer;
    private String path;
    Boolean button_player_d_is_played = false;
    Boolean button_player_r_is_played = false;
    Boolean button_recorder_d_is_record = false;
    Boolean button_recorder_r_is_record = false;
    Boolean player_is_played = false;
    Boolean recorder_is_record = false;
    Boolean autoplayenable = false;
    Boolean text_is_play = false;
    Boolean play_1_enable = false;
    Boolean repeat_enable = false;

    Button button_record_d;
    Button button_record_r;
    Button button_player_d;
    Button button_player_r;
    ProgressBar ProgressBar;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    Switch switch1;
    Button button_play_1;
    Button button_repeat;
    RatingBar ratingBar;
    Cursor cursor3 = null;
    TextView text;
    TextView text2;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        button_player_d_is_played = false;
        button_player_r_is_played = false;
        button_recorder_d_is_record = false;
        button_recorder_r_is_record = false;

        Boolean button_player_d_is_played = false;


        autoplayenable = false;
        ctx = (Context) Training.this;
        dbHelper = new DBHelper(this);
        path = Environment.getExternalStorageDirectory() + "/Podcasts/Memory/";

        themax = getIntent().getStringExtra("thema");
        themaposition = getIntent().getIntExtra("themaposition",0);
        row= getIntent().getStringExtra("row");
        satz = getIntent().getStringExtra("satz");
        translate = getIntent().getStringExtra("translate");
        filename_satz = satz.trim().replaceAll("\\p{Punct}","_");
        filename_translate = translate.trim().replaceAll("\\p{Punct}","_");

        Log.d(LOG_TAG, "themax  = "+themax);
        Log.d(LOG_TAG, "themaposition  = "+themaposition);
        //    Log.d(LOG_TAG, "satz  = "+satz);
    //    Log.d(LOG_TAG, "filename_satz  = "+filename_satz);
    //    Log.d(LOG_TAG, "translate = "+translate);

        setContentView(R.layout.training);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        Spinner spinner = findViewById(R.id.spinner);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        ratingBar = findViewById(R.id.ratingBar);
        switch1 = findViewById(R.id.switch1);

        text = (TextView) findViewById(R.id.editText);
        text2 = (TextView) findViewById(R.id.editText2);

        // Получаем список тем
        int count_themen=0;
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String [] themen0 = new String[100];

        Cursor cursor = database.query(
                "anfangtable",
                null,
                null,
                null,
                "Lesson",
                null,
                "_id");
//               new String[] {String.valueOf(my_lesson)},
        cursor.moveToFirst();
        do{
            int themaIndex = cursor.getColumnIndex(DBHelper.KEY_LESSON);
            themen0[count_themen]= cursor.getString(themaIndex);
            //Log.d(LOG_TAG, "themen0 = " + themen0[count_themen]);
            count_themen++;
        } while (cursor.moveToNext());

        //dbHelper.close();
        themen = new String[count_themen];
        //count_themen--;
        do{
            //Log.d(LOG_TAG, "count_themen0 = " + count_themen);
            Current_theme = themen0[count_themen-1];
            themen[count_themen-1] = Current_theme;
            count_themen--;
            // Log.d(LOG_TAG, "themen = " + themen[count_themen]);

        } while (count_themen > 0);

        // Настраиваем адаптер
        //ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.lessons, android.R.layout.simple_spinner_item);
        ArrayAdapter<?> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, themen);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // Вызываем адаптер
        spinner.setAdapter(adapter);

        spinner.setSelection(themaposition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {

                counter=-1;
                //String[] choose = getResources().getStringArray(R.array.lessons);
                //  String[] choose = themen;

               // selectedItemPosition = 2;



                try {
                    //   thema = choose[selectedItemPosition];
                    thema = themen[selectedItemPosition];
         //           thema=themax;
                    //    my_lesson = Integer.parseInt(choose[selectedItemPosition]);
                    //    my_lesson = Integer.parseInt(themen[selectedItemPosition]);

                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

                //===
                //======================================================================
                //  выбираем данные выбранного урока
                //======================================================================

                Log.d(LOG_TAG, "thema = " + thema);

                SQLiteDatabase database2 = dbHelper.getWritableDatabase();
                String allData[] = new String[5];

                count=0;

                Cursor cursor3 = database2.query(
                        "anfangtable",
                        null,
                        "lesson = ?",
                        new String[] {thema},
                        null,
                        null,
                        null);
//               new String[] {String.valueOf(my_lesson)},
                cursor3.moveToFirst();
                do{
                    int deutschtextIndex = cursor3.getColumnIndex(com.example.memoriz.DBHelper.KEY_DEUTSCHTEXT);
                    int ourtextIndex = cursor3.getColumnIndex(com.example.memoriz.DBHelper.KEY_OURTEXT);
                    int idIndex = cursor3.getColumnIndex(com.example.memoriz.DBHelper.KEY_ID);
                    int rating = cursor3.getColumnIndex(DBHelper.KEY_DEUTSCHSOUND);
                    allData[0]= cursor3.getString(deutschtextIndex);
                    allData[2]= cursor3.getString(ourtextIndex);
                    allData[1]= cursor3.getString(idIndex);
                    allData[3]= String.valueOf(count);
                    allData[4]= cursor3.getString(rating);

                    training_data [count] [0] = allData[0];
                    training_data [count] [1] = allData[2];
                    training_data [count] [2] = allData[4];
                //    Log.d(LOG_TAG, "Rating = " + allData[4]);

                    count++;
                } while (cursor3.moveToNext());
                Log.d(LOG_TAG, "count = " + count);
                dbHelper.close();




                //======================================================================


            }
                @Override
                public void onNothingSelected (AdapterView < ? > adapterView){

            }
        });



        rollgasse();

//=================================

        button_play_1 = (Button) findViewById(R.id.button_play_1);
        //  button_vorwarts.setText(R.string.button_vorwarts);
        button_play_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_play_1 ");

                if(play_1_enable){
                    play_1_enable = false;
                    button_play_1.setText("play");
                }else{
                    if(! deutschtext.isEmpty()){
                        play_1_enable = true;
                        button_play_1.setText("stop");
                        text.setText(deutschtext);
                        text2.setText(ourtext);
                        playStop();
                    }
                }
                if(play_1_enable && ! deutschtext.isEmpty()) {
                    running_play_1();


                }
            }


        });

        button_repeat = (Button) findViewById(R.id.button_play_10);
        //  button_vorwarts.setText(R.string.button_vorwarts);
        button_repeat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_play_10 ");

                if(repeat_enable){
                    repeat_enable = false;
                    stop = true;
                    button_repeat.setText("repeat");
                }else{
                    if(! deutschtext.isEmpty()){
                        repeat_enable = true;
                        button_repeat.setText("stop");
                        text.setText(deutschtext);
                        text2.setText(ourtext);
                        stop = false;
                        playStop();
                    }
                }
                if(repeat_enable && ! deutschtext.isEmpty()) {
                    Log.d(LOG_TAG, "stop0 = " + stop);
                    running_play_1();
                }
            }


        });

//=================================================================================================



        Button button_ruckwarts = (Button) findViewById(R.id.button_ruck);
      //  button_vorwarts.setText(R.string.button_vorwarts);
        button_ruckwarts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_ruckwarts ");

                stop = false;
                if(radioButton1.isChecked()){
                    if(counter>0) {counter--;} else {counter=0;}
                }

                if(radioButton2.isChecked()){
                    SecureRandom random = new SecureRandom();
                    counter = random.nextInt(count);
                }




                deutschtext = training_data [counter] [0];
                ourtext = training_data [counter] [1];
                rating =  training_data [counter] [2];

                Log.d(LOG_TAG, "deutschtext = "+ deutschtext);
                Log.d(LOG_TAG, "ourtext = "+ ourtext);
                Log.d(LOG_TAG, "Rating = "+ rating);


                if(switch1.isChecked()) {
                    text.setText(deutschtext);
                }else{
                    text.setText(". . .");
                }

  //              final TextView text2 = (TextView) findViewById(R.id.editText2);
                if(switch1.isChecked()) {
                    text2.setText(". . .");
                }else{
                    text2.setText(ourtext);
                }


                ratingBar.setRating(Float.parseFloat(rating));


            //    playStart(deutschtext+".mp3");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(!stop){
  //                      final TextView text = (TextView) findViewById(R.id.editText);
                        if (switch1.isChecked()) {
                            text2.setText(ourtext);
                        } else {
                            text.setText(deutschtext);
                        }
                        if (switch1.isChecked()) {
                            filename_satz = ourtext.trim().replaceAll("\\p{Punct}", "_");
                        } else {
                            filename_satz = deutschtext.trim().replaceAll("\\p{Punct}", "_");
                        }
                        playStart(filename_satz + ".mp3");

                    }
                    }
                }, duration+1500);

            }
        });

        Button button_auto = (Button) findViewById(R.id.button_auto);
        //  button_vorwarts.setText(R.string.button_vorwarts);
        button_auto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_auto ");

            if(autoplayenable){
                autoplayenable = false;
                button_auto.setText("run");
                stop = true;
                playStop();
                releasePlayer();
            }else{
                stop = false;
                autoplayenable = true;
                button_auto.setText("stop");
            }

                if(autoplayenable) {
                    running_auto();
                }

            }


        });


        Button button_vorwarts = (Button) findViewById(R.id.button_vor);
        //  button_vorwarts.setText(R.string.button_vorwarts);
        button_vorwarts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_vorwarts ");

                stop = false;
                if(radioButton1.isChecked()){
                    if(counter<count) counter++;
                }

                if(radioButton2.isChecked()){
                    SecureRandom random = new SecureRandom();
                    counter = random.nextInt(count);
                }

                deutschtext = training_data [counter] [0];
                ourtext = training_data [counter] [1];
                rating =  training_data [counter] [2];

                Log.d(LOG_TAG, "deutschtext = "+ deutschtext);
                Log.d(LOG_TAG, "ourtext = "+ ourtext);
                Log.d(LOG_TAG, "Rating = "+ rating);

   //             final TextView text = (TextView) findViewById(R.id.editText);
                if(switch1.isChecked()) {
                    text.setText(deutschtext);
                }else{
                    text.setText(". . .");
                }

   //             final TextView text2 = (TextView) findViewById(R.id.editText2);
                if(switch1.isChecked()) {
                    text2.setText(". . .");
                }else{
                    text2.setText(ourtext);
                }

                ratingBar.setRating(Float.parseFloat(rating));


                //    playStart(deutschtext+".mp3");

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!stop){
   //                         final TextView text = (TextView) findViewById(R.id.editText);
                        if (switch1.isChecked()) {
                            text2.setText(ourtext);
                        } else {
                            text.setText(deutschtext);
                        }
                        if (switch1.isChecked()) {
                            filename_satz = ourtext.trim().replaceAll("\\p{Punct}", "_");
                        } else {
                            filename_satz = deutschtext.trim().replaceAll("\\p{Punct}", "_");
                        }
                        playStart(filename_satz + ".mp3");
                    }
                    }
                }, duration+1500); //пауза после нашего слова
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            // Called when the user swipes the RatingBar
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratio, boolean fromUser) {

            //    Log.d(LOG_TAG, "Rating1 = " + ratio);

                int ratioint = Math.round(ratio);
                rating = String.valueOf(ratioint);

                Log.d(LOG_TAG, "rating = " + rating);
                Log.d(LOG_TAG, "lesson = " + thema);
                Log.d(LOG_TAG, "deutschtext = " + deutschtext);
                Log.d(LOG_TAG, "owntext = " + ourtext);



                // если есть слова, записываем в DB значение сложности
                if(!deutschtext.equals("") && !ourtext.equals("")) {

                    training_data[counter][2] = rating;
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_LESSON, thema);
                    contentValues.put(DBHelper.KEY_DEUTSCHTEXT, deutschtext);
                    contentValues.put(DBHelper.KEY_OURTEXT, ourtext);
                    contentValues.put(DBHelper.KEY_DEUTSCHSOUND, rating);

                    Cursor cursor = database.query(
                            "anfangtable",
                            null,
                            "lesson = ? AND deutschtext = ? AND owntext = ?",
                            new String[]{thema, deutschtext, ourtext},
                            null,
                            null,
                            null);

                    if (cursor.moveToFirst()) {
                        database.update(
                                "anfangtable",
                                contentValues,
                                "lesson = ? AND deutschtext = ? AND owntext = ?",
                                new String[]{thema, deutschtext, ourtext,});
                    }
                // иначе выбираем сложность слов
                } else {
                    SQLiteDatabase database2 = dbHelper.getWritableDatabase();
                    String allData[] = new String[5];
                    count=0;

                           cursor3 = database2.query(
                                   "anfangtable",
                                   null,
                                   "lesson = ? AND deutschsound = ?",
                                   new String[] {thema, rating},
                                   null,
                                   null,
                                   null);
//               new String[] {String.valueOf(my_lesson)},
                           cursor3.moveToFirst();
                           do{
                               int deutschtextIndex = cursor3.getColumnIndex(com.example.memoriz.DBHelper.KEY_DEUTSCHTEXT);
                               int ourtextIndex = cursor3.getColumnIndex(com.example.memoriz.DBHelper.KEY_OURTEXT);
                               int idIndex = cursor3.getColumnIndex(com.example.memoriz.DBHelper.KEY_ID);
                               int rating = cursor3.getColumnIndex(DBHelper.KEY_DEUTSCHSOUND);
                               allData[0]= cursor3.getString(deutschtextIndex);
                               allData[2]= cursor3.getString(ourtextIndex);
                               allData[1]= cursor3.getString(idIndex);
                               allData[3]= String.valueOf(count);
                               allData[4]= cursor3.getString(rating);

                               training_data [count] [0] = allData[0];
                               training_data [count] [1] = allData[2];
                               training_data [count] [2] = allData[4];
                           //    Log.d(LOG_TAG, "Rating = " + allData[4]);

                               count++;
                           } while (cursor3.moveToNext());
                           Log.d(LOG_TAG, "count = " + count);
                           dbHelper.close();
                           //======================================================================

                }

            }
         //================


        });
    }

    private void running_play_1() {

        filename_satz = deutschtext.trim().replaceAll("\\p{Punct}","_");
        playStart(filename_satz + ".mp3");

    }


    private void rollgasse() {


        //   Context ctx = (Context)Fragment1.this.getActivity();
        Log.d(LOG_TAG, " == processing == ");
        //    dbHelper = new dbHelper(ctx);

    }


    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(LOG_TAG, "onCompletion");
        Log.d(LOG_TAG, "stop = " + stop);

        if(autoplayenable && text_is_play){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!stop){
                    if (autoplayenable) {
                        running_auto();
                    }
                }
                }
            }, duration+timepause2);
        }
        text_is_play =false;
        play_1_enable = false;
        button_play_1.setText("play");

        if(repeat_enable){

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!stop){
                    if (repeat_enable) {
                        Log.d(LOG_TAG, "===running_play_1=== = ");
                        running_play_1();
                    }
                }
                }
            }, duration+timepause2);
        }

    }
    private void playStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }
    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

        }
    }


    private void playStart(String fileName) {

        Log.d(LOG_TAG, "===fileName=== = " + path + fileName);

        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path+fileName);
            mediaPlayer.prepare();
            duration=mediaPlayer.getDuration();
        //    Log.d(LOG_TAG,"====Player start, duration = "+ duration);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================
    private void running_auto() {

        if(radioButton1.isChecked()){
            if(counter<count-1) {counter++;} else {counter=0;}
        }

        if(radioButton2.isChecked()){
            SecureRandom random = new SecureRandom();
            counter = random.nextInt(count);
        }

        if(radioButton3.isChecked()){
            SecureRandom random = new SecureRandom();
            revers = random.nextBoolean();

            SecureRandom random2 = new SecureRandom();
            counter = random2.nextInt(count);
        }

        if(autoplayenable) {
        deutschtext = training_data [counter] [0];
        ourtext = training_data [counter] [1];
        rating =  training_data [counter] [2];

        switch (rating) {
            case "1":   timepause1 = 2000;
                        timepause2 = 500;
                break;
            case "2":   timepause1 = 3000;
                        timepause2 = 1000;
                break;
            case "3":   timepause1 = 4000;
                        timepause2 = 2000;
                break;
            case "4":   timepause1 = 5000;
                        timepause2 = 3000;
                break;
            case "5":   timepause1 = 6000;
                        timepause2 = 4000;
                break;

        }

            Log.d(LOG_TAG, "count................. = "+ count);
            Log.d(LOG_TAG, "counter................. = "+ counter);
            Log.d(LOG_TAG, "deutschtext = "+ deutschtext);
            Log.d(LOG_TAG, "ourtext = "+ ourtext);
            Log.d(LOG_TAG, "Rating = "+ rating);

  //      final TextView text = (TextView) findViewById(R.id.editText);

            if(radioButton3.isChecked()){
                if(revers) {
                    text.setText(deutschtext);
                }else{
                    text.setText(". . .");
                }
            }else{
                if(switch1.isChecked()) {
                    text.setText(deutschtext);
                }else{
                    text.setText(". . .");
                }
            }



  //      final TextView text2 = (TextView) findViewById(R.id.editText2);
            if(radioButton3.isChecked()){
                if(revers) {
                    text2.setText(". . .");
                }else{
                    text2.setText(ourtext);
                }
            }else{
                if(switch1.isChecked()) {
                    text2.setText(". . .");
                }else{
                    text2.setText(ourtext);
                }
            }

        ratingBar.setRating(Float.parseFloat(rating));

            if(radioButton3.isChecked()){
                if(revers) {
                    filename_satz = deutschtext.trim().replaceAll("\\p{Punct}", "_");
                }else{
                    filename_satz = ourtext.trim().replaceAll("\\p{Punct}", "_");
                }
            }else{
                if(switch1.isChecked()) {
                    filename_satz = deutschtext.trim().replaceAll("\\p{Punct}", "_");
                }else{
                    filename_satz = ourtext.trim().replaceAll("\\p{Punct}", "_");
                }
            }

        playStart(filename_satz + ".mp3");
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!stop){

  //              final TextView text = (TextView) findViewById(R.id.editText);
  //              final TextView text2 = (TextView) findViewById(R.id.editText2);

                if (radioButton3.isChecked()) {
                    if (revers) {
                        text2.setText(ourtext);
                    } else {
                        text.setText(deutschtext);
                    }
                } else {
                    if (switch1.isChecked()) {
                        text2.setText(ourtext);
                    } else {
                        text.setText(deutschtext);
                    }
                }


                text_is_play = true;
                if (radioButton3.isChecked()) {
                    if (revers) {
                        filename_satz = ourtext.trim().replaceAll("\\p{Punct}", "_");
                    } else {
                        filename_satz = deutschtext.trim().replaceAll("\\p{Punct}", "_");
                    }
                } else {
                    if (switch1.isChecked()) {
                        filename_satz = ourtext.trim().replaceAll("\\p{Punct}", "_");
                    } else {
                        filename_satz = deutschtext.trim().replaceAll("\\p{Punct}", "_");
                    }
                }

                if (autoplayenable) {
                    playStart(filename_satz + ".mp3");
                }
            }
            }
        }, duration + timepause1);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");

        stop = true;
        playStop();
        releasePlayer();
      //  handler.removeCallbacks(myRunnable);

    }
    @Override
    protected void onStop(){
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }
}