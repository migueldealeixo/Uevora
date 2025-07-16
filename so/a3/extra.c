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
    int n;
    printf("Enter the number of threads: ");
    scanf("%d", &n);

    pthread_t threads[n];
    struct ThreadData td[n];
    int range = 100;
    int total_sum = 0;

    for (int i = 0; i < n; i++) {
        td[i].input = i + 1;
        pthread_create(&threads[i], NULL, sum_function, (void *)&td[i]);
    }

    for (int i = 0; i < n; i++) {
        pthread_join(threads[i], NULL);
        printf("Thread %d finished with sum = %d\n", td[i].input, td[i].output);
        total_sum += td[i].output;
    }

    printf("Total sum (1 to %d) = %d\n", range * n, total_sum);

    exit(EXIT_SUCCESS);
}

void *sum_function(void *ptr)
{
    struct ThreadData *data = (struct ThreadData *)ptr;
    int start = (data->input - 1) * 100 + 1;
    int end = data->input * 100;
    int sum = 0;

    for (int i = start; i <= end; i++) {
        sum += i;
    }

    data->output = sum;
    pthread_exit(NULL);
}
