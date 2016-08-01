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

import io.github.t3r1jj.checkersai.model.ai.EvaluatorConfig;
import io.github.t3r1jj.checkersai.model.Board;
import io.github.t3r1jj.checkersai.model.Game;
import io.github.t3r1jj.checkersai.model.Move;
import io.github.t3r1jj.checkersai.model.Turn;
import io.github.t3r1jj.checkersai.model.ai.Statistics;
import io.github.t3r1jj.checkersai.model.checker.RedKing;
import io.github.t3r1jj.checkersai.model.checker.RedPawn;
import io.github.t3r1jj.checkersai.model.checker.WhiteKing;
import io.github.t3r1jj.checkersai.model.checker.WhitePawn;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;

public class GUI extends JFrame implements UI {

    private static JPanel checkersBoard;
    private static Game game;
    private static Border border;
    private static Thread playGameThread;
    private static JTextArea textArea;
    private static Point source, destination;
    private static List<Move> possibleMoves;
    private static Color moveColor;
    private static final Color POSSIBLE_MOVE_COLOR = Color.ORANGE;
    private static final Color FORCED_MOVE_COLOR = Color.RED;
    private static final Color SELECTED_COLOR = Color.GREEN;
    private static final EvaluatorConfig CONFIG = new EvaluatorConfig();
    private static final JPanel GUI = new JPanel(new BorderLayout(3, 3));
    private static final JButton[][] BOARD_SQUARES = new JButton[8][8];
    private static final String COLS = "ABCDEFGH";
    private static final int BOARD_ELEMENT_SIZE = 76;
    private static final Color BOARD_BG_COLOR = Color.WHITE;
    private static final Color BOARD_FG_COLOR = Color.BLACK;
    private static final Color BOARD_TILE_COLOR = new Color(70, 55, 40);
    private static final Color BOARD_COLOR = new Color(130, 110, 80);
    private static final Image ICON = new ImageIcon(GUI.class.getResource("/io/github/t3r1jj/checkersai/images/icon.png")).getImage();

    public static Point getSource() {
        return source;
    }

    public static Point getDestination() {
        return destination;
    }

    GUI() {
        super("CheckersAI - English draughts");
        setIconImage(ICON);
        initializeGui();
        this.add(GUI);
    }

    public final void initializeGui() {
        getImages();
        GUI.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        GUI.add(tools, BorderLayout.PAGE_START);

        Action newGameAction = new AbstractAction("New") {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameOption(GUI.this);
            }
        };

        Action stepAction = new AbstractAction("Step") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!game.isStopIntervals()) {
                    game.setStopIntervals(true);
                }
                game.nextStep();
            }
        };

        Action continueAction = new AbstractAction("Continue") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.isStopIntervals()) {
                    game.setStopIntervals(false);
                }
                game.nextStep();
            }
        };

        Action resignAction = new AbstractAction("Resign / Force stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playGameThread != null) {
                    if (!game.isStopped()) {
                        game.setStopped(true);
                        if (CONFIG.getGameplay() == 'w' || CONFIG.getGameplay() == 'r') {
                            printEnd("You have lost (surrender)", null);
                        } else {
                            printEnd("Game has ended (by user action)", null);
                        }
                    }
                }
            }
        };

        Action helpAction = new AbstractAction("Help") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI, "1. Grading options can be managed using Save/Load buttons. Gameplay option is also saved.\n"
                        + "2. Game can be run in text user interface by passing an -ui=tui argument in a command line."
                        + "\n\tGrading values will be loaded from CheckersAI.properties file in the same directory or default ones will be loaded if the file does not exist.\n"
                        + "3. " + CONFIG.getFunctionConfig().substring(1) + ".\n4. The AI is not complex as it is based only on alpha-beta alghoritm and grading (no base/break moves sets).",
                        "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        Action aboutAction = new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(GUI, "CheckersAI - English draughts, v1.0.0\nAuthor: Damian Terlecki\nArtifical Inteligence - search "
                        + "strategies\nAlpha-beta alghoritm with heuristics\n31.05.2015", "Author", JOptionPane.INFORMATION_MESSAGE);
            }
        };

        tools.add(newGameAction);
        tools.addSeparator();
        tools.add(stepAction);
        tools.add(continueAction);
        tools.addSeparator();
        tools.add(resignAction);
        tools.addSeparator();
        tools.add(new FileChooser(this));
        tools.addSeparator();
        tools.add(helpAction);
        tools.addSeparator();
        tools.add(aboutAction);
        GUI.add(new JLabel(""), BorderLayout.LINE_START);

        checkersBoard = new JPanel(new GridLayout(0, 9)) {
            @Override
            public final Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                Dimension preferredSize = null;
                Component parent = getParent();
                if (parent == null) {
                    preferredSize = new Dimension(
                            (int) dimension.getWidth(), (int) dimension.getHeight());
                } else if (parent != null
                        && parent.getWidth() > dimension.getWidth()
                        && parent.getHeight() > dimension.getHeight()) {
                    preferredSize = parent.getSize();
                } else {
                    preferredSize = dimension;
                }
                int width = (int) preferredSize.getWidth();
                int height = (int) preferredSize.getHeight();
                int size = (width > height ? height : width);
                return new Dimension(size, size);
            }
        };
        checkersBoard.setBorder(new CompoundBorder(
                new EmptyBorder(8, 8, 8, 8),
                new LineBorder(Color.BLACK)
        ));

        checkersBoard.setBackground(BOARD_TILE_COLOR);
        JPanel boardConstrain = new JPanel(new GridBagLayout());
        boardConstrain.setBackground(BOARD_COLOR);
        boardConstrain.add(checkersBoard);
        GUI.add(boardConstrain);

        // Create checkers board buttons (squares)
        Insets buttonMargin = new Insets(0, 0, 0, 0);
        ImageIcon icon = new ImageIcon(
                new BufferedImage(BOARD_ELEMENT_SIZE, BOARD_ELEMENT_SIZE, BufferedImage.TYPE_INT_ARGB));
        for (int i = 0; i < BOARD_SQUARES.length; i++) {
            for (int j = 0; j < BOARD_SQUARES[i].length; j++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                b.setIcon(icon);
                if ((j % 2 == 1 && i % 2 == 1)
                        || (j % 2 == 0 && i % 2 == 0)) {
                    b.setBackground(BOARD_BG_COLOR);
                } else {
                    b.setBackground(BOARD_FG_COLOR);
                }

                BOARD_SQUARES[i][j] = b;
                if ((i % 2 == 0 && j % 2 == 1) || (i % 2 == 1 && j % 2 == 0)) {
                    BOARD_SQUARES[i][j].setName(Integer.toString(j + i * 8));
                    BOARD_SQUARES[i][j].addActionListener(new ClickAction());
                    BOARD_SQUARES[i][j].addKeyListener(new KeyListener() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                BOARD_SQUARES[source.y][source.x].setBorder(border);
                                source = null;
                                destination = null;
                            }
                            for (Move move : possibleMoves) {
                                BOARD_SQUARES[move.getDestination().y][move.getDestination().x].setBorder(border);
                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                        }

                        @Override
                        public void keyTyped(KeyEvent e) {
                        }
                    });
                }
            }
        }

        checkersBoard.add(new JLabel(""));
        for (int j = 0; j < 8; j++) {
            checkersBoard.add(withWhiteText(new JLabel(COLS.substring(j, j + 1), SwingConstants.CENTER)));
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (j) {
                    case 0:
                        checkersBoard.add(withWhiteText(new JLabel("" + (9 - (i + 1)),
                                SwingConstants.CENTER)));
                    default:
                        checkersBoard.add(BOARD_SQUARES[i][j]);
                }
            }
        }

        textArea = new JTextArea(5, 30);
        textArea.setBackground(new Color(240, 240, 240));
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane scrollableTextArea = new JScrollPane(textArea);
        int horizontalPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;

        scrollableTextArea.setHorizontalScrollBarPolicy(horizontalPolicy);
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();

        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        GUI.add(scrollableTextArea, BorderLayout.EAST);
        border = BOARD_SQUARES[0][1].getBorder();

    }

    public final JLabel withWhiteText(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        return label;
    }

    private void getImages() {
        RedPawn.loadImage(BOARD_ELEMENT_SIZE, BOARD_ELEMENT_SIZE);
        WhitePawn.loadImage(BOARD_ELEMENT_SIZE, BOARD_ELEMENT_SIZE);
        RedKing.loadImage(BOARD_ELEMENT_SIZE, BOARD_ELEMENT_SIZE);
        WhiteKing.loadImage(BOARD_ELEMENT_SIZE, BOARD_ELEMENT_SIZE);
    }

    @Override
    public final void printCheckers(Board board) {
        for (int i = 0; i < board.getHeight(); i++) {
            int j = (i % 2) == 0 ? 1 : 0;
            for (; j < board.getLength(); j++) {
                if (board.checkers[i][j] == null) {
                    BOARD_SQUARES[i][j].setIcon(new ImageIcon());
                } else {
                    BOARD_SQUARES[i][j].setIcon(board.checkers[i][j].getImage());
                }
            }
        }
    }

    @Override
    public boolean isComplex() {
        return true;
    }

    @Override
    public char getGameplay() {
        return CONFIG.getGameplay();
    }

    @Override
    public int getRedDepth() {
        return CONFIG.getRedDepth();
    }

    @Override
    public int getWhiteDepth() {
        return CONFIG.getWhiteDepth();

    }

    private class ClickAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Integer value = Integer.parseInt(((JButton) e.getSource()).getName());
            int y = (int) value / 8;
            int x = value % 8;
            clickedButton(x, y);
        }

        private void clickedButton(int x, int y) {
            if (BOARD_SQUARES[y][x].getIcon() != null) {
                if (source == null) {
                    source = new Point(x, y);
                    BOARD_SQUARES[y][x].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(SELECTED_COLOR, 2), BorderFactory.createCompoundBorder(
                            BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder())));
                    possibleMoves = game.generateForcedPlayerMoves();
                    if (possibleMoves.isEmpty()) {
                        possibleMoves = game.getBoard().checkers[y][x].generateNonForcedMoves(game.getBoard());
                        moveColor = POSSIBLE_MOVE_COLOR;
                    } else {
                        moveColor = FORCED_MOVE_COLOR;
                    }
                    for (Move move : possibleMoves) {
                        BOARD_SQUARES[move.getDestination().y][move.getDestination().x].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(moveColor, 2), BorderFactory.createCompoundBorder(
                                BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder())));
                    }
                } else if (destination == null) {
                    Point newDestination = new Point(x, y);
                    if (!newDestination.equals(source) && Math.abs(newDestination.x - source.x) <= 2 && Math.abs(newDestination.y - source.y) <= 2) {
                        destination = newDestination;
                    }
                } else {
                    BOARD_SQUARES[source.y][source.x].setBorder(border);
                    for (Move move : possibleMoves) {
                        BOARD_SQUARES[move.getDestination().y][move.getDestination().x].setBorder(border);
                    }
                    source = new Point(x, y);
                    BOARD_SQUARES[y][x].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(SELECTED_COLOR, 2), BorderFactory.createCompoundBorder(
                            BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder())));
                    destination = null;
                }
            }
        }
    }

    @Override
    public Move getNextMove() {
        while (source == null || destination == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                return null;
            }
        }
        for (Move move : possibleMoves) {
            BOARD_SQUARES[move.getDestination().y][move.getDestination().x].setBorder(border);
        }
        Move move = new Move(new Point(source), new Point(destination));
        BOARD_SQUARES[source.y][source.x].setBorder(border);
        source = null;
        destination = null;
        return move;
    }

    @Override
    public void printEnd(String message, Turn turn) {
        if (message == null) {
            JOptionPane.showMessageDialog(GUI, "Congratulations! " + turn
                    + " has won\nMore informations at right menu", "Game has ended", JOptionPane.INFORMATION_MESSAGE);
            printDataInfo("\n\n______________________________________________________");
            printDataInfo("\n" + turn + " has won");
        } else {
            JOptionPane.showMessageDialog(GUI, message, "Game has ended", JOptionPane.INFORMATION_MESSAGE);
            printDataInfo("\n\n______________________________________________________");
            printDataInfo("\n" + message);
        }
        switch (CONFIG.getGameplay()) {
            case 'r':
                printDataInfo("\n\nTotal number of nodes expanded by white AI:\n"
                        + Statistics.TOTAL_NODES_EXPANDED_FOR_WHITE_AI);
                printDataInfo("\n" + CONFIG.getWhiteConfig());
                printDataInfo("\n" + CONFIG.getFunctionConfig());
                break;
            case 'w':
                printDataInfo("\n\nTotal number of nodes expanded by red AI:\n"
                        + Statistics.TOTAL_NODES_EXPANDED_FOR_RED_AI);
                printDataInfo("\n" + CONFIG.getRedConfig());
                printDataInfo("\n" + CONFIG.getFunctionConfig());
                break;
            case 'a':
                printDataInfo("\n\nTotal number of nodes expanded by white AI:\n"
                        + Statistics.TOTAL_NODES_EXPANDED_FOR_WHITE_AI);
                printDataInfo("\n\nTotal number of nodes expanded by red AI:\n"
                        + Statistics.TOTAL_NODES_EXPANDED_FOR_RED_AI);
                printDataInfo("\n" + CONFIG.getWhiteConfig());
                printDataInfo("\n" + CONFIG.getRedConfig());
                printDataInfo("\n" + CONFIG.getFunctionConfig());
                break;
        }
    }

    @Override
    public void printIllegalMove(Turn turn) {
        JOptionPane.showMessageDialog(GUI, "Illegal move for " + turn + " player!", "Error - Invalid move", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void printDataInfo(String message) {
        textArea.append(message);
    }

    @Override
    public void printTurn(Turn turn) {
        textArea.append("------------------------------------------------------"
                + "----------------------------\n");
        textArea.append("\t          " + turn + "'s TURN\n");
        textArea.append("------------------------------------------------------"
                + "----------------------------\n");
    }

    @Override
    public void printMoves(List<Move> moveSeq) {
        for (Move m : moveSeq) {
            textArea.append(" " + (char) ('A' + m.getSource().x) + (8 - m.getSource().y)
                    + " -> " + (char) ('A' + m.getDestination().x) + (8 - m.getDestination().y));
            textArea.append(", ");
        }
        textArea.append("\n");
    }

    private static Component center(JComponent component) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(component);
        box.add(Box.createHorizontalGlue());
        return box;

    }

    private static class GameOption extends JDialog {

        GUI gui;

        GameOption(GUI gui) {
            super(gui);
            this.setTitle("New game");
            this.gui = gui;
            JPanel jPanel = new JPanel();
            JPanel jPanel2 = new JPanel();
            final JTextField redAI = new javax.swing.JTextField(Integer.toString(CONFIG.getRedDepth()));
            final JTextField whiteAI = new javax.swing.JTextField(Integer.toString(CONFIG.getWhiteDepth()));

            Action newAIVsAIGameAction = new AbstractAction("AI vs AI game") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CONFIG.setGameplay('a');
                    try {
                        CONFIG.setWhiteDepth(Integer.parseInt(whiteAI.getText()));
                        CONFIG.setRedDepth(Integer.parseInt(redAI.getText()));
                        if (CONFIG.getRedDepth() < 1 || CONFIG.getWhiteDepth() < 1) {
                            JOptionPane.showMessageDialog(null, "Wrong input",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        GameOption.this.dispose();
                        if (playGameThread != null) {
                            game.setStopped(true);
                            playGameThread.interrupt();
                            playGameThread.join();
                        }
                        textArea.setText("NEW GAME: AI VS AI\n");
                        game = new Game(GameOption.this.gui, CONFIG);
                        playGameThread = new PlayGame();
                        playGameThread.start();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Wrong input format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };

            Action newPlayerWhiteVsAIGameAction = new AbstractAction("Player white"
                    + " (starting) vs AI game") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CONFIG.setGameplay('w');
                    try {
                        CONFIG.setRedDepth(Integer.parseInt(redAI.getText()));
                        if (CONFIG.getRedDepth() < 1) {
                            JOptionPane.showMessageDialog(null, "Wrong input",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        GameOption.this.dispose();
                        if (playGameThread != null) {
                            game.setStopped(true);
                            playGameThread.interrupt();
                            playGameThread.join();
                        }
                        textArea.setText("NEW GAME: PLAYER WHITE (STARTING) VS AI\n");
                        game = new Game(GameOption.this.gui, CONFIG);
                        playGameThread = new PlayGame();
                        playGameThread.start();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Wrong input format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };

            Action newPlayerRedVsAIGameAction = new AbstractAction("Player red vs AI game") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CONFIG.setGameplay('r');
                    try {
                        CONFIG.setWhiteDepth(Integer.parseInt(whiteAI.getText()));
                        if (CONFIG.getWhiteDepth() < 1) {
                            JOptionPane.showMessageDialog(null, "Wrong input",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        GameOption.this.dispose();
                        if (playGameThread != null) {
                            game.setStopped(true);
                            playGameThread.interrupt();
                            playGameThread.join();
                        }
                        textArea.setText("NEW GAME: PLAYER RED VS AI\n");
                        game = new Game(GameOption.this.gui, CONFIG);
                        playGameThread = new PlayGame();
                        playGameThread.start();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Wrong input format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };

            Action moreOptionsAction = new AbstractAction("Grading values") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new AdditionalOptions(GameOption.this);
                }
            };

            JPanel tools = new JPanel();
            tools.setLayout(new BoxLayout(tools, BoxLayout.Y_AXIS));
            tools.add(center(new JButton(newAIVsAIGameAction)));
            tools.add(center(new JButton(newPlayerWhiteVsAIGameAction)));
            tools.add(center(new JButton(newPlayerRedVsAIGameAction)));
            jPanel.add(new JLabel("White AI depth"));
            whiteAI.setColumns(2);
            whiteAI.setHorizontalAlignment(JTextField.CENTER);
            jPanel.add(whiteAI);
            jPanel2.add(new JLabel("Red AI depth"));
            redAI.setColumns(2);
            redAI.setHorizontalAlignment(JTextField.CENTER);
            jPanel2.add(redAI);
            tools.add(center(jPanel));
            tools.add(center(jPanel2));
            tools.add(center(new JButton(moreOptionsAction)));
            this.add(tools);
            tools.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            this.setLocationByPlatform(true);
            this.pack();
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }

        private static class AdditionalOptions extends JDialog {

            JPanel tools = new JPanel();
            JTextField redPawn;
            JTextField redKing;
            JTextField redWon;
            JTextField redDraw;
            JLabel redPawnL = new JLabel("Red AI pawn:");
            JLabel redKingL = new JLabel("Red AI king:");
            JLabel redWonL = new JLabel("Red AI won:");
            JLabel redDrawL = new JLabel("Red AI draw:");
            JTextField whitePawn;
            JTextField whiteKing;
            JTextField whiteWon;
            JTextField whiteDraw;
            JLabel whitePawnL = new JLabel("White AI pawn:");
            JLabel whiteKingL = new JLabel("White AI king:");
            JLabel whiteWonL = new JLabel("White AI won:");
            JLabel whiteDrawL = new JLabel("White AI draw:");
            JPanel columnsContainer = new JPanel();

            public AdditionalOptions(JDialog dialog) {
                super(dialog);
                whitePawn = new JTextField(Integer.toString(CONFIG.getWhitePawn()));
                whiteKing = new JTextField(Integer.toString(CONFIG.getWhiteKing()));
                whiteWon = new JTextField(Integer.toString(CONFIG.getWhiteWin()));
                whiteDraw = new JTextField(Integer.toString(CONFIG.getWhiteDraw()));
                tools.setLayout(new BoxLayout(tools, BoxLayout.PAGE_AXIS));
                redPawn = new JTextField(Integer.toString(CONFIG.getRedPawn()));
                redKing = new JTextField(Integer.toString(CONFIG.getRedKing()));
                redWon = new JTextField(Integer.toString(CONFIG.getRedWin()));
                redDraw = new JTextField(Integer.toString(CONFIG.getRedDraw()));
                columnsContainer.setLayout(new BoxLayout(columnsContainer, BoxLayout.PAGE_AXIS));
                columnsContainer.setLayout(new BoxLayout(columnsContainer, BoxLayout.PAGE_AXIS));
                columnsContainer.setLayout(new GridLayout(8, 2));
                columnsContainer.add(whitePawnL);
                columnsContainer.add(whitePawn);
                columnsContainer.add(whiteKingL);
                columnsContainer.add(whiteKing);
                columnsContainer.add(whiteWonL);
                columnsContainer.add(whiteWon);
                columnsContainer.add(whiteDrawL);
                columnsContainer.add(whiteDraw);
                columnsContainer.add(redPawnL);
                columnsContainer.add(redPawn);
                columnsContainer.add(redKingL);
                columnsContainer.add(redKing);
                columnsContainer.add(redWonL);
                columnsContainer.add(redWon);
                columnsContainer.add(redDrawL);
                columnsContainer.add(redDraw);
                tools.add(columnsContainer);

                Action saveAction = new AbstractAction("Save") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        CONFIG.setGameplay('r');
                        try {
                            CONFIG.setWhitePawn(Integer.parseInt(whitePawn.getText()));
                            CONFIG.setWhiteKing(Integer.parseInt(whiteKing.getText()));
                            CONFIG.setWhiteWin(Integer.parseInt(whiteWon.getText()));
                            CONFIG.setWhiteDraw(Integer.parseInt(whiteDraw.getText()));
                            CONFIG.setRedPawn(Integer.parseInt(redPawn.getText()));
                            CONFIG.setRedKing(Integer.parseInt(redKing.getText()));
                            CONFIG.setRedWin(Integer.parseInt(redWon.getText()));
                            CONFIG.setRedDraw(Integer.parseInt(redDraw.getText()));
                            GUI.GameOption.AdditionalOptions.this.dispose();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Wrong input format",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                tools.add(center(new JButton(saveAction)));
                tools.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                this.setLocationByPlatform(true);
                this.add(tools);
                this.pack();
                this.setVisible(true);
            }
        }
    }

    private static class PlayGame extends Thread {

        @Override
        public void run() {
            game.PlayGame();
        }
    }

    private static class FileChooser extends JPanel implements ActionListener {

        JButton openButton, saveButton;
        JFileChooser fc;
        GUI gui;

        public FileChooser(GUI gui) {
            super(new BorderLayout());
            this.gui = gui;
            fc = new JFileChooser();
            openButton = new JButton("Load");
            openButton.addActionListener(this);
            saveButton = new JButton("Save");
            saveButton.addActionListener(this);
            add(openButton, BorderLayout.WEST);
            add(saveButton, BorderLayout.EAST);
            setOpaque(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == openButton) {
                int returnVal = fc.showOpenDialog(FileChooser.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try (FileInputStream fis = new FileInputStream(file)) {
                        CONFIG.load(fis);
                        if (playGameThread != null) {
                            game.setStopped(true);
                        }
                        game = new Game(gui, CONFIG);
                        playGameThread = new GUI.PlayGame();
                        playGameThread.start();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FileChooser.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (e.getSource() == saveButton) {
                int returnVal = fc.showSaveDialog(FileChooser.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        CONFIG.store(fos, "generated by Checkers application (game)");
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new GUI();
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent evt) {
                        onExit();
                    }
                });

                frame.setLocationByPlatform(true);
                frame.pack();
                frame.setMinimumSize(frame.getSize());
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);
            }
        }
        );
    }

    public static void onExit() {
        if (game != null && playGameThread != null) {
            game.setStopped(true);
            playGameThread.interrupt();
        }
        System.exit(0);
    }
}
