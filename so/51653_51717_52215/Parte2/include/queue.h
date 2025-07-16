// queue.h
#ifndef QUEUE_H
#define QUEUE_H

typedef struct Processo Processo; // Declaração antecipada

typedef struct Node {
    Processo *processo;
    struct Node *next;
} Node;

// Definição nomeada da estrutura Queue
typedef struct Queue {
    Node *front;
    Node *rear;
    int size;
} Queue;

// Protótipos das funções
Queue* criar_queue();
void enqueue(Queue *q, Processo *p);
Processo* dequeue(Queue *q);
int is_empty(Queue *q);
int size(Queue *q);
int contains(Queue *q, Processo *p);
void libertar_queue(Queue *q);

#endif