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

import io.github.t3r1jj.checkersai.model.ai.Statistics;
import io.github.t3r1jj.checkersai.ui.UI;
import java.awt.Point;
import java.util.List;

public class Player {

    private Board board;
    boolean isGameStopped = false;
    private final UI ui;

    public Player(Board board, UI ui) {
        this.board = board;
        this.ui = ui;
    }

    public boolean isIsGameStopped() {
        return isGameStopped;
    }

    public void setIsGameStopped(boolean isGameStopped) {
        this.isGameStopped = isGameStopped;
    }

    public void makeNextWhiteMoves() {
        boolean incorrectOption = true;
        while (incorrectOption) {
            Move move = ui.getNextMove();
            if (isGameStopped) {
                break;
            }
            if (validMoveForWhite(move)) {
                incorrectOption = false;
                if (isGameStopped) {
                    break;
                }
            } else {
                ui.printIllegalMove(Turn.WHITE);
                ui.printCheckers(board);
            }
        }
    }

    public void makeNextRedMoves() {
        boolean incorrectOption = true;
        while (incorrectOption) {
            Move move = ui.getNextMove();
            if (isGameStopped) {
                break;
            }
            if (validMoveForRed(move)) {
                incorrectOption = false;
                if (isGameStopped) {
                    break;
                }
            } else {
                ui.printIllegalMove(Turn.RED);
                ui.printCheckers(board);
            }
        }
    }

    public boolean validMoveForWhite(Move move) {
        List<Move> forcedMoves = board.generateForcedWhiteMoves();
        if (!forcedMoves.isEmpty()) {
            if (forcedMoves.contains(move)) {
                board.captureRedChecker(move.getSource(), move.getDestination());
                Statistics.MOVES_COUNT++;
                if (ui.isComplex()) {
                    ui.printDataInfo(" " + (char) ('A' + move.getSource().x) + (8 - move.getSource().y)
                            + " -> " + (char) ('A' + move.getDestination().x) + (8 - move.getDestination().y) + ",");
                    List<Move> furtherMoves = board.checkers[move.getDestination().y][move.getDestination().x].generateForcedMoves(board);
                    if (!furtherMoves.isEmpty()) {
                        furtherMoveForRed(new Point(move.getDestination().x, move.getDestination().y));
                    }
                    return true;
                } else {
                    List<Move> furtherMoves = board.checkers[move.getDestination().y][move.getDestination().x].generateForcedMoves(board);
                    return furtherMoves.isEmpty();
                }
            }
        } else {
            List<Move> availableMoves = board.generateNonForcedWhiteMoves();
            if (availableMoves.contains(move)) {
                if (ui.isComplex()) {
                    ui.printDataInfo(" " + (char) ('A' + move.getSource().x) + (8 - move.getSource().y)
                            + " -> " + (char) ('A' + move.getDestination().x) + (8 - move.getDestination().y) + ",");
                }
                board.genericWhiteMove(move);
                Statistics.MOVES_COUNT++;
                return true;
            }
        }
        return false;
    }

    public boolean validMoveForRed(Move move) {
        List<Move> forcedMoves = board.generateForcedRedMoves();
        if (!forcedMoves.isEmpty()) {
            if (forcedMoves.contains(move)) {
                board.captureWhiteChecker(move.getSource(), move.getDestination());
                Statistics.MOVES_COUNT++;
                if (ui.isComplex()) {
                    ui.printDataInfo(" " + (char) ('A' + move.getSource().x) + (8 - move.getSource().y)
                            + " -> " + (char) ('A' + move.getDestination().x) + (8 - move.getDestination().y) + ",");
                    List<Move> furtherMoves = board.checkers[move.getDestination().y][move.getDestination().x].generateForcedMoves(board);
                    if (!furtherMoves.isEmpty()) {
                        furtherMoveForRed(new Point(move.getDestination().x, move.getDestination().y));
                    }
                    return true;
                } else {
                    List<Move> furtherMoves = board.checkers[move.getDestination().y][move.getDestination().x].generateForcedMoves(board);
                    return furtherMoves.isEmpty();
                }
            }
        } else {
            List<Move> availableMoves = board.generateNonForcedRedMoves();
            if (availableMoves.contains(move)) {
                if (ui.isComplex()) {
                    ui.printDataInfo(" " + (char) ('A' + move.getSource().x) + (8 - move.getSource().y)
                            + " -> " + (char) ('A' + move.getDestination().x) + (8 - move.getDestination().y) + ",");
                }
                board.genericRedMove(move);
                Statistics.MOVES_COUNT++;
                return true;
            }
        }
        return false;
    }

    public void furtherMoveForRed(Point source) {
        if (ui.isComplex()) {
            ui.printCheckers(board);
            boolean incorrectMove = true;
            while (incorrectMove) {
                Move move = ui.getNextMove();
                if (isGameStopped) {
                    return;
                }
                List<Move> furtherMoves = board.checkers[source.y][source.x].generateForcedMoves(board);
                if (furtherMoves.contains(move)) {
                    board.captureWhiteChecker(move.getSource(), move.getDestination());
                    List<Move> furtherMoves2 = board.checkers[move.getDestination().y][move.getDestination().x].generateForcedMoves(board);
                    if (!furtherMoves2.isEmpty()) {
                        furtherMoveForRed(new Point(move.getDestination().x, move.getDestination().y));
                    }
                    incorrectMove = false;
                } else {
                    ui.printIllegalMove(Turn.RED);
                    ui.printCheckers(board);
                }
            }
        }
    }

    public void furtherMoveForWhite(Point source) {
        if (ui.isComplex()) {
            ui.printCheckers(board);
            boolean incorrectMove = true;
            while (incorrectMove) {
                Move move = ui.getNextMove();
                if (isGameStopped) {
                    return;
                }
                List<Move> furtherMoves = board.checkers[source.y][source.x].generateForcedMoves(board);
                if (furtherMoves.contains(move)) {
                    board.captureRedChecker(move.getSource(), move.getDestination());
                    List<Move> furtherMoves2 = board.checkers[move.getDestination().y][move.getDestination().x].generateForcedMoves(board);
                    if (!furtherMoves2.isEmpty()) {
                        furtherMoveForRed(new Point(move.getDestination().x, move.getDestination().y));
                    }
                    incorrectMove = false;
                } else {
                    ui.printIllegalMove(Turn.WHITE);
                    ui.printCheckers(board);
                }
            }
        }
    }
}
