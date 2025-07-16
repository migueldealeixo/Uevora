#include <stdio.h>
#include <stdlib.h>
#include "processo.h"
#include "queue.h"

#define TEMPO_MAX 100


extern int input00[5][20];
extern int input01[5][20];
extern int input02[4][20];
extern int input03[5][20];
extern int input04[11][20];
extern int input05[11][20];

const int (*selecionar_input())[MAX_INSTRUCTIONS] {
    int escolha;
    printf("Escolha o input (0 a 5):\n");
    printf("0: input00\n1: input01\n2: input02\n3: input03\n4: input04\n5: input05\n");
    scanf("%d", &escolha);

    char nome_output[30];
    snprintf(nome_output, sizeof(nome_output), "outputs/output%02d.out", escolha);
    FILE *fp = freopen(nome_output, "w", stdout);
    if (!fp) {
        perror("Erro ao redirecionar o output");
        exit(EXIT_FAILURE);
    }

    switch (escolha) {
        case 0: return (const int(*)[MAX_INSTRUCTIONS])input00;
        case 1: return (const int(*)[MAX_INSTRUCTIONS])input01;
        case 2: return (const int(*)[MAX_INSTRUCTIONS])input02;
        case 3: return (const int(*)[MAX_INSTRUCTIONS])input03;
        case 4: return (const int(*)[MAX_INSTRUCTIONS])input04;
        case 5: return (const int(*)[MAX_INSTRUCTIONS])input05;
        default: 
            printf("Input inválido. Usando input00 por padrão.\n");
            return (const int(*)[MAX_INSTRUCTIONS])input00;
    }
}

Processo* all_processes[MAX_PROCESSOS];
int total_processes = 0;

void imprimir_cabecalho() {
    printf("time inst   ");
    for (int i = 0; i < MAX_PROCESSOS; i++) {
        printf("proc%-12d", i + 1);
    }
    printf("\n");
}

void imprimir_estado(int tempo) {
    printf("%-12d", tempo);
    for (int i = 0; i < MAX_PROCESSOS; i++) {
        if (i < total_processes && processo_ativo(all_processes[i])) {
            char *estado_str = "";
            switch (all_processes[i]->estado) {
                case NEW:     estado_str = "NEW"; break;
                case READY:   estado_str = "READY"; break;
                case RUNNING: estado_str = "RUN"; break;
                case BLOCKED: estado_str = "BLOCKED"; break;
                case EXIT:    estado_str = (all_processes[i]->tempo_exit == 0) ? "EXIT" : "TERM"; break;
            }
            printf("%-15s", estado_str);
        } else {
            printf("%-15s", "");
        }
    }
    printf("\n");
}

int main() {
    Queue *new_queue = criar_queue();
    Queue *ready_queue = criar_queue();
    Queue *exit_queue = criar_queue();
    Processo *cpu = NULL;

    const int (*programas)[MAX_INSTRUCTIONS] = selecionar_input();
    Processo *p0 = criar_processo(0, programas);
    all_processes[total_processes++] = p0;
    enqueue(new_queue, p0);

    imprimir_cabecalho();

   
    for (int tempo = 1; tempo <= TEMPO_MAX; tempo++) {

         // BLOCKED && BLOCKED-> EXIT
        if (cpu != NULL && cpu->block_restante > 0) {
            cpu->estado = BLOCKED;
            cpu = NULL;
        } else if (cpu != NULL && cpu->block_restante == -1 ) {
            cpu->estado = EXIT;
            cpu->tempo_exit = -1;
            enqueue(exit_queue, cpu);
            
            cpu = NULL;
        }
        // Atualizar estados NEW->READY && BLOCKED->READY
        for (int i = 0; i < total_processes; i++) {
            atualizar_estado(all_processes[i]);
        }

        // Processos que sairam do BLOCKED
        for (int i = 0; i < total_processes; i++) {
            Processo *p = all_processes[i];
            if (p->estado == READY && p->estado_anterior == BLOCKED && !contains(ready_queue, p)) {
                enqueue(ready_queue, p);
            }
        }
        // Verificação de READY na new_queue
        int new_size = size(new_queue);
        for (int i = 0; i < new_size; i++) {
            Processo *p = dequeue(new_queue);
            if (p->estado == READY) {
                if (!contains(ready_queue, p)) {
                    enqueue(ready_queue, p);
                }
            } else {
                enqueue(new_queue, p); // Mantem em NEW
            }
        }
    
        if (cpu && (cpu->estado == READY || (cpu->estado == EXIT && cpu->tempo_exit >= 1))) {
            if (cpu->estado == READY && !contains(ready_queue, cpu)) {
                enqueue(ready_queue, cpu);
            }
            cpu = NULL;
        }
    
        // Escolha do proximo processo
        if (!cpu && !is_empty(ready_queue)) {
            cpu = dequeue(ready_queue);
            cpu->estado = RUNNING;
            cpu->quantum = QUANTUM;
        }
        
        // Execução de Instruções
        if (cpu && cpu->estado == RUNNING) {
            executar_instrucao(cpu, tempo, &total_processes, all_processes, programas, new_queue);
        }
    
        imprimir_estado(tempo);
    }
 
    libertar_queue(new_queue);
    libertar_queue(ready_queue);
    for (int i = 0; i < total_processes; i++) {
        free(all_processes[i]);
    }

    return 0;
}
