#include "HybridSimAgent.h"

void HybridSimAgent::calculate(bool is_opponent) {
	if (currentNode->childStates == nullptr) {
		expand();
	}
	else {
		nodeSelection(is_opponent);
	}
}

void HybridSimAgent::nodeSelection(bool opponent) {
	if (opponent) {
		double sum = 0;
		for (const StateNode* s : *currentNode->childStates) {
			sum += (*s).enemyValue;
		}
		const double c_value = sum * uniform_0_1(srs);
		sum = 0;
		StateNode* selectedState = nullptr;
		for (StateNode* s : *currentNode->childStates) {
			sum += (*s).enemyValue;
			if (sum >= c_value) {
				selectedState = s;
				break;
			}
		}
		nextNode = selectedState;
		return;
	}

	double sum = 0;
	for (const StateNode* s : *currentNode->childStates) {
		sum += (*s).selfValue;
	}
	const double c_value = sum * uniform_0_1(srs);
	sum = 0;
	StateNode* selectedState = nullptr;
	for (StateNode* s : *currentNode->childStates) {
		sum += (*s).selfValue;
		if (sum >= c_value) {
			selectedState = s;
			break;
		}
	}
	nextNode = selectedState;
}

void HybridSimAgent::expand() {
	MatrixArray nextNodes;
	const int crd = currentNode->depth;
	if (crd < 10)
	{
		int rev_size = 1500 - crd * 150;
		rev_size = max(rev_size, 550);
		nextNodes.reserve(rev_size);
	}
	else if (crd < 15)
	{
		nextNodes.reserve(250);
	}
	getPossibleStates(nextNodes, currentNode->mat, side);

	const u64 size_i = nextNodes.size();
	OrderedPairList valueList(size_i);
	for (int i = 0; i < size_i; i++) {
		valueList[i].x = i;
		valueList[i].y = 0;
		valueList[i].y += numPossibleMoves(nextNodes[i], side);
		valueList[i].y -= numPossibleMoves(nextNodes[i], enemy_side);
	}
	OrderedPairList::sort_by_y(valueList);
	OrderedPairList betterList;
	OrderedPairList::front(betterList, valueList, 32);
	if (betterList.empty()) {
		nextNode = currentNode;
		return;
	}

	MatrixArray top_mat;
	const u64 size_j = betterList.size();
	for (int i = 0; i < size_j; i++) {
		top_mat.push_back(nextNodes[betterList[i].x]);
	}

	auto* top_states = new StateNodesArray(top_mat, *mem_pool);
	sna_list.push_back(top_states);
	currentNode->childStates = top_states;

	for (StateNode* state : *(currentNode->childStates)) {
		state->parentState = currentNode;
		state->depth++;
		if (turn_num < 5)
			state->gamma = 0.80 - 0.01 * turn_num;
		else if (turn_num < 10)
			state->gamma = 0.70;
		else if (turn_num < 30)
			state->gamma = 0.66;
		else
			state->gamma = 0.63;
	}
	const int x = (int)((*top_states).size() * uniform_0_1(srs));
	nextNode = (*top_states).at(x);
}

void HybridSimAgent::update(StateNode* s) {
	currentNode = s;
}

OrderedPairList HybridSimAgent::getAction() const {
	return parseAction(currentNode->mat, nextNode->mat);
}