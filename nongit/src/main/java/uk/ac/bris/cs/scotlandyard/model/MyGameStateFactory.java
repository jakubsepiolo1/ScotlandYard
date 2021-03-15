package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import java.util.*;
import javax.annotation.Nonnull;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

import static uk.ac.bris.cs.scotlandyard.model.ScotlandYard.DETECTIVE_LOCATIONS;
import static uk.ac.bris.cs.scotlandyard.model.ScotlandYard.STANDARD24ROUNDS;


/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {


	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
			return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
			}
		private final class MyGameState implements GameState {
			private GameSetup setup;
			private ImmutableSet<Piece> remaining;
			private ImmutableList<LogEntry> log;
			private Player mrX;
			private List<Player> detectives;
			private ImmutableList<Player> everyone;
			private ImmutableSet<Move> moves;
			private ImmutableSet<Piece> winner;
			private MyGameState( final GameSetup setup,
								 final ImmutableSet<Piece> remaining,
								 final ImmutableList<LogEntry> log,
								 final Player mrX,
								 final List<Player> detectives){



				if (detectives == null) {throw new NullPointerException();}
				if (mrX == null) {throw new NullPointerException();}
				if (!(mrX.isMrX())) {throw new IllegalArgumentException();}

				for (int i=0; i < detectives.size(); i++) {
					if ((detectives.get(i)).isMrX()) {throw new IllegalArgumentException();}
				}
				String xcolour = (mrX.piece().webColour());
				if (!(xcolour.equals("#000"))) {throw new IllegalArgumentException();}

				for (int i=0; i < detectives.size()-1; i++) {
					for (int j=1; j < detectives.size(); j++) {
						if ((detectives.get(i)).equals(detectives.get(j))) {throw new IllegalArgumentException();}
						else if ((detectives.get(i).location()) == (detectives.get(j).location())) {throw new IllegalArgumentException();}
					}
				}


				for (int i=0; i < detectives.size(); i++) {
					if ((detectives.get(i)).has(Ticket.SECRET)) {throw new IllegalArgumentException();}
					else if ((detectives.get(i)).has(Ticket.DOUBLE)) {throw new IllegalArgumentException();}
				}


				if (((setup.rounds).isEmpty())) {throw new IllegalArgumentException();}
				if (setup.graph.nodes().isEmpty()) {throw new IllegalArgumentException();}































			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;



			}


			@Override public GameSetup getSetup() {
				return setup;
			}
			@Override  public ImmutableSet<Piece> getPlayers() {
				return remaining;
			}
			@Nonnull
			@Override
			public Optional<Integer> getDetectiveLocation(Detective detective) {
				for (int i=0; i < detectives.size(); i++) {
					if (((detectives.get(i)).piece()).equals(detective)) {
						return Optional.of((detectives.get(i)).location());
					}
				}
				return Optional.empty();
			}
			@Nonnull
			@Override
			public Optional<TicketBoard> getPlayerTickets(Piece piece) {
				return Optional.empty();
			}
			@Nonnull
			@Override
			public ImmutableList<LogEntry> getMrXTravelLog() {
				return log;
			}
			@Nonnull
			@Override
			public ImmutableSet<Piece> getWinner() {
				return null;
			}
			@Nonnull
			@Override
			public ImmutableSet<Move> getAvailableMoves() {
				return null;
			}
			@Override public GameState advance(Move move) {
				return null;
			}

	}


}
