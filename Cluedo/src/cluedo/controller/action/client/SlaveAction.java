package cluedo.controller.action.client;

import cluedo.controller.action.Action;
import cluedo.model.Board;
import cluedo.view.BoardFrame;

public interface SlaveAction extends Action {
	public void execute(Board game,BoardFrame frame);
}
