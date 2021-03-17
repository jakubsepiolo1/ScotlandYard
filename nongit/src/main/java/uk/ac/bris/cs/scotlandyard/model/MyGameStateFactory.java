package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.ImmutableValueGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

import static uk.ac.bris.cs.scotlandyard.model.ScotlandYard.DETECTIVE_LOCATIONS;
import static uk.ac.bris.cs.scotlandyard.model.ScotlandYard.STANDARD24ROUNDS;
import static uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Ticket.*;

/**
 * cw-model
 * Stage 1: Complete this class
 */



public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {

		//very bad but it works somehow
		ImmutableSet<Piece> test = ImmutableSet.of(mrX.piece());
		Set<Piece> test2 = new HashSet<Piece>();
		for(Player d : detectives){
			test2.add(d.piece());

		}
		ImmutableSet<Piece> test3 = test2.stream().collect(ImmutableSet.toImmutableSet());
		ImmutableSet<Piece> test4 = ImmutableSet.<Piece>builder().addAll(test).addAll(test3).build();


		return new MyGameState(setup, test4, ImmutableList.of(), mrX, detectives);

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
		private ImmutableSet<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source)
		{
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

		private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log, final Player mrX, final List<Player> detectives)
		{

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
					if(i==j){continue;} //if looping over same detective
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
			this.winner = ImmutableSet.of();
		}


		@Override
		public GameSetup getSetup() {
			return setup;
		}

		@Override
		public ImmutableSet<Piece> getPlayers() {
			return remaining;
		}

		@Nonnull
		@Override
		public Optional<Integer> getDetectiveLocation(Piece.Detective detective) {
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
			Player playerTest = mrX;
			if (piece.isMrX()){ }
			else{
				for(Player playerLoop : detectives)
					if(playerLoop.piece() == piece){
						playerTest = playerLoop;
					}
			}
			Player finalPlayer = playerTest;
			TicketBoard mytickets = new TicketBoard() {
				@Override
				public int getCount(@Nonnull Ticket ticket) {
					return finalPlayer.tickets().get(ticket);
				}
			};
			if (remaining.contains(piece)){
				return Optional.of(mytickets);
			}
			else{
				return Optional.empty();
			}

		}

		@Nonnull
		@Override
		public ImmutableList<LogEntry> getMrXTravelLog() {
			return log;
		}

		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			return winner;
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
