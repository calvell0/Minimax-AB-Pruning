package com.assignment;/*
 * The University of North Carolina at Charlotte
 * ITCS 3153 - Intro to Artificial Intelligence
 * 
 * Programming Assignment 2 - Adversarial Search
 * 
 * Based on code from Dilip Kumar Subramanian
 * 
 * Modified by Julio C. Bahamon
 */


import java.util.ArrayList;

public class Minimax
{
	private static String AI_AGENT = "X";
	private static String HUMAN_PLAYER = "O";
	private static int nodesTraversed = 0;
	/*
	 * We assume that MAX moves first
	 * 
	 * This method returns the move that results in the best utility.
	 * 
	 * @param com.assignment.GameState
	 * 			The current state/node from which the move is being made.
	 * 
	 */
	public static GameState miniMax(GameState state)
	{
		//	Keep track of the number of states generated/explored
		GameAI.setTotalCount(GameAI.getTotalCount() + 1);

		GameState bestMove = findMax(state);





		if (bestMove != null)
		{
			Log.debug("Selected move: ");
			Log.debug("--------------------------------------");
			GameAI.showBoardState(bestMove.getBoardState());
			Log.debug("--------------------------------------");
		}

		Log.debug("Total nodes Traversed: " + nodesTraversed);
		return bestMove;
	}

	/**
	 * max function for minimax
	 * @param state the node to be expanded
	 * @return ideal move for max player at the layer of this state
	 */
	public static GameState findMax(GameState state){
		nodesTraversed++;

		//return if current node is a leaf node
		if (GameState.isWinState(state.getBoardState()) || GameState.boardFullCheck(state.getBoardState())) {
			state.setValue(calculateUtility(state));
			return state;
		}

		double best = Double.NEGATIVE_INFINITY;
		GameState bestMove = new GameState();
		//generate next layer of possible moves
		ArrayList<GameState> nextMoves = GameState.generateSuccessors(state, AI_AGENT);

		for (GameState child : nextMoves){
			//call minimax on each node
			child.setValue(findMin(child).getValue());
			bestMove = (child.getValue() > best) ? child : bestMove;
			best = bestMove.getValue();
		}
		return bestMove;
	}


	/**
	 * min function for minimax
	 * @param state the node to be expanded
	 * @return ideal move for min player at the layer of this state
	 */
	public static GameState findMin(GameState state){
		nodesTraversed++;

		//return if current node is a leaf node
		if (GameState.isWinState(state.getBoardState()) || GameState.boardFullCheck(state.getBoardState())) {
			state.setValue(calculateUtility(state));
			return state;
		}

		double best = Double.POSITIVE_INFINITY;
		GameState bestMove = new GameState();
		//generate next layer of possible moves
		ArrayList<GameState> nextMoves = GameState.generateSuccessors(state, HUMAN_PLAYER);

		for (GameState child : nextMoves){
			//call minimax on each node
			child.setValue(findMax(child).getValue());
			bestMove = (child.getValue() < best) ? child : bestMove;
			best = bestMove.getValue();
		}
		return bestMove;
	}

	/**
	 * Calls alpha-beta minimax on the given gamestate to find the best move
	 * @param state current gamestate
	 * @return best move
	 */
	public static GameState miniMaxAB(GameState state){

		GameAI.setTotalCount(GameAI.getTotalCount() + 1);

		GameState best = findMaxAB(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

		if (best != null)
		{
			Log.debug("Selected move: ");
			Log.debug("--------------------------------------");
			GameAI.showBoardState(best.getBoardState());
			Log.debug("--------------------------------------");
		}

		Log.debug("Total nodes Traversed: " + nodesTraversed);


		return best;
	}

	/**
	 * max function for alpha-beta pruning minimax
	 * @param state the node to be expanded
	 * @param alpha highest utility found so far
	 * @param beta lowest utility found so far
	 * @return ideal move for max player at the layer of this state
	 */
	public static GameState findMaxAB(GameState state, double alpha, double beta){
		nodesTraversed++;

		//return if current node is a leaf node
		if (GameState.isWinState(state.getBoardState()) || GameState.boardFullCheck(state.getBoardState())) {
			state.setValue(calculateUtility(state));
			return state;
		}

		double best = Double.NEGATIVE_INFINITY;

		//get the next layer of possible moves
		ArrayList<GameState> nextMoves = GameState.generateSuccessors(state, AI_AGENT);
		GameState bestMove = new GameState();

		//iterate through next moves
		for (GameState child : nextMoves){
			//call minimaxAB on each node
			child.setValue(findMinAB(child, alpha, beta).getValue());
			bestMove = (child.getValue() > best) ? child : bestMove;
			best = bestMove.getValue();
			if (bestMove.getValue() >= beta) return bestMove;
			alpha = Math.max(alpha, best);
		}
		return bestMove;
	}

	/**
	 * min function for Alpha-beta pruning minimax
	 * @param state the node to be expanded
	 * @param alpha highest utility found so far
	 * @param beta lowest utility found so far
	 * @return ideal move for min player at the layer of this state
	 */
	public static GameState findMinAB(GameState state, double alpha, double beta){
		nodesTraversed++;

		//return if current node is a leaf node
		if (GameState.isWinState(state.getBoardState()) || GameState.boardFullCheck(state.getBoardState())) {
			state.setValue(calculateUtility(state));
			return state;
		}

		double best = Double.POSITIVE_INFINITY;
		//get the next layer of possible moves
		ArrayList<GameState> nextMoves = GameState.generateSuccessors(state, HUMAN_PLAYER);
		GameState bestMove = new GameState();

		//iterate through next moves
		for (GameState child : nextMoves){
			//call minimaxAB on each node
			child.setValue(findMaxAB(child, alpha, beta).getValue());
			bestMove = (child.getValue() < best) ? child : bestMove;
			best = bestMove.getValue();
			if (bestMove.getValue() <= alpha) return bestMove;
			beta = Math.min(beta, best);
		}
		return bestMove;


	}

	/*
	 * Checks the given gameState and returns the utility.
	 * Utility is calculated as follows:
	 * 		- If MAX wins, the utility is +1
	 * 		- If MIN wins, the utility is -1
	 * 		- If game is tied, the utility is 0
	 * 
	 * Makes the assumption that we are at a terminal (or leaf) node.
	 * This method should only be called on a node in which the game has ended.
	 * 
	 * @param com.assignment.GameState
	 * 			The terminal state/node that is being evaluated.
	 */
	public static int calculateUtility(GameState gameState)
	{
		//	First, check for a winner
		if (GameState.isWinState(gameState.getBoardState()))
		{
			if (GameState.checkWinner(gameState.getBoardState(), AI_AGENT))
			{
//				//	Debug code. Enable/disable as needed
//				com.assignment.Log.debug("Leaf node - MAX wins:");
//				com.assignment.GameAI.showBoardState(gameState.getBoardState());

				//	MAX wins
				return 1;
			}
			else
			{
//				//	Debug code. Enable/disable as needed
//				com.assignment.Log.debug("Leaf node - MIN wins:");
//				com.assignment.GameAI.showBoardState(gameState.getBoardState());

				// MIN wins
				return -1;
			}
		}
		else
		{
			//	Assuming that the board is full, this is a tie.
			return 0;
		}
	}
}