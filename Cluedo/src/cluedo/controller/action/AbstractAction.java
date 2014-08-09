package cluedo.controller.action;

public abstract class AbstractAction implements Action{
	
	public enum ActionType {
		INITIALIZE,
		MOVE,
		SUGGESTION,
		ACCUSATION,
		ROLL,
		REFUTE,
		NOTIFY,
	}
	
	
	public static void main(String[] arg){
		System.out.println(ActionType.ACCUSATION.ordinal());
		System.out.println(ActionType.values()[ActionType.ACCUSATION.ordinal()]);
	}
}
