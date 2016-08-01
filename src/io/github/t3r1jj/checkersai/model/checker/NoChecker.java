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
package io.github.t3r1jj.checkersai.model.checker;

import io.github.t3r1jj.checkersai.model.Board;
import io.github.t3r1jj.checkersai.model.Move;
import io.github.t3r1jj.checkersai.model.Turn;
import io.github.t3r1jj.checkersai.model.ai.EvaluatorConfig;
import java.awt.Point;
import java.util.Collections;
import java.util.List;
import javax.swing.ImageIcon;

public class NoChecker extends Checker {

    private static final ImageIcon IMAGE = new ImageIcon();
    private static final Turn SIDE = Turn.NONE;
    private static final NoChecker INSTANCE = new NoChecker();

    protected NoChecker() {
    }

    public static NoChecker getInstance() {
        return INSTANCE;
    }

    @Override
    public void move(Point newCoordinates) {
        throw new UnsupportedOperationException("Empty checker place cannot initialize movement");
    }

    @Override
    public List<Move> generateForcedMoves(Board board) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Move> generateNonForcedMoves(Board board) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public ImageIcon getImage() {
        return IMAGE;
    }

    @Override
    public boolean isEnemy(Checker checker) {
        return false;
    }

    @Override
    public Turn getSide() {
        return SIDE;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public Checker clone() {
        return this;
    }

    @Override
    public String toString() {
        return (char) 27 + "[30;40m" + "  " + (char) 27 + "[39;49m";
    }

    @Override
    public Checker tryPromoting(Board board) {
        return this;
    }

    @Override
    public int getValue(EvaluatorConfig config, Turn turn) {
        return 0;
    }

}
