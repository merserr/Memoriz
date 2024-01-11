package com.example.memoriz;


//import static com.example.memoriz.MainActivity.BROADCAST_ACTION_CONTROL;
//import static com.example.memory.MainActivity.MESSAGEOUTPUTCONTROL;

import android.app.Activity;
//import android.content.BroadcastReceiver;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class Control_panel extends Activity implements OnCompletionListener {
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
    String num;
    int numint;
    String thema;
    String row;
    int rowint;
    String satz;
    String translate;
    String filename_satz;
    String filename_translate;
    String fileNameDeutschText;
    String fileNameOurText;

    DBHelper dbHelper;

//    BroadcastReceiver br;
    Context ctx;

    private static final String LOG_TAG = "===Control_panel===" ;

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


    Button button_record_d;
    Button button_record_r;
    Button button_player_d;
    Button button_player_r;

    TextView text;
    TextView text2;
    TextView title_lesson;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        button_player_d_is_played = false;
        button_player_r_is_played = false;
        button_recorder_d_is_record = false;
        button_recorder_r_is_record = false;

        Boolean button_player_d_is_played = false;


        ctx = (Context)Control_panel.this;
        path = Environment.getExternalStorageDirectory() + "/Podcasts/Memory/";

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Memory");
        if(!rootPath.exists()) {
           // Toast toast = Toast.makeText(getApplicationContext(),"Create Direktory ", Toast.LENGTH_SHORT);
           // toast.show();
            Log.d(LOG_TAG, "Create Direktory");
        //    rootPath.mkdirs();


            try {
                rootPath.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "Error Create Direktory2");
            }


            Log.d(LOG_TAG, "Create Direktory2");
        }

        dbHelper = new DBHelper(this);

        thema = getIntent().getStringExtra("thema");
        num = getIntent().getStringExtra("num");
        numint = Integer.parseInt(num);
        row = getIntent().getStringExtra("row");
        rowint = Integer.parseInt(row);
        satz = getIntent().getStringExtra("satz");
        translate = getIntent().getStringExtra("translate");
        filename_satz = satz.trim().replaceAll("\\p{Punct}","_");
        filename_translate = translate.trim().replaceAll("\\p{Punct}","_");

        Log.d(LOG_TAG, "thema  = "+thema);
        Log.d(LOG_TAG, "num  = "+num);
        Log.d(LOG_TAG, "row  = "+row);
        Log.d(LOG_TAG, "satz  = "+satz);
        Log.d(LOG_TAG, "filename_satz  = "+filename_satz);
        Log.d(LOG_TAG, "translate = "+translate);

        setContentView(R.layout.control_panel);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        title_lesson  = (TextView) findViewById(R.id.title_lesson);
        title_lesson.setText(thema);

        text = (TextView) findViewById(R.id.editText);
        text.setText(satz);

        text2 = (TextView) findViewById(R.id.editText2);
        text2.setText(translate);


        button_record_d = findViewById(R.id.button_record_d);
        button_record_r = findViewById(R.id.button_record_r);
        button_player_d = findViewById(R.id.button_player_d);
        button_player_r = findViewById(R.id.button_player_r);

        button_record_d.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recorder_is_record) {
                    recordStart(filename_satz+".mp3");
                    button_record_d.setText("Stop");
                    }else {
                    recordStop();
                    button_record_d.setText("Record");
                    }
            }
        });

        button_player_d.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player_is_played) {
                    playStart(filename_satz+".mp3");
                    button_player_d.setText("Stop");
                }else {
                    playStop();
                    button_player_d.setText("Play");
               }
            }
        });

        button_record_r.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recorder_is_record) {
                    recordStart(filename_translate+".mp3");
                    button_record_r.setText("Stop");
                }else {
                    recordStop();
                    button_record_r.setText("Record");
                }
            }
        });

        button_player_r.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!player_is_played) {
                    playStart(filename_translate+".mp3");
                    button_player_r.setText("Stop");
                }else {
                    playStop();
                    button_player_r.setText("Play");
                }
            }
        });

        Button button_save = (Button) findViewById(R.id.button_save);
      //  button_save.setText(R.string.button_save);
        button_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_save ");
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
 //               Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
 //               vibrator.vibrate(70);
                satz = text.getText().toString();
                translate = text2.getText().toString();

                contentValues.put(com.example.memoriz.DBHelper.KEY_LESSON, "all");
                contentValues.put(com.example.memoriz.DBHelper.KEY_OURTEXT, translate);
                contentValues.put(com.example.memoriz.DBHelper.KEY_DEUTSCHTEXT, satz);
                contentValues.put(com.example.memoriz.DBHelper.KEY_OURSOUND, translate);
                contentValues.put(com.example.memoriz.DBHelper.KEY_DEUTSCHSOUND, satz);

                database.insert(
                        "satz",
                        null,
                        contentValues);

            }
        }); //database.delete("satz", "id = " + id, null);

        Button button_next = (Button) findViewById(R.id.button_next);
        button_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "onClick button_next ");
                //SQLiteDatabase database = dbHelper.getWritableDatabase();
                //ContentValues contentValues = new ContentValues();
                //database.delete("satz", "_id = " + row, null);

                rowint++;
                //Log.d(LOG_TAG, "rowint = " + rowint);

                SQLiteDatabase database = dbHelper.getWritableDatabase();
                String rows = Integer.toString(rowint);
                Cursor cursor = database.query(
                        "anfangtable",
                        null,
                        "_id = ?",
                        new String[] {rows},
                        null,
                        null,
                        null);
                if (cursor.moveToFirst()) {
                    int fileNameDeutschTextIndex_int = cursor.getColumnIndex(com.example.memoriz.DBHelper.KEY_DEUTSCHTEXT);
                    int fileNameOurTextIndex_int = cursor.getColumnIndex(com.example.memoriz.DBHelper.KEY_OURTEXT);
                    int themaIndex = cursor.getColumnIndex(DBHelper.KEY_LESSON);
                    thema = cursor.getString(themaIndex);
                    fileNameDeutschText = cursor.getString(fileNameDeutschTextIndex_int);
                    fileNameOurText = cursor.getString(fileNameOurTextIndex_int);

                    title_lesson.setText(thema);
                    text.setText(fileNameDeutschText);
                    text2.setText(fileNameOurText);

                    filename_satz = fileNameDeutschText.trim().replaceAll("\\p{Punct}","_");
                    filename_translate = fileNameOurText.trim().replaceAll("\\p{Punct}","_");
                    //Log.d(LOG_TAG, "===fileNameDeutschText=== = " + fileNameDeutschText);
                }
                dbHelper.close();


            }
        }); //


        //==========================================================================
        // Receive massage from TCPService and make Toast
        //==========================================================================
        // create BroadcastReceiver

        //==========================================================================
    }

    private void recordStop() {
        recorder_is_record = false;
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    private void recordStart(String fileName) {
        recorder_is_record = true;
        try {
            releaseRecorder();
            Log.d(LOG_TAG, "===fileName=== = " + path);

            File outFile = new File(path);
            if (outFile.exists()) {
                Log.d(LOG_TAG, "===delete=== = ");
                outFile.delete();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioEncodingBitRate(192000);
            mediaRecorder.setAudioSamplingRate(16000);
            mediaRecorder.setOutputFile(path+fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            Log.d(LOG_TAG, "===Exception e=== = ");
            e.printStackTrace();
        }

    }
    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void playStop() {
        player_is_played = false;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void playStart(String fileName) {
        player_is_played = true;
        Log.d(LOG_TAG, "===path=== =   " + path);
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path + fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.d(LOG_TAG, "onCompletion");
        button_player_d.setText("Play");
        button_player_r.setText("Play");
        player_is_played = false;
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    //    unregisterReceiver(br);
    }









}
