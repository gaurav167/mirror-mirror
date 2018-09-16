package me.kungfucat.codencounter;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    ImageView microphoneImageView, darkCloudImageView;
    GifImageView mainGifImageView;
    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    Vibrator vibrator;
    Context context;
    TextToSpeech textToSpeech;
    int normalStill, normalSpeaking, fatStill, fatSpeaking, rippedStill, rippedSpeaking;
    int curPersonId, curPersonIdSpeakingForm;
    int turnsBeforeCompletion;
    ArrayList<String> badWords, goodWords;
    ArrayList<Integer> goodSentencesTime, badSentencesTime, averageSentencesTime;
    ArrayList<String> goodSentences, badSentences, averageSentences;
    MediaPlayer mediaPlayer;
    AlphaAnimation animation1, animation2;
    boolean cloudAlreadyThere;
    TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setUp();

        textView = findViewById(R.id.textBox);
        animation1 = new AlphaAnimation(0.00f, 1.0f);
        animation1.setDuration(4000);
        animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(4000);
        cloudAlreadyThere = false;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = MediaPlayer.create(this, R.raw.darkness_old_friend);
        mediaPlayer.seekTo(2000);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(30f, 30f);
        darkCloudImageView = findViewById(R.id.darkCloudImageView);
        darkCloudImageView.setAlpha(0.00f);

        normalSpeaking = R.drawable.normal_speaking;
        normalStill = R.drawable.normal_still;
        fatSpeaking = R.drawable.fat_speaking;
        fatStill = R.drawable.fat_still;
        rippedSpeaking = R.drawable.ripped_speaking;
        rippedStill = R.drawable.ripped_still;

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    for (Voice tmpVoice : textToSpeech.getVoices()) {
                        if (tmpVoice.getName().equals("en-us-x-sfg#male_1-local")) {
                            textToSpeech.setVoice(tmpVoice);
//                            Log.d("MYVOICE", tmpVoice.toString());
                            break;
                        }
                    }

                    String string = "Hi I am your virtual self. Greetings from the mirror world!";
                    speak(string, normalSpeaking, normalStill, 3300);
                }
            }
        }, "com.google.android.tts");


        mainGifImageView = findViewById(R.id.mainGifView);

        mainGifImageView.setImageResource(normalStill);
        curPersonId = normalStill;
        curPersonIdSpeakingForm = normalSpeaking;

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        microphoneImageView = findViewById(R.id.speakerImageView);


        microphoneImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        //when the user removed the finger
                        mSpeechRecognizer.stopListening();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        //finger is on the button
                        vibrator.vibrate(30);
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        Toast.makeText(context, "Listening...",
                                Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });
        setUpSpeechRecognition();
    }

    public void setUpSpeechRecognition() {

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null) {
                    Log.d("TEXTED", matches.get(0));
                    getResultsForString(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    public void getResultsForString(String string) {
        string = string.toLowerCase();
        if (turnsBeforeCompletion <= 0 && (curPersonId == fatStill)) {
            speak("This is the last time you did this.", curPersonIdSpeakingForm, normalStill, 1600);
            curPersonIdSpeakingForm = normalSpeaking;
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            curPersonId = normalStill;
            return;
        }
        if (turnsBeforeCompletion > 0) {
            turnsBeforeCompletion--;
            speak("You cannot run away from reality.", curPersonIdSpeakingForm, curPersonId, 1800);
            return;
        }

        for (int i = 0; i < badWords.size(); i++) {

            if (string.contains(badWords.get(i))) {
                turnsBeforeCompletion = 1;
                Random random = new Random();
                mediaPlayer.start();

//                darkCloudImageView.setAlpha(1f);
                if (!cloudAlreadyThere) {
//                    darkCloudImageView.startAnimation(animation1);
                    cloudAlreadyThere = true;
                }

                int index = random.nextInt(badSentences.size());
                speak(badSentences.get(index), fatSpeaking, fatStill, badSentencesTime.get(index));
                curPersonIdSpeakingForm = fatSpeaking;
                curPersonId = fatStill;
                return;
            }
        }

        for (int i = 0; i < goodWords.size(); i++) {

            if (string.contains(goodWords.get(i))) {
                Random random = new Random();
                int index = random.nextInt(goodSentences.size());
                speak(goodSentences.get(index), rippedSpeaking, normalStill, goodSentencesTime.get(index));
                curPersonIdSpeakingForm = normalSpeaking;
                curPersonId = normalStill;
                return;
            }
        }

        Random random = new Random();
        int index = random.nextInt(averageSentences.size());
        speak(averageSentences.get(index), normalSpeaking, normalStill, averageSentencesTime.get(index));
        curPersonId = normalStill;
        curPersonIdSpeakingForm = normalSpeaking;
    }

    public int getWordCount(String string) {

        int words = 0;
        for (int i = 0; i < string.length() - 1; i++) {
            if ((string.charAt(i) == ' ') && (string.charAt(i + 1) != ' ')) {
                words++;
            }
        }
        return words;
    }


    public void setUp() {

        badWords = new ArrayList<>();
        badSentences = new ArrayList<>();
        goodSentences = new ArrayList<>();
        goodSentencesTime = new ArrayList<>();
        badSentencesTime = new ArrayList<>();
        goodWords = new ArrayList<>();
        averageSentences = new ArrayList<>();
        averageSentencesTime = new ArrayList<>();

        turnsBeforeCompletion = 0;
        badWords.add("pizza");
        badWords.add("burger");
        badWords.add("cheese");
        badWords.add("coke");
        badWords.add("softdrinks");
        badWords.add("cigrattes");
        badWords.add("tobacco");
        badWords.add("many calories");
        badWords.add("a lot of calories");
        badWords.add("chips");
        badWords.add("lays");

        goodWords.add("exercise");
        goodWords.add("ate less");
        goodWords.add("water");
        goodWords.add("protein");

        badSentences.add("How could you do this to me? This is not good for my health.");
        badSentencesTime.add(2700);
        badSentences.add("I am dying inside please work out!");
        badSentencesTime.add(1700);
        badSentences.add("Here it comes again the unbearable pain.");
        badSentencesTime.add(1800);
        badSentences.add("Why are you screwing up my life?");
        badSentencesTime.add(1500);

        goodSentences.add("Well done boy I am so proud of you.");
        goodSentencesTime.add(1500);
        goodSentences.add("Very well. You can go reward yourself.");
        goodSentencesTime.add(1900);
        goodSentences.add("My long lost dream just came true.");
        goodSentencesTime.add(1900);
        goodSentences.add("A step in the right direction kid, way to go.");
        goodSentencesTime.add(2200);

        averageSentences.add("Pardon me!");
        averageSentencesTime.add(500);
        averageSentences.add("I didn't get you.");
        averageSentencesTime.add(800);
        averageSentences.add("Can you please repeat?");
        averageSentencesTime.add(1000);

    }

    public void speak(String string, int speakAs, final int getBackTo, int timeTime) {
        textView.setText(string);
        if (speakAs == fatSpeaking) {
            textToSpeech.setPitch(0.7f);
        } else {
            textToSpeech.setPitch(1f);
//            mediaPlayer.stop();
            cloudAlreadyThere = false;
            darkCloudImageView.setAlpha(0.00f);
        }
        mainGifImageView.setImageResource(speakAs);
        textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainGifImageView.setImageResource(getBackTo);
                if (getBackTo != fatStill && cloudAlreadyThere) {
                    cloudAlreadyThere = false;
//                    mediaPlayer.stop();
                    darkCloudImageView.setAlpha(0.00f);
                }
            }

        }, timeTime);
    }
}
