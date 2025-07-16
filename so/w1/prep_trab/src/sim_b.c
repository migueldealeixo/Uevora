#include <stdio.h>
#include <stdlib.h>
#include "queue.h"

#define MAX_INSTRUCTIONS 3
#define HALT 0

typedef struct {
    int pid;
    int instructions[MAX_INSTRUCTIONS];
    int index;
    char *state;
} Process;


int execute(Process *p) {
    while (p->index < MAX_INSTRUCTIONS) {
        int instr = p->instructions[p->index++];
        if (instr == HALT || instr < 0) {
            return instr;
        }
    }
    return HALT;  
}


void run(Process processes[], int numProcesses) {
    Queue *readyQueue = createQueue();

    for (int i = 0; i < numProcesses; i++) {
        processes[i].state = "READY";
        enqueue(readyQueue, &processes[i]);
    }

    int instante = 1;
    while (!isEmpty(readyQueue)) {
        printf("Instante %d: ", instante);

        for (int i = 0; i < numProcesses; i++) {
            if (processes[i].state != NULL && processes[i].state != "RUNNING")
                processes[i].state = "READY";
        }

        Process *running = (Process *)dequeue(readyQueue);
        if (running == NULL) break;

        running->state = "RUNNING";
        
        int instruction = execute(running);

        for (int i = 0; i < numProcesses; i++) {
            if (processes[i].state != NULL) {
                printf("P%d: %s  ", processes[i].pid, processes[i].state);
            }
        }
        printf("\n");

        if (instruction == HALT) {
            running->state = NULL;  
        }
        else {
            running->state = "READY";
            enqueue(readyQueue, running);
        }

        instante++;
    }

    deleteQueue(readyQueue);  
}


int main() {
    Process processes[] = {
        {1, {3, 4, 2}, 0, "READY"},
        {2, {1, -2, 1}, 0, "READY"},
        {3, {-5, 4, 5}, 0, "READY"}
    };

    int numProcesses = sizeof(processes) / sizeof(processes[0]);

    run(processes, numProcesses);
    
    return 0;
}
