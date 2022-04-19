#pragma once
#include "StateNode.h"
#include "BaseAIAgent.h"
#include "Operations.h"
constexpr int HPSTART = 11;

class HybridSimAgent : public BaseAIAgent
{
	HybridSimAgent(StateNode* inputState, const byte sd, memPool* mp)
		: BaseAIAgent((*inputState).mat, sd),
		currentNode(inputState), mem_pool(mp){}
	friend void random_simulation(StateNode*, int*, byte, int, int);
public:
	int turn_num;
	int agentNum;
	memPool* mem_pool;

	std::vector<StateNodesArray*> sna_list;
	StateNode* currentNode = nullptr;
	StateNode* nextNode = nullptr;

	void calculate(bool is_opponent);
	void nodeSelection(bool opponent);
	void expand();
	void update(StateNode* s);
	OrderedPairList getAction() const override;
};