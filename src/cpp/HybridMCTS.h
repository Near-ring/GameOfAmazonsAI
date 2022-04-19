#pragma once
#include <thread>
#include "BaseAIAgent.h"
#include "StateNode.h"
#include "HybridSimAgent.h"

class HybridMCTS : public BaseAIAgent {
	int hybrid_hyperparam = HPSTART;
	int max_sim_num;
	std::thread thread_pool[128];

public:
	HybridMCTS(const Matrix& board, const byte side) : BaseAIAgent(board, side) {}

	void calculate() override;
};