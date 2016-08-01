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
package io.github.t3r1jj.checkersai.ui;

import io.github.t3r1jj.checkersai.model.Board;
import io.github.t3r1jj.checkersai.model.Move;
import io.github.t3r1jj.checkersai.model.Turn;
import java.util.List;

public interface UI {

    Move getNextMove();

    void printTurn(Turn turn);

    void printIllegalMove(Turn turn);

    void printCheckers(Board board);

    void printDataInfo(String string);

    void printEnd(String message, Turn turn);

    void printMoves(List<Move> list);

    char getGameplay();

    int getRedDepth();

    int getWhiteDepth();

    boolean isComplex();
}
