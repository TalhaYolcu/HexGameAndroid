package com.example.hexgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

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

        Intent previous_activity=getIntent();

        last=new Intent(BoardActivity.this,LastActivity.class);

        tw_turn=findViewById(R.id.turn);
        tw_p1_score=findViewById(R.id.p1_score);
        tw_p2_score=findViewById(R.id.p2_score);

        width= Resources.getSystem().getDisplayMetrics().widthPixels;
        button_area=new TableLayout(BoardActivity.this);

        screen=(ConstraintLayout)findViewById(R.id.screen);
        game_type=previous_activity.getStringExtra("gameType");
        game_size=previous_activity.getStringExtra("gameSize");
        bot_level=previous_activity.getStringExtra("botLevel");

        stroke = new GradientDrawable();
        stroke.setColor(Color.parseColor("#C3C3C3") );
        stroke.setStroke(2, Color.BLACK);

        row_num=Integer.parseInt(game_size);
        hex_buttons=new Button[row_num][row_num];

        for(int row=0;row<row_num;row++) {
            TableRow currentRow=new TableRow(this);
            currentRow.setPadding((int) (row*(width/(1.5*(row_num))/2)),0,0,0);
            for(int column=0;column<row_num;column++) {
                hex_buttons[row][column]=new Button(this);
                hex_buttons[row][column].setId(row*row_num+column);
                hex_buttons[row][column].setLayoutParams(new TableRow.LayoutParams((int) (width/(1.5*(row_num))), (int) (width/(1.5*(row_num)))));
                hex_buttons[row][column].setBackgroundColor(Color.GRAY);
                hex_buttons[row][column].setVisibility(View.VISIBLE);
                hex_buttons[row][column].setClickable(true);
                hex_buttons[row][column].setOnClickListener(this);
                hex_buttons[row][column].setBackground(stroke);
                currentRow.addView(hex_buttons[row][column]);
            }
            button_area.addView(currentRow);
        }

        screen.addView(button_area);
        setSize(row_num);
        setType(1);
        create_table();

        TextView tv_size=findViewById(R.id.size);
        tv_size.setText (game_size);
        TextView tv_type=findViewById(R.id.type);
        tv_type.setText (game_type);
        if (game_type.equals("Player vs Bot")){
            setType(2);
            TextView tv_level=findViewById(R.id.level);
            tv_level.setVisibility(View.VISIBLE);
            (findViewById(R.id.Bot_Level)).setVisibility(View.VISIBLE);
            tv_level.setText (bot_level);
        }
        tw_turn.setText("Player 1");
        ( findViewById(R.id.new_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main= new Intent(BoardActivity.this, MainActivity.class);
                finish();
                startActivity(main);
            }
        });
        findViewById(R.id.undo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
            }
        });

        findViewById(R.id.reset_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int row = 0; row < row_num; row++)
                    for (int colm = 0; colm < row_num; colm++) {
                        hex_buttons[row][colm].setBackgroundColor(Color.GRAY);
                        hex_buttons[row][colm].setVisibility(View.VISIBLE);
                        putTable(Cell_type.EMPTY.type,row,colm);
                        hex_buttons[row][colm].setClickable(true);
                        hex_buttons[row][colm].setEnabled (true);
                        hex_buttons[row][colm].setBackground(stroke);
                    }

                create_table();
                tw_turn.setText("Player 1");
                tw_p1_score.setText(userScore(Cell_type.PLAYER1.type));
                tw_p2_score.setText(userScore(Cell_type.PLAYER2.type));
            }
        });
/*
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
                builder.setTitle(R.string.game_save);
                builder.setMessage(R.string.file_ask);
                final EditText input = new EditText(BoardActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String file_name = input.getText().toString();
                        saveGame(file_name);
                    }
                });
                builder.show();

            }
        });

 */
/*
        findViewById(R.id.load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
                builder.setTitle(R.string.game_load);
                builder.setMessage(R.string.file_ask);
                final EditText input = new EditText(BoardActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String file_name = input.getText().toString();
                        loadGame(file_name);
                    }
                });
                builder.show();
            }
        });


 */

    }

    /*private void loadGame(String fileName) {
        FileInputStream inputStream = null;
        try {
            inputStream = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(BoardActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {

            for(int i=0;i<size;i++) {
                for(int k=0;k<size;k++) {
                    hex_buttons[i][k].setVisibility(View.GONE);
                }
            }

            hexCells=null;
            moves=null;

            int temp_size;
            int ai_int;
            int move_int;
            line=bReader.readLine();
            size=Integer.parseInt(line);
            temp_size=size;
            line=bReader.readLine();
            ai_int=Integer.parseInt(line);
            if(ai_int==0) {
                ai=false;
            }
            line=bReader.readLine();
            move_int=Integer.parseInt(line);
            if(move_int==0) {
                turn2=false;
            }
            board=new ImageButton[size][size];
            cells=new Cell[size][size];
            moves=new Cell[size*size];
            inner=new RelativeLayout(this);
            inner=findViewById(R.id.inner);
            setWidthHeight();
            bottom=findViewById(R.id.constraintLayout);
            turn2=true;
            int width_of_one_button=(int) (((double) width) / (size + (size - 1) * 1.0 / 2));
            int counter=0;
            int shift = (int) (width_of_one_button / 2);
            turn=findViewById(R.id.turn_text);
            board=new ImageButton[size][size];



            for(int i=0;i<size;i++){
                board[i]=new ImageButton[size];
                cells[i]=new Cell[size];
                for(int k=0;k<size;k++) {
                    board[i][k]=new ImageButton(EmptyActivity.this);
                    board[i][k].setLayoutParams(new RelativeLayout.LayoutParams(width_of_one_button+30,width_of_one_button+30));
                    board[i][k].setX(k*width_of_one_button+(i*(int)(width_of_one_button/2))-5);
                    board[i][k].setY(i*width_of_one_button+start_y+200);
                    board[i][k].setId(counter++);
                    board[i][k].setOnClickListener(getOnClick(i,k));
                    board[i][k].setImageResource(R.drawable.gray_hexagon);
                    board[i][k].setBackgroundResource(R.drawable.ic_launcher_background);
                    board[i][k].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    board[i][k].setAdjustViewBounds(true);
                    inner.addView(board[i][k]);
                    line=bReader.readLine();
                    int i1=Integer.parseInt(line);
                    line=bReader.readLine();
                    int k1=Integer.parseInt(line);
                    char not=bReader.readLine().charAt(0);
                    cells[i][k]=new Cell(k1,i1,not);
                    switch (not) {
                        case 'o':
                            board[i][k].setImageResource(R.drawable.blue_hexagon);
                            break;
                        case 'x':
                            board[i][k].setImageResource(R.drawable.red_hexagon);
                            break;
                        case '.':
                            board[i][k].setImageResource(R.drawable.gray_hexagon);
                            break;
                    }
                }
            }
            char z=bReader.readLine().charAt(0);
            if(z!='-') {
                throw new IOException();
            }
            line=bReader.readLine();
            number_of_moves=Integer.parseInt(line);
            for(int i=0;i<size*size;i++) {
                moves[i]=new Cell(0,0,'.');
            }
            for(int j=0;j<number_of_moves;j++) {
                moves[j].setX(Integer.parseInt(bReader.readLine()));
                moves[j].setY(Integer.parseInt(bReader.readLine()));
                moves[j].setNot(bReader.readLine().charAt(0));
            }
        }
        catch (IOException | IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

     */

    /*private void saveGame(String fname) {

        if(fname==null) {
        }
        else if(fname.isEmpty()){
            Toast.makeText(BoardActivity.this, R.string.file_alert, Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            try {
                FileOutputStream file = openFileOutput(fname, MODE_PRIVATE);
                OutputStreamWriter outputFile = new OutputStreamWriter(file);
                int ai_int=(ai)? 1:0;
                int move_int=(turn2)?1:0;
                outputFile.write(size+"\n"+ai_int+"\n"+move_int+"\n");
                for(int i=0;i<size;i++) {
                    for(int k=0;k<size;k++) {
                        outputFile.write(i+"\n"+k+"\n"+cells[i][k].getNot()+"\n");
                    }
                }
                outputFile.write("-"+"\n");
                outputFile.write(number_of_moves+"\n");
                for(int i=0;i<number_of_moves;i++) {
                    outputFile.write(moves[i].getX()+"\n"+moves[i].getY()+"\n"+moves[i].getNot()+"\n");
                }
                //outputFile.flush();
                outputFile.close();

                Toast.makeText(EmptyActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

     */


    @Override
    public void onClick(View v) {
        for (int i=0;i<row_num;i++)
            for (int j=0;j<row_num;j++)
                if (v.getId()==hex_buttons[i][j].getId()){
                    set_turn();
                    play(extractCell(i,(char)(j+'a')));
                    if(isFinished()==1){
                        Toast.makeText(BoardActivity.this,"Finished" ,Toast.LENGTH_SHORT).show();
                    }else if (type==2) {
                        try {
                            play();
                        } catch (Exception e){
                            Toast.makeText(BoardActivity.this,e.getMessage() ,Toast.LENGTH_SHORT).show();
                        }
                    }
                }
    }

    @Override
    public void setSize(int _size_) {
        size=_size_;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setType(int _type_) {
        type=_type_;
    }

    @Override
    public int getType() {
        return type;
    }

    private  void set_turn(){
        switch (check_turn()){
            case 2 : tw_turn.setText("Player 1");
                break;
            case 1: if (getType()==1) tw_turn.setText("Player 2");
            else tw_turn.setText("Bot");
                break;
            default: 	tw_turn.setText("-");
        }
    }
    /**Helper For Computer's Move*/
    private int check_input(int y,char x){
        if (1<=y && y<=size && 'a'<=x && x<'a'+size)
            if (hexCells[y-1][(int)(x-'a')].getState() == Cell_type.EMPTY.type){
                return 1;
            }else
                return 0;
        return 0;
    }
    private Cell easy_bot(){
        Random r = new Random();
        char x ;int y;
        do{
            y = r.nextInt(getSize()) ;
            x = (char) (r.nextInt(getSize())+'a');
        }while (check_input(y+1,x)==0 );
        putTable(Cell_type.COMPUTER.type,y,x-'a');
        return extractCell(y,x);
    }
    /**Plays Computer's Move*/
    @Override
    public Cell play(){
        Cell played;
        set_turn();
        switch(bot_level){
            case "Easy" : played=easy_bot();
                break;
            case "Normal" :  played=normal_bot(last_int,last_char);
                break;
            case "Hard" :  played=hard_bot(last_int,last_char);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + bot_level);
        }
        hex_buttons[played.getY()][(int)(played.getX()-'a')].setBackgroundColor(Color.BLUE);
        hex_buttons[played.getY()][(int)(played.getX()-'a')].setClickable(false);
        tw_p2_score.setText(userScore(Cell_type.COMPUTER.type));
        putLast(played);
        if (find_initial(Cell_type.COMPUTER.type,0,0)==1 ){
            make_upper(Cell_type.COMPUTER.type);
            Toast.makeText(BoardActivity.this,"Finished" ,Toast.LENGTH_SHORT).show();
            delay(2000,"Bot",userScore(Cell_type.COMPUTER.type));
        }
        return played;
    }
    private Cell normal_bot(int y, char x) {
        return put_empty(y,x);
    }

    @Override
    public void play(Cell cell) {
        switch(check_turn())
        {
            case 1:	putTable(Cell_type.PLAYER1.type,cell.getY(),cell.getX()-'a');
                tw_p1_score.setText(userScore(Cell_type.PLAYER1.type));
                hex_buttons[cell.getY()][(int)(cell.getX()-'a')].setBackgroundColor(Color.RED);
                hex_buttons[cell.getY()][(int)(cell.getX()-'a')].setEnabled(false);
                last_int=cell.getY()+1;
                last_char=cell.getX();
                break;
            case 2: putTable(Cell_type.PLAYER2.type,cell.getY(),cell.getX()-'a');
                tw_p2_score.setText(userScore(Cell_type.PLAYER2.type));
                hex_buttons[cell.getY()][(int)(cell.getX()-'a')].setBackgroundColor(Color.BLUE);
                hex_buttons[cell.getY()][(int)(cell.getX()-'a')].setEnabled(false);
                break;
        }
        putLast(cell);
        if (find_initial(Cell_type.PLAYER1.type,0,0)==1 || find_initial(Cell_type.PLAYER2.type,0,0)==1 ){
            if (check_turn()==1){
                make_upper(Cell_type.PLAYER2.type);
                delay(2000,"Player 2",tw_p2_score.getText().toString());
            }else {
                make_upper(Cell_type.PLAYER1.type);
                delay(2000,"Player 1",tw_p1_score.getText().toString());
            }
        }
    }
    /**Changes The Color of Winner Buttons*/
    private void make_upper(char winner){
        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                if (getTable(i,j)==winner)
                    if (getTable(i,j+1)==winner-32 || getTable(i+1,j)==winner-32 || getTable(i,j-1)==winner-32 ||getTable(i-1,j+1)==winner-32 || getTable(i-1,j)==winner-32 ||getTable(i+1,j-1)==winner-32 ){
                        putTable((char)(winner-32),i,j);
                        hex_buttons[i][j].setBackgroundColor(Color.MAGENTA);
                        make_upper(winner);
                    }
    }
    private int find_initial(char player,int x,int y){
        int check=0;
        if (player==Cell_type.PLAYER1.type){
            for (int i = x; i <=size-1; ++i)
                if (getTable(i,y)==Cell_type.PLAYER1.type){
                    check=check_win(Cell_type.PLAYER1.type,i-1,y+1);
                    if (check==1){
                        putTable((char)(Cell_type.PLAYER1.type-32),i,y);
                        hex_buttons[i][y].setBackgroundColor(Color.MAGENTA);
                    }
                }
        }else if (player==Cell_type.PLAYER2.type)
            for (int i = y; i <=size-1; ++i)
                if (getTable(x,i)==Cell_type.PLAYER2.type){
                    check=check_win(Cell_type.PLAYER2.type,x+1,i-1);
                    if (check==1){
                        putTable((char)(Cell_type.PLAYER2.type-32),x,i);
                        hex_buttons[x][i].setBackgroundColor(Color.MAGENTA);
                    }
                }
        return check;
    }
    /**Checks For The Winning Path*/
    private int check_win(char player,int x,int y){
        int check=0;
        if (player==Cell_type.PLAYER1.type){
            for (int i = x;  i<x+2; ++i)//checks right and right-up
                if (getTable(i,y) ==player){
                    hexCells[i][y].setState((char)(hexCells[i][y].getState()-32));
                    if (y==size-1){
                        hex_buttons[i][y].setBackgroundColor(Color.MAGENTA);
                        return 1;//x win
                    }
                    check=check_win(player,i-1,y+1);
                    if (check==0)
                        hexCells[i][y].setState((char)(hexCells[i][y].getState()+32));
                    else
                        hex_buttons[i][y].setBackgroundColor(Color.MAGENTA);
                    return check;
                }
            for (int i = y-2; i <y; i++)//checks left-down and down
                if (getTable(x+2,i)==Cell_type.PLAYER1.type){
                    hexCells[x+2][i].setState((char)(hexCells[x+2][i].getState()-32));
                    check=check_win(player,x+1,i+1);
                    if (check==0)
                        hexCells[x+2][i].setState((char)(hexCells[x+2][i].getState()+32));
                    else
                        hex_buttons[x+2][i].setBackgroundColor(Color.MAGENTA);
                    return check;
                }
            for (int i = y-2 ,j=x+1 ; i < y && j>x-1 ; ++i, --j)//checks left and up
                if (getTable(j,i)==player){
                    hexCells[j][i].setState((char)(hexCells[j][i].getState()-32));
                    check=check_win(player,j-1,i+1);
                    if (check==0)
                        hexCells[j][i].setState((char)(hexCells[j][i].getState()+32));
                    else
                        hex_buttons[j][i].setBackgroundColor(Color.MAGENTA);
                    return check;
                }
        }else if (player==Cell_type.PLAYER2.type){
            for (int i = y; i<y+2 ; ++i)
                if (getTable(x,i)==player){
                    hexCells[x][i].setState((char)(hexCells[x][i].getState()-32));
                    if (x==size-1){
                        hex_buttons[x][i].setBackgroundColor(Color.MAGENTA);
                        return 1;//O win
                    }
                    check=check_win(player,x+1,i-1);
                    if (check==0){
                        hexCells[x][i].setState((char)(hexCells[x][i].getState()+32));
                    }else{
                        hex_buttons[x][i].setBackgroundColor(Color.MAGENTA);
                        return 1;
                    }
                }
            for (int i = x-2;  i <x; ++i)
                if (getTable(i,y+2)==player){
                    hexCells[i][y+2].setState((char)(hexCells[i][y+2].getState()-32));
                    check=check_win(player,i+1,y+1);
                    if (check==0){
                        hexCells[i][y+2].setState((char)(hexCells[i][y+2].getState()+32));
                    }else{
                        hex_buttons[i][y+2].setBackgroundColor(Color.MAGENTA);
                        return 1;
                    }
                }
            for (int i = x-2 , j=y+1 ; i < x && j>y-1 ; ++i,--j)
                if (getTable(i,j)==player){
                    hexCells[i][j].setState((char)(hexCells[i][j].getState()-32));
                    check=check_win(player,i+1,j-1);
                    if (check==0){
                        hexCells[i][j].setState((char)(hexCells[i][j].getState()+32));
                    }else{
                        hex_buttons[i][j].setBackgroundColor(Color.MAGENTA);
                        return 1;
                    }
                }
            return check;
        }
        return 0;
    }

    /**Helper For Undo*/
    private void putLast(Cell cell){
        try{
            Cell[] new_moves;
            new_moves = new Cell[marked_cells()];
            for (int i = 0; i < marked_cells()-1; ++i){
                new_moves[i]=new Cell();
                new_moves[i]=moves[i];
            }
            moves = new Cell[marked_cells()];
            for (int i = 0; i < marked_cells()-1; ++i){
                moves[i]=new Cell();
                moves[i]=new_moves[i];
            }
            moves[marked_cells()-1]=new Cell();
            moves[marked_cells()-1]= cell;
        }catch(Exception e){
            Toast.makeText(BoardActivity.this, "Something happened while memory allocation", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public int marked_cells(){
        int number=0;
        for (int i = 0; i < getSize(); ++i)
            for (int j = 0; j < getSize(); ++j)
                if (getTable(i,j)!= Cell_type.EMPTY.type)
                    number++;
        return number;
    }

    @Override
    public int isFinished(){
        for (int i = 0; i < getSize(); ++i)
            for (int j = 0; j < getSize(); ++j){
                if (getTable(i,j)==Cell_type.PLAYER1.type-32 || getTable(i,j)==Cell_type.PLAYER2.type-32|| getTable(i,j)==Cell_type.COMPUTER.type-32)
                    return 1;
            }
        return 0;
    }
    /**Calculates And Returns The Score*/
    @Override
    public String userScore(char player){
        double score=0.0,score_perConnect=100.0/(getSize()*getSize());
        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                if (getTable(i,j)==player || getTable(i,j)==player-32)
                    if (player==Cell_type.PLAYER1.type){
                        score+=score_perConnect*neighboorCells_x(i,j,player)*(j+1)*(i+1);
                    }else
                        score+=score_perConnect*neighboorCells_o(i,j,player)*(j+1)*(i+1);
        return  Integer.toString((int)score);
    }
    /**Helper For Score*/
    private int neighboorCells_x(int i,int j,char player){
        int n_Cells=0;
        if (getTable( i,j+1)==player || getTable(i,j+1)==player-32)
            n_Cells++;
        if (getTable(i,j-1)==player ||getTable(i,j-1)==player-32 )
            n_Cells++;
        if (getTable(i,j+1)==player||getTable(i-1,j+1)==player-32)
            n_Cells++;
        if (getTable(i,j-1)==player || getTable(i+1,j-1)==player-32)
            n_Cells++;
        return n_Cells +1;
    }
    /**Helper For Score*/
    private int neighboorCells_o(int i,int j,char player){
        int n_Cells=0;
        if (getTable(i+1,j)==player||getTable(i+1,j)==player-32)
            n_Cells++;
        if (getTable(i-1,j+1)==player||getTable(i-1,j+1)==player-32)
            n_Cells++;
        if (getTable(i-1,j)==player||getTable(i-1,j)==player-32)
            n_Cells++;
        if (getTable(i+1,j-1)==player||getTable(i+1,j-1)==player-32)
            n_Cells++;
        return n_Cells +1;
    }
    /**Returns The Wanted Cell's State*/
    private char getTable(int y,int x){
        if (0<=x && 0<=y && x<size&& y<size){
            return hexCells[y][x].getState();
        }else
            return Cell_type.EMPTY.type;
    }

    /**Determines The Turn*/
    private  int check_turn(){
        int counter= 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (getTable(i,j) != Cell_type.EMPTY.type){
                    counter+=1;
                }
        if (counter%2==0)
            return 1;
        else
            return 2;
    }
    /**Resets The Table*/
    private void create_table(){
        moves = new Cell[1];
        hexCells= new Cell[size][size];
        for (int i = 0; i < size; ++i){
            for (int j = 0; j < size; ++j){
                hexCells[i][j] = new Cell();
                hexCells[i][j].setX((char)(j+'a'));
                hexCells[i][j].setY(i);
            }
        }
        if (hexCells == null){
            Toast.makeText(BoardActivity.this, "Something happened while memory allocation", Toast.LENGTH_LONG).show();
        }else
            for (int i = 0; i < size; ++i){
                for (int j = 0; j < size; ++j){
                    putTable(Cell_type.EMPTY.type,i,j);
                }
            }
    }
    /**Returns The Wanted Cell */
    protected Cell extractCell(int y, char x){ return hexCells[y][x-'a']; }
    /**Puts The Type In Cell*/
    private void putTable(char player,int y,int x){
        hexCells[y][x].setX((char)(x+'a'));
        hexCells[y][x].setY(y);
        hexCells[y][x].setState(player);
    }
    /**Helper For Computer's Move*/
    private Cell hard_bot(int y,char x){
        char new_x= (char)(((int)x+(int)(getSize()+'a'))/2);//for smart moves ->average of inputed and size
        char new_x2= (char)((((int)x+(int)'a')/2)+1);//for smart moves -> average of inputed and zero('a' in alphabet)
        if ((double)getSize()/2>(int)(x-'a')){//left half of the table
            if (getTable(y-1,(int)(x-'a')-1)==Cell_type.PLAYER1.type)//if the left side is 'x'
            {
                if (check_input(y+1,new_x)==1)	{
                    hexCells[y][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y,new_x);
                }else if (check_input(y-1,new_x)==1){
                    hexCells[y-2][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y-2,new_x);
                }else if (check_input(y+1,(char)(new_x-1))==1){
                    hexCells[y][(int)(new_x-'a')-1].setState(Cell_type.COMPUTER.type);
                    return extractCell(y,(char)(new_x-1));
                }else
                    return put_empty(y,x);
            }else{//if the left side is not 'x'
                if (check_input(y,new_x)==1){
                    hexCells[y-1][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y-1,new_x);
                }else if (check_input(y+1,new_x)==1){
                    hexCells[y][(int)(new_x-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y,new_x);
                }else
                    return put_empty(y,x);
            }
        }else{//right half of the table
            if (getTable(y-1,(int)(x-'a')+1)==Cell_type.PLAYER1.type)//if the right side is 'x'
            {
                if (check_input(y+1,new_x2)==1){
                    hexCells[y][(int)(new_x2-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y,new_x2);
                }else if (check_input(y+1,new_x2)==1)
                {
                    hexCells[y-2][(int)(new_x2-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y-2,new_x2);
                }else if (check_input(y+1,(char)(new_x2-1))==1)
                {
                    hexCells[y][(int)(new_x2-'a')-1].setState(Cell_type.COMPUTER.type);
                    return extractCell(y,(char)(new_x2-1));
                }else
                    return put_empty(y,x);
            }else{//if the right side is not 'x'
                if (check_input(y,new_x2)==1)
                {
                    hexCells[y-1][(int)(new_x2-'a')].setState(Cell_type.COMPUTER.type);
                    return extractCell(y-1,new_x2);
                }else if (check_input(y,(char)(new_x2-1))==1)
                {
                    hexCells[y-1][(int)(new_x2-'a')-1].setState(Cell_type.COMPUTER.type);
                    return extractCell(y-1,(char)(new_x2-1));
                }else
                    return put_empty(y,x);
            }
        }
    }
    /**Helper For Computer's Move*/
    private Cell put_empty(int y,char x){
        if (check_input(y-1,(char)(x+1)) == 1 ){//if the right-up side is Cell_type.EMPTY.type
            hexCells[y-2][(int)(x-'a')+1].setState(Cell_type.COMPUTER.type);
            return extractCell(y-2,(char)(x+1));
        }else if (check_input(y,(char)(x+1)) == 1 ){//if the right side is Cell_type.EMPTY.type
            hexCells[y-1][(int)(x-'a')+1].setState(Cell_type.COMPUTER.type);
            return extractCell(y-1,(char)(x+1));
        }else if (check_input(y,(char)(x-1)) == 1 ){//if the left side is Cell_type.EMPTY.type
            hexCells[y-1][(int)(x-'a')-1].setState(Cell_type.COMPUTER.type);
            return extractCell(y-1,(char)(x-1));
        }else if (check_input(y+1,(char)(x-1)) == 1 ){//if the left-down side is Cell_type.EMPTY.type
            hexCells[y][(int)(x-'a')-1].setState(Cell_type.COMPUTER.type);
            return extractCell(y,(char)(x-1));
        }else if (check_input(y+1,x) == 1 ){//if the down side is Cell_type.EMPTY.type
            hexCells[y][(int)(x-'a')].setState(Cell_type.COMPUTER.type);
            return extractCell(y,x);
        }else if (check_input(y-1,x) == 1 ){//if the up side is Cell_type.EMPTY.type
            hexCells[y-2][(int)(x-'a')].setState(Cell_type.COMPUTER.type);
            return extractCell(y-2,x);
        }else{
            int i,j=0;
            for (i = 0; i < getSize()-1; i++){
                for (j = 0; j < getSize()-1; j++)
                    if (getTable(i,j)==Cell_type.EMPTY.type)
                        break;
                if (getTable(i,j)==Cell_type.EMPTY.type)
                    break;
            }
            hexCells[i][j].setState(Cell_type.COMPUTER.type);
            return extractCell(i,(char)(j+'a'));
        }
    }

    private void delay(int ms, String winner,String score) {
        Handler handler = new Handler();
        tw_turn.setText("-");
        for (int i=0;i<getSize();i++)
            for (int j=0;j<getSize();j++)
                hex_buttons[i][j].setClickable(false);

        ((Button) findViewById(R.id.new_button)).setClickable(false);
        ((Button) findViewById(R.id.undo_button)).setClickable(false);
        ((Button) findViewById(R.id.reset_button)).setClickable(false);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                last.putExtra("winner", winner);
                last.putExtra("winner_score", score);
                finish();
                startActivity(last);
            }
        }, ms);
    }


    private void undo(){
        if (moves!=null && marked_cells()!=0){
            for (int i = 0; i < getType(); ++i){
                set_turn();
                int y=moves[marked_cells()-1].getY();
                char x = moves[marked_cells()-1].getX();
                putTable(Cell_type.EMPTY.type,y,x-'a');
                hex_buttons[y][x-'a'].setBackgroundColor(Color.parseColor("#C3C3C3"));
                hex_buttons[y][x-'a'].setBackground(stroke);
                hex_buttons[y][x-'a'].setClickable(true);
                hex_buttons[y][x-'a'].setEnabled(true);
            }
            tw_p1_score.setText(userScore(Cell_type.PLAYER1.type));
            if (getType()==2) {
                tw_p2_score.setText(userScore(Cell_type.COMPUTER.type));
            }else
                tw_p2_score.setText(userScore(Cell_type.PLAYER2.type));
        }else
            Toast.makeText(BoardActivity.this, "NO MOVES", Toast.LENGTH_SHORT).show();
    }






}