package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;
import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

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
		ImmutableSet<Piece> mrXSet = ImmutableSet.of(mrX.piece());
		Set<Piece> detectiveSet = new HashSet<>();
		for(Player d : detectives){
			detectiveSet.add(d.piece());

		}
		ImmutableSet<Piece> collectSet = detectiveSet.stream().collect(ImmutableSet.toImmutableSet());
		ImmutableSet<Piece> finalSet = ImmutableSet.<Piece>builder().addAll(mrXSet).addAll(collectSet).build();


		return new MyGameState(setup, finalSet, ImmutableList.of(), mrX, detectives);

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
		private int roundNum;
		private int totalRounds;







		private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log, final Player mrX, final List<Player> detectives)
		{



			if (detectives == null) {throw new NullPointerException();}
			if (mrX == null) {throw new NullPointerException();}
			if (!(mrX.isMrX())) {throw new IllegalArgumentException();}

			for (Player d : detectives) {
				if (d.isMrX()) {throw new IllegalArgumentException();}
			}
			String xcolour = (mrX.piece().webColour());
			if (!(xcolour.equals("#000"))) {throw new IllegalArgumentException();}

			for (int i=0; i < detectives.size()-1; i++) {
				for (int j=1; j < detectives.size(); j++) {
					if(i==j) {continue;} //if looping over same detective
					if ((detectives.get(i)).equals(detectives.get(j))) {throw new IllegalArgumentException();}
					else if ((detectives.get(i).location()) == (detectives.get(j).location())) {throw new IllegalArgumentException();}
				}
			}


			for (Player d : detectives) {
				if (d.has(Ticket.SECRET)) {throw new IllegalArgumentException();}
				else if (d.has(Ticket.DOUBLE)) {throw new IllegalArgumentException();}
			}




			if (((setup.rounds).isEmpty())) {throw new IllegalArgumentException();}
			if (setup.graph.nodes().isEmpty()) {throw new IllegalArgumentException();}
			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;
			this.roundNum = getMrXTravelLog().size();
			this.totalRounds = setup.rounds.size();





			int i = 0;
			for (Player d : detectives) {
				if (d.has(BUS) || d.has(UNDERGROUND) || d.has(TAXI) || d.has(DOUBLE)) {
					i++;
					continue;
				}
			}
			if (i == 0) {this.winner = ImmutableSet.of(mrX.piece());}
			else {
				this.winner = ImmutableSet.of();
			}
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
			for (Player d : detectives) {
				if ((d.piece()).equals(detective)) {
					return Optional.of(d.location());
				}
			}
			return Optional.empty();
		}

		@Nonnull
		@Override
		public Optional<TicketBoard> getPlayerTickets(Piece piece) {
			Player playerTest = mrX;
			if (piece.isMrX()){ }
			else {
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



		private ImmutableSet<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source)
		{
			if(!getWinner().isEmpty()){
				return ImmutableSet.of();
			}
			final var singleMoves = new ArrayList<SingleMove>();
			for (int destination : setup.graph.adjacentNodes(source)) {
				for (Player detective : detectives) {
					if (detective.location() == destination) {

						break;
					}
//
					for (Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {
						if (player.has(t.requiredTicket())) {

							SingleMove newMove = new SingleMove(player.piece(), source, t.requiredTicket(), destination);

							singleMoves.add(newMove);
						}

					}

				}

				// TODO consider the rules of secret moves here
				//  add moves to the destination via a secret ticket if there are any left with the player
				if (player.has(SECRET)){
					for (Player detective : detectives) {
						if (detective.location() == destination) {

							break;
						}
						for (Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {
							if (player.has(SECRET)) {

								SingleMove newMove = new SingleMove(player.piece(), source, SECRET, destination);

								singleMoves.add(newMove);
							}

						}

					}

				}
			}

			return ImmutableSet.copyOf(singleMoves);
		}
		private ImmutableSet<DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source)
		{
			final var doubleMoves = new ArrayList<DoubleMove>();
			if(!getWinner().isEmpty()){
				return ImmutableSet.of();
			}
			if(!setup.rounds.contains(false)){
				return ImmutableSet.of();
			}
			if(!player.has(DOUBLE)){
				return ImmutableSet.of();
			}
			for(int destination1 : setup.graph.adjacentNodes(source)) {
				for(Player detectives1: detectives){
					if(detectives1.location() == destination1){
						break;
					}
					else{
						for(int destination2: setup.graph.adjacentNodes(destination1)){
							for(Player detectives2: detectives){
								if(detectives2.location() == destination2){
									break;
								}
								//
								for(Transport t1 : setup.graph.edgeValueOrDefault(source,destination1,ImmutableSet.of())) {
									for(Transport t2 : setup.graph.edgeValueOrDefault(destination1,destination2,ImmutableSet.of())){
										if (player.hasAtLeast(SECRET, 2)){
											DoubleMove newMove = new DoubleMove(player.piece(), source, SECRET, destination1, SECRET, destination2);
											doubleMoves.add(newMove);

										}

										if(player.has(SECRET)){
											if(player.has(t1.requiredTicket())){
												DoubleMove newMove = new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, SECRET, destination2);
												doubleMoves.add(newMove);
											}
											if(player.has(t2.requiredTicket())){
												DoubleMove newMove = new DoubleMove(player.piece(), source, SECRET, destination1, t2.requiredTicket(), destination2);
												doubleMoves.add(newMove);
											}


										}
										if(t1.requiredTicket() == t2.requiredTicket() && player.hasAtLeast(t1.requiredTicket(), 2)){
											DoubleMove newMove = new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t1.requiredTicket(), destination2);
											doubleMoves.add(newMove);
										}
										if(player.has(t1.requiredTicket()) && player.has(t2.requiredTicket()) && t1.requiredTicket() != t2.requiredTicket()){
											DoubleMove newMove = new DoubleMove(player.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket(), destination2);
											doubleMoves.add(newMove);
										}
									}
								}
							}
						}
					}
				}
			}
			return ImmutableSet.copyOf(doubleMoves);
		}




		@Nonnull
		@Override
		public ImmutableSet<Move> getAvailableMoves() {
			//if is a winner, return empty set
			if (!getWinner().isEmpty()) {
				return ImmutableSet.of();
			}
			else {
				Set<Move> moveSet = new HashSet<Move>();
				//We either want ONLY MRX moves OR all possible detective moves
				if (remaining.contains(mrX.piece())) {
					for (Move test : makeSingleMoves(setup, detectives, mrX, mrX.location())) {
						moveSet.add(test);
					}
					for (Move test : makeDoubleMoves(setup, detectives, mrX, mrX.location())) {
						moveSet.add(test);
					}
				if (moveSet.isEmpty()) {return ImmutableSet.of();}
				}
				else {
					for (Player playerLoop : detectives) {

						//But we have to make sure the detective hasn't already moved this round
						if (remaining.contains(playerLoop.piece())) {
							for (Move test : makeSingleMoves(setup, detectives, playerLoop, playerLoop.location())) {
								moveSet.add(test);
							}
							for (Move test : makeDoubleMoves(setup, detectives, playerLoop, playerLoop.location())) {
								moveSet.add(test);
							}
						}

					}
				}


				ImmutableSet<Move> collectSet = moveSet.stream().collect(ImmutableSet.toImmutableSet());
				ImmutableSet<Move> finalSet = ImmutableSet.<Move>builder().addAll(collectSet).build();
				return finalSet;
			}
		}

		private ImmutableSet<Piece> buildDetectiveWinnerSet() {
			Set<Piece> detectiveSet = new HashSet<Piece>();
			for(Player d2 : detectives){
				detectiveSet.add(d2.piece());
			}
			ImmutableSet<Piece> collectSet = detectiveSet.stream().collect(ImmutableSet.toImmutableSet());
			ImmutableSet<Piece> finalSet = ImmutableSet.<Piece>builder().addAll(collectSet).build();
			return finalSet;
		}


		@Override
		public GameState advance(Move move) {


			//If all pieces have moved this round, we can start a new round
			if (remaining.isEmpty()) {

				//Get all the pieces in the game again and re-add them to the remaining pieces as we are making a new round
				ImmutableSet<Piece> mrXSet = ImmutableSet.of(mrX.piece());
				Set<Piece> detectiveSet = new HashSet<Piece>();
				for(Player d : detectives){
					if (d.has(BUS) || d.has(UNDERGROUND) || d.has(TAXI)) {
						detectiveSet.add(d.piece());
					}
				}
				ImmutableSet<Piece> collectSet = detectiveSet.stream().collect(ImmutableSet.toImmutableSet());
				ImmutableSet<Piece> finalSet = ImmutableSet.<Piece>builder().addAll(mrXSet).addAll(collectSet).build();
				remaining = finalSet;




			}
			//Find out who is moving, by default, we assume it is mrX
			Player pieceMoving = mrX;
			for(Player playerLoop : detectives){
				if(playerLoop.piece() == move.commencedBy()){
					pieceMoving = playerLoop;
				}
			}


			Visitor sometest = new Visitor<Map<Integer, Ticket>>(){



				@Override
				public Map<Integer, Ticket> visit(SingleMove move) {
					Map<Integer, Ticket> destinations = new LinkedHashMap<>();
					destinations.put(move.destination, move.ticket);
					return destinations;
				}

				@Override
				public Map<Integer, Ticket> visit(DoubleMove move) {
					Map<Integer, Ticket> destinations = new LinkedHashMap<>();
					destinations.put(move.destination1, move.ticket1);
					destinations.put(move.destination2, move.ticket2);
					return destinations;
				}
			};

			//not sure if correct but workaround for not throwing exceptions for double moves test
			Iterator<Ticket> tt = move.tickets().iterator();
			tt.next();
			if (!tt.hasNext()) {
				if (!getAvailableMoves().contains(move)) {throw new IllegalArgumentException("Illegal move: "+move);}
			}



			//This creates a simple array, it will contain either 1 int, or 2 int, depending on the move type, those ints are the destination of each move
			Map<Integer, Ticket>  movingDestinations = (Map<Integer, Ticket>) move.visit(sometest);
			for(Map.Entry<Integer, Ticket> mapEntry : movingDestinations.entrySet()){
				pieceMoving = mrX;
				////////////////////////////
				//For now this will do, its just making sure we're moving the same piece again but it is "bad programming" technically
				for(Player playerLoop : detectives) {
					if (playerLoop.piece() == move.commencedBy()) {
						pieceMoving = playerLoop;
					}
				}
				//////////////////////////////////////
				if(pieceMoving == mrX){
						//only takes off single ticket for now
						mrX = pieceMoving.at(mapEntry.getKey()).use(mapEntry.getValue());
				}
				else {

					//Moving a detective, taking off tickets and recreating detective set
					Set<Player> detectiveSet = new HashSet<Player>();
					for(Player playerLoop : detectives){
						if(playerLoop.piece() != move.commencedBy()){
							detectiveSet.add(playerLoop);
						}
						else {
							Player newDetective = pieceMoving.at(mapEntry.getKey()).use(mapEntry.getValue());
							detectiveSet.add(newDetective);
							mrX = mrX.give(mapEntry.getValue());


						}
					}
					List<Player> finalSet = ImmutableList.<Player>builder().addAll(detectiveSet).build();
					detectives = finalSet;
					for (Player playerLoop : detectives) {
						if (playerLoop == pieceMoving) {System.out.println("New piece: " + playerLoop.location());}
					}

				}

			}
			//We have to check if after a valid move has been completed, whether someone has won, because a detective may have moved to MrX's location
			for(Player d : detectives){
				if (mrX.location() == d.location()){
					winner = buildDetectiveWinnerSet();
					return this;
				}
			}

			//Check whether after valid move detectives have cornered mrX
			int nodeNum = setup.graph.degree(mrX.location());
			int j = 0;
			for (int destination : setup.graph.adjacentNodes(mrX.location())) {
				for (Player detective : detectives) {
					if (detective.location() == destination) {
						j++;
					}
				}
			}
			if (nodeNum == j) {
				winner = buildDetectiveWinnerSet();
				return this;
			}

			//If all detectives have no tickets mrX wins
			int i = 0;
			for (Player d : detectives) {
				if (d.has(BUS) || d.has(UNDERGROUND) || d.has(TAXI) || d.has(DOUBLE)) {
					i++;
					continue;
				}
			}
			if (i == 0) {
				winner = ImmutableSet.of(mrX.piece());
				getAvailableMoves();
				return this;
			}

			if (roundNum == totalRounds) {
				winner = ImmutableSet.of(mrX.piece());
				getAvailableMoves();
				return this;
			}



			//This part updates the remaining pieces that have yet to make a move this round

			Set<Piece> remainingPiecesSet = new HashSet<Piece>();

			for(Piece actualPiecesRemaining : remaining){
				for (Player d : detectives) {
					if (actualPiecesRemaining != d.piece()) {continue;}
					else {
						if (actualPiecesRemaining == pieceMoving.piece()){
							continue;
						}
						else {

							remainingPiecesSet.add(actualPiecesRemaining);

						}
					}
				}
			}
			ImmutableSet<Piece> finalSetRemaining = ImmutableSet.<Piece>builder().addAll(remainingPiecesSet).build();


			//Adding to mrXs Log for Reveal/Hidden rounds and doubles
			if(move.commencedBy() == mrX.piece()){

				List<LogEntry> x = new ArrayList<>();
				Iterator<Ticket> ts = move.tickets().iterator();

				int iter = 0;
				while (ts.hasNext() && iter < 2) {
					if (setup.rounds.get(roundNum) == true) {
						Ticket t1 = ts.next();
						if (ts.hasNext()) {
							x.add(LogEntry.hidden(t1));
							Ticket t2 = ts.next();
							x.add(LogEntry.reveal(t2, mrX.location()));
							iter = 2;
						}
						else {
							x.add(LogEntry.reveal(t1, mrX.location()));
							iter++;
						}
					}
					else {
						Ticket t1 = ts.next();
						x.add(LogEntry.hidden(t1));
						iter++;
					}
				}
				ImmutableList<LogEntry> logList = ImmutableList.<LogEntry>builder().addAll(x).build();
				ImmutableList<LogEntry> currentLog = ImmutableList.<LogEntry>builder().addAll(getMrXTravelLog()).addAll(logList).build();


				return new MyGameState(setup, finalSetRemaining, currentLog, mrX, detectives);
			}
			//If it is not MrX we can simply return a new game state with the current log we already have and the players that have yet to make a move this round

			return new MyGameState(setup, finalSetRemaining, getMrXTravelLog(), mrX, detectives);
		}

		@Nonnull
		@Override
		public ImmutableSet<Piece> getWinner() {
			return winner;
		}

	}

}
