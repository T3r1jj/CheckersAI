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
package checkers.model;

import checkers.model.ai.Computer;
import checkers.model.ai.EvaluatorConfig;
import checkers.model.checker.Checker;
import checkers.model.checker.NoChecker;
import checkers.model.checker.RedPawn;
import checkers.model.checker.WhitePawn;
import checkers.model.checker.EmptySpace;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Board {

    public Checker[][] checkers;
    private int stalemateCounter;
    private int lastCheckersCount;
    private int redCheckersCount;
    private int whiteCheckersCount;
    private static int maxRoundsWithoutProgress;
    private EvaluatorConfig config;

    public Board(int x, int maxRoundsWithoutProgress, EvaluatorConfig config) {
        this.checkers = new Checker[x][x];
        this.config = config;
        Board.maxRoundsWithoutProgress = maxRoundsWithoutProgress;
        initializeBoard();
    }

    public Board(Board board) {
        this.config = board.getConfig();
        this.checkers = new Checker[board.getHeight()][board.getLength()];
        for (int i = 0; i < board.getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < getLength(); j += 2) {
                this.checkers[i][j] = board.checkers[i][j].clone();
            }
        }
        this.redCheckersCount = board.redCheckersCount;
        this.whiteCheckersCount = board.whiteCheckersCount;
        this.stalemateCounter = board.stalemateCounter;
        this.lastCheckersCount = board.lastCheckersCount;
    }

    public EvaluatorConfig getConfig() {
        return config;
    }

    public final int getLength() {
        return checkers[0].length;
    }

    public final int getHeight() {
        return checkers.length;
    }

    private void initializeBoard() {
        for (int i = 0; i < getLength(); i++) {
            if (i % 2 == 1) {
                checkers[0][i] = new RedPawn(new Point(i, 0));
                checkers[getHeight() - 2][i] = new WhitePawn(new Point(i, getHeight() - 2));
                checkers[2][i] = new RedPawn(new Point(i, 2));
                checkers[4][i] = NoChecker.getInstance();
                redCheckersCount += 2;
                whiteCheckersCount += 1;
                for (int j = 1; j < getHeight(); j += 2) {
                    checkers[j][i] = EmptySpace.getInstance();
                }
            } else {
                checkers[getHeight() - 1][i] = new WhitePawn(new Point(i, getHeight() - 1));
                checkers[1][i] = new RedPawn(new Point(i, 1));
                checkers[getHeight() - 3][i] = new WhitePawn(new Point(i, getHeight() - 3));
                checkers[3][i] = NoChecker.getInstance();
                redCheckersCount += 1;
                whiteCheckersCount += 2;
                for (int j = 0; j < getHeight(); j += 2) {
                    checkers[j][i] = EmptySpace.getInstance();
                }
            }
        }
    }

    public void move(Point source, Point destination) {
        this.checkers[source.y][source.x].move(destination);
        this.checkers[destination.y][destination.x] = this.checkers[source.y][source.x];
        this.checkers[source.y][source.x] = NoChecker.getInstance();
        // Promote pawn to king
        if (destination.y == 0 || destination.y == getHeight() - 1) {
            this.checkers[destination.y][destination.x] = this.checkers[destination.y][destination.x].tryPromoting(this);
        }
    }

    public void captureRedChecker(Point source, Point destination) {
        Point dir = new Point((destination.x - source.x) / 2, (destination.y - source.y) / 2);
        this.checkers[source.y + dir.y][source.x + dir.x] = NoChecker.getInstance();
        redCheckersCount--;
        this.move(source, destination);
    }

    public void captureWhiteChecker(Point source, Point destination) {
        Point dir = new Point((destination.x - source.x) / 2, (destination.y - source.y) / 2);
        this.checkers[source.y + dir.y][source.x + dir.x] = NoChecker.getInstance();
        whiteCheckersCount--;
        this.move(source, destination);
    }

    public void genericWhiteMove(Move move) {
        if ((Math.abs(move.getSource().x - move.getDestination().x) == 2 && Math.abs(move.getSource().y - move.getDestination().y) == 2)) {
            captureRedChecker(move.getSource(), move.getDestination());
        } else {
            this.move(move.getSource(), move.getDestination());
        }
        if (lastCheckersCount == redCheckersCount + whiteCheckersCount) {
            stalemateCounter++;
        } else {
            lastCheckersCount = redCheckersCount + whiteCheckersCount;
            stalemateCounter = 0;
        }
    }

    public void genericRedMove(Move move) {
        if ((Math.abs(move.getSource().getX() - move.getDestination().getX()) == 2 && Math.abs(move.getSource().getY() - move.getDestination().getY()) == 2)) {
            captureWhiteChecker(move.getSource(), move.getDestination());
        } else {
            this.move(move.getSource(), move.getDestination());
        }
        if (lastCheckersCount == redCheckersCount + whiteCheckersCount) {
            stalemateCounter++;
        } else {
            lastCheckersCount = redCheckersCount + whiteCheckersCount;
            stalemateCounter = 0;
        }
    }

    public List<Move> generateForcedWhiteMoves() {
        List<Move> forcedMovesForWhite = new LinkedList<Move>();
        for (int i = 0; i < getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < getLength(); j += 2) {
                if (checkers[i][j].getSide() == Turn.WHITE) {
                    forcedMovesForWhite.addAll(checkers[i][j].generateForcedMoves(this));
                }
            }
        }
        return forcedMovesForWhite;
    }

    public List<Move> generateNonForcedWhiteMoves() {
        List<Move> allNonForcedMovesForWhite = new LinkedList<Move>();
        for (int i = 0; i < getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < getLength(); j += 2) {
                if (checkers[i][j].getSide() == Turn.WHITE) {
                    allNonForcedMovesForWhite.addAll(checkers[i][j].generateNonForcedMoves(this));
                }
            }
        }
        return allNonForcedMovesForWhite;
    }

    public List<Move> generateForcedRedMoves() {
        List<Move> forcedMovesForRed = new LinkedList<Move>();
        for (int i = 0; i < getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < getLength(); j += 2) {
                if (checkers[i][j].getSide() == Turn.RED) {
                    forcedMovesForRed.addAll(checkers[i][j].generateForcedMoves(this));
                }
            }
        }
        return forcedMovesForRed;
    }

    public List<Move> generateNonForcedRedMoves() {
        List<Move> allNonForcedMovesForRed = new LinkedList<Move>();
        for (int i = 0; i < getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < getLength(); j += 2) {
                if (checkers[i][j].getSide() == Turn.RED) {
                    allNonForcedMovesForRed.addAll(checkers[i][j].generateNonForcedMoves(this));
                }
            }
        }
        return allNonForcedMovesForRed;
    }

    public boolean isFinished() {
        return redCheckersCount == 0 || whiteCheckersCount == 0;
    }

    public boolean isBlocked(Turn turn) {
        List<LinkedList<Move>> possibleMoveSeq = new Computer(this, 2, turn).expandMoves(new Board(this), turn);
        return possibleMoveSeq.isEmpty();
    }

    public boolean noProgress() {
        if (stalemateCounter > maxRoundsWithoutProgress) {
            return true;
        }
        return false;
    }

    public boolean isWinnerWhite() {
        return (redCheckersCount == 0 || isBlocked(Turn.RED));
    }

    public boolean isWinnerRed() {
        return (whiteCheckersCount == 0 || isBlocked(Turn.WHITE));
    }

    public int countReds() {
        return redCheckersCount;
    }

    public int countWhites() {
        return whiteCheckersCount;
    }

    public String toString(int i) {
        return (char) 27 + "[1;37;43m" + i + " " + (char) 27 + "[0;39;49m";
    }
    
    @Override
    public String toString() {
        return (char) 27 + "[1;37;43m  " + (char) 27 + "[0;39;49m";
    }
    
}
