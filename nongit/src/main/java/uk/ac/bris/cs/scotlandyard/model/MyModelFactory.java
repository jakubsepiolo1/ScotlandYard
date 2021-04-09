package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import javafx.beans.InvalidationListener;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

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

		//very bad but it works somehow
		ImmutableSet<Piece> mrXSet = ImmutableSet.of(mrX.piece());
		Set<Piece> detectiveSet = new HashSet<>();
		for(Player d : detectives){
			detectiveSet.add(d.piece());

		}
		ImmutableSet<Piece> collectSet = detectiveSet.stream().collect(ImmutableSet.toImmutableSet());
		ImmutableSet<Piece> finalSet = ImmutableSet.<Piece>builder().addAll(mrXSet).addAll(collectSet).build();

		List<Observer> observers = new ArrayList<>();
		Board.GameState gameState = new Board.GameState(setup, finalSet, ImmutableList.of(), mrX, detectives);


		return new GameModel(gameState, observers);
	}



	public final class GameModel implements Model {
		private Board.GameState state;
		private List<Observer> observers;

		private GameModel(Board.GameState state, final List<Observer> observers) {
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
			if (!getObservers().contains(0)) {throw new IllegalArgumentException();}
			observers.remove(o);
		}

		public ImmutableSet<Observer> getObservers() {
			ImmutableSet<Observer> x = ImmutableSet.copyOf(observers);
			return x;
		}

		public void chooseMove(Move move) {
			state.advance(move);
			if (state.getWinner().isEmpty()) {
				//notify Event.MOVE_MADE
			}
			else {
				//notify Event.GAME_OVER
			}
		}

	}


	private final class Observer implements Model.Observer {
		private Observer x;

		Observer(Observer x) {
			this.x = x;
		}

		 public void onModelChanged(Board board, Event event) {
			System.out.println(event);
		}
	}
}


