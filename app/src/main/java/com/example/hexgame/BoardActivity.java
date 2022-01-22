package com.example.hexgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener,HexInterface {

    private	String  game_type,bot_level, game_size;
    private	TextView tw_turn ,tw_p1_score ,tw_p2_score;
    private	Button[][] hex_buttons ;
    private	TableLayout button_area;
    private	ConstraintLayout screen;
    private	int width , row_num;
    private	GradientDrawable stroke;
    private	Intent last;
    private Cell[][] hexCells;
    private Cell[] moves;
    private	char last_char;
    private	int size,type,last_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        last=new Intent(BoardActivity.this,LastActivity.class);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void setSize(int _size_) {

    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void setType(int _type_) {

    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Cell play() {
        return null;
    }

    @Override
    public void play(Cell cell) {

    }

    @Override
    public int marked_cells() {
        return 0;
    }

    @Override
    public int isFinished() {
        return 0;
    }

    @Override
    public String userScore(char player) {
        return null;
    }
}