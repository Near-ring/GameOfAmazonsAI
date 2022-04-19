#pragma once
#include "Operations.h"

class BaseAIAgent
{
protected:
	/**
	 * WHITE or BLACK
	 */
	const byte side;
	const byte enemy_side;
	/**
	 * current game board
	 */
	Matrix gameBoard;
	/**
	 * new state(board) after AI action calculate()
	 */
	Matrix newState;

	BaseAIAgent(const Matrix& board, const byte pside)
		: side(pside), enemy_side(pside == WHITE ? BLACK : WHITE), gameBoard(board), newState(board) {}

public:

	Matrix getNewState() const
	{
		return newState;
	}

	virtual ~BaseAIAgent() = default;
	/**
	 * parse action from new state
	 * API for caller (ex. main())
	 *
	 * @return array of action coordinate in one game turn
	 */
	virtual OrderedPairList getAction() const
	{
		return parseAction(gameBoard, newState);
	}

	/**
	 * updates the gameBoard to get new game state
	 * API for caller (ex. main())
	 */
	virtual void update(Matrix board) {
		gameBoard = board;
	}

	/**
	 * overwrite this in AI to update your move and write it in newState[][]
	 */
	virtual void calculate() {
		printf("==========ERR==========\n");
		newState = gameBoard;
	}
};
