#pragma once
#include "Operations.h"
#include <cmath>

struct StateNodesArray;

struct StateNode {
	double gamma;
	Matrix mat;
	int depth = 0;
	double selfValue = 1;
	double enemyValue = 1;
	bool opponent = false;
	StateNode* parentState = nullptr;
	StateNodesArray* childStates = nullptr;

	StateNode(const Matrix& m) : mat(m) {}
	StateNode(const Matrix& m, memPool& mPool) : mat(m,mPool) {}
	void backPropagation(bool win, int steps);
	void destruct()
	{
		mat.~Matrix();
	}
};

inline void StateNode::backPropagation(bool win, int steps)
{

	if (!opponent) {
		if (win) {
			selfValue += 1 * pow(gamma, steps);
			enemyValue -= 1 * pow(gamma, steps);
			if (steps == 0)
			{
				selfValue = INF;
				parentState->selfValue = INF;
				parentState->enemyValue = -INF;
			}
		}
		else {
			selfValue -= 1 * pow(gamma, steps);
			enemyValue += 1 * pow(gamma, steps);
		}
	}

	if (opponent) {
		if (win) {
			selfValue -= 1 * pow(gamma, steps);
			enemyValue += 1 * pow(gamma, steps);
			if (steps == 0)
			{
				enemyValue = INF;
				parentState->selfValue = -INF;
				parentState->enemyValue = INF;
			}
		}
		else {
			enemyValue -= 1 * pow(gamma, steps);
			selfValue += 1 * pow(gamma, steps);
		}
	}
	if (selfValue < 0) {
		selfValue = 0;
	}
	if (enemyValue < 0) {
		enemyValue = 0;
	}

	if (parentState == nullptr) {
		return;
	}
	parentState->backPropagation(win, ++steps);
}


struct StateNodesArray : std::vector<StateNode*>
{
	StateNodesArray(const MatrixArray& m, memPool& m_pool)
	{
		this->reserve(m.size());
		for (const Matrix& b : m) {
			void* ptr = m_pool.memloc(sizeof(StateNode));
			auto* sn = new(ptr) StateNode(b, m_pool);
			this->push_back(sn);
		}
	}
	~StateNodesArray()
	{
		for(auto* g:*this)
		{
			g->destruct();
		}
	}
	StateNodesArray() = default;
};