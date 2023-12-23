package com.example.memoriz;

import static com.example.memoriz.DBHelper.TABLE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    //   private static final String DATA_SD = ;
    ProgressBar ProgressBar;
    SeekBar seekBar;
    TextView work_stroke1;
    TextView work_stroke2;
    TextView textscore1;
    TextView textscore2;
    CheckBox CheckBox3;
    CheckBox CheckBox1;
    CheckBox CheckBox2;
    RadioButton radioButton0;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;
    RadioButton radioButton5;
    RadioButton radioButton6;

    DBHelper dbHelper;
    private List<View> allEds;

    int index;
    String indexstr;
    int countermax;
    int counter2max;
    String dataForSaving="";
    private static final String LOG_TAG = "==MainActivity==";
  //  private static final String FILENAME = "deutsch_database.txt";
 //   private static final String DIR_SD = "Download";
    private static final String FILENAME = "satz_database.txt";
    private static final String DIR_SD = "/Podcasts/Memory";
    String readFromFile="";
    MediaPlayer mediaPlayer;
    private String path = "Podcasts/Memory/";

    AudioManager am;
    boolean isplay;
    boolean presskeyenable=true;
    boolean playenable;
    //   boolean nextplayenable;
    String workstroke1;
    String workstroke2;
    String name_sound_file_u;
    String name_sound_file_d ="s";
    String thema;
    String themen[]={"--- No Database ---", "second"};

    int totalcount;
    int index_id;
    int index_lesson;
    int my_lesson;
    int index_ourtext;
    int index_deutschtext;
    int index_oursound;
    int index_deutschsound;
    int progress;
    int themaposition;

    String satz;
    String translate ="1122.3344.5566";
    String row;
    int rowint;
    String num;
    int numint;
    String filename_satz;
    String filename_translate;
    String fileNameDeutschText;
    String fileNameOurText;
    int count;
    int counter;
    int counter2;
    int duration;
    Double coefficient = 1.3;  //   коеффициент изменения задержки после воспроизведения

    private MediaRecorder mediaRecorder;
    //  private MediaPlayer mediaPlayer;
    private String fileName;
    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        setContentView(R.layout.activity_main);
        allEds = new ArrayList<View>();

        path = Environment.getExternalStorageDirectory() + "/Podcasts/Memory/";
        //     fileName = Environment.getExternalStorageDirectory() + "/record.3gpp";
        fileName = Environment.getExternalStorageDirectory() + "/Memory/record.mp3";
        File rootPath = new File(Environment.getExternalStorageDirectory(), "Memory");
        if(!rootPath.exists()) {
            Toast toast = Toast.makeText(getApplicationContext(),"Create Direktory ", Toast.LENGTH_SHORT);
            toast.show();
            rootPath.mkdirs();
        }

        radioButton0 = findViewById(R.id.radioButton0);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        radioButton5 = findViewById(R.id.radioButton5);
        radioButton6 = findViewById(R.id.radioButton6);



        ProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        Spinner spinner = findViewById(R.id.spinner);

        //==========================================================================================
        // Получаем список тем
        int count_themen=0;
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String [] themen0 = new String[100];

        try {

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
            do {
                int themaIndex = cursor.getColumnIndex(DBHelper.KEY_LESSON);
                themen0[count_themen] = cursor.getString(themaIndex);
                //    themen0[count_themen]= "not";
                //Log.d(LOG_TAG, "themen0 = " + themen0[count_themen]);
                count_themen++;
            } while (cursor.moveToNext());

            dbHelper.close();
            themen = new String[count_themen];
            //count_themen--;
            do {
                //Log.d(LOG_TAG, "count_themen0 = " + count_themen);
                String a = themen0[count_themen - 1];
                themen[count_themen - 1] = a;
                count_themen--;
                // Log.d(LOG_TAG, "themen = " + themen[count_themen]);

            } while (count_themen > 0);

        } catch (Exception $e) {
            // Ничего не делаем
            Toast toast = Toast.makeText(getApplicationContext(),
                    "неисправна База Данных", Toast.LENGTH_SHORT);
            toast.show();
            Log.d(LOG_TAG, "===============Exeption==============");
        }
//==========================================================================================


        // Настраиваем адаптер
        //ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.lessons, android.R.layout.simple_spinner_item);
        ArrayAdapter<?> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, themen);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Вызываем адаптер
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {

                //String[] choose = getResources().getStringArray(R.array.lessons);
              //  String[] choose = themen;

                themaposition = selectedItemPosition;

                try {
                 //   thema = choose[selectedItemPosition];
                    thema = themen[selectedItemPosition];
                //    my_lesson = Integer.parseInt(choose[selectedItemPosition]);
                    my_lesson = Integer.parseInt(themen[selectedItemPosition]);

                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
           //     extractionID();
                LinearLayout linear = (LinearLayout) findViewById(R.id.linear1);
                allEds.clear();
                linear.removeAllViews();
                try {
                    Processing();
                }catch (Exception $e) {
                    // Ничего не делаем
                    Log.d(LOG_TAG, "===============Exeption2==============");
                }
               // Toast toast = Toast.makeText(getApplicationContext(),
               //         "Ваш выбор: " + my_lesson, Toast.LENGTH_SHORT);
               // toast.show();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    //    allEds = new ArrayList<View>();

        Button button_training = (Button)findViewById(R.id.button_training);
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear1);
        button_training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "button_Training");
        //        allEds.clear();
        //        linear.removeAllViews();
        //        Processing();

            //    row = ((TextView)view.findViewWithTag("id3")).getText().toString();
            //    satz = ((TextView)view.findViewWithTag("id1")).getText().toString();
            //    translate = ((TextView)view.findViewWithTag("id2")).getText().toString();
                Log.d(LOG_TAG, "thema  = "+thema);
                Intent intent = new Intent(MainActivity.this, Training.class);
                intent.putExtra("thema", thema);
                intent.putExtra("themaposition", themaposition);
                intent.putExtra("row", "1");
                intent.putExtra("satz", "satz");
                intent.putExtra("translate", "translate");
                //intent.setClass(ctx, Control_panel.class);



                startActivity(intent);


            }
        });
    }

    void Processing() {

        //   Context ctx = (Context)Fragment1.this.getActivity();
        Log.d(LOG_TAG, " == processing == ");
        //    dbHelper = new dbHelper(ctx);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String allData[] = new String[5];
        count = 0;
        /*
        Cursor cursor2 = database.query("anfangtable",
                null,
                null,
                null,
                null,
                null,
                null);
        //    Log.d(LOG_TAG, "---cursor2---");

         */


            Cursor cursor2 = database.query(
                    "anfangtable",
                    null,
                    "lesson = ?",
                    new String[]{thema},
                    null,
                    null,
                    null);
//               new String[] {String.valueOf(my_lesson)},
            cursor2.moveToFirst();
            do {
                int deutschtextIndex = cursor2.getColumnIndex(com.example.memoriz.DBHelper.KEY_DEUTSCHTEXT);
                int ourtextIndex = cursor2.getColumnIndex(com.example.memoriz.DBHelper.KEY_OURTEXT);
                int idIndex = cursor2.getColumnIndex(com.example.memoriz.DBHelper.KEY_ID);
                int colorIndex = cursor2.getColumnIndex(DBHelper.KEY_DEUTSCHSOUND);
                //    allData[0]= "0";
                //    allData[2]= "2";
                //    allData[1]= "1";
                allData[0] = cursor2.getString(deutschtextIndex);
                allData[2] = cursor2.getString(ourtextIndex);
                allData[1] = cursor2.getString(idIndex);
                allData[3] = String.valueOf(count);
                allData[4] = cursor2.getString(colorIndex);
                createfield(allData);
                count++;
            } while (cursor2.moveToNext());
            Log.d(LOG_TAG, "allData[4] = " + allData[4]);
            dbHelper.close();
        }




    @SuppressLint("ResourceAsColor")
    void createfield(String[] textfield){
      //  Context ctx = (Context)Fragment1.this.getActivity();
        LinearLayout linear = (LinearLayout) findViewById(R.id.linear1);
        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
        final View view = getLayoutInflater().inflate(R.layout.custom_edittext_layout, null);

        TextView text = (TextView) view.findViewById(R.id.editText);
        text.setText(textfield[0]);
        if(textfield[4].equals("1")){text.setTextColor(Color.GREEN);}
        if(textfield[4].equals("2")){text.setTextColor(Color.YELLOW);}



        TextView text2 = (TextView) view.findViewById(R.id.editText2);
        text2.setText(textfield[2]);
        if(textfield[4].equals("1")){text2.setTextColor(Color.GRAY);}
        if(textfield[4].equals("2")){text2.setTextColor(Color.GRAY);}

        Button buttongo = (Button) view.findViewById(R.id.button_go);
        buttongo.setText(textfield[1]);

        TextView textViewID = (TextView) view.findViewById(R.id.textViewID);
        textViewID.setText(textfield[3]);
//------------------------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------
        buttongo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "onClick: ");
                buttongo.setAlpha(0.7F);



                if(!playenable) {
                    playenable = true;
                    satz = ((TextView)view.findViewWithTag("id1")).getText().toString();
                    translate = ((TextView)view.findViewWithTag("id2")).getText().toString();
                    row = ((TextView)view.findViewWithTag("id3")).getText().toString();
                    num = ((TextView)view.findViewWithTag("id5")).getText().toString();
                    try{
                        rowint = Integer.parseInt(row);
                        numint = Integer.parseInt(num);
                    }
                    catch (NumberFormatException ex){
                        ex.printStackTrace();
                    }
                    if(numint < allEds.size()) {
                        ((Button) allEds.get(numint).findViewById(R.id.button_go)).setTextColor(Color.WHITE);
                    }

                    getFileName(rowint);
                     counter = 0;
                    counter2 = 0;

                    Log.d(LOG_TAG, "filename_satz:   "+filename_satz);

                    playStart(filename_satz+".mp3");
                }
                else{
                    if(numint < allEds.size()) {
                        ((Button) allEds.get(numint).findViewById(R.id.button_go)).setTextColor(Color.BLACK);
                    }
                    playStop();
                    playenable = false;
                }
            }
        });

        buttongo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(LOG_TAG, "onLongClick: ");

                if(numint < allEds.size()) {
                    ((Button) allEds.get(numint).findViewById(R.id.button_go)).setTextColor(Color.BLACK);
                }
                playStop();
                playenable = false;

                row = ((TextView)view.findViewWithTag("id3")).getText().toString();
                satz = ((TextView)view.findViewWithTag("id1")).getText().toString();
                translate = ((TextView)view.findViewWithTag("id2")).getText().toString();

                Intent intent = new Intent(MainActivity.this, Control_panel.class);
                intent.putExtra("row", row);
                intent.putExtra("satz", satz);
                intent.putExtra("translate", translate);
                //intent.setClass(ctx, Control_panel.class);



                startActivity(intent);

                return false;
            }
        });

        //добавляем все что создаем в массив
        allEds.add(view);

        //Log.d(LOG_TAG, "allEds.size(): "+allEds.size());
        //добавляем елементы в linearlayout
        linear.addView(view);



        //linear.getChildAt(1).findViewWithTag("id3").getBackground().setAlpha(1);
       // Log.d(LOG_TAG, "linear.getChildCount(): "+linear.getChildCount());
    }

    @Override
    public void onClick(View view) {

        // создаем объект для данных
        ContentValues cv = new ContentValues();


        // подключаемся к БД
        //    SQLiteDatabase db = dbHelper.getWritableDatabase();

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("anfangtable", null, null, null, null, null, null);


        // закрываем подключение к БД
        cursor.close();
        dbHelper.close();



    }


    void getFileName(int rowint){
        //Context ctx = (Context)Fragment1.this.getActivity();
        Log.d(LOG_TAG, "rowint = "+ rowint);
        //dbHelper = new dbHelper(ctx);
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
            fileNameDeutschText = cursor.getString(fileNameDeutschTextIndex_int);
            fileNameOurText = cursor.getString(fileNameOurTextIndex_int);
            filename_satz = fileNameDeutschText.trim().replaceAll("\\p{Punct}","_");
            filename_translate = fileNameOurText.trim().replaceAll("\\p{Punct}","_");
            //Log.d(LOG_TAG, "===fileNameDeutschText=== = " + fileNameDeutschText);
        }
        dbHelper.close();
    }


    private void playStart(String fileName) {
        Log.d(LOG_TAG, "===fileName0=== = " + path + fileName);
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path+fileName);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
            Log.d(LOG_TAG,"====Player start, duration = "+ duration);

            Double dduration;
            dduration = duration * coefficient;
            Log.d(LOG_TAG,"====Player start, dduration = "+ dduration);
            duration = Integer.valueOf(dduration.intValue());
            Log.d(LOG_TAG,"====Player start, duration = "+ duration);

            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void recordStop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    private void recordStart() {
        try {
            releaseRecorder();

            File outFile = new File(fileName);
            if (outFile.exists()) {
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
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
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
    }
    private void play() {
        isplay=true;
        if(true){

            presskeyenable = false;

            mediaPlayer = new MediaPlayer();
            final String DATA_SD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS) + "/" + name_sound_file_d + ".mp3";

            try {
                mediaPlayer.setDataSource(DATA_SD);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressBar.setProgress(counter+1);
            mediaPlayer.setOnCompletionListener(this);

            mediaPlayer.start();
        }
    }

    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){

            case R.id.update:
                Toast.makeText(this, "update Clicked", Toast.LENGTH_SHORT).show();
                read_file_from_SD();
                break;
            case R.id.about:
                сreateDialog(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void read_file_from_SD() {
        readFromFile="";
        Log.d(LOG_TAG, "read_DB_from_SD");
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }



        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        //File sdPath = Environment.getExternalStoragePublicDirectory("Install");
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        //   sdPath = new File("/storage/2A35-1DF5/Download");
        //  формируем объект File, который содержит путь к файлу
        Log.d(LOG_TAG, "Read from: " + String.valueOf(sdPath));
        Log.d(LOG_TAG, "Read from: " + String.valueOf(FILENAME));
        File sdFile = new File(sdPath, FILENAME);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                readFromFile=readFromFile+str+"&";
            }
            Log.d(LOG_TAG, "readFromFile = " + readFromFile);
            //Toast.makeText(NewActivity.this, readFromFile, Toast.LENGTH_LONG).show();
            try {
                ParseFile(readFromFile);
            }catch (Exception $e) {
                // Ничего не делаем
                Toast toast = Toast.makeText(getApplicationContext(),
                        "неправильный файл satz_database.txt", Toast.LENGTH_SHORT);
                toast.show();
                Log.d(LOG_TAG, "===============Exeption3==============");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void read_file_from_SD_2() {
        readFromFile="";
        Log.d(LOG_TAG, "read_DB_from_SD");
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
        //  формируем объект File, который содержит путь к файлу
        Log.d(LOG_TAG, "Read from: " + String.valueOf(sdPath));
        File sdFile = new File(sdPath, FILENAME);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                readFromFile=readFromFile+str+"&";
            }
            Log.d(LOG_TAG, "readFromFile = " + readFromFile);
            Toast.makeText(MainActivity.this, "readFromFile!", Toast.LENGTH_LONG).show();

            try{
            ParseFile(readFromFile);
            }catch (Exception $e) {
                // Ничего не делаем
                Log.d(LOG_TAG, "===============Exeption4==============");
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void ParseFile(String inputMassage){

        //    SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();



        //     if (inputMassage.matches("(.*;.*;.*;.*;.*&)")) { //\u0009
        if (inputMassage.matches("(.*\\u0009.*\\u0009.*\\u0009.*\\u0009.*\\u0009.*&)")) {
            inputMassage = inputMassage.trim().replaceAll(" +", " ");
            Log.d(LOG_TAG,"begin");
            String line[] = inputMassage.split("&");  // разделяем по записи "&"
            int count=0;

            database.delete(TABLE_NAME,null,null);

            while (count < line.length){
                String subline[] = line[count].split("\\u0009");

                Log.d(LOG_TAG,"----------------------------------------------------");
                Log.d(LOG_TAG, subline[0]+"   "+subline[1]+"   "+subline[2]+"   "+subline[3]+"   "+subline[4]+"   "+subline[5]);

                contentValues.put(DBHelper.KEY_LESSON, subline[1]);
                contentValues.put(DBHelper.KEY_OURTEXT, subline[2]);
                contentValues.put(DBHelper.KEY_DEUTSCHTEXT, subline[3]);
                contentValues.put(DBHelper.KEY_OURSOUND, subline[4]);
                contentValues.put(DBHelper.KEY_DEUTSCHSOUND, subline[5]);

                database.insert(
                        "anfangtable",
                        null,
                        contentValues);

                count++;
                //  Log.d(LOG_TAG, "Position = " + String.valueOf(cursor2.getPosition()));
            }
            Toast.makeText(MainActivity.this, "Database changed!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(MainActivity.this, "File format is wrong!", Toast.LENGTH_LONG).show();
        }
        dbHelper.close();
        getthema();
    }


    @Override
    public void onCompletion(MediaPlayer MP) {
        Log.d(LOG_TAG,"====Player stopped====");
        if(radioButton0.isChecked()){countermax=1;counter2max=1;}
        if(radioButton1.isChecked()){countermax=1;counter2max=2;}
        if(radioButton2.isChecked()){countermax=2;counter2max=2;}
        if(radioButton3.isChecked()){countermax=3;counter2max=2;}
        if(radioButton4.isChecked()){countermax=4;counter2max=3;}

        //Counter for play translate
        if(counter < countermax) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (playenable) playStart(filename_satz + ".mp3");
                }
            }, duration+1500);//пауза перед следующим повтором
            counter++;

            //Counter2 for spring to next row
            if(counter2 >= counter2max){
                if(numint < allEds.size()) {
                    ((Button) allEds.get(numint).findViewById(R.id.button_go)).setTextColor(Color.BLACK);
                    rowint++;
                    numint++;
                }
                if(numint < allEds.size()) {
                    ((Button) allEds.get(numint).findViewById(R.id.button_go)).setTextColor(Color.WHITE);
                }
                counter2 = 0;
                getFileName(rowint);
            }
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (playenable) playStart(filename_translate + ".mp3");
                }
            }, duration+500);
            counter = 0;

            counter2 ++;
        }
    }
    public void extractionID(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("anfangtable", null, null, null, null, null, null);

        cursor = database.query(
                "anfangtable",
                null,
                "lesson = ?",
                new String[] {String.valueOf(my_lesson)},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            index_id = cursor.getColumnIndex(DBHelper.KEY_ID);

            index = cursor.getInt(index_id);
            Log.d(LOG_TAG,"///////////////////////////////////////////");
            Log.d(LOG_TAG,"my_id = " + index);

        } else
            Log.d("mLog","0 rows");


    }
    public void getthema(){
        //==========================================================================================
        // Получаем список тем
        int count_themen=0;
        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String [] themen0 = new String[100];

        try {

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
            do {
                int themaIndex = cursor.getColumnIndex(DBHelper.KEY_LESSON);
                themen0[count_themen] = cursor.getString(themaIndex);
                //    themen0[count_themen]= "not";
                //Log.d(LOG_TAG, "themen0 = " + themen0[count_themen]);
                count_themen++;
            } while (cursor.moveToNext());

            dbHelper.close();
            themen = new String[count_themen];
            //count_themen--;
            do {
                //Log.d(LOG_TAG, "count_themen0 = " + count_themen);
                String a = themen0[count_themen - 1];
                themen[count_themen - 1] = a;
                count_themen--;
                // Log.d(LOG_TAG, "themen = " + themen[count_themen]);

            } while (count_themen > 0);

        } catch (Exception $e) {
            // Ничего не делаем
            Toast toast = Toast.makeText(getApplicationContext(),
                    "неисправна База Данных", Toast.LENGTH_SHORT);
            toast.show();
            Log.d(LOG_TAG, "===============Exeption==============");
        }
//==========================================================================================

    }

    public void сreateDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(R.layout.about);
        builder.create().show();
    }

}