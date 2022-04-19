#pragma once
#include "BaseAIAgent.h"

class Greedy : public BaseAIAgent
{

public:
	Greedy(const Matrix& board, const byte side) : BaseAIAgent(board, side) {}

	void calculate() override
	{
		MatrixArray nextStates;
		getPossibleStates(nextStates, gameBoard, side);
		OrderedPairList valueList = OrderedPairList();
		const u64 size = nextStates.size();
		for (int i = 0; i < size; i++) {
			auto value = OrderedPair(0, 0);
			value.y += numPossibleMoves(nextStates[i], side);
			value.y -= numPossibleMoves(nextStates[i], enemy_side);
			value.x = i;
			valueList.push_back(value);
		}
		OrderedPairList::sort_by_y(valueList);
		OrderedPairList betterList;
		OrderedPairList::front(betterList, valueList, 10);
		if (valueList.empty()) {
			newState = gameBoard;
			return;
		}
		const int tmp = (int)(uniform_0_1(srs) * 10.0);
		newState = nextStates.at(betterList[tmp].x);
	}
};
