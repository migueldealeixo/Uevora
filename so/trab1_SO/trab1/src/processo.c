#include <stdio.h>
#include <stdlib.h>
#include "processo.h"
#include "queue.h"

extern int total_processes;

Processo* criar_processo(int programa_id, const int programas[][MAX_INSTRUCTIONS]) {
    if (programa_id < 0 || programa_id >= MAX_PROCESSOS) return NULL;

    Processo *p = (Processo*)malloc(sizeof(Processo));
    if (!p) return NULL;

    static int next_id = 1;

    p->id = next_id++;
    p->estado = NEW;
    p->estado_anterior = NEW;
    p->pc = 0;
    p->tempo_estado = 0;
    p->block_restante = 0;
    p->quantum = QUANTUM;
    p->tempo_exit = 0;
    p->num_instrucoes = 0;

    for (int i = 0; i < MAX_INSTRUCTIONS; i++) {
        int instr = programas[i][programa_id];
        p->instrucoes[i] = instr;
        if (instr == 0) break; 
        p->num_instrucoes++;
    }

    return p;
}

void atualizar_estado(Processo *p) {
    p->estado_anterior = p->estado;

    switch (p->estado) {
        case NEW:
            p->tempo_estado++;
            if (p->tempo_estado >= 3) {
                p->estado = READY;
                p->tempo_estado = 0;
            }
            break;
        case BLOCKED:
            if (p->block_restante > 0) {
                p->block_restante--;
                if (p->block_restante == 0) {
                    p->estado = READY;
                    p->quantum = QUANTUM; 
                }
            }
            break;
        case EXIT:
            p->tempo_exit++;
            break;
        default:
            break;
    }
}

void executar_instrucao(Processo *p, int tempo, int *total_processos, Processo *todos[], const int programas[][MAX_INSTRUCTIONS], Queue *new_queue) {
    if (p->estado != RUNNING) return;

    if (p->pc > p->num_instrucoes) {
        p->estado = EXIT;
        return;
    }

    int instr = p->instrucoes[p->pc];

    if (instr == 0) { // HALT
        
        p->block_restante = -1; // Notificar o scheduler o proximo estado é exit
       
        return;
    }


    if (instr < 0) { // I/O
        // Notificar o scheduler que o processo quer mudar para blocked
        // p->estado = BLOCKED;
        p->block_restante = abs(instr) + 1;
        p->pc++;
        return;
    }

    if (instr > 100 && instr < 200) { // JUMP
        int salto = instr % 100;
        p->pc = (p->pc >= salto) ? (p->pc - salto) : 0;
    } 
    else if (instr > 200 && instr < 300) { // EXEC
        int programa_id = (instr % 100) - 1;
        if (*total_processos < MAX_PROCESSOS) {
            Processo *novo = criar_processo(programa_id, programas);
            if (novo) {
                novo->tempo_estado++;
                todos[*total_processos] = novo;
                (*total_processos)++;
                enqueue(new_queue, novo);
            }
        }
        p->pc++;
    } 
    else { 
        p->pc++;
    }

    p->quantum--;
    if (p->quantum <= 0 && p->estado == RUNNING) {
        p->estado = READY;
        p->quantum = QUANTUM;
    }
}

int processo_ativo(const Processo *p) {
    return !(p->estado == EXIT && p->tempo_exit >= 1);
}