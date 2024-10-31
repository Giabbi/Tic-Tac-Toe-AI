// (c) 2024 Roland Labana

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Queue;

interface Player {
    char getSymbol();

    void makeMove(TicTacToe game);
}

class HumanPlayer implements Player {
    private char symbol;

    public HumanPlayer(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    //todo - disallow move outside board bounds to avoid runtime error
    @Override
    public void makeMove(TicTacToe game) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your move (" + symbol + "): ");
            int move = scanner.nextInt();
            if (game.isValidMove(move)) {
                game.makeMove(move, symbol);
                break;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
    }
}

class AIPlayer implements Player {
    private char symbol;
    private AI strategy;

    public AIPlayer(char symbol, AI strategy) {
        this.symbol = symbol;
        this.strategy = strategy;
    }

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public void makeMove(TicTacToe game) {
        System.out.println(symbol + "'s AI is thinking...");
        int move = strategy.determineMove(game);
        game.makeMove(move, symbol);
    }
}

interface AI {
    int determineMove(TicTacToe game);
}

// This simply picks the next open square
class SimpleAI implements AI {
    @Override
    public int determineMove(TicTacToe game) {
        for (int i = 0; i < 9; i++) {
            if (game.isValidMove(i)) {
                return i;
            }
        }
        return -1; // No valid move
    }
}

class RandomAI implements AI {
    private Random random = new Random();

    @Override
    public int determineMove(TicTacToe game) {
        List<Integer> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (game.isValidMove(i)) {
                possibleMoves.add(i);
            }
        }

        if (possibleMoves.isEmpty()) {
            return -1; // No valid moves
        }

        int randomIndex = random.nextInt(possibleMoves.size());
        return possibleMoves.get(randomIndex);
    }
}

/**
    "AI" that assignes priorities (or weightes) to different tiles and plays the tile with the highest one
    which is represented by its position in the sequence queue. If two tiles have the same priority, a random
    tile is picked to be on a higher position in the queue. The priority set now is the following:
    center -> corners -> sides
*/
class Giancarlo_AI_Weighted implements AI {

    private int firstPriority = 4;
    // 0, 2, 6, 8
    private List<Integer> secondPriority = new ArrayList<>();
    // 1, 3, 5, 7
    private List<Integer> lastPriority = new ArrayList<>();

    private Queue<Integer> sequence = new LinkedList<>();


    // Initialize first sequence
    public Giancarlo_AI_Weighted() {
        initializeSequence();
    }

    private void initializeSequence() {
        // Initialize lists
        secondPriority.add(0);
        secondPriority.add(2);
        secondPriority.add(6);
        secondPriority.add(8);
        lastPriority.add(1);
        lastPriority.add(3);
        lastPriority.add(5);
        lastPriority.add(7);

        sequence.add(firstPriority);

        // Scramble the order of tiles with the same priority/weight
        for (int i = 0; i < secondPriority.size(); i++) {
            sequence.add(secondPriority.remove((int) (Math.random() * secondPriority.size())));

        }

        for (int i = 0; i < secondPriority.size(); i++) {
            sequence.add(secondPriority.remove((int) (Math.random() * secondPriority.size())));
        }
    }

    @Override
    public int determineMove(TicTacToe game) {
        while (true) {
            for (int i = 0; i < sequence.size(); i++) {
                // Play move from the sequence queue
                if (game.isValidMove(sequence.peek())) return sequence.remove();
                else sequence.remove();
            }
            // if queue is empty, then initialize it again
            initializeSequence();
        }
    }
}

class TicTacToe {
    private char[] board;
    private List<Player> players;

    public TicTacToe(Player player1, Player player2) {
        board = new char[9];
        for (int i = 0; i < 9; i++) {
            board[i] = ' ';
        }
        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
    }

    // Getter for board array; added by Giancarlo
    public char[] getBoard() {
        return board;
    }
    
    public void play() {
        while (true) {
            for (Player player : players) {
                displayBoard();
                player.makeMove(this);
                if (checkWin(board)) {
                    displayBoard();
                    System.out.println(player.getSymbol() + " wins!");
                    return;
                }
                if (isBoardFull()) {
                    displayBoard();
                    System.out.println("It's a draw!");
                    return;
                }
            }
        }
    }

    public boolean isValidMove(int move) {
        return board[move] == ' ' && move >= 0 && move <= 8;
    }

    public void makeMove(int move, char symbol) {
        board[move] = symbol;
    }

public boolean checkWin(char[]theBoard) {
    // Check rows
    for (int i = 0; i < 3; i++) {
        if (theBoard[i * 3] == theBoard[i * 3 + 1] && theBoard[i * 3] == theBoard[i * 3 + 2] && theBoard[i * 3] != ' ') {
            return true;
        }
    }

    // Check columns
    for (int i = 0; i < 3; i++) {
        if (theBoard[i] == theBoard[i + 3] && theBoard[i] == theBoard[i + 6] && theBoard[i] != ' ') {
            return true;
        }
    }

    // Check diagonals
    if (theBoard[0] == theBoard[4] && theBoard[0] == theBoard[8] && theBoard[0] != ' ') {
        return true;
    }
    if (theBoard[2] == theBoard[4] && theBoard[2] == theBoard[6] && theBoard[2] != ' ') {
        return true;
    }

    return false;
}

    public boolean isBoardFull() {
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }

    public void displayBoard() {
    
        for (int i = 0; i < 9; i++) {
            System.out.print(" " + board[i] + " ");
            if (i % 3 == 2) {
                System.out.println();
                if (i != 8) {
                    System.out.println("-----------");
                }
            } else {
                System.out.print("|");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Player player1 = new HumanPlayer('X');
        //Player player2 = new HumanPlayer('X');
        Player player2 = new AIPlayer('O', new SimpleAI());   // ie: "Jim-AI"
        //Player player2 = new AIPlayer('X', new RandomAI());
        TicTacToe game = new TicTacToe(player1, player2);
        game.play();
    }
}
