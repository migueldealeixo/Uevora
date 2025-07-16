#include <stdlib.h>
#include "queue.h"

Queue* criar_queue() {
    Queue *q = malloc(sizeof(Queue));
    q->front = q->rear = NULL;
    q->size = 0;
    return q;
}

void enqueue(Queue *q, Processo *p) {
    Node *node = malloc(sizeof(Node));
    node->processo = p;
    node->next = NULL;
    if (q->rear) {
        q->rear->next = node;
    } else {
        q->front = node;
    }
    q->rear = node;
    q->size++;
}

Processo* dequeue(Queue *q) {
    if (!q->front) return NULL;
    Node *temp = q->front;
    Processo *p = temp->processo;
    q->front = temp->next;
    if (!q->front) q->rear = NULL;
    free(temp);
    q->size--;
    return p;
}

int is_empty(Queue *q) {
    return q->front == NULL;
}

int size(Queue *q) {
    return q->size;
}

int contains(Queue *q, Processo *p) {
    Node *curr = q->front;
    while (curr) {
        if (curr->processo == p) return 1;
        curr = curr->next;
    }
    return 0;
}

void libertar_queue(Queue *q) {
    while (!is_empty(q)) {
        dequeue(q);
    }
    free(q);
}
