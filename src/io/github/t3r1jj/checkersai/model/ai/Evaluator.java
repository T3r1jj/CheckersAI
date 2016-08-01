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
package io.github.t3r1jj.checkersai.model.ai;

import io.github.t3r1jj.checkersai.model.Board;
import io.github.t3r1jj.checkersai.model.Turn;

public class Evaluator {

    private Turn owner;
    private final EvaluatorConfig config;

    public Evaluator(EvaluatorConfig config) {
        this.config = config;
    }

    public void setOwner(Turn owner) {
        this.owner = owner;
    }

    public int evaluateBoard(Board board, Turn turn) {
        boolean isWhiteBlocked = board.isBlocked(Turn.WHITE);
        boolean isRedBlocked = board.isBlocked(Turn.RED);
        if (board.noProgress() || (isWhiteBlocked && isRedBlocked)) {
            return (owner.equals(Turn.WHITE)) ? config.getWhiteDraw() : config.getRedDraw();
        }
        if (turn == Turn.RED) {
            return evalueateBoardForRed(board, isWhiteBlocked, isRedBlocked);
        } else {
            return evaluateBoardForWhite(board, isWhiteBlocked, isRedBlocked);
        }
    }

    private int evaluateBoardForWhite(Board board, boolean isWhiteBlocked, boolean isRedBlocked) {
        int wValue = 0;
        if (owner.equals(Turn.WHITE)) {
            if (isRedBlocked || board.countReds() == 0) {
                return config.getWhiteWin();
            } else if (isWhiteBlocked || board.countWhites() == 0) {
                return -config.getWhiteWin();
            }
            wValue = sumWhiteValue(board);
        } else {
            if (isRedBlocked || board.countReds() == 0) {
                return -config.getRedWin();
            } else if (isWhiteBlocked || board.countWhites() == 0) {
                return config.getRedWin();
            }
            wValue = sumRedValue(board);
        }
        wValue /= board.countReds();

        return wValue;
    }

    private int evalueateBoardForRed(Board board, boolean isWhiteBlocked, boolean isRedBlocked) {
        int bValue = 0;
        if (owner.equals(Turn.WHITE)) {
            if (isWhiteBlocked || board.countWhites() == 0) {
                return -config.getWhiteWin();
            } else if (isRedBlocked || board.countReds() == 0) {
                return config.getWhiteWin();
            }
            bValue = sumWhiteValue(board);
        } else {
            if (isWhiteBlocked || board.countWhites() == 0) {
                return config.getRedWin();
            } else if (isRedBlocked || board.countReds() == 0) {
                return -config.getRedWin();
            }
            bValue = sumRedValue(board);
        }
        bValue /= board.countWhites();
        return bValue;
    }

    private int sumRedValue(Board board) {
        int value = 0;
        for (int i = 0; i < board.getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < board.getLength(); j += 2) {
                value += board.checkers[i][j].getValue(config, owner);
            }
        }
        return value;
    }

    private int sumWhiteValue(Board board) {
        int value = 0;
        for (int i = 0; i < board.getHeight(); i++) {
            int j = (i % 2 == 0) ? 1 : 0;
            for (; j < board.getLength(); j += 2) {
                value += board.checkers[i][j].getValue(config, owner);
            }
        }
        return value;
    }
}
