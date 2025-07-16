#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define NUM_FRAMES 7
#define FRAME_SIZE 3000
#define MAX_PROCS 20

typedef struct {
    int pid;
    int page;
    int loaded_time;
    int last_used_time;
    int valid;
} Frame;

Frame memory[NUM_FRAMES];
int current_time = 0;

// Colocar todos os frames livres e o tempo a 0.
void inicializar_memoria() {
    memset(memory, 0, sizeof(memory));
    current_time = 0;
}

// Simula um acesso a memoria
int aceder_memoria(int pid, int endereco, int limite) {
    // Verifica se ocorre SIGSEGV
    if (endereco >= limite) {
        return -1;  
    }

    int page = endereco / FRAME_SIZE; 

    // Verifica se é hit
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (memory[i].valid && memory[i].pid == pid && memory[i].page == page) {
            memory[i].last_used_time = current_time++;
            return 0;
        }
    }

    // Miss
        // Procura frame livre
    int free_idx = -1;
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (!memory[i].valid) {
            free_idx = i;
            break;
        }
    }
    int use_idx;   
    if (free_idx != -1) {
        use_idx = free_idx; // Carrega pagina no frame livre

         // Subsituição LRU
    } else {
        int lru_idx = 0; // Começa no frame 0
        for (int i = 1; i < NUM_FRAMES; i++) {
        if (memory[i].last_used_time < memory[lru_idx].last_used_time) { 
            lru_idx = i;
        }
}
        use_idx = lru_idx;
    }

    // Inserção na memoria
    memory[use_idx].pid = pid;
    memory[use_idx].page = page;
    memory[use_idx].loaded_time = current_time;
    memory[use_idx].last_used_time = current_time;
    memory[use_idx].valid = 1;
    current_time++;

    return 0;
}

// Quando sai definitivamente dos tempos de EXIT
void libertar_memoria(int pid) {
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (memory[i].valid && memory[i].pid == pid) {
            memory[i].valid = 0;
            memory[i].pid = -1;
            memory[i].page = -1;
            memory[i].loaded_time = -1;
            memory[i].last_used_time = -1;
        }
    }
}

void imprimir_memoria_estado(FILE *out, int num_procs) {
    fprintf(out, "[");
    for (int pid = 1; pid <= num_procs; pid++) {
        char buffer[64] = "";
        int first = 1;
        for (int i = 0; i < NUM_FRAMES; i++) {
            if (memory[i].valid && memory[i].pid == pid) {
                if (!first) strcat(buffer, ",");
                char temp[8];
                sprintf(temp, "F%d", i);
                strcat(buffer, temp);
                first = 0;
            }
        }
        fprintf(out, "%s", buffer);
        if (pid != num_procs) fprintf(out, "] [");
    }
    fprintf(out, "]\n");
}

// Construtuor de strings
void obter_frames_processo(int pid, char *str) {
    typedef struct {
        int frame_idx;
        int page;
    } FrameInfo;

    FrameInfo frames[NUM_FRAMES];
    int count = 0;

    for (int i = 0; i < NUM_FRAMES; i++) {
        if (memory[i].valid && memory[i].pid == pid) {
            frames[count].frame_idx = i;
            frames[count].page = memory[i].page;
            count++;
        }
    }
    for (int i = 0; i < count - 1; i++) {
        for (int j = i + 1; j < count; j++) {
            if (frames[j].page < frames[i].page) {
                FrameInfo temp = frames[i];
                frames[i] = frames[j];
                frames[j] = temp;
            }
        }
    }
    str[0] = '\0';
    for (int i = 0; i < count; i++) {
        char temp[6];
        if (i > 0) strcat(str, ",");
        sprintf(temp, "F%d", frames[i].frame_idx);
        strcat(str, temp);
    }
}


