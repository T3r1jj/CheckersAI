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
import java.awt.Image;
import java.util.List;
import java.awt.Point;
import javax.swing.ImageIcon;

public class WhiteKing extends WhitePawn {

    private static ImageIcon image;

    public static void loadImage(int width, int height) {
        image = new ImageIcon(new ImageIcon(WhiteKing.class.getResource("/io/github/t3r1jj/checkersai/images/whiteKing.png")).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
    
    public WhiteKing(Point point) {
        super(point);
    }

    @Override
    public List<Move> generateForcedMoves(Board board) {
        List<Move> moves = super.generateForcedMoves(board);
        Move move = downRightCapture(board);
        if (move != null) {
            moves.add(move);
        }
        move = downLeftCapture(board);
        if (move != null) {
            moves.add(move);
        }
        return moves;
    }

    @Override
    public List<Move> generateNonForcedMoves(Board board) {
        List<Move> moves = super.generateNonForcedMoves(board);
        Move move = downRight(board);
        if (move != null) {
            moves.add(move);
        }
        move = downLeft(board);
        if (move != null) {
            moves.add(move);
        }
        return moves;
    }

    public Move downLeft(Board board) {
        Move upLeft = null;
        if (this.coordinates.y < board.getHeight() - 1 && this.coordinates.x > 0
                && !board.checkers[this.coordinates.y + 1][this.coordinates.x - 1].isCollidable()) {
            upLeft = new Move(new Point(coordinates), new Point(this.coordinates.x - 1, this.coordinates.y + 1));
        }
        return upLeft;
    }

    public Move downRight(Board board) {
        Move upRight = null;
        if (this.coordinates.y < board.getHeight() - 1 && this.coordinates.x < board.getLength() - 1
                && !board.checkers[this.coordinates.y + 1][this.coordinates.x + 1].isCollidable()) {
            upRight = new Move(new Point(coordinates), new Point(this.coordinates.x + 1, this.coordinates.y + 1));
        }
        return upRight;
    }

    public Move downRightCapture(Board board) {
        Move downRightCapture = null;

        if (this.coordinates.y < board.getHeight() - 2 && this.coordinates.x < board.getLength() - 2
                && isEnemy(board.checkers[this.coordinates.y + 1][this.coordinates.x + 1])
                && !board.checkers[this.coordinates.y + 2][this.coordinates.x + 2].isCollidable()) {
            downRightCapture = new Move(new Point(coordinates), new Point(this.coordinates.x + 2, this.coordinates.y + 2));
        }
        return downRightCapture;
    }

    public Move downLeftCapture(Board board) {
        Move downLeftCapture = null;

        if (this.coordinates.y < board.getHeight() - 2 && this.coordinates.x > 1
                && isEnemy(board.checkers[this.coordinates.y + 1][this.coordinates.x - 1])
                && !board.checkers[this.coordinates.y + 2][this.coordinates.x - 2].isCollidable()) {
            downLeftCapture = new Move(new Point(coordinates), new Point(this.coordinates.x - 2, this.coordinates.y + 2));
        }
        return downLeftCapture;
    }

    @Override
    public String toString() {
        return (char) 27 + "[1;37;40m" + "K " + (char) 27 + "[0;39;49m";
    }

    @Override
    public ImageIcon getImage() {
        return image;
    }

    @Override
    public Checker clone() {
        return new WhiteKing(new Point(coordinates));
    }

    @Override
    public Checker tryPromoting(Board board) {
        return this;
    }

    @Override
    public int getValue(EvaluatorConfig config, Turn turn) {
        return (turn == WhitePawn.SIDE) ? config.getWhiteKing() : -config.getWhiteKing();
    }

}
