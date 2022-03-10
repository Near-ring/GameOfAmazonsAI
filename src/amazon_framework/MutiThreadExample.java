package amazon_framework;

import static amazon_framework.Operations.*;

public class MutiThreadExample {
    public static void main(String[] args) {
        Amazon localGame = new Amazon();
        localGame.gameInit();
        byte[][] board = localGame.getGameBoardClone();
        long startTime, endTime;

        /**
         * Sequential code
         */
        final int array_size = 10000;
        startTime = System.currentTimeMillis();
        int[] a = new int[array_size];
        int[] sum_a = new int[1];
        for (int i = 0; i < array_size; i++) {
            MatrixArray n = getPossibleStates(board, WHITE);
            a[i] = i;
            sum_a[0] = sum_a[0] + 1;
        }
        endTime = System.currentTimeMillis();
        printf("\nSequential for Total execution time: %d\na[876]=%d\n", (endTime - startTime), a[876]);
        printf("\nSequential sum = %d\n", sum_a[0]);

        /**
         * Parallel code
         */
        startTime = System.currentTimeMillis();
        // <parallel for exmaple>
        final int thread_num = 10;
        final int blocksize = array_size / thread_num;

        final int[] b = new int[array_size];
        final int[] sum_b = new int[1];
        Thread[] threads = new Thread[thread_num];
        for (int l = 0; l < thread_num; l++) {
            final int threadID = l;
            threads[l] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = threadID * blocksize; i < (threadID + 1) * blocksize; i++) {
                        MatrixArray n = getPossibleStates(board, WHITE);
                        b[i] = i;
                        sum_b[0] = sum_b[0] + 1;
                    }
                }
            });
            threads[l].start();
        }
        for (Thread job : threads) {
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // </parallel for exmaple>
        endTime = System.currentTimeMillis();
        printf("\nMuti thread for Total execution time: %d\nb[876]=%d\n", (endTime - startTime), b[876]);
        printf("\nParallel sum = %d\n", sum_b[0]);
        //sum is likely diffient due to racing condition
    }
}
