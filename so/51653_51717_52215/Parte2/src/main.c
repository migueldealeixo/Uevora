#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/types.h>
#include "processo.h"
#include "queue.h"
#include "mem.h"
#include <string.h>

#define TEMPO_MAX 100

extern int input00[8][20];
extern int input01[8][20];
extern int input02[8][20];
extern int input03[8][20];
extern int input04[8][20];
extern int input05[8][20];
extern int input06[5][20];
extern int input07[12][20];
extern int input08[12][20];
extern int input09[12][20];
extern int input10[12][20];
extern int input11[12][20];

const int (*selecionar_input())[MAX_INSTRUCTIONS] {
    int escolha;
    printf("Escolha o input (0 a 11):\n");
    printf(" 0: input00\n 1: input01\n 2: input02\n 3: input03\n");
    printf(" 4: input04\n 5: input05\n 6: input06\n 7: input07\n");
    printf(" 8: input08\n 9: input09\n10: input10\n11: input11\n");
    scanf("%d", &escolha);

    mkdir("outputs", 0755); 
    char nome_output[30];
    snprintf(nome_output, sizeof(nome_output), "outputs/output2T%02d.out", escolha);
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
        case 6: return (const int(*)[MAX_INSTRUCTIONS])input06;
        case 7: return (const int(*)[MAX_INSTRUCTIONS])input07;
        case 8: return (const int(*)[MAX_INSTRUCTIONS])input08;
        case 9: return (const int(*)[MAX_INSTRUCTIONS])input09;
        case 10: return (const int(*)[MAX_INSTRUCTIONS])input10;
        case 11: return (const int(*)[MAX_INSTRUCTIONS])input11;
        default: 
            printf("Input inválido. Usando input00 por padrão.\n");
            return (const int(*)[MAX_INSTRUCTIONS])input00;
    }
}

Processo* all_processes[MAX_PROCESSOS];
int total_processes = 0;

void imprimir_cabecalho() {
    printf("%-10s", "time inst");
    for (int i = 0; i < MAX_PROCESSOS; i++) {
        char header[10];
        sprintf(header, "proc%d", i+1);
        printf("%-25s", header);
    }
    printf("\n");
}


void imprimir_estado(int tempo) {
    printf("%-10d", tempo);
    for (int i = 0; i < MAX_PROCESSOS; i++) {
        if (i < total_processes && processo_ativo(all_processes[i])) {
            Processo *p = all_processes[i];
            char campo[100] = "";
            char frames_str[80] = "";
            obter_frames_processo(p->id, frames_str);

            if (p->erro != NO_ERROR) {
                switch (p->erro) {
                    case SIGSEGV:
                        snprintf(campo, sizeof(campo), "SIGSEGV [%s]", frames_str);
                        break;
                    case SIGEOF:
                        snprintf(campo, sizeof(campo), "SIGEOF [%s]", frames_str);
                        break;
                    case SIGILL:
                        snprintf(campo, sizeof(campo), "SIGILL [%s]", frames_str);
                        break;
                    default:
                        snprintf(campo, sizeof(campo), "UNKNOWN [%s]", frames_str);
                        break;
                }
                p->erro = NO_ERROR; 
            } else {
                char estado_str[10] = "";
                switch (p->estado) {
                    case NEW:     strcpy(estado_str, "NEW"); break;
                    case READY:   strcpy(estado_str, "READY"); break;
                    case RUNNING: strcpy(estado_str, "RUN"); break;
                    case BLOCKED: strcpy(estado_str, "BLOCKED"); break;
                    case EXIT:    strcpy(estado_str, "EXIT"); break;
                }
                if (p->estado == NEW) {
                    snprintf(campo, sizeof(campo), "%s", estado_str); // Não imprime os frames se for NEW
                } else {
                    snprintf(campo, sizeof(campo), "%s [%s]", estado_str, frames_str);
                }
            }
            printf("%-30s", campo); 
        } else {
            printf("%-30s", "");
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

    // Cria o primeiro processo
    Processo *p0 = criar_processo(0, programas);
    all_processes[total_processes++] = p0;
    enqueue(new_queue, p0);

    inicializar_memoria();
    imprimir_cabecalho();

    for (int tempo = 1; tempo <= TEMPO_MAX; tempo++) {
        // Verifica se o processo na CPU deve sair (Se tiver a flag ativa)
        if (cpu && cpu->should_exit) {
            cpu->tempo_exit = 0;
            cpu->estado = EXIT;
        }
        // Gestão de Processos bloqueados
        if (cpu != NULL && cpu->block_restante > 0) { // Transição para block
            cpu->estado = BLOCKED;
            cpu = NULL;
        } else if (cpu != NULL && cpu->block_restante == -1 ) { // Terminou o tempo em blocked e vai para exit
            cpu->estado = EXIT;
            cpu->tempo_exit = -1;
            enqueue(exit_queue, cpu);
            cpu = NULL;
        }

        for (int i = 0; i < total_processes; i++) {
            atualizar_estado(all_processes[i]);
        }

        // Transição de BLOCKED para READY
        for (int i = 0; i < total_processes; i++) {
            Processo *p = all_processes[i];
            if (p->estado == READY && p->estado_anterior == BLOCKED && !contains(ready_queue, p)) {
                enqueue(ready_queue, p);
            }
        }

        // Move de NEW para READY se tiverem prontos
        int new_size = size(new_queue);
        for (int i = 0; i < new_size; i++) {
            Processo *p = dequeue(new_queue);
            if (p->estado == READY && !contains(ready_queue, p)) {
                enqueue(ready_queue, p);
            } else {
                enqueue(new_queue, p);
            }
        }

        // Gestão do QUANTUM
        if (cpu && cpu->estado == RUNNING) {
            if (cpu->quantum > 0) {  cpu->quantum--;} // Decrementar o quantum
            if (cpu->quantum <= 0 && !is_empty(ready_queue)) { // Preemptadao
                cpu->estado = READY;
                cpu->quantum = QUANTUM;
                enqueue(ready_queue, cpu);
                cpu = NULL;
            }
        }

        if (cpu && (cpu->estado == EXIT && cpu->tempo_exit >= 1)) {
            cpu = NULL;
        }

        // Faz dispatch de um processo caso a cpu esteja livre
        if (!cpu && !is_empty(ready_queue)) {
            cpu = dequeue(ready_queue);
            cpu->estado = RUNNING;
            cpu->quantum = QUANTUM ;
        }

        // Execuçao de instruções
        if (cpu && cpu->estado == RUNNING) {
            executar_instrucao(cpu, tempo, &total_processes, all_processes, programas, new_queue, ready_queue);
            if (cpu->erro != NO_ERROR) {
                enqueue(exit_queue, cpu);
                cpu = NULL;
            }
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
