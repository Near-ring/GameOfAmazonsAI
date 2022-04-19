#pragma once
#include <vector>
#include <algorithm>
#include <cmath>
#include "types.h"

inline u64 min(u64 a, u64 b) { return a < b ? a : b; }

struct OrderedPair
{
	int x, y;

	OrderedPair(int a, int b) :x(a), y(b) {}
	OrderedPair() = default;
	OrderedPair& operator=(const OrderedPair& e) = default;

	bool operator==(const OrderedPair& p) const
	{
		return p.x == this->x && p.y == this->y;
	}
	double euclideanDist(OrderedPair p) {
		return sqrt(pow(this->x - p.x, 2) + pow(this->y - p.y, 2));
	}
	void print() const
	{
		printf("(%d,%d) ", x, y);
	}
};

struct OrderedPairList :std::vector<OrderedPair>
{
	OrderedPairList() = default;

	OrderedPairList(u64 n)
	{
		this->resize(n);
	}

	bool contains(const OrderedPair& c) const
	{
		bool contain = false;
		const u64 size = this->size();
		for (int i = 0; i < size; i++) {
			if (this->at(i) == c) {
				contain = true;
				break;
			}
		}
		return contain;
	}

	void push_all(const OrderedPairList& a)
	{
		this->reserve(a.size());
		for (const OrderedPair& b : a)
		{
			this->push_back(b);
		}
	}

	static void front(OrderedPairList& dst, const OrderedPairList& opl, u64 n)
	{
		n = min(n, opl.size());
		dst.resize(n);
		for (int i = 0; i < n; i++) {
			dst[i] = opl[i];
		}
	}

	static void sort_by_x(OrderedPairList& s)
	{
		std::sort(s.begin(), s.end(), [](OrderedPair a, OrderedPair b) {
			return a.x > b.x;
			});
	}

	static void sort_by_y(OrderedPairList& s)
	{
		std::sort(s.begin(), s.end(), [](OrderedPair a, OrderedPair b) {
			return a.y > b.y;
			});
	}
};