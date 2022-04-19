#pragma once
#include <cstdlib>
#include <vector>
#include "types.h"

struct memPool
{
	u64 curr_size;
	byte* stack_ptr;
	std::vector<byte*> initptr;

	memPool()
	{
		byte* tmp = (byte*)malloc(16000000);
		stack_ptr = tmp;
		initptr.push_back(tmp);
		curr_size = 0;
	}

	byte* memloc(u64 size)
	{
		curr_size += size;
		if (curr_size >= 16000000)
		{
			byte* tmp = (byte*)malloc(16000000);
			initptr.push_back(tmp);
			curr_size = size;
			stack_ptr = tmp + size;
			return tmp;
		}
		byte* ret_ptr = stack_ptr;
		stack_ptr += size;
		return ret_ptr;
	}

	void memfree()
	{
		for(auto& a : initptr)
		{
			free(a);
		}
		initptr.clear();
		curr_size = 0;
	}
};