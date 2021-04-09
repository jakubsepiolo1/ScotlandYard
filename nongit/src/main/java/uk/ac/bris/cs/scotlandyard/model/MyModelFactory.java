package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import javafx.beans.InvalidationListener;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.ui.model.BoardViewProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.*;
import java.beans.*;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model>  {

	@Nonnull @Override public GameModel build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {


		List<Model.Observer> observers = new ArrayList<Model.Observer>();
		GameState state = new MyGameStateFactory().build(setup, mrX, detectives);


		return new GameModel(state, observers);
	}



	public final class GameModel implements Model {
		private Board.GameState state;
		private List<Observer> observers;



		private GameModel(Board.GameState state, List<Observer> observers) {
			this.state = state;
			this.observers = observers;
		}




		public Board getCurrentBoard() {
			return getCurrentBoard();

		}

		public void registerObserver(Observer o) {
			if (o == null) {throw new NullPointerException();}
			if (getObservers().contains(o)) {throw new IllegalArgumentException();}
			observers.add(o);
		}

		public void unregisterObserver(Observer o) {
			if (o == null) {throw new NullPointerException();}
			if (!getObservers().contains(o)) {throw new IllegalArgumentException();}
			observers.remove(o);
		}

		public ImmutableSet<Observer> getObservers() {
			ImmutableSet<Observer> x = ImmutableSet.copyOf(observers);
			return x;
		}

		public void chooseMove(Move move) {
			state.advance(move);
			if (state.getWinner().isEmpty()) {
				for (Observer o : observers) {
					o.onModelChanged(getCurrentBoard(), Observer.Event.MOVE_MADE);
				}
			}
			else {
				for (Observer o : observers) {
					o.onModelChanged(getCurrentBoard(), Observer.Event.GAME_OVER);
				}
			}
		}

	}


	private final class Observer implements Model.Observer {

		 public void onModelChanged(Board board, Event event) {
			System.out.println(event);
		}
	}
}


