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
package io.github.t3r1jj.checkersai.model;

import io.github.t3r1jj.checkersai.model.ai.EvaluatorConfig;
import io.github.t3r1jj.checkersai.model.ai.Statistics;
import io.github.t3r1jj.checkersai.model.ai.Computer;
import io.github.t3r1jj.checkersai.model.checker.CheckersOwner;
import io.github.t3r1jj.checkersai.ui.UI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {

    private final Board board;
    private Player player;
    private Computer computerWhite;
    private Computer computerRed;
    private CheckersOwner whiteOwner;
    private CheckersOwner redOwner;
    private boolean stopped = false;
    private boolean stopIntervals = false;
    private boolean stopAtIntervals = true;
    private EvaluatorConfig config;
    private final UI ui;

    public boolean isStopped() {
        return stopped;
    }

    public void nextStep() {
        this.stopAtIntervals = !this.stopAtIntervals;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
        if (this.player != null) {
            this.player.setIsGameStopped(stopped);
        }
    }

    public boolean isStopIntervals() {
        return stopIntervals;
    }

    public void setStopIntervals(boolean stopIntervals) {
        this.stopIntervals = stopIntervals;
    }

    public Game(UI ui) {
        this.ui = ui;
        this.config = new EvaluatorConfig();
        System.out.println("CheckersAI - English checkers, v1.0.0, author: Damian Terlecki © 2015");
        try (FileInputStream fis = new FileInputStream("CheckersAI.properties")) {
            config.load(fis);
        } catch (FileNotFoundException ex) {
            System.err.println("CheckersAI.properties not found. Loading default values...");
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        board = new Board(8, 50, config);
        ui.printCheckers(board);
        initOwners(ui.getGameplay());
        Statistics.cleanStatistics();
    }

    public Game(UI ui, EvaluatorConfig config) {
        this.ui = ui;
        this.config = config;
        board = new Board(8, 50, config);
        ui.printCheckers(board);
        initOwners(config.getGameplay());
        Statistics.cleanStatistics();
    }

    public void whiteMove() {
        ui.printTurn(Turn.WHITE);
        if (whiteOwner.equals(CheckersOwner.HUMAN)) {
            ui.printDataInfo("White player's move was:");
            player.makeNextWhiteMoves();
            if (stopped) {
                return;
            }
            ui.printDataInfo("\n");
        } else {
            List<Move> resultantMoveSeq = new LinkedList<Move>();
            int predictedScore = computerWhite.makeNextWhiteMoves(resultantMoveSeq);
            ui.printDataInfo(Statistics.MOVES_COUNT + ". White computer's move was: ");
            ui.printMoves(resultantMoveSeq);
            ui.printDataInfo("Prediction depth = " + computerWhite.getMaxDepth() + ", Prediction score: " + predictedScore + "\n");
            ui.printDataInfo("Current board score for white: " + computerWhite.getEvaluator().evaluateBoard(board, Turn.WHITE) + "\n");
        }
    }

    public void redMove() {
        ui.printTurn(Turn.RED);
        if (redOwner.equals(CheckersOwner.HUMAN)) {
            ui.printDataInfo("Red player's move was:");
            player.makeNextRedMoves();
            if (stopped) {
                return;
            }
            ui.printDataInfo("\n");
        } else {
            List<Move> resultantMoveSeq = new LinkedList<Move>();
            int predictedScore = computerRed.makeNextRedMoves(resultantMoveSeq);
            ui.printDataInfo(Statistics.MOVES_COUNT + ". Red computer's move was: ");
            ui.printMoves(resultantMoveSeq);
            ui.printDataInfo("Prediction depth = " + computerRed.getMaxDepth() + ", Prediction score: " + predictedScore + "\n");
            ui.printDataInfo("Current board score for red: " + computerRed.getEvaluator().evaluateBoard(board, Turn.RED) + "\n");
        }
    }

    public void PlayGame() {
        if (!ui.isComplex()) {
            while (!board.isFinished() && !stopped) {
                boolean whiteIsBlocked = board.isBlocked(Turn.WHITE);
                boolean redIsBlocked = board.isBlocked(Turn.RED);
                if (whiteIsBlocked && redIsBlocked || board.noProgress()) {
                    ui.printEnd("Draw!", null);
                    break;
                } else {
                    if (whiteIsBlocked) {
                        ui.printEnd(null, Turn.RED);
                        break;
                    }
                    if (redIsBlocked) {
                        ui.printEnd(null, Turn.WHITE);
                        break;
                    }
                }

                whiteMove();
                if (board.isFinished()) {
                    ui.printCheckers(board);
                    ui.printEnd(null, Turn.WHITE);
                    break;
                }
                ui.printCheckers(board);

                redMove();
                if (board.isFinished()) {
                    ui.printCheckers(board);
                    ui.printEnd(null, Turn.RED);
                    break;
                }
                ui.printCheckers(board);
            }
        } else {
            while (!board.isFinished() && !stopped) {
                boolean whiteIsBlocked = board.isBlocked(Turn.WHITE);
                boolean redIsBlocked = board.isBlocked(Turn.RED);
                if (whiteIsBlocked && redIsBlocked || board.noProgress()) {
                    ui.printEnd("Draw!", null);
                    break;
                } else {
                    if (whiteIsBlocked) {
                        ui.printEnd(null, Turn.RED);
                        break;
                    }
                    if (redIsBlocked) {
                        ui.printEnd(null, Turn.WHITE);
                        break;
                    }
                }

                if (stopIntervals) {
                    while (!stopAtIntervals) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                whiteMove();
                if (stopped) {
                    return;
                }
                if (board.isFinished()) {
                    ui.printCheckers(board);
                    ui.printEnd(null, Turn.WHITE);
                    break;
                }
                ui.printCheckers(board);

                if (stopIntervals) {
                    while (stopAtIntervals) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                redMove();
                if (stopped) {
                    return;
                }
                if (board.isFinished()) {
                    ui.printCheckers(board);
                    ui.printEnd(null, Turn.RED);
                    break;
                }
                ui.printCheckers(board);
            }
        }
    }

    private void initOwners(char decision) {
        switch (decision) {
            case 'w':
                player = new Player(board, ui);
                computerRed = new Computer(board, ui.getRedDepth(), Turn.RED);
                whiteOwner = CheckersOwner.HUMAN;
                redOwner = CheckersOwner.COMPUTER;
                break;
            case 'r':
                player = new Player(board, ui);
                computerWhite = new Computer(board, ui.getWhiteDepth(), Turn.WHITE);
                whiteOwner = CheckersOwner.COMPUTER;
                redOwner = CheckersOwner.HUMAN;
                break;
            case 'n':
                player = new Player(board, ui);
                whiteOwner = CheckersOwner.HUMAN;
                redOwner = CheckersOwner.HUMAN;
                break;
            case 'a':
                computerWhite = new Computer(board, ui.getWhiteDepth(), Turn.WHITE);
                computerRed = new Computer(board, ui.getRedDepth(), Turn.RED);
                whiteOwner = CheckersOwner.COMPUTER;
                redOwner = CheckersOwner.COMPUTER;
                break;
        }
    }

    public Board getBoard() {
        return board;
    }

    public List<Move> generateForcedPlayerMoves() {
        if (whiteOwner == CheckersOwner.HUMAN) {
            return board.generateForcedWhiteMoves();
        } else {
            return board.generateForcedRedMoves();
        }
    }

}
