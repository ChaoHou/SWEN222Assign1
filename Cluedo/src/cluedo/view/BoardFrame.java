package cluedo.view;

import cluedo.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class BoardFrame extends JFrame {
    private final BoardCanvas canvas;
    private boolean rollDisabled;
    private boolean actionDisabled;
    private boolean refuteDisabled;

    public BoardFrame(String title, Board game, MouseListener mouse, ActionListener action) {
        super(title);

        canvas = new BoardCanvas(/* uid,*/game);
        //Master doesn't have listener
            canvas.addMouseListener(mouse);

        //does this line necessary?
        setLayout(new BorderLayout());

        add(canvas, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create JButtons and add ActionListener received
        JButton ac = new JButton("ACCUSATION");
        JButton as = new JButton("ASSUMPTION");
        ac.addActionListener(action);
        as.addActionListener(action);

        //create combo boxes
        String[] charas = {"SCARLETT","MUSTARD","WHITE","GREEN","PEACOCK","PLUM"};
        String[] weapons = {"CANDLESTICK","DAGGER","LEAD PIPE","REVOLVER","ROPE","SPANNER"};
        String[] rooms = {"KITCHEN","BALL_ROOM","CONSERVATORY","DINING_ROOM","BILLIARD_ROOM","LIBRARY","LOUNGE","HALL","STUDY"};
        JComboBox chara = new JComboBox(charas);
        JComboBox weapon = new JComboBox(weapons);
        JComboBox room = new JComboBox(rooms);

        //create panel for buttons and combo box
        JPanel p = new JPanel();
        p.add(chara);
        p.add(weapon);
        p.add(room);
        p.add(ac);
        p.add(as);
        add(p,BorderLayout.SOUTH);

        //pack components nicely
        pack();
        //we don't want user to change window size
        setResizable(false);

        //set UI visible
        setVisible(true);
    }

    /**
     *  disable the roll button
     */
    public void disableRoll() {

    }

    public void enableRoll() {

    }

    /**
     * disabled the ability to make announcement or accusation. For example disable buttons.
     */
    public void disableAction() {

    }

    public void enableAction() {

    }

    /**
     * disable the ability to select a card to refute.
     *
     */
    public void disableRefute() {

    }

    public void enableRefute() {

    }

    public void initPlayer() {

    }

    public void showMessage(String str) {
        JOptionPane.showMessageDialog(this, str, "Listen!", JOptionPane.PLAIN_MESSAGE);
    }

    public void repaint() {
        canvas.repaint();
    }


}
