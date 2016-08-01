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

import java.util.Properties;

public class EvaluatorConfig extends Properties {

    public EvaluatorConfig() {
        setGameplay('a');
        setWhiteWin(100000);
        setWhiteDraw(0);
        setWhiteKing(2000);
        setWhitePawn(1000);
        setWhiteDepth(5);
        setRedWin(100000);
        setRedDraw(0);
        setRedKing(2000);
        setRedPawn(1000);
        setRedDepth(3);
    }

    public String getWhiteConfig() {
        return "\nWhite AI grading system: \nPAWN=" + getWhitePawn()
                + ", KING=" + getWhiteKing() + ", WIN=" + getWhiteWin()
                + ", DRAW=" + getWhiteDraw() + "\nWhite's max depth search (alpha-beta)="
                + getWhiteDepth() + "";
    }

    public String getRedConfig() {
        return "\nRed AI grading system: \nPAWN=" + getRedPawn()
                + ", KING=" + getRedKing() + ", WIN=" + getRedWin()
                + ", DRAW=" + getRedDraw() + "\nRed's max depth search (alpha-beta)="
                + getRedDepth() + "";
    }

    public String getFunctionConfig() {
        return "\nGrading function: difference in points between 2 players divided by"
                + " number of checkers left for enemy player";
    }

    public char getGameplay() {
        return getProperty("gameplay").charAt(0);
    }

    public final void setGameplay(char decision) {
        setProperty("gameplay", Character.toString(decision));
    }

    public int getWhiteWin() {
        return Integer.parseInt(getProperty("white_win"));
    }

    public final void setWhiteWin(int whiteWin) {
        setProperty("white_win", String.valueOf(whiteWin));
    }

    public int getWhiteKing() {
        return Integer.parseInt(getProperty("white_king"));
    }

    public final void setWhiteKing(int whiteKing) {
        setProperty("white_king", String.valueOf(whiteKing));
    }

    public int getWhitePawn() {
        return Integer.parseInt(getProperty("white_pawn"));
    }

    public final void setWhitePawn(int whitePawn) {
        setProperty("white_pawn", String.valueOf(whitePawn));
    }

    public int getWhiteDraw() {
        return Integer.parseInt(getProperty("white_draw"));
    }

    public final void setWhiteDraw(int whiteDraw) {
        setProperty("white_draw", String.valueOf(whiteDraw));
    }

    public int getRedWin() {
        return Integer.parseInt(getProperty("red_win"));
    }

    public final void setRedWin(int redWin) {
        setProperty("red_win", String.valueOf(redWin));
    }

    public int getRedKing() {
        return Integer.parseInt(getProperty("red_king"));
    }

    public final void setRedKing(int redKing) {
        setProperty("red_king", String.valueOf(redKing));
    }

    public int getRedPawn() {
        return Integer.parseInt(getProperty("red_pawn"));
    }

    public final void setRedPawn(int redPawn) {
        setProperty("red_pawn", String.valueOf(redPawn));
    }

    public int getRedDraw() {
        return Integer.parseInt(getProperty("red_draw"));
    }

    public final void setRedDraw(int redDraw) {
        setProperty("red_draw", String.valueOf(redDraw));
    }

    public int getRedDepth() {
        return Integer.parseInt(getProperty("red_depth"));
    }

    public final void setRedDepth(int redDepth) {
        setProperty("red_depth", String.valueOf(redDepth));
    }

    public int getWhiteDepth() {
        return Integer.parseInt(getProperty("white_depth"));
    }

    public final void setWhiteDepth(int whiteDepth) {
        setProperty("white_depth", String.valueOf(whiteDepth));
    }

}
