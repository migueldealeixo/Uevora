#include <pthread.h>
#include <stdio.h>

#define MAX_COUNT 100

int sum1 = 0;
int sum2 = 0;

void* calculate_sum(void* arg) {
    int* start = (int*) arg;
    int sum = 0;
    
    for (int i = *start; i < *start + MAX_COUNT; i++) {
        sum += i;
    }
    
    if (*start == 1) {
        sum1 = sum;
    } else {
        sum2 = sum;
    }

    pthread_exit(NULL);
}

int main() {
    pthread_t thread1, thread2;
    int start1 = 1;
    int start2 = MAX_COUNT + 1;

    pthread_create(&thread1, NULL, calculate_sum, &start1);
    pthread_create(&thread2, NULL, calculate_sum, &start2);

    pthread_join(thread1, NULL);
    pthread_join(thread2, NULL);

    printf("Soma de 1 a 100: %d\n", sum1);
    printf("Some de 101 a 200: %d\n", sum2);

    return 0;
}
