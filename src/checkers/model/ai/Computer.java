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
package checkers.model.ai;

import checkers.model.Board;
import checkers.model.Move;
import checkers.model.Turn;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class Computer {

    private final Turn owner;
    private final Evaluator evaluator;
    private final Board board;
    private final int maxDepth;

    public Computer(Board board, int maxDepth, Turn owner) {
        this.board = board;
        this.maxDepth = maxDepth;
        this.owner = owner;
        evaluator = new Evaluator(board.getConfig());
        evaluator.setOwner(owner);
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public int makeNextWhiteMoves(List<Move> resultantMoveSeq) {
        int predictedScore;
        if (owner.equals(Turn.WHITE)) {
            predictedScore = alphaBetaWhite(board, Turn.WHITE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);
        } else {
            predictedScore = alphaBetaRed(board, Turn.WHITE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);
        }

        for (Move m : resultantMoveSeq) {
            board.genericWhiteMove(m);
        }
        Statistics.MOVES_COUNT++;
        return predictedScore;
    }

    public int makeNextRedMoves(List<Move> resultantMoveSeq) {
        int predictedScore;
        if (owner.equals(Turn.WHITE)) {
            predictedScore = alphaBetaWhite(board, Turn.RED, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);
        } else {
            predictedScore = alphaBetaRed(board, Turn.RED, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, resultantMoveSeq);
        }

        for (Move m : resultantMoveSeq) {
            board.genericRedMove(m);
        }
        Statistics.MOVES_COUNT++;
        return predictedScore;
    }

//01 function alphabeta(node, depth, α, β, maximizingPlayer)
//02      if depth = 0 or node is a terminal node
//03          return the heuristic value of node
//04      if maximizingPlayer
//05          v := -∞
//06          for each child of node
//07              v := max(v, alphabeta(child, depth - 1, α, β, FALSE))
//08              α := max(α, v)
//09              if β ≤ α
//10                  break (* β cut-off *)
//11          return v
//12      else
//13          v := ∞
//14          for each child of node
//15              v := min(v, alphabeta(child, depth - 1, α, β, TRUE))
//16              β := min(β, v)
//17              if β ≤ α
//18                  break (* α cut-off *)
//19          return v
    // White max
    private int alphaBetaWhite(Board board, Turn player, int depth, int alpha, int beta, List<Move> resultMoveSeq) {
        Statistics.TOTAL_NODES_EXPANDED_FOR_WHITE_AI = Statistics.TOTAL_NODES_EXPANDED_FOR_WHITE_AI.add(BigInteger.ONE);
        if (!canExploreFurther(board, player, depth)) {
            int value = evaluator.evaluateBoard(board, player);
            return value;
        }

        List<LinkedList<Move>> possibleMoveSeq = expandMoves(board, player);
        List<Board> possibleBoardConf = getPossibleBoardConf(board, possibleMoveSeq, player);
        List<Move> bestMoveSeq = null;

        if (player == Turn.WHITE) {
            for (int i = 0; i < possibleBoardConf.size(); i++) {
                Board b = possibleBoardConf.get(i);
                List<Move> moveSeq = possibleMoveSeq.get(i);

                int value = alphaBetaWhite(b, Turn.RED, depth + 1, alpha, beta, resultMoveSeq);

                if (value > alpha) {
                    alpha = value;
                    bestMoveSeq = moveSeq;
                }
                if (alpha > beta) {
                    break;
                }
            }
            if (depth == 0 && bestMoveSeq != null) {
                resultMoveSeq.addAll(bestMoveSeq);
            }

            return alpha;

        } else { //Red's turn
            for (int i = 0; i < possibleBoardConf.size(); i++) {
                Board b = possibleBoardConf.get(i);
                List<Move> moveSeq = possibleMoveSeq.get(i);

                int value = alphaBetaWhite(b, Turn.WHITE, depth + 1, alpha, beta, resultMoveSeq);
                if (value < beta) {
                    bestMoveSeq = moveSeq;
                    beta = value;
                }
                if (alpha > beta) {
                    break;
                }
            }
            if (depth == 0 && bestMoveSeq != null) {
                resultMoveSeq.addAll(bestMoveSeq);
            }

            return beta;
        }
    }

    private int alphaBetaRed(Board board, Turn player, int depth, int alpha, int beta, List<Move> resultMoveSeq) {
        Statistics.TOTAL_NODES_EXPANDED_FOR_RED_AI = Statistics.TOTAL_NODES_EXPANDED_FOR_RED_AI.add(BigInteger.ONE);
        if (!canExploreFurther(board, player, depth)) {
            int value = evaluator.evaluateBoard(board, player);
            return value;
        }

        List<LinkedList<Move>> possibleMoveSeq = expandMoves(board, player);
        List<Board> possibleBoardConf = getPossibleBoardConf(board, possibleMoveSeq, player);
        List<Move> bestMoveSeq = null;

        if (player == Turn.RED) {
            for (int i = 0; i < possibleBoardConf.size(); i++) {
                Board b = possibleBoardConf.get(i);
                List<Move> moveSeq = possibleMoveSeq.get(i);

                int value = alphaBetaRed(b, Turn.WHITE, depth + 1, alpha, beta, resultMoveSeq);

                if (value > alpha) {
                    alpha = value;
                    bestMoveSeq = moveSeq;
                }
                if (alpha > beta) {
                    break;
                }
            }
            if (depth == 0 && bestMoveSeq != null) {
                resultMoveSeq.addAll(bestMoveSeq);
            }

            return alpha;

        } else {// White turn
            for (int i = 0; i < possibleBoardConf.size(); i++) {
                Board b = possibleBoardConf.get(i);
                List<Move> moveSeq = possibleMoveSeq.get(i);

                int value = alphaBetaRed(b, Turn.RED, depth + 1, alpha, beta, resultMoveSeq);

                if (value < beta) {
                    bestMoveSeq = moveSeq;
                    beta = value;
                }
                if (alpha > beta) {
                    break;
                }
            }
            if (depth == 0 && bestMoveSeq != null) {
                resultMoveSeq.addAll(bestMoveSeq);
            }

            return beta;
        }
    }

    public List<LinkedList<Move>> expandMoves(Board board, Turn player) {

        List<LinkedList<Move>> outerList = new LinkedList<LinkedList<Move>>();

        if (player == Turn.RED) {

            List<Move> moves = null;
            moves = board.generateForcedRedMoves();

            if (moves.isEmpty()) {
                moves = board.generateNonForcedRedMoves();

                for (Move m : moves) {
                    LinkedList<Move> innerList = new LinkedList<Move>();
                    innerList.add(m);
                    outerList.add(innerList);
                }

            } else {
                for (Move m : moves) {

                    int r = m.getDestination().y;
                    int c = m.getDestination().x;
                    List<Move> innerList = new LinkedList<Move>();

                    innerList.add(m);

                    Board boardCopy = new Board(board);
                    boardCopy.genericRedMove(m);
                    expandMoveRecursivelyForRed(boardCopy, outerList, innerList, r, c);

                    innerList.remove(m);

                }
            }

        } else if (player == Turn.WHITE) {
            List<Move> moves = null;
            moves = board.generateForcedWhiteMoves();
            if (moves.isEmpty()) {
                moves = board.generateNonForcedWhiteMoves();
                for (Move m : moves) {
                    List<Move> innerList = new LinkedList<Move>();
                    innerList.add(m);
                    outerList.add((LinkedList<Move>) innerList);
                }
            } else {
                for (Move m : moves) {
                    int r = m.getDestination().y;
                    int c = m.getDestination().x;
                    List<Move> innerList = new LinkedList<Move>();

                    innerList.add(m);

                    Board boardCopy = new Board(board);
                    boardCopy.genericWhiteMove(m);
                    expandMoveRecursivelyForWhite(boardCopy, outerList, innerList, r, c);

                    innerList.remove(m);

                }

            }
        }
        return outerList;
    }

    private void expandMoveRecursivelyForWhite(Board board, List<LinkedList<Move>> outerList, List<Move> innerList, int r, int c) {

        List<Move> forcedMoves = board.checkers[r][c].generateForcedMoves(board);

        if (forcedMoves.isEmpty()) {
            List<Move> innerCopy = (LinkedList<Move>) ((LinkedList<Move>) innerList).clone();
            outerList.add((LinkedList<Move>) innerCopy);
            return;

        } else {
            for (Move m : forcedMoves) {

                Board boardCopy = new Board(board);
                boardCopy.genericWhiteMove(m);

                innerList.add(m);
                expandMoveRecursivelyForWhite(boardCopy, outerList, innerList, m.getDestination().y, m.getDestination().x);
                innerList.remove(m);

            }
        }

    }

    private void expandMoveRecursivelyForRed(Board board, List<LinkedList<Move>> outerList, List<Move> innerList, int r, int c) {

        List<Move> forcedMoves = board.checkers[r][c].generateForcedMoves(board);

        if (forcedMoves.isEmpty()) {
            List<Move> innerCopy = (LinkedList<Move>) ((LinkedList<Move>) innerList).clone();
            outerList.add((LinkedList<Move>) innerCopy);
            return;

        } else {
            for (Move m : forcedMoves) {
                Board boardCopy = new Board(board);
                boardCopy.genericRedMove(m);

                innerList.add(m);
                expandMoveRecursivelyForRed(boardCopy, outerList, innerList, m.getDestination().y, m.getDestination().x);
                innerList.remove(m);

            }
        }
    }

    private boolean canExploreFurther(Board board, Turn player, int depth) {
        boolean res = true;
        if (board.isFinished() || board.isBlocked(player)) {
            res = false;
        }
        if (depth == maxDepth) {
            res = false;
        }
        return res;
    }

    public static List<Board> getPossibleBoardConf(Board board, List<LinkedList<Move>> possibleMoveSeq, Turn player) {
        List<Board> possibleBoardConf = new LinkedList<Board>();
        for (List<Move> moveSeq : possibleMoveSeq) {
            Board boardCopy = new Board(board);
            for (Move move : moveSeq) {
                if (player == Turn.RED) {
                    boardCopy.genericRedMove(move);
                } else {
                    boardCopy.genericWhiteMove(move);
                }
            }
            possibleBoardConf.add(boardCopy);
        }
        return possibleBoardConf;
    }
}
