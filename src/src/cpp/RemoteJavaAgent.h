#pragma once
#include "Matrix.h"
#include "OrderedPair.h"
#include <chrono>
#include <filesystem>
#include <thread>
#include "BaseAIAgent.h"
#include "types.h"

extern bool cal_done;
extern bool rev_done;
extern int8 bdata[7];

class RemoteJavaAgent : public BaseAIAgent {
	int n = 0;
public:
	RemoteJavaAgent(Matrix board, byte side) : BaseAIAgent(board, side) {}
	void calculate() override
	{
		if (n > 0 || side == BLACK)
			write_action_remote(parseAction(newState, gameBoard));
		n++;
		const OrderedPairList action = getRemoteAction();
		if (action.empty()) return;
		newState = gameBoard;
		forceAction(newState, action[0], action[1], MOVE);
		forceAction(newState, action[1], action[2], BLOCK);
	}

	static void write_action_remote(const OrderedPairList& a)
	{
		//java_write(a);
		if (a.empty())
		{
			bdata[6] = 3;
			cal_done = true;
			return;
		}
		bdata[0] = a[0].x;
		bdata[1] = a[0].y;
		bdata[2] = a[1].x;
		bdata[3] = a[1].y;
		bdata[4] = a[2].x;
		bdata[5] = a[2].y;
		bdata[6] = 0;
		cal_done = true;
	}

	static OrderedPairList getRemoteAction() {
		while (!rev_done)
		{
			std::this_thread::sleep_for(std::chrono::milliseconds(100));
		}
		OrderedPairList ac;
		if (bdata[6] == 3)
		{
			return ac;
		}
		ac.push_back(OrderedPair(bdata[0], bdata[1]));
		ac.push_back(OrderedPair(bdata[2], bdata[3]));
		ac.push_back(OrderedPair(bdata[4], bdata[5]));
		rev_done = false;
		return ac;
	}
};