package cluedo.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cluedo.controller.action.ActionHelper;
import cluedo.controller.action.ActionHelper.ActionType;
import cluedo.controller.action.client.SlaveAction;
import cluedo.controller.action.server.Move;
import cluedo.controller.action.server.Move.Direction;
import cluedo.controller.action.Action;
import cluedo.controller.connection.SlaveConnection;
import cluedo.exception.IllegalRequestException;
import cluedo.model.Board;
import cluedo.model.Card;
import cluedo.model.Card.ROOM;
import cluedo.model.Player;
import cluedo.model.Room;
import cluedo.tests.MockSlave;
import cluedo.view.BoardFrame;

/**
 * Client side action handler,
 * Also implements some listeners to handle user input
 * @author C
 *
 */
public class SlaveActionHandler extends Thread implements ActionHandler,MouseListener,ActionListener,KeyListener,WindowListener{

	private SlaveConnection connection;
	private Board game;
	private int gameClock;
	private BoardFrame frame;
	private Queue<SlaveAction> actionQueue = new ConcurrentLinkedQueue<SlaveAction>();
	
	public SlaveActionHandler(SlaveConnection con,int clock){
		connection = con;
		gameClock = clock;
		
		game = new Board();
		frame = new BoardFrame("cludo",game,connection.uid(),this,this);
		frame.addKeyListener(this);
		frame.setFocusable(true);
		frame.requestFocus();
		frame.addWindowListener(this);
		
		frame.repaint();
		//enable the popup to ask user for user name and token
		//then send to server
	}
	
	public void run(){
		System.out.println("CLIENT RUNNING");
		
		
		
		while(!connection.isClosed()){
			try {
				if(!actionQueue.isEmpty()){
					SlaveAction action = actionQueue.poll();
					action.execute(game,frame);
				}
				
				
				
				Thread.sleep(gameClock);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public boolean isEmpty() {
		return actionQueue.isEmpty();
	}

	@Override
	public Action pollAction() {
		return actionQueue.poll();
	}

	@Override
	public void offerAction(Action action) {
		assert(action instanceof SlaveAction);
		actionQueue.offer((SlaveAction)action);
	}

	@Override
	public void mouseClicked(MouseEvent arg) {
		//check player's state, if needs inputs, 
		// send info to server
		//if server confirmed the action, change player's state
		System.out.println("MOUSE CLICKED");
		int x = arg.getX();
		int y = arg.getY();
		
		try {
			Player player = game.getPlayer(connection.uid());
			Player.STATUS status = player.getStatus();
			if(status.equals(Player.STATUS.ROLLING)){
				//check if user is clicking on the dice
				if(frame.clickOnDie(x, y)){
					ActionHelper.requestRoll(connection);
				}
				
				
			}else if(status.equals(Player.STATUS.REFUTING)){
				//check user is clicking on the cards 
				//or clicking on the pass
				//get the card it clicked on
				
				
				Card card = frame.clickOnHand(x, y);
				if(card != null){
					
					ActionHelper.requestRefute(connection, card);
					
				}else{
					if(frame.clickOnPass(x, y)){
						ActionHelper.requestRefute(connection, null);
					}
				}
				
			}else if(status.equals(Player.STATUS.MOVING)){
				if(frame.clickOnPass(x, y)){
					System.out.println("Request pass");
					ActionHelper.requestPassTurn(connection);
				}
				
				if(!player.canMove()) return;
				Direction direction = frame.clickOnArrow(x, y);
				System.out.println("Client request moving direction: "+direction);
				if(direction != null){
					ActionHelper.requestMove(connection, direction);
				}
				
				
			}else if(status.equals(Player.STATUS.MAKINGANNOUNCEMENT)){
				System.out.println("x:"+x+"y:"+y);
				if(frame.clickOnPass(x, y)){
					System.out.println("Request pass");
					ActionHelper.requestPassTurn(connection);
				}
			}else{
				//do nothing if not in the status
				return;
			}
			
		} catch (IllegalRequestException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		Player player = null;
		
		try {
			player = game.getPlayer(connection.uid());
		} catch (IllegalRequestException e) {
			e.printStackTrace();
		}
		assert(player != null);
		
		Player.STATUS status = player.getStatus();
		if(!(status.equals(Player.STATUS.MAKINGANNOUNCEMENT)
				|| status.equals(Player.STATUS.MOVING))){
			return;
		}
		
		//get character, room, weapon of the announcement
		String[] announcement = frame.getAnnouncement();
		
		Card.CHARACTER character = Card.CHARACTER.valueOf(announcement[0]);
		Card.WEAPON weapon = Card.WEAPON.valueOf(announcement[1]);
		Card.ROOM room = Card.ROOM.valueOf(announcement[2]);
		
		
		
		String button = arg.getActionCommand();
		
		if(button.equals("ASSUMPTION")){
			ActionHelper.requestAnnouncement(connection, ActionType.SUGGESTION, character, weapon, room);
		}else if(button.equals("ACCUSATION")){
			ActionHelper.requestAnnouncement(connection, ActionType.ACCUSATION, character, weapon, room);
		}else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void keyPressed(KeyEvent arg) {
		System.out.println("Key preessed");
		Player player = null;
		
		try {
			player = game.getPlayer(connection.uid());
		} catch (IllegalRequestException e) {
			e.printStackTrace();
		}
		assert(player != null);
		//check the state of the board, if allows to move
		if(!player.getStatus().equals(Player.STATUS.MOVING)){
			return;
		}
		if(!player.canMove()){
			return;
		}
		
		
		System.out.println("Client request Move");
		
		int keyCode = arg.getKeyCode();
		Direction dir = null;
		if(keyCode == KeyEvent.VK_UP){
			dir = Direction.UP;
		}else if(keyCode == KeyEvent.VK_DOWN){
			dir = Direction.DOWN;
		}else if(keyCode == KeyEvent.VK_LEFT){
			dir = Direction.LEFT;
		}else if(keyCode == KeyEvent.VK_RIGHT){
			dir = Direction.RIGHT;
		}else{
			return;
		}
		ActionHelper.requestMove(connection, dir);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		ActionHelper.requestDisconnect(connection.getSocket(),connection.uid());
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}
