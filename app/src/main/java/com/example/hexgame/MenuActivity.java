package com.example.hexgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button start_button = findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_game = new Intent(MenuActivity.this, BoardActivity.class);

                final RadioGroup gametyperadio = findViewById(R.id.radio_type);
                int type_selected = gametyperadio.getCheckedRadioButtonId();
                start_game.putExtra("gameType", ((RadioButton) findViewById(type_selected)).getText().toString());

                final RadioGroup gamesizeradio = findViewById(R.id.radio_size);
                int size_selected = gamesizeradio.getCheckedRadioButtonId();
                start_game.putExtra("gameSize", ((RadioButton) findViewById(size_selected)).getText().toString());

                if (gametyperadio.getCheckedRadioButtonId() == ((RadioButton) findViewById(R.id.pvb)).getId()) {
                    final RadioGroup rg_bot = (RadioGroup) findViewById(R.id.radio_bot);
                    int selected_bot = rg_bot.getCheckedRadioButtonId();
                    start_game.putExtra("botLevel", ((RadioButton) findViewById(selected_bot)).getText().toString());
                }
                //finish();
                startActivity(start_game);
            }
        });

        ( findViewById(R.id.info_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info = new Intent(MenuActivity.this, InfoActivity.class);
                startActivity(info);
            }
        });

    }
}