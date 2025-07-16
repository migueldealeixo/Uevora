#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>

void *sum_function(void *ptr);

int main()
{
    pthread_t thread1, thread2;
    int x1 = 1, x2 = 2;
    int iret1, iret2;

    iret1 = pthread_create(&thread1, NULL, sum_function, (void *)&x1);
    if (iret1) {
        fprintf(stderr, "Error - pthread_create() return code: %d\n", iret1);
        exit(EXIT_FAILURE);
    }

    iret2 = pthread_create(&thread2, NULL, sum_function, (void *)&x2);
    if (iret2) {
        fprintf(stderr, "Error - pthread_create() return code: %d\n", iret2);
        exit(EXIT_FAILURE);
    }

    pthread_join(thread1, NULL);
    printf("Thread 1 finished\n");

    pthread_join(thread2, NULL);
    printf("Thread 2 finished\n");

    exit(EXIT_SUCCESS);
}

void *sum_function(void *ptr)
{
    int *input = (int *)ptr;
    int start1 = 1, end1 = 100;
    int start2 = 101, end2 = 200;
    int sum1 = 0, sum2 = 0;

    for (int i = start1; i <= end1; i++) {
        sum1 += i;
    }

    for (int i = start2; i <= end2; i++) {
        sum2 += i;
    }

    printf("Thread %d: Sum of 1 to 100 = %d\n", *input, sum1);
    printf("Thread %d: Sum of 101 to 200 = %d\n", *input, sum2);

    pthread_exit(NULL);
}
