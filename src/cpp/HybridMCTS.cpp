#include "HybridMCTS.h"
#include "HybridSimAgent.h"

void random_simulation(StateNode*, int*, byte, int, int);

void HybridMCTS::calculate()
{
	using std::thread;
	max_sim_num = -170 + 30 * hybrid_hyperparam;
	max_sim_num = min(max_sim_num, 1000);
	int maxSimValue = -INF;
	Matrix bestNode;

	// <Expansion>
	// Gets the possible states and calculates available moves for each child state
	MatrixArray childStatesArr[4];
	OrderedPairList valueArray[4];
	piecePossibleStates(childStatesArr, gameBoard, side);

	for (int i = 0; i < 4; i++)
	{
		const u64 size_j = childStatesArr[i].size();
		valueArray[i].reserve(size_j);
		for (int j = 0; j < size_j; j++) {
			OrderedPair value(0, 0);
			value.y += numPossibleMoves(childStatesArr[i].at(j), side);
			value.y -= numPossibleMoves(childStatesArr[i].at(j), enemy_side);
			value.x = j;
			valueArray[i].push_back(value);
		}
		// largest possible move state list, descending order
		OrderedPairList::sort_by_y(valueArray[i]);
	}
	// </Expansion>

	// <Selection>
	int frontnum = 12;
	for (int i = 0; i < 9; i++)
	{
		if (hybrid_hyperparam - HPSTART > (40 - i * 5))
		{
			frontnum++;
		}
	}

	MatrixArray selectedNodeArray;
	for (int i = 0; i < 4; i++)
	{
		OrderedPairList frontx;
		OrderedPairList::front(frontx, valueArray[i], frontnum);
		for (const OrderedPair& e : frontx)
		{
			selectedNodeArray.push_back(childStatesArr[i].at(e.x));
		}
	}
	const u64 arr_size = selectedNodeArray.size();
	if (selectedNodeArray.empty()) {
		newState = gameBoard;
		return;
	}
	const int sizes = 4 * frontnum * sizeof(int);
	int* simValue = (int*)malloc(sizes * sizeof(int));
	for (int i = 0; i < sizes; i++) {
		simValue[i] = 0;
	}
	// </Selection>


	// <Simulation>
	printf("Max Sim Value = %d\n", max_sim_num);
	printf("Hybrid_MCTS is simulating possible states...\nValue: ");
	std::vector<StateNode*> gc_list;
	for (int i = 0; i < arr_size; i++)
	{
		auto* node = new StateNode(selectedNodeArray[i]);
		thread_pool[i] = thread(random_simulation, node, &(simValue[i]), side, max_sim_num, hybrid_hyperparam - HPSTART);
		gc_list.push_back(node);
	}
	for (int i = 0; i < arr_size; i++)
	{
		thread_pool[i].join();
	}
	// </Simulation>


	for (int i = 0; i < arr_size; i++) {
		if (simValue[i] > maxSimValue) {
			bestNode = selectedNodeArray[i];
			maxSimValue = simValue[i];
		}
	}
	printf("max value = %d\n", maxSimValue);
	newState = bestNode;
	free(simValue);
	for (auto& g : gc_list)
	{
		delete(g);
	}
	hybrid_hyperparam++;
}

void random_simulation(StateNode* s, int* value, byte side, int max_sim_num, int tn)
{
	clock_t start = clock();
	memPool mem_pool;
	const byte enemy_side = (side == WHITE ? BLACK : WHITE);
	HybridSimAgent shadow = HybridSimAgent(s, side, &mem_pool);
	HybridSimAgent eshadow = HybridSimAgent(s, enemy_side, &mem_pool);
	shadow.turn_num = tn;
	eshadow.turn_num = tn;
	for (int j = 0; j < max_sim_num; j++)
	{
		StateNode* currentState = s;
		while (true)
		{
			currentState->opponent = true;
			eshadow.update(currentState);
			// Selection and Expansion
			eshadow.calculate(true);
			currentState = eshadow.nextNode;
			if (eshadow.getAction().empty())
			{
				(*value)++;
				// Backpropagation
				eshadow.currentNode->backPropagation(false, 0);
				break;
			}
			currentState->opponent = false;
			shadow.update(currentState);
			shadow.calculate(false);
			currentState = shadow.nextNode;
			if (shadow.getAction().empty())
			{
				(*value)--;
				shadow.currentNode->backPropagation(false, 0);
				break;
			}
		}
	}
	printf("%d ", *value);
	for (auto* g : shadow.sna_list)
	{
		delete(g);
	}
	for (auto* g : eshadow.sna_list)
	{
		delete(g);
	}
	mem_pool.memfree();
}