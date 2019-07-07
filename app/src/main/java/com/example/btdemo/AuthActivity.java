package com.example.btdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {



    private String keyboardTextS = "";

    private TextView textKeyboard;

    private RelativeLayout keyboardRLWithText;

    private TextView numberOne, numberTwo, numberThree, numberFour, numberFive, numberSix, numberSeven, numberEight,
            numberNine, numberZero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        findIds();


    }


    private void findIds() {


        textKeyboard = findViewById(R.id.textKeyboard);


        numberOne = findViewById(R.id.numberOne);
        numberOne.setOnClickListener(this);

        numberTwo = findViewById(R.id.numberTwo);
        numberTwo.setOnClickListener(this);

        numberThree = findViewById(R.id.numberThree);
        numberThree.setOnClickListener(this);

        numberFour = findViewById(R.id.numberFour);
        numberFour.setOnClickListener(this);

        numberFive = findViewById(R.id.numberFive);
        numberFive.setOnClickListener(this);

        numberSix = findViewById(R.id.numberSix);
        numberSix.setOnClickListener(this);

        numberSeven = findViewById(R.id.numberSeven);
        numberSeven.setOnClickListener(this);

        numberEight = findViewById(R.id.numberEight);
        numberEight.setOnClickListener(this);

        numberNine = findViewById(R.id.numberNine);
        numberNine.setOnClickListener(this);

        numberZero = findViewById(R.id.numberZero);
        numberZero.setOnClickListener(this);

        ImageView numberBack = findViewById(R.id.numberCross);
        numberBack.setOnClickListener(this);

        keyboardRLWithText = findViewById(R.id.keyboardRLWithText);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.numberOne:
                afterButtonPressed("1");
                break;
            case R.id.numberTwo:
                afterButtonPressed("2");
                break;
            case R.id.numberThree:
                afterButtonPressed("3");
                break;
            case R.id.numberFour:
                afterButtonPressed("4");
                break;
            case R.id.numberFive:
                afterButtonPressed("5");
                break;
            case R.id.numberSix:
                afterButtonPressed("6");
                break;
            case R.id.numberSeven:
                afterButtonPressed("7");
                break;
            case R.id.numberEight:
                afterButtonPressed("8");
                break;
            case R.id.numberNine:
                afterButtonPressed("9");
                break;
            case R.id.numberZero:
                afterButtonPressed("0");
                break;
            case R.id.numberCross:
                if (!keyboardTextS.isEmpty()) {
                    keyboardTextS = keyboardTextS.substring(0, keyboardTextS.length() - 1);
                    textKeyboard.setText(keyboardTextS);
                } else {
                    keyboardRLWithText.setVisibility(View.GONE);
                    startNewActivity();
                }
                break;

        }
    }

    private void startNewActivity() {

        Intent intent = new Intent();
        intent.putExtra("key", keyboardTextS);
        setResult(RESULT_OK, intent);

        finish();

    }

    private void afterButtonPressed(String number) {
        if (keyboardTextS.length() < 4) {
            keyboardTextS = keyboardTextS + number;
            textKeyboard.setText(keyboardTextS);
            if (keyboardTextS.length() == 4) {
                unfocusAllButtons();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        keyboardRLWithText.setVisibility(View.GONE);
                        startNewActivity();
                    }
                }, 500);
            }
        }
    }

    private void unfocusAllButtons() {
        numberOne.setFocusable(false);
        numberTwo.setFocusable(false);
        numberThree.setFocusable(false);
        numberFour.setFocusable(false);
        numberFive.setFocusable(false);
        numberSix.setFocusable(false);
        numberSeven.setFocusable(false);
        numberEight.setFocusable(false);
        numberNine.setFocusable(false);
        numberZero.setFocusable(false);
    }

}
