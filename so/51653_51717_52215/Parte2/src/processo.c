#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "processo.h"
#include "queue.h"
#include "mem.h"

extern int total_processes;

Processo* criar_processo(int programa_id, const int programas[][MAX_INSTRUCTIONS]) {
    if (programa_id < 0 || programa_id >= MAX_PROCESSOS) return NULL;

    Processo *p = (Processo*)malloc(sizeof(Processo));
    if (!p) return NULL;

    static int next_id = 1;
    p->id = next_id++;
    p->estado = NEW;
    p->should_exit = 0;     // Flag que inica se o processo decidiu terminar
    p->estado_anterior = NEW;
    p->pc = 0;
    p->tempo_estado = 0;
    p->block_restante = 0;
    p->quantum = QUANTUM;
    p->tempo_exit = -1;
    p->num_instrucoes = 0;
    p->erro = NO_ERROR;
    p->espaco_enderecamento = programas[0][programa_id];  // Limite de memoria

    
    for (int i = 1; i <= 8; i++) {
        int instr = programas[i][programa_id];
        p->instrucoes[i - 1] = instr;
        p->num_instrucoes++;
    }

    return p;
}

void atualizar_estado(Processo *p) {

    p->estado_anterior = p->estado; // Guarda estado anterior
    switch (p->estado) {
        case NEW: // NEW-> READY
            p->tempo_estado++;
            if (p->tempo_estado >= 3) { // Permance 3 ciclos em NEW
                p->estado = READY;  // Quando termina transita para READY
                p->tempo_estado = 0;
            }
            break;

        case BLOCKED: // BLOCKED -> READY
            if (p->block_restante > 0) {
                p->block_restante--;
                if (p->block_restante == 0) {
                    p->estado = READY;
                    p->quantum = QUANTUM;
                }
            }
            break;

        case EXIT:
            if (p->tempo_exit == -1) { 
                p->tempo_exit = 0; 
            }
            else p->tempo_exit++;

            if (p->tempo_exit == 4) {
                libertar_memoria(p->id); 
            }
            break;

        default:
            break;
    }
}

void executar_instrucao(Processo *p, int tempo, int *total_processos, Processo *todos[], const int programas[][MAX_INSTRUCTIONS], Queue *new_queue, Queue *ready_queue) {
    if (p->estado != RUNNING) return;   // Só executa se o processo tiver em estado RUNNING

    // Se o pc passou do fim da lista de instruções o processo termina com erro SIGEOF e entra em exit
    if (p->pc >= p->num_instrucoes - 1) {
        p->estado = EXIT;
        p->erro = SIGEOF;
        p->tempo_exit = 0;
        return;
    }
    
    int instr = p->instrucoes[p->pc]; // Obter instrução atual

    // Instrução HALT (termino voluntario, sinaliza com should_exit)
    if (instr == 0) {
        p->should_exit = 1;
        p->erro = NO_ERROR;
        p->tempo_exit = 0;
        return;
    }

    // I/O (BLOCKED)
    if (instr < 0) {
        p->block_restante = abs(instr) + 1;
        p->pc++;
        return;
    }

    // JUMPF (JUMP FORWARD)
    if (instr >= 1 && instr <= 100) {
        int offset = instr;
        if (p->pc + offset >= p->num_instrucoes - 1) {
            p->estado = EXIT;
            p->erro = SIGILL; // Se o salto for out of bounds sinaliza SIGILL
            p->tempo_exit = 0;
            return;
        }
        p->pc += offset;
    }
    // JUMPB (JUMP BACKWARDS)
    else if (instr >= 101 && instr <= 199) {
        int offset = instr - 100;
        if (p->pc < offset) {
            p->estado = EXIT;
            p->erro = SIGILL;
            p->tempo_exit = 0;
            return;
        }
        p->pc -= offset;
    }
    // LOAD/STORE (1000-15999)
    else if (instr >= 1000 && instr <= 15999) {
        int endereco = instr - 1000;
        if (aceder_memoria(p->id, endereco, p->espaco_enderecamento) != 0) {
            p->estado = EXIT;
            p->erro = SIGSEGV;
            p->tempo_exit = 0;
            return;
        }
        p->pc++;
    }
    
    else if (instr >= 1000000000 && instr <= 2109999999) {
        int valor = instr;
        int end1 = ((valor / 100000) % 100000) - 10000; // Endereço 1 
        int end2 = valor % 100000; // Endereço 2: ultimo 5 digitios do valor

        // Simula um acesso a memoria 
        if (aceder_memoria(p->id, end1, p->espaco_enderecamento) != 0 ||
            aceder_memoria(p->id, end2, p->espaco_enderecamento) != 0) {
            p->estado = EXIT;
            p->erro = SIGSEGV;
            p->tempo_exit = 0;
            return;
        }
        p->pc++;
    }
    
    // EXEC (200-299)
    else if (instr >= 200 && instr <= 299) {
        int programa_id = (instr % 100) - 1;
        
        if (programa_id < 0 || programa_id >= MAX_PROCESSOS) {
            p->estado = EXIT;
            p->erro = SIGILL;
            p->tempo_exit = 0;
            return;
        }
        
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
    } else {
        p->pc++;
    }
}

int processo_ativo(const Processo *p) {
    // Só deixa de ser ativo após 3 ciclos completos no EXIT
    return !(p->estado == EXIT && p->tempo_exit >= 4);
}
