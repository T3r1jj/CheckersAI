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

import java.awt.Point;
import java.util.List;
import java.util.Objects;

public class Move {

    private Point source;
    private Point destination;

    public Move(Point source, Point destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public boolean equals(Object move) {
        if (move != null && move instanceof Move) {
            return this.source.equals(((Move) move).source) && this.destination.equals(((Move) move).destination);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.source);
        hash = 23 * hash + Objects.hashCode(this.destination);
        return hash;
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public boolean existsInList(List<Move> moves) {
        return moves.contains(this);
    }

    @Override
    public String toString() {
        return "Move{" + "source = y:" + source.y + ", x:" + source.x + " destination = y:" + destination.y + ", x:" + destination.x + '}';
    }
}
