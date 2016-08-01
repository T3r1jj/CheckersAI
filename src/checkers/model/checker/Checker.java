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
package checkers.model.checker;

import checkers.model.Board;
import checkers.model.Move;
import checkers.model.Turn;
import checkers.model.ai.EvaluatorConfig;
import java.awt.Point;
import java.util.List;
import javax.swing.ImageIcon;

public abstract class Checker implements Cloneable {

    protected Point coordinates;

    public Checker(Point coordinates) {
        this.coordinates = coordinates;
    }

    public Checker() {
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public void move(Point newCoordinates) {
        coordinates.x = newCoordinates.x;
        coordinates.y = newCoordinates.y;
    }

    public abstract ImageIcon getImage();

    public abstract int getValue(EvaluatorConfig config, Turn turn);

    public abstract List<Move> generateForcedMoves(Board board);

    public abstract List<Move> generateNonForcedMoves(Board board);

    public abstract boolean isEnemy(Checker checker);

    public abstract boolean isCollidable();

    public abstract Turn getSide();

    public abstract Checker tryPromoting(Board board);

    @Override
    public abstract Checker clone();
}
