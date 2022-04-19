#include "Greedy.h"
#include "HybridMCTS.h"
#include "Matrix.h"
#include "Operations.h"
#include "RemoteJavaAgent.h"
#include <WinSock2.h>
#include "memPool.h"

bool rev_done;
bool cal_done;
bool connected;
int uside;
int8 bdata[7];

class AmazonServer {

	Matrix gameBoard;

	void gameInit() {
		for (int y = 0; y < BOARD_SIZE; y++) {
			for (int x = 0; x < BOARD_SIZE; x++) {
				gameBoard(x, y, 0);
			}
		}
		gameBoard(3, 0, BLACK);
		gameBoard(0, 3, BLACK);
		gameBoard(6, 0, BLACK);
		gameBoard(9, 3, BLACK);
		gameBoard(0, 6, WHITE);
		gameBoard(3, 9, WHITE);
		gameBoard(9, 6, WHITE);
		gameBoard(6, 9, WHITE);
	}

public:
	AmazonServer() {
		gameInit();
	}

	void printBoard() const { displayBoard(gameBoard); }

	Matrix getGameBoardClone() const {
		return gameBoard;
	}

	void movePiece(OrderedPair src, OrderedPair dst, byte value) {
		OrderedPairList vms;
		getValidMoves(vms, gameBoard, src);
		if (vms.contains(dst)) {
			forceAction(gameBoard, src, dst, value);
		}
		else {
			printf("\nwarning: Illegal move, game result is now invalid, consider restart\n");
			forceAction(gameBoard, src, dst, value);
		}
	}

	void startGameLoop() {
		using namespace std;

		BaseAIAgent* player1;
		BaseAIAgent* player2;

		if (uside == WHITE)
		{
			player1 = new HybridMCTS(getGameBoardClone(), WHITE);
			player2 = new RemoteJavaAgent(getGameBoardClone(), BLACK);
			printf("We are White\n");
		}
		else
		{
			player1 = new RemoteJavaAgent(getGameBoardClone(), WHITE);
			player2 = new HybridMCTS(getGameBoardClone(), BLACK);
			printf("We are Black\n");
		}

		OrderedPairList action;

		clock_t start;
		int turnNum = 0;
		while (true) {
			// Player1
			printf("\n  Turn: %d Player 1:\n\n", turnNum);
			player1->update(getGameBoardClone());
			start = clock();
			player1->calculate();
			printf("\nTime taken: %lf\n", (clock() - start) / (double)(CLOCKS_PER_SEC / 1000));
			action = player1->getAction();
			//player2->write_action_remote(action);
			if (action.empty()) {
				printf("\nGAME END - Black wins!!!\n\n");
				break;
			}
			// Action 0 = src, Action 1 = dest
			movePiece(action.at(0), action.at(1), MOVE);
			movePiece(action.at(1), action.at(2), BLOCK);
			printBoard();

			// Player2
			printf("\n  Turn: %d Player 2:\n\n", turnNum++);
			player2->update(getGameBoardClone());
			start = clock();
			player2->calculate();
			printf("\nTime taken: %lf\n", (clock() - start) / (double)(CLOCKS_PER_SEC / 1000));
			action = player2->getAction();
			//player1->write_action_remote(action);
			if (action.empty()) {
				printf("\n\nGAME END - White wins!!!\n\n");
				break;
			}
			movePiece(action.at(0), action.at(1), MOVE);
			movePiece(action.at(1), action.at(2), BLOCK);
			printBoard();
			MatrixArray tmp;
			getPossibleStates(tmp, getGameBoardClone(), WHITE);
			//printf("\nsize of ps = %d\n", tmp.size());
		}
	}
};

void server();

int main()
{
	//printf("size = %d",sizeof(StateNode));
	// Game Main Loop:
	AmazonServer localGame = AmazonServer();
	connected = false;
	rev_done = false;
	cal_done = false;
	std::thread svr(server);
	while (!connected)
	{
		std::this_thread::sleep_for(std::chrono::milliseconds(100));
	}
	printf("Game Start\n");
	localGame.startGameLoop();
	svr.join();
	printf("\n\nPress Enter to exit...");
	getchar();
	return 0;


	//clock_t start = clock();
	//
	//printf("\nTime taken: %lf\n", (clock() - start) / (double)(CLOCKS_PER_SEC / 1000));
}

void server()
{
#pragma comment(lib, "ws2_32.lib")
	//初始化WSA  
	WORD sockVersion = MAKEWORD(2, 2);
	WSADATA wsaData;
	if (WSAStartup(sockVersion, &wsaData) != 0)
	{
		return;
	}

	//创建套接字  
	SOCKET slisten = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (slisten == INVALID_SOCKET)
	{
		printf("socket error !");
		return;
	}

	//绑定IP和端口  
	sockaddr_in sin;
	sin.sin_family = AF_INET;
	sin.sin_port = htons(15333);
	sin.sin_addr.S_un.S_addr = INADDR_ANY;
	if (bind(slisten, (LPSOCKADDR)&sin, sizeof(sin)) == SOCKET_ERROR)
	{
		printf("bind error !");
	}

	//开始监听  
	if (listen(slisten, 5) == SOCKET_ERROR)
	{
		printf("listen error !");
		return;
	}

	//循环接收数据  
	SOCKET sClient;
	sockaddr_in remoteAddr;
	int nAddrlen = sizeof(remoteAddr);
	char revData[255];
	printf("\nWelcome, C++ AI server has started on port 15333\n");
	while (true)
	{
		printf("Waiting client action...\n");
		sClient = accept(slisten, (SOCKADDR*)&remoteAddr, &nAddrlen);
		if (sClient == INVALID_SOCKET)
		{
			printf("Accept error !");
			continue;
		}

		//接收数据
		int ret = recv(sClient, revData, 255, 0);
		if (ret > 0)
		{
			revData[ret] = 0x00;
			//printf(revData);
			if (revData[6] == 0)
			{
				for (int i = 0; i < 7; i++)
				{
					//printf("%d", revData[i]);
					bdata[i] = revData[i];
				}
				rev_done = true;
				//let Java client continue
				char sendData[7] = { 0,0,0,0,0,0,0 };
				send(sClient, sendData, 7, 0);
				closesocket(sClient);
				continue;
			}
			if (revData[6] == BLACK)
			{
				uside = BLACK;
			}
			else if (revData[6] == WHITE)
			{
				uside = WHITE;
			}
			else if (revData[6] == 3)
			{
				bdata[6] = 3;
				break;
			}
			else
			{
				printf("C++ Server: received request\n");
			}
		}
		printf("\n");

		if (!connected)
		{
			printf("Connection established\n");
			char sendData[7] = { 0,0,0,0,0,0,0 };
			send(sClient, sendData, 7, 0);
			closesocket(sClient);
			connected = true;
			continue;
		}

		//发送数据  
		while (!cal_done)
		{
			std::this_thread::sleep_for(std::chrono::milliseconds(100));
		}
		send(sClient, bdata, 7, 0);
		closesocket(sClient);
		cal_done = false;
		if (bdata[6] == 3)
			break;
	}
	closesocket(sClient);
	closesocket(slisten);
	WSACleanup();
	rev_done = true;
	printf("Connection closed\n");
}