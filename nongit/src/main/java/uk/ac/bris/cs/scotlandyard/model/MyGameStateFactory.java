package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.*;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

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
			private ImmutableSet<SingleMove> makeSingleMoves(
					GameSetup setup,
					List<Player> detectives,
					Player player,
					int source){
				final var singleMoves = new ArrayList<SingleMove>();
				for(int destination : setup.graph.adjacentNodes(source)) {
					for(Player detective: detectives){
						if(detective.location() == destination){
							// need to fix this
							break;
						}


					}

					//
					for(Transport t : setup.graph.edgeValueOrDefault(source,destination,ImmutableSet.of())) {
						if(player.has(t.requiredTicket())){
							SingleMove newMove = new SingleMove(player.piece(), source, t.requiredTicket(), destination);

							singleMoves.add(newMove);
						}

					}
					// TODO consider the rules of secret moves here
					//  add moves to the destination via a secret ticket if there are any left with the player
				}
				return ImmutableSet.copyOf(singleMoves);
			}


			private MyGameState(final GameSetup setup,
								final ImmutableSet<Piece> remaining,
								final ImmutableList<LogEntry> log,
								final Player mrX,
								final List<Player> detectives) {
				if (mrX == null) {
					throw new NullPointerException();
				}
				this.setup = setup;
				this.remaining = remaining;
				this.log = log;
				this.mrX = mrX;
				this.detectives = detectives;

			}


			@Override
			public GameSetup getSetup() {
				return setup;
			}

			@Override
			public ImmutableSet<Piece> getPlayers() {
				return null;
			}

			@Nonnull
			@Override
			public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
				if(detective.isDetective()){
					for(Player detective1: detectives){
						if (detective1.piece() == detective){return Optional.of(detective1.location());}
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

			@Override
			public GameState advance(Move move) {
				return null;
			}

		}

}
