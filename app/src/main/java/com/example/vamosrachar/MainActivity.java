package com.example.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    EditText editTextPeople, editTextValue;
    TextView textViewResult;
    FloatingActionButton share, play;
    TextToSpeech ttsPLayer;
    int people = 1;
    double value = 0.00;
    String formattedResult = "R$ 0,00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPeople = (EditText) findViewById(R.id.peopleNumber);
        editTextValue = (EditText) findViewById(R.id.value);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        share = (FloatingActionButton) findViewById(R.id.shareFloatingActionButton);
        play = (FloatingActionButton) findViewById(R.id.playFloatingActionButton);

        EditObserver editObserver = new EditObserver(this);
        editTextValue.addTextChangedListener(editObserver);
        editTextPeople.addTextChangedListener(editObserver);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "O valor é de R$ " + textViewResult.getText().toString() + " para cada pessoa.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsPLayer != null) {
                    ttsPLayer.speak("O valor é de " + formattedResult + " reais por pessoa.", TextToSpeech.QUEUE_FLUSH, null, "ID1");
                }
            }
        });

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 1122);

    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS ativado", Toast.LENGTH_LONG).show();
        }
    }

    //extraído de https://stackoverflow.com/questions/12967498/android-tts-speech-synthesizer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1122) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // the user has the necessary data - create the TTS
                ttsPLayer = new TextToSpeech(this, this);
            } else {
                // no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    protected void calculateValue() {
        try {
            value = Double.parseDouble(editTextValue.getText().toString());
            people = Integer.parseInt(editTextPeople.getText().toString());
            if (people != 0) {
                formattedResult = new DecimalFormat("##.00").format(value / people);
                textViewResult.setText("R$ "+ formattedResult);
            } else {
                textViewResult.setText("R$ 0,00");
            }
        } catch(Exception e) {
            textViewResult.setText("R$ 0,00");
        }
    }
}