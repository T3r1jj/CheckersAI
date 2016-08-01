/* 
 * Copyright 2015 Damian Terlecki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package checkers.ui;

import checkers.model.Board;
import checkers.model.Game;
import checkers.model.Move;
import checkers.model.Turn;
import java.awt.Point;
import java.util.List;
import java.util.Scanner;

public class TUI implements UI {

    @Override
    public Move getNextMove() {
        int sourceX;
        int sourceY;
        int destinationX;
        int destinationY;
        System.out.println("Enter your Move.");
        System.out.println("Checker To Move:");
        System.out.print("\tRow(0-7): ");
        sourceY = TakeInput();
        System.out.print("\tCol(0-7): ");
        sourceX = TakeInput();
        System.out.println("Destination");
        System.out.print("\tRow(0-7): ");
        destinationY = TakeInput();
        System.out.print("\tCol(0-7): ");
        destinationX = TakeInput();
        return new Move(new Point(sourceX, sourceY), new Point(destinationX, destinationY));
    }

    private int TakeInput() {
        int choice;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= 0 && choice < 8) {
                    break;
                }
            } catch (Exception ex) {
            }

            System.out.print("Wrong Choice...Type again (0-7): ");
        }
        return choice;
    }

    @Override
    public void printEnd(String message, Turn turn) {
        System.out.println("____________________________________________________");
        if (message == null) {
            System.out.println("Congratulations! " + turn + " has Won.");
        } else {
            System.out.println(message);
        }
    }

    @Override
    public void printMoves(List<Move> moveSeq) {
        for (Move m : moveSeq) {
            System.out.print(m);
            System.out.print(", ");
        }
        System.out.println();
    }

    @Override
    public void printTurn(Turn turn) {
        System.out.println("\n----------------------------------------------------");
        System.out.println("\t\t" + turn + "'s TURN");
        System.out.println("----------------------------------------------------");
    }

    @Override
    public void printIllegalMove(Turn turn) {
        System.out.println("################## INVALID MOVE ####################");
    }

    @Override
    public void printCheckers(Board board) {
        System.out.print(board);
        for (int j = 0; j < board.getLength(); j++) {
            System.out.print(board.toString(j));
        }
        System.out.println(board);
        for (int i = 0; i < board.getHeight(); i++) {
            System.out.print(board.toString(i));
            for (int j = 0; j < board.getLength(); j++) {
                System.out.print(board.checkers[i][j]);
            }
            System.out.println(board);
        }
        System.out.print(board);
        for (int j = 0; j < board.getLength(); j++) {
            System.out.print(board);
        }
        System.out.println(board);
    }

    @Override
    public void printDataInfo(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        Game game = new Game(new TUI());
        game.PlayGame();
    }

    @Override
    public boolean isComplex() {
        return false;
    }

    @Override
    public char getGameplay() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter \"w\" or \"W\" if you want to play as white (starting choice).");
        System.out.println("Enter \"r\" or \"R\" if you want to play as red.");
        System.out.println("Enter \"n\" or \"n\" for two player game.");
        System.out.println("Enter \"a\" or \"A\" for AI vs AI game.");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
        String choice;
        while (true) {
            try {
                System.out.print("Enter your Choice (w/W/r/R/a/A/n/N): ");
                choice = scanner.nextLine().toLowerCase();
                if (choice.equals("w") || choice.equals("r") || choice.equals("a") || choice.equals("n")) {
                    break;
                }
            } catch (Exception ex) {
            }

            System.out.println("\nWrong Choice...Type again (w/W/r/R/n/N/a/A): ");
        }
        return choice.charAt(0);
    }

    @Override
    public int getRedDepth() {
        return getSearchDepth(Turn.RED);
    }

    @Override
    public int getWhiteDepth() {
        return getSearchDepth(Turn.WHITE);
    }

    private int getSearchDepth(Turn player) {
        Scanner br = new Scanner(System.in);
        System.out.print("Enter search depth for AI " + player + " (prefferably 1-8, higher will make you wait for computer's decision): ");
        int choice;
        while (true) {
            try {
                choice = br.nextInt();
                br.nextLine();
                if (choice > 0) {
                    break;
                }
            } catch (Exception ex) {
            }
            System.out.println("\nWrong Choice... Type again (1-8): ");
        }
        return choice;
    }
}
