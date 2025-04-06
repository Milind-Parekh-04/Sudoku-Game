import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.border.MatteBorder;

public class DisplayFile {
    private static final int SIZE = 9;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private int[][] board;
    private static int[][] ans_sudoku;
    private JTextField selectedCell = null;  // Track selected cell
    private int lives = 3;  // Add lives counter
    private JLabel livesLabel;  // Add lives label
    private JFrame frame;    // Make frame accessible throughout class
    private int[][] initialBoard; // Store initial board state
    private JLabel timerLabel;  // Add timer label
    private Timer gameTimer;
    private int secondsElapsed;
    private Stack<JTextField> moveHistory; // Add this field

    public DisplayFile(int[][] board) {
        this.board = board;
        this.moveHistory = new Stack<>(); // Initialize the stack
        // Create deep copy of initial board
        this.initialBoard = new int[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                this.initialBoard[i][j] = board[i][j];
            }
        }
        secondsElapsed = 0;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Sudoku Solver");
        frame.setLayout(new BorderLayout(10, 10));  // Add gaps between components

        // Create top panel with both lives and timer
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        // Lives label setup
        livesLabel = new JLabel("Lives: ❤️❤️❤️");
        livesLabel.setFont(new Font("Arial", Font.BOLD, 20));
        livesLabel.setForeground(new Color(220, 20, 60));
        livesLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Timer label setup
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setForeground(new Color(0, 100, 0));
        timerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(livesLabel);
        topPanel.add(timerLabel);

        frame.add(topPanel, BorderLayout.NORTH);
        
        // Start the timer
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            updateTimerLabel();
        });
        gameTimer.start();
        
        // Create panel for Sudoku grid
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE));
        Color tealColor = new Color(0, 128, 128);
        Color skyBlueColor = new Color(135, 206, 235);
        
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                int top = (row % 3 == 0) ? 3 : 1;
                int left = (col % 3 == 0) ? 3 : 1;
                int bottom = (row == 8) ? 3 : 1;
                int right = (col == 8) ? 3 : 1;
                cells[row][col].setBorder(new MatteBorder(top, left, bottom, right, Color.BLACK));

                // Calculate box number (0-8) and set background color
                int boxNumber = (row / 3) * 3 + (col / 3);
                if (boxNumber == 1 || boxNumber == 3 || boxNumber == 5 || boxNumber == 7) {
                    cells[row][col].setBackground(tealColor);
                } else {
                    cells[row][col].setBackground(skyBlueColor);
                }

                if (board[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(board[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setForeground(Color.WHITE);
                    cells[row][col].setFont(new Font("Arial", Font.BOLD, 24));
                } else {
                    final int r = row, c = col;
                    cells[row][col].setEditable(false);  // Disable keyboard input
                    cells[row][col].addMouseListener(new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            if (selectedCell != null) {
                                // Only restore original color if cell wasn't wrong (red)
                                if (selectedCell.getBackground() != new Color(255, 102, 102)) {
                                    int prevBox = (Integer.parseInt(selectedCell.getName()) / 9) / 3 * 3 + 
                                                (Integer.parseInt(selectedCell.getName()) % 9) / 3;
                                    selectedCell.setBackground(prevBox == 1 || prevBox == 3 || 
                                                             prevBox == 5 || prevBox == 7 ? tealColor : skyBlueColor);
                                }
                            }
                            selectedCell = cells[r][c];
                            if (selectedCell.getBackground() != new Color(255, 102, 102)) {
                                selectedCell.setBackground(new Color(176, 224, 230)); // Light blue
                            }
                        }
                    });
                    // Store position as name for later reference
                    cells[row][col].setName(String.valueOf(row * 9 + col));
                }
                gridPanel.add(cells[row][col]);
            }
        }

        // Create number buttons panel with enhanced styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        for (int i = 1; i <= 9; i++) {
            JButton btn = new JButton(String.valueOf(i));
            btn.setFont(new Font("Arial", Font.BOLD, 24));
            btn.setPreferredSize(new Dimension(60, 60));
            btn.setBackground(new Color(51, 122, 183));  // Nice blue color
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createRaisedBevelBorder());
            btn.setFocusPainted(false);
            
            // Add hover effect
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(40, 96, 144));
                }
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(51, 122, 183));
                }
            });

            final int number = i;
            btn.addActionListener(e -> {
                if (selectedCell != null && selectedCell.isEditable() == false && 
                    selectedCell.getBackground() != Color.LIGHT_GRAY) {
                    String text = String.valueOf(number);
                    selectedCell.setText(text);
                    moveHistory.push(selectedCell); // Add to move history
                    
                    // Get the position of selected cell
                    for (int r = 0; r < SIZE; r++) {
                        for (int c = 0; c < SIZE; c++) {
                            if (cells[r][c] == selectedCell) {
                                if (number == ans_sudoku[r][c]) {
                                    selectedCell.setBackground(getOriginalCellColor(r, c));
                                } else {
                                    selectedCell.setBackground(new Color(255, 102, 102)); // Clear light red
                                    lives--;
                                    updateLivesLabel();
                                    if (lives <= 0) {
                                        showGameOverDialog();
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            });

            buttonPanel.add(btn);
        }

        // Add submit button
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(new Color(240, 240, 240));
        // Create a wrapper panel for the submit and undo buttons
        JPanel submitWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        submitWrapper.setBackground(new Color(240, 240, 240));

        // Add undo button
        JButton undoButton = createStyledButton("Undo", new Color(255, 165, 0)); // Orange color
        undoButton.setPreferredSize(new Dimension(100, 60));
        undoButton.setFont(new Font("Arial", Font.BOLD, 20));
        undoButton.addActionListener(e -> undoLastMove());
        
        JButton submitButton = createStyledButton("Submit", new Color(75, 181, 67));
        submitButton.setPreferredSize(new Dimension(140, 60));
        submitButton.setFont(new Font("Arial", Font.BOLD, 20));
        submitButton.addActionListener(e -> checkSolution());

        submitWrapper.add(undoButton);
        submitWrapper.add(submitButton);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        controlPanel.add(submitWrapper, BorderLayout.EAST);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.add(gridPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 800);
        frame.setVisible(true);
    }

    private void updateTimerLabel() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    private void checkSolution() {
        boolean isComplete = true;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String value = cells[r][c].getText().trim();
                if (value.isEmpty() || Integer.parseInt(value) != ans_sudoku[r][c]) {
                    isComplete = false;
                    break;
                }
            }
            if (!isComplete) break;
        }
        if (isComplete) {
            showSuccessDialog();
        } else {
            JOptionPane.showMessageDialog(frame, 
                "Not quite right! Keep trying!", 
                "Keep Going", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showSuccessDialog() {
        gameTimer.stop(); // Stop the timer when puzzle is completed
        JDialog dialog = new JDialog(frame, "Congratulations!", true);
        dialog.setLayout(new BorderLayout(10, 10));
        // Create message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        JLabel congratsLabel = new JLabel("Congratulations! Completed successfully!");
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel timeLabel = new JLabel(String.format("Time taken: %02d:%02d", secondsElapsed/60, secondsElapsed%60));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(congratsLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        messagePanel.add(timeLabel);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // ...existing button panel code...
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton newGameButton = createStyledButton("New Game", new Color(46, 204, 113));
        JButton exitButton = createStyledButton("Exit", new Color(231, 76, 60));
        newGameButton.addActionListener(e -> {
            dialog.dispose();
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                main(new String[]{});
            });
        });
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);
        dialog.add(messagePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void updateLivesLabel() {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            hearts.append("❤️");
        }
        livesLabel.setText("Lives: " + hearts.toString());
    }

    private void resetBoard() {
        lives = 3;
        updateLivesLabel();
        secondsElapsed = 0;
        updateTimerLabel();
        gameTimer.restart();
        moveHistory.clear(); // Clear the move history when resetting
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (initialBoard[r][c] == 0) {
                    cells[r][c].setText("");
                    // Only reset background if cell wasn't wrong
                    if (cells[r][c].getBackground() != new Color(255, 102, 102)) {
                        cells[r][c].setBackground(getOriginalCellColor(r, c));
                    }
                }
            }
        }
    }

    private void showGameOverDialog() {
        gameTimer.stop(); // Stop timer when game is over
        JDialog dialog = new JDialog(frame, "Game Over", true);
        dialog.setLayout(new BorderLayout(10, 10));
        JLabel messageLabel = new JLabel("Game Over! Would you like to play again?");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton replayButton = createStyledButton("Play Again", new Color(46, 204, 113));
        JButton exitButton = createStyledButton("Exit", new Color(231, 76, 60));
        replayButton.addActionListener(e -> {
            dialog.dispose();
            resetBoard();  // Just reset the current board instead of creating new game
        });
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        buttonPanel.add(replayButton);
        buttonPanel.add(exitButton);
        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private Color getOriginalCellColor(int row, int col) {
        int boxNumber = (row / 3) * 3 + (col / 3);
        return (boxNumber == 1 || boxNumber == 3 || boxNumber == 5 || boxNumber == 7) 
               ? new Color(0, 128, 128)  // teal
               : new Color(135, 206, 235);  // sky blue
    }

    private void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            JTextField lastCell = moveHistory.pop();
            lastCell.setText("");
            lastCell.setBackground(getOriginalCellColor(
                Integer.parseInt(lastCell.getName()) / 9,
                Integer.parseInt(lastCell.getName()) % 9
            ));
        }
    }

    public static void main(String[] args) {
        questionmaker question = new questionmaker();
        int[][] sudokuPuzzle = question.main();
        // Create a deep copy of sudokuPuzzle
        int[][] question_copy = new int[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                question_copy[i][j] = sudokuPuzzle[i][j];
            }
        }
        Sudoku_backend answer = new Sudoku_backend();
        ans_sudoku = answer.main(question_copy);
        SwingUtilities.invokeLater(() -> {
            new DisplayFile(sudokuPuzzle);
        });
    }
}
