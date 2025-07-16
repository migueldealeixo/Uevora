#ifndef MEM_H
#define MEM_H

#include <stdio.h>

#define NUM_FRAMES 7
#define FRAME_SIZE 3000

typedef struct {
    int pid;
    int page;
    int loaded_time;
    int last_used_time;
    int valid;
} Frame;

extern Frame memory[NUM_FRAMES];
extern int current_time;

void inicializar_memoria();
int aceder_memoria(int pid, int endereco, int limite);
void libertar_memoria(int pid);
void imprimir_memoria_estado(FILE *out, int num_procs);
void obter_frames_processo(int pid, char *str);

#endif