#pragma once
#include <random>
#include "types.h"
#include "Matrix.h"
#include "OrderedPair.h"

constexpr byte EMPTY = 0;
constexpr byte MOVE = 0;
constexpr byte BLACK = 1;
constexpr byte WHITE = 2;
constexpr byte BLOCK = 3;
constexpr int INF = 1000000000;
constexpr int BOARD_SIZE = 10;
constexpr int MAX_LEN = BOARD_SIZE - 1;

void valueLoc(OrderedPairList&, const Matrix&, byte);
void getValidMoves(OrderedPairList&, const Matrix&, const OrderedPair&);
void forceAction(Matrix& board, const OrderedPair& src, const OrderedPair& dst, byte value);
void displayBoard(const Matrix& board);
void markValidMoves(Matrix&, const OrderedPair&);
void getPossibleStates(MatrixArray&, const Matrix&, byte);
void piecePossibleStates(MatrixArray possible_states[4], const Matrix& board, byte playerSide);
void playerPiecesLoc(OrderedPair* availablePieces, const Matrix& board, byte player);
int numPossibleMoves(const Matrix& board, byte side);
int numPossibleStates(const Matrix& board, byte playerSide);
OrderedPairList parseAction(const Matrix& oldb, const Matrix& newb);
int terminalState(const Matrix& board, byte player);

template<typename T>
T min(T a, T b) { return a < b ? a : b; }
template<typename T>
T max(T a, T b) { return a > b ? a : b; }

inline std::random_device rd;  // seed for the random number engine
inline std::mt19937 srs(rd()); // standard mersenne_twister_engine seeded with rd()
inline std::uniform_real_distribution<> uniform_0_1(0.0, 1.0);