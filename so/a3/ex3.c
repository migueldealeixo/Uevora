#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>

struct ThreadData {
    int input;
    int output;
};

void *sum_function(void *ptr);

int main()
{
    pthread_t thread1, thread2;
    struct ThreadData td1, td2;
    td1.input = 1;
    td2.input = 2;

    pthread_create(&thread1, NULL, sum_function, (void *)&td1);
    pthread_create(&thread2, NULL, sum_function, (void *)&td2);

    pthread_join(thread1, NULL);
    printf("Thread 1 finished\n");
    pthread_join(thread2, NULL);
    printf("Thread 2 finished\n");

    int total_sum = td1.output + td2.output;
    printf("Total sum (1 to 200) = %d\n", total_sum);

    exit(EXIT_SUCCESS);
}

void *sum_function(void *ptr)
{
    struct ThreadData *data = (struct ThreadData *)ptr;
    int start1 = 1, end1 = 100;
    int start2 = 101, end2 = 200;
    int sum1 = 0, sum2 = 0;
    for (int i = start1; i <= end1; i++) {
        sum1 += i;
    }
    for (int i = start2; i <= end2; i++) {
        sum2 += i;
    }
    data->output = sum1 + sum2;
    printf("Thread %d: Sum = %d\n", data->input, data->output);
    pthread_exit(NULL);
}
