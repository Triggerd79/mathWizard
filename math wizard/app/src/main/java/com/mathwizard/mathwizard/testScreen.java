package com.mathwizard.mathwizard;

import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Calendar;


public class testScreen extends AppCompatActivity {

    int ans;
    int greater;
    int smaller;
    String equation = "";
    int n1, n2;
    int answer;
    int attempted = 0;
    int correct = 0;
    int wrong = 0;
    private TextView score;
    private EditText ansField;
    private EditText phoneNumberField;

    // generate a random number
    public int getRandomInt(int multiplier){
        return (int)Math.floor(Math.random() * multiplier + 1);
    }

    public void loadQuestion(String operator, int noOfDigits){

        StringBuilder multiplierStr = new StringBuilder("1");
        for(int i = 0; i < noOfDigits; i++) multiplierStr.append("0");

        int multiplier = Integer.parseInt(multiplierStr.toString());
        n1 = getRandomInt(multiplier);
        n2 = getRandomInt(multiplier);

        equation = " " + n1 + " " + operator + " " + n2 + "";
        switch (operator){
            case "+":
                answer = n1 + n2;
                break;
            case "-":
                greater = Math.max(n1, n2);
                smaller = Math.min(n1, n2);
                answer = greater - smaller;
                equation = " " + greater + " " + operator + " " + smaller + "";
                break;
            case "/":
                greater = Math.max(n1, n2);
                smaller = Math.min(n1, n2);
                answer = greater / smaller;
                equation = " " + greater + " " + operator + " " + smaller + "";
                break;
            case "*":
                answer = n1 * n2;
                break;
        }

        TextView eq = findViewById(R.id.equation);
        eq.setText(equation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_screen);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        int noOfDigits = intent.getIntExtra("digits", 10);
        String operator = intent.getStringExtra("operator");

        loadQuestion(operator, noOfDigits);

        Button submit = findViewById(R.id.check);
        score =  findViewById(R.id.score);
        ansField = findViewById(R.id.answer);
        Button sendReport = findViewById(R.id.sendReport);
        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.test_page_banner_ad));




        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                attempted++;

                String ansfieldText = ansField.getText().toString();
                if (!ansfieldText.isEmpty()) {
                    ans = Integer.parseInt(ansField.getText().toString());
                } else {
                    ans = 0;
                }

                int finalAnswer = answer;
                if (Integer.parseInt(ansField.getText().toString()) == finalAnswer) {
                    correct++;
                    score.setText("Score: " + correct);
                }else{
                    wrong++;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                loadQuestion(operator, noOfDigits);
            }
        });

        sendReport.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                phoneNumberField = findViewById(R.id.phoneNumField);
                String phoneNumText = phoneNumberField.getText().toString();
                String phoneNumber = phoneNumText;
                String message = "Today report Date:" + Calendar.getInstance().getTime() +
                        " \nQuestion Attempted:" + attempted +
                        "\n wrong: " + wrong +
                        "\nCorrect: " + correct;

                String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

                // Set the package name of WhatsApp so that only WhatsApp is opened, instead of other messaging apps
                intent.setPackage("com.whatsapp");

                // Check if WhatsApp is installed on the device, and if so, start the activity with the Intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }
}