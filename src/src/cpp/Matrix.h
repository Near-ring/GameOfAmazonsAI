#pragma once
#include "types.h"
#include <vector>

#include "memPool.h"
constexpr int row_len = 10, col_len = 10;
constexpr int msize = row_len * col_len;

class Matrix
{
	byte* mat;
	bool usefree;
public:
	Matrix() : mat((byte*)malloc(msize * sizeof(byte))) , usefree(true){}

	Matrix(const Matrix& cm) : mat((byte*)malloc(msize * sizeof(byte)))
	{
		usefree = true;
		for (int i = 0; i < msize; i++)
		{
			mat[i] = cm.mat[i];
		}
	}

	Matrix(const Matrix& cm, memPool& mPool)
	{
		usefree = false;
		mat = mPool.memloc(msize * sizeof(byte));
		for (int i = 0; i < msize; i++)
		{
			mat[i] = cm.mat[i];
		}
	}

	byte operator() (int x, int y) const
	{
		return mat[row_len * y + x];
	}

	void operator() (int x, int y, byte override)
	{
		mat[row_len * y + x] = override;
	}

	Matrix& operator= (const Matrix& cm)
	{
		for (int i = 0; i < msize; i++)
		{
			mat[i] = cm.mat[i];
		}
		return *this;
	}
	~Matrix()
	{
		if(usefree)
		{
			free(mat);
		}
	}
};

typedef std::vector<Matrix> MatrixArray;