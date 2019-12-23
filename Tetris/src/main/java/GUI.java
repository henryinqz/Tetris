import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener {
    // PROPERTIES
    JFrame theframe = new JFrame("Tetris");
    JPanel mainPanel = new JPanel();
    BoardPanel boardPanel = new BoardPanel();

    // Debug buttons
    JButton butRotateLeft = new JButton("Rotate Left");
    JButton butRotateRight = new JButton("Rotate Right");
    JButton butMoveLeft = new JButton("Move Left");
    JButton butMoveRight = new JButton("Move Right");

    Timer thetimer = new Timer(1000 / 60, this); //60FPS

    // METHODS
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == thetimer) {
            this.boardPanel.repaint();
        } else if (evt.getSource() == butRotateLeft) {
            Controller.rotate(BoardPanel.blockCurrent, "left");
        } else if (evt.getSource() == butRotateRight) {
            Controller.rotate(BoardPanel.blockCurrent, "right");
        } else if (evt.getSource() == butMoveLeft) {
            Controller.moveLeft(BoardPanel.blockCurrent);
        } else if (evt.getSource() == butMoveRight) {
            Controller.moveRight(BoardPanel.blockCurrent);
        }
    }

    // CONSTRUCTOR
    public GUI() {
        //mainPanel.setPreferredSize(new Dimension(1280, 720));
        this.boardPanel.setPreferredSize(new Dimension(BoardPanel.intXMax + 400, BoardPanel.intYMax));
        this.boardPanel.setLayout(null);
        //Block JBlock = new Block(Block.JBlock);

        //Debug buttons
        this.butRotateLeft.addActionListener(this);
        this.butRotateLeft.setSize(110,30);
        this.butRotateLeft.setLocation(BoardPanel.intXMax + 50, 50);
        this.boardPanel.add(butRotateLeft);

        this.butRotateRight.addActionListener(this);
        this.butRotateRight.setSize(110,30);
        this.butRotateRight.setLocation(BoardPanel.intXMax + 50, 90);
        this.boardPanel.add(butRotateRight);

        this.butMoveLeft.addActionListener(this);
        this.butMoveLeft.setSize(110,30);
        this.butMoveLeft.setLocation(BoardPanel.intXMax + 170, 50);
        this.boardPanel.add(butMoveLeft);

        this.butMoveRight.addActionListener(this);
        this.butMoveRight.setSize(110,30);
        this.butMoveRight.setLocation(BoardPanel.intXMax + 170, 90);
        this.boardPanel.add(butMoveRight);

        this.theframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.theframe.setContentPane(boardPanel);
        this.theframe.pack();
        this.theframe.setResizable(false);
        this.theframe.setLocationRelativeTo(null);
        this.theframe.setVisible(true);

        this.thetimer.start();

    }
}
