#ifndef PROCESSO_H
#define PROCESSO_H

#define MAX_INSTRUCTIONS 20
#define MAX_PROCESSOS 20
#define QUANTUM 3

struct Queue; 

typedef enum {
    NEW,
    READY,
    RUNNING,
    BLOCKED,
    EXIT
} Estado;

typedef enum {
    NO_ERROR,
    SIGSEGV,
    SIGILL,
    SIGEOF
} ErroExecucao;

typedef struct Processo {
    int id;
    Estado estado;
    Estado estado_anterior;
    int pc;
    int instrucoes[MAX_INSTRUCTIONS];
    int num_instrucoes;
    int tempo_estado;
    int block_restante;
    int quantum;
    int should_exit;
    int tempo_exit;

    int espaco_enderecamento;
    ErroExecucao erro;       
} Processo;

Processo* criar_processo(int programa_id, const int programas[][MAX_INSTRUCTIONS]);
void atualizar_estado(Processo *p);
void executar_instrucao(Processo *p, int tempo, int *total_processos, Processo *todos[], const int programas[][MAX_INSTRUCTIONS], struct Queue *new_queue, struct Queue *ready_queue);
int processo_ativo(const Processo *p);

#endif
