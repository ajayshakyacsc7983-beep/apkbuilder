package com.example.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private boolean vsAI = true;
    private boolean playerXTurn = true; // true = X, false = O
    private boolean gameActive = true;

    // Board state representation: 0 = Empty, 1 = X, 2 = O
    private final int[] boardState = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Winning combinations
    private final int[][] winPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
    };

    private int scoreX = 0;
    private int scoreO = 0;
    private int scoreTies = 0;

    private Button[] buttons = new Button[9];
    private Button btnVsAI, btnVsFriend, btnResetBoard, btnResetScores;
    private TextView tvScoreX, tvScoreO, tvScoreTies, tvStatus, tvLabelO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        tvScoreX = findViewById(R.id.tvScoreX);
        tvScoreO = findViewById(R.id.tvScoreO);
        tvScoreTies = findViewById(R.id.tvScoreTies);
        tvStatus = findViewById(R.id.tvStatus);
        tvLabelO = findViewById(R.id.tvLabelO);

        btnVsAI = findViewById(R.id.btnVsAI);
        btnVsFriend = findViewById(R.id.btnVsFriend);
        btnResetBoard = findViewById(R.id.btnResetBoard);
        btnResetScores = findViewById(R.id.btnResetScores);

        // Grid Buttons
        buttons[0] = findViewById(R.id.btn0);
        buttons[1] = findViewById(R.id.btn1);
        buttons[2] = findViewById(R.id.btn2);
        buttons[3] = findViewById(R.id.btn3);
        buttons[4] = findViewById(R.id.btn4);
        buttons[5] = findViewById(R.id.btn5);
        buttons[6] = findViewById(R.id.btn6);
        buttons[7] = findViewById(R.id.btn7);
        buttons[8] = findViewById(R.id.btn8);

        // Set Board Button Listeners
        for (int i = 0; i < 9; i++) {
            final int index = i;
            buttons[i].setOnClickListener(v -> handleCellClick(index));
        }

        // Mode Selector Listeners
        btnVsAI.setOnClickListener(v -> setGameMode(true));
        btnVsFriend.setOnClickListener(v -> setGameMode(false));

        // Reset Buttons
        btnResetBoard.setOnClickListener(v -> resetBoard());
        btnResetScores.setOnClickListener(v -> resetScores());

        // Setup Initial UI State
        updateModeUI();
        resetBoard();
    }

    private void setGameMode(boolean isVsAI) {
        if (vsAI != isVsAI) {
            vsAI = isVsAI;
            updateModeUI();
            resetScores();
            resetBoard();
        }
    }

    private void updateModeUI() {
        if (vsAI) {
            btnVsAI.setBackgroundResource(R.drawable.button_accent_bg);
            btnVsAI.setTextColor(ContextCompat.getColor(this, R.color.text_white));
            btnVsFriend.setBackgroundColor(Color.TRANSPARENT);
            btnVsFriend.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            tvLabelO.setText("AI Bot (O)");
        } else {
            btnVsFriend.setBackgroundResource(R.drawable.button_accent_bg);
            btnVsFriend.setTextColor(ContextCompat.getColor(this, R.color.text_white));
            btnVsAI.setBackgroundColor(Color.TRANSPARENT);
            btnVsAI.setTextColor(ContextCompat.getColor(this, R.color.text_muted));
            tvLabelO.setText("Player O");
        }
    }

    private void handleCellClick(int index) {
        if (!gameActive || boardState[index] != 0) return;

        // Player X Turn
        if (playerXTurn) {
            boardState[index] = 1; // 1 represents X
            buttons[index].setText("X");
            buttons[index].setTextColor(ContextCompat.getColor(this, R.color.color_x));
            
            if (checkGameEnd()) return;

            if (vsAI) {
                playerXTurn = false;
                tvStatus.setText("AI is planning...");
                disableBoardInteraction(true);
                
                // Delay AI move slightly for realism
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    makeAIMove();
                    disableBoardInteraction(false);
                }, 600);
            } else {
                playerXTurn = false;
                tvStatus.setText("Player O's Turn");
            }
        } else {
            // Player O Turn (only in vs Friend mode)
            boardState[index] = 2; // 2 represents O
            buttons[index].setText("O");
            buttons[index].setTextColor(ContextCompat.getColor(this, R.color.color_o));
            
            if (checkGameEnd()) return;
            
            playerXTurn = true;
            tvStatus.setText("Player X's Turn");
        }
    }

    private void makeAIMove() {
        if (!gameActive) return;

        int aiMove = getSmartAIMove();
        if (aiMove != -1) {
            boardState[aiMove] = 2;
            buttons[aiMove].setText("O");
            buttons[aiMove].setTextColor(ContextCompat.getColor(this, R.color.color_o));
            
            if (!checkGameEnd()) {
                playerXTurn = true;
                tvStatus.setText("Your Turn!");
            }
        }
    }

    private int getSmartAIMove() {
        // 1. Can AI win in this move? If yes, take it.
        for (int[] combo : winPositions) {
            int countO = 0, countEmpty = 0, emptyIdx = -1;
            for (int idx : combo) {
                if (boardState[idx] == 2) countO++;
                else if (boardState[idx] == 0) { countEmpty++; emptyIdx = idx; }
            }
            if (countO == 2 && countEmpty == 1) return emptyIdx;
        }

        // 2. Can Player win in next move? If yes, block them.
        for (int[] combo : winPositions) {
            int countX = 0, countEmpty = 0, emptyIdx = -1;
            for (int idx : combo) {
                if (boardState[idx] == 1) countX++;
                else if (boardState[idx] == 0) { countEmpty++; emptyIdx = idx; }
            }
            if (countX == 2 && countEmpty == 1) return emptyIdx;
        }

        // 3. Take center if available
        if (boardState[4] == 0) return 4;

        // 4. Take corners
        int[] corners = {0, 2, 6, 8};
        List<Integer> availableCorners = new ArrayList<>();
        for (int c : corners) {
            if (boardState[c] == 0) availableCorners.add(c);
        }
        if (!availableCorners.isEmpty()) {
            return availableCorners.get(new Random().nextInt(availableCorners.size()));
        }

        // 5. Take remaining sides
        List<Integer> availableSides = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (boardState[i] == 0) availableSides.add(i);
        }
        if (!availableSides.isEmpty()) {
            return availableSides.get(new Random().nextInt(availableSides.size()));
        }

        return -1;
    }

    private boolean checkGameEnd() {
        int winner = checkWinner();
        if (winner == 1) {
            scoreX++;
            tvScoreX.setText(String.valueOf(scoreX));
            tvStatus.setText("Player X Wins! 🎉");
            gameActive = false;
            highlightWinningCombo(getWinningCombo(1));
            return true;
        } else if (winner == 2) {
            scoreO++;
            tvScoreO.setText(String.valueOf(scoreO));
            tvStatus.setText(vsAI ? "AI Bot Wins! 🤖" : "Player O Wins! 🎉");
            gameActive = false;
            highlightWinningCombo(getWinningCombo(2));
            return true;
        } else if (isBoardFull()) {
            scoreTies++;
            tvScoreTies.setText(String.valueOf(scoreTies));
            tvStatus.setText("It's a Tie! 🤝");
            gameActive = false;
            return true;
        }
        return false;
    }

    private int checkWinner() {
        for (int[] combo : winPositions) {
            if (boardState[combo[0]] != 0 &&
                boardState[combo[0]] == boardState[combo[1]] &&
                boardState[combo[1]] == boardState[combo[2]]) {
                return boardState[combo[0]];
            }
        }
        return 0; // No winner
    }

    private int[] getWinningCombo(int winner) {
        for (int[] combo : winPositions) {
            if (boardState[combo[0]] == winner &&
                boardState[combo[1]] == winner &&
                boardState[combo[2]] == winner) {
                return combo;
            }
        }
        return null;
    }

    private void highlightWinningCombo(int[] combo) {
        if (combo == null) return;
        for (int idx : combo) {
            buttons[idx].setBackgroundColor(ContextCompat.getColor(this, R.color.primary_purple));
        }
    }

    private boolean isBoardFull() {
        for (int state : boardState) {
            if (state == 0) return false;
        }
        return true;
    }

    private void disableBoardInteraction(boolean disable) {
        for (Button btn : buttons) {
            btn.setEnabled(!disable);
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 9; i++) {
            boardState[i] = 0;
            buttons[i].setText("");
            buttons[i].setBackgroundResource(R.drawable.grid_button_bg);
            buttons[i].setEnabled(true);
        }
        playerXTurn = true;
        gameActive = true;
        tvStatus.setText("Player X's turn! Tap a cell");
    }

    private void resetScores() {
        scoreX = 0;
        scoreO = 0;
        scoreTies = 0;
        tvScoreX.setText("0");
        tvScoreO.setText("0");
        tvScoreTies.setText("0");
    }
}