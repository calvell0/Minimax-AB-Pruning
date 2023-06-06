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
import java.util.Arrays;


public class GameState
{
	private int boardSize;
	private String[] boardState;
	private int value;
	
	
	//	This array contains all the possible terminal states
	//	in which a player can win the game
	//	The tiles (squares) are numbered from zero to 8,
	//	hence the layout is as follows:
	//	 
	//	  		[0] [1] [2]
	//	  		[3] [4] [5]
	//	 		[6] [7] [8]
	//	 
	private static int[][] winningStates = new int[][] { 
			{ 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, 	// horizontal wins
			{ 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, 	// vertical wins
			{ 0, 4, 8 }, { 2, 4, 6 } 				// diagonal wins
	};


	// Constructor for new Board
	public GameState(int boardSize, String[] boardState)
	{
		this.boardState = boardState;
		this.boardSize = boardSize;
	}


	public GameState()
	{
	}


	// Getters and Setters for the board
	public int getBoardSize()
	{
		return boardSize;
	}


	public void setBoardSize(int boardSize)
	{
		this.boardSize = boardSize;
	}


	public String[] getBoardState()
	{
		return boardState;
	}


	public void setBoardState(String[] boardState)
	{
		this.boardState = boardState;
	}


	public int getValue()
	{
		return value;
	}


	public void setValue(int value)
	{
		this.value = value;
	}


	/**
	 * Checks whether the given state is a terminal state and returns true
	 * if the winning player is the same as currentPlayer.
	 *
	 * @param state
	 *            	Current game state.
	 * @param currentPlayer
	 *            	The player who is making the current move.
	 * @return boolean
	 * 				Returns true if the specified player is the winner.
	 *
	 **/
	public static boolean checkWinner(String[] gameState, String currentPlayer)
	{
		boolean isWinner = false;
		
		//	Check the game state for winning combinations.
		//	There are 7 possible terminal states in which someone wins
		for (int i = 0; i <= 7; i++)
		{
			//	Compare the board state with one of the winning combinations
			if (gameState[winningStates[i][0]].equals(gameState[winningStates[i][1]])
					&& gameState[winningStates[i][1]].equals(gameState[winningStates[i][2]])
					&& !(gameState[winningStates[i][0]].toString().equals("-")))
			{	
				// 	The current board state is a win for one of the players
				//	Check whether the winning player is the same as currentPlayer
				if (gameState[winningStates[i][0]].toString().equals(currentPlayer))
				{
					isWinner = true;
					break;
				}	
			}
		}
		
		return isWinner;
	}
	
	
	/*
	 * Checks the given state to determine if it is a terminal state
	 * or leaf node in which the game has ended and one of the players
	 * has won.
	 * 
	 * @param state
	 *            A given board state
	 * @return boolean
	 * 			True if the state is a terminal state (or leaf node)
	 * 	 
	 */
	public static boolean isWinState(String[] gameState)
	{
		boolean leafNode = false;
			
		
		// 	Check the game state for winning combinations.
		//	There are 7 possible terminal states in which someone wins
		for (int i = 0; i <= 7; i++)
		{
			//	Compare the board state with one of the winning combinations
			if (gameState[winningStates[i][0]].equals(gameState[winningStates[i][1]])
					&& gameState[winningStates[i][1]].equals(gameState[winningStates[i][2]])
					&& !(gameState[winningStates[i][0]].toString().equals("-")))
			{	
				// The current board state is a win for one of the players
				leafNode = true;
				break;
			}
		}
		
		return leafNode;
	}
	

	/**
	 *
	 * Converts the boardState(represented as an array of Strings 
	 * into a String matrix of boardSize * boardSize for the 
	 * checkWinner function
	 *
	 * @param boardState
	 *            boardState which needs to be converted to matrix
	 * @return a String matrix
	 **/
	public static String[][] convertToArray(String[] boardState)
	{
		String[][] boardStateArray = new String[GameAI.BOARD_SIZE][GameAI.BOARD_SIZE];

		int next = 0;
		
		for (int i = 0; i < GameAI.BOARD_SIZE; i++)
		{
			for (int j = 0; j < GameAI.BOARD_SIZE; j++)
			{
				boardStateArray[i][j] = boardState[next++];
			}
		}
		return boardStateArray;
	}


	/**
	 *
	 * Checks whether the game board is full. Returns true if there are
	 * no more available moves (i.e., the board is full) otherwise the
	 * method returns false.
	 *
	 * @param state
	 *            Board state of the current boardState
	 * @return boolean
	 *
	 **/
	public static boolean boardFullCheck(String[] state)
	{
		for (int i = 0; i < state.length; i++)
		{
			if (state[i].equals("-"))
				return false;
		}
		return true;
	}


	/**
	 *
	 * Returns the set of next moves that are available, given the current state
	 * of the game board and the player who is making the next move (currentPlayer)
	 *
	 * @param board
	 *            current state of the game board
	 * @param currentPlayer
	 *            the player whose turn it is to move
	 * @return ArrayList of board states, i.e., the next possible states given the available moves
	 *
	 **/
	public static ArrayList<GameState> generateSuccessors(GameState board, String currentPlayer)
	{
		ArrayList<GameState> tmpBoard = new ArrayList<GameState>();
		
		Integer[] globalIndex = new Integer[GameAI.BOARD_SIZE * GameAI.BOARD_SIZE
				- countFullSquares(board.getBoardState())];
		int count = 0;
		String[] tmpState = board.getBoardState();

		int iterateValue = GameAI.BOARD_SIZE * GameAI.BOARD_SIZE - countFullSquares(board.getBoardState());
		
		for (int i = 0; i < iterateValue; i++)
		{
			int[] indices = findAvaibleIndex(tmpState, globalIndex);
			globalIndex[count++] = indices[0];
			String[] maybeState = new String[board.getBoardState().length];
			for (int s = 0; s < maybeState.length; s++)
				maybeState[s] = board.getBoardState()[s];

			maybeState[indices[0]] = currentPlayer;
			tmpState = maybeState;
			GameState newBoard = new GameState(GameAI.BOARD_SIZE, tmpState);
			tmpBoard.add(newBoard);
			tmpState = newBoard.getBoardState();
		}
		
		return tmpBoard;
	}


	/**
	 *
	 * Returns the available index for generating the new Boards
	 *
	 * @param mboardState
	 *            Board state to generate the available index
	 * @param globalI
	 *            globalIndex for which the states are already generated
	 * @return the Array of integers where the board states are yet to be generated
	 *
	 **/
	private static int[] findAvaibleIndex(String[] mboardState, Integer[] globalI)
	{
		int[] tmpBoardState = new int[mboardState.length];
		int index = 0;

		for (int i = 0; i < mboardState.length; i++)
		{
			if (mboardState[i].equals("-"))
			{
				if (!(Arrays.asList(globalI).contains(i)))
					tmpBoardState[index++] = i;

			}
		}
		return tmpBoardState;
	}


	/**
	 *
	 * Prints the given gameboard state to the console (output).
	 *
	 * @param state
	 *            current/requested boardState to be printed
	 * @return void
	 **/
	public void printBoardState(String[] state)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < state.length; i++)
		{
			sb.append(state[i]);
		}
		Log.debug("Printing the board state " + sb.toString());
	}


	/**
	 *
	 * Returns the count for the number of X's and O's already set in the given
	 * board state
	 *
	 * @param state
	 *            current/requested boardState to be printed
	 * @return count for the number of x's and o's
	 **/
	private static int countFullSquares(String[] state)
	{
		int count = 0;
		for (int i = 0; i < state.length; i++)
		{
			if (state[i] != null)
				if (state[i].equals("X") || state[i].equals("O"))
					count++;
		}
		return count;
	}
}