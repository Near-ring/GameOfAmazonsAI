#include "Operations.h"
#include <cstdio>
#include <WinSock2.h>

void valueLoc(OrderedPairList& availablePieces, const Matrix& board, byte player)
{
	for (int x = 0; x < BOARD_SIZE; x++)
	{
		for (int y = 0; y < BOARD_SIZE; y++)
		{
			if (board(x, y) == player)
			{
				availablePieces.push_back(OrderedPair(x, y));
			}
		}
	}
}

void playerPiecesLoc(OrderedPair* availablePieces, const Matrix& board, byte player)
{
	int i = 0;
	for (int j = 0; j < 10; j++)
	{
		for (int k = 0; k < 10; k++)
		{
			if (board(j, k) == player)
			{
				availablePieces[i].x = j;
				availablePieces[i].y = k;
				++i;
			}
		}
	}
}

void getValidMoves(OrderedPairList& validMoves, const Matrix& board, const OrderedPair& piece)
{
	const int x = piece.x;
	const int y = piece.y;
	int i;
	for (i = x - 1; i > -1 && !board(i, y); --i) {
		validMoves.push_back(OrderedPair(i, y));
	}

	for (i = x + 1; i < BOARD_SIZE && !board(i, y); ++i) {
		validMoves.push_back(OrderedPair(i, y));
	}

	for (i = y + 1; i < BOARD_SIZE && !board(x, i); ++i) {
		validMoves.push_back(OrderedPair(x, i));
	}

	for (i = y - 1; i > -1 && !board(x, i); --i) {
		validMoves.push_back(OrderedPair(x, i));
	}

	int j = y;
	for (i = x + 1; i < BOARD_SIZE && j >= 1 && !board(i, --j); ++i) {
		validMoves.push_back(OrderedPair(i, j));
	}
	j = y;
	for (i = x - 1; i > -1 && j < MAX_LEN && !board(i, ++j); --i) {
		validMoves.push_back(OrderedPair(i, j));
	}
	j = y;
	for (i = x + 1; i < BOARD_SIZE && j < MAX_LEN && !board(i, ++j); ++i) {
		validMoves.push_back(OrderedPair(i, j));
	}
	j = y;
	for (i = x - 1; i > -1 && j >= 1 && !board(i, --j); --i) {
		validMoves.push_back(OrderedPair(i, j));
	}
}


int num_piece_moves(const Matrix& board, const OrderedPair& piece)
{
	int num = 0;
	const int x = piece.x;
	const int y = piece.y;
	int i;
	for (i = x - 1; i > -1 && !board(i, y); --i) {
		num++;
	}

	for (i = x + 1; i < BOARD_SIZE && !board(i, y); ++i) {
		num++;
	}

	for (i = y + 1; i < BOARD_SIZE && !board(x, i); ++i) {
		num++;
	}

	for (i = y - 1; i > -1 && !board(x, i); --i) {
		num++;
	}

	int j = y;
	for (i = x + 1; i < BOARD_SIZE && j >= 1 && !board(i, --j); ++i) {
		num++;
	}
	j = y;
	for (i = x - 1; i > -1 && j < MAX_LEN && !board(i, ++j); --i) {
		num++;
	}
	j = y;
	for (i = x + 1; i < BOARD_SIZE && j < MAX_LEN && !board(i, ++j); ++i) {
		num++;
	}
	j = y;
	for (i = x - 1; i > -1 && j >= 1 && !board(i, --j); --i) {
		num++;
	}
	return num;
}

void forceAction(Matrix& board, const OrderedPair& src, const OrderedPair& dst, byte value) {
	if (value == MOVE) {
		board(dst.x, dst.y, board(src.x, src.y));
		board(src.x, src.y, EMPTY);
	}
	if (value == BLOCK) {
		board(dst.x, dst.y, BLOCK);
	}
}

inline void forceActionMove(Matrix& board, const OrderedPair& src, const OrderedPair& dst) {
	board(dst.x, dst.y, board(src.x, src.y));
	board(src.x, src.y, EMPTY);
}

inline void forceActionBlock(Matrix& board, const OrderedPair& src, const OrderedPair& dst) {
	board(dst.x, dst.y, BLOCK);
}

void displayBoard(const Matrix& board) {
	printf("  0 1 2 3 4 5 6 7 8 9\n");
	for (int y = 0; y < BOARD_SIZE; y++) {
		printf("%d ", y);
		for (int x = 0; x < BOARD_SIZE; x++) {
			if (board(x, y) == EMPTY) {
				printf("  ");
			}
			else if (board(x, y) == BLACK) {
				printf("B ");
			}
			else if (board(x, y) == WHITE) {
				printf("W ");
			}
			else if (board(x, y) == BLOCK) {
				printf("X ");
			}
			else {
				printf("o ");
			}
		}
		printf("\n");
	}
}

/**
 * ONLY use for TEST COPY of board
 * mark all valid moves for Coordinate p,
 * call displayBoard() after to display
 */
void markValidMoves(Matrix& board, const OrderedPair& p) {
	OrderedPairList vms = OrderedPairList();
	getValidMoves(vms, board, p);
	for (OrderedPair& c : vms) {
		board(c.x, c.y, -1);
	}
}

/**
 * Gets an array of all possible states in one turn for a player
 * of a given state(board)
 */
void getPossibleStates(MatrixArray& possible_states, const Matrix& board, byte playerSide) {
	OrderedPair available_pieces[4];
	playerPiecesLoc(available_pieces, board, playerSide);

	for (int i = 0; i < 4; i++) {
		OrderedPair& piece = available_pieces[i];
		OrderedPairList validMoves;
		getValidMoves(validMoves, board, piece);
		const u64 size_j = validMoves.size();

		for (int j = 0; j < size_j; j++) {
			OrderedPair& dst = validMoves[j];
			Matrix boardJ = board;
			forceActionMove(boardJ, piece, dst);
			OrderedPairList validBlock;
			getValidMoves(validBlock, boardJ, dst);
			const u64 size_k = validBlock.size();

			for (int k = 0; k < size_k; k++) {
				Matrix boardK = boardJ;
				forceActionBlock(boardK, dst, validBlock[k]);
				possible_states.push_back(boardK);
			}
		}
	}
}

void piecePossibleStates(MatrixArray possible_states[4], const Matrix& board, byte playerSide) {
	OrderedPair available_pieces[4];
	playerPiecesLoc(available_pieces, board, playerSide);

	for (int i = 0; i < 4; i++) {
		OrderedPair& piece = available_pieces[i];
		OrderedPairList validMoves;
		getValidMoves(validMoves, board, piece);
		const u64 size_j = validMoves.size();

		for (int j = 0; j < size_j; j++) {
			OrderedPair& dst = validMoves[j];
			Matrix boardJ = board;
			forceActionMove(boardJ, piece, dst);
			OrderedPairList validBlock;
			getValidMoves(validBlock, boardJ, dst);
			const u64 size_k = validBlock.size();

			for (int k = 0; k < size_k; k++) {
				Matrix boardK = boardJ;
				forceActionBlock(boardK, dst, validBlock[k]);
				possible_states[i].push_back(boardK);
			}
		}
	}
}




int numPossibleStates(const Matrix& board, byte playerSide) {
	int num = 0;
	OrderedPair available_pieces[4];
	playerPiecesLoc(available_pieces, board, playerSide);

	for (int i = 0; i < 4; i++) {
		OrderedPair& piece = available_pieces[i];
		OrderedPairList validMoves;
		getValidMoves(validMoves, board, piece);
		const u64 size_j = validMoves.size();

		for (int j = 0; j < size_j; j++) {
			OrderedPair& dst = validMoves[j];
			Matrix boardJ = board;
			forceActionMove(boardJ, piece, dst);
			OrderedPairList validBlock;
			getValidMoves(validBlock, boardJ, dst);
			const u64 size_k = validBlock.size();

			for (int k = 0; k < size_k; k++) {
				num++;
			}
		}
	}
	return num;
}

/**
 * number of possible moves (for pieces) for a player in next turn
 */
int numPossibleMoves(const Matrix& board, byte side) {
	int num = 0;
	OrderedPair aps[4];
	playerPiecesLoc(aps, board, side);
	for (int i = 0; i < 4; ++i) {
		num += num_piece_moves(board, aps[i]);
	}
	return num;
}

OrderedPairList parseAction(const Matrix& oldb, const Matrix& newb) {
	OrderedPairList action_array;
	OrderedPair move;
	OrderedPair destination;
	OrderedPair block;
	bool has_vm = false;
	bool is_same = true;

	for (int x = 0; x < row_len; x++) {
		for (int y = 0; y < col_len; y++) {
			if (oldb(x, y) != newb(x, y)) {
				if (newb(x, y) == BLOCK) {
					block.x = x;
					block.y = y;
					has_vm = true;
					continue;
				}
				if (newb(x, y) != EMPTY) {
					destination.x = x;
					destination.y = y;
					continue;
				}
				move.x = x;
				move.y = y;
				is_same = false;
			}
		}
	}

	if (has_vm) {
		if (is_same) {
			move = block;
		}
		action_array.push_back(move);
		action_array.push_back(destination);
		action_array.push_back(block);
	}
	return action_array;
}

int terminalState(const Matrix& board, byte player) {
	bool currentHasAvailableMoves = false;
	bool opponentHasAvailableMoves = false;
	OrderedPairList availablePieces;
	const byte opponent = (player == WHITE) ? BLACK : WHITE;

	// Check opponent
	valueLoc(availablePieces, board, opponent);
	for (OrderedPair pieceLocation : availablePieces) {
		opponentHasAvailableMoves |= (num_piece_moves(board, pieceLocation) > 0);
	}
	if (!opponentHasAvailableMoves) {
		return 1;
	}

	// Check self
	valueLoc(availablePieces, board, player);
	for (OrderedPair pieceLocation : availablePieces) {
		currentHasAvailableMoves |= (num_piece_moves(board, pieceLocation) > 0);
	}
	if (!currentHasAvailableMoves) {
		return -1;
	}
	return 0;
}

