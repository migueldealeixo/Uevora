#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>

#define NUM_FRAMES 7
#define FRAME_SIZE 3000
#define MAX_PROCS 20  
#define MAX_PAGES 4

extern int inputP1Mem00[5];
extern int inputP1Mem01[5];
extern int inputP1Mem02[5];
extern int inputP1Mem03[10];
extern int inputP1Mem04[20];
extern int inputP1Mem05[3];

extern int inputP1Exec00[24];    
extern int inputP1Exec01[12];    
extern int inputP1Exec02[36];    
extern int inputP1Exec03[52];    
extern int inputP1Exec04[200];   
extern int inputP1Exec05[120];   

typedef struct {
    int pid;
    int page;
    int loaded_time; // FIFO
    int last_used_time; // LRU 
    int valid; // valid = 0 = livre, valid = 1 = alocado
} Frame;

typedef enum {FIFO, LRU} Algorithm;

Frame memory[NUM_FRAMES];
int current_time = 0;
FILE *output_file;

void create_directories() {
    mkdir("output", 0755);
    mkdir("output/fifo", 0755);
    mkdir("output/lru", 0755);
}

// Procura um frame livre
int get_free_frame() {
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (!memory[i].valid) return i;
    }
    return -1;
}

// Procura se a pagina de mem ja esta carregada num frame
int find_frame(int pid, int page) {
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (memory[i].valid && memory[i].pid == pid && memory[i].page == page) {
            return i;
        }
    }
    return -1;
}


int choose_victim(Algorithm algo) {
    int victim = -1;
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (!memory[i].valid) continue;

        if (victim == -1) {
            victim = i;
        } else {
            if (algo == FIFO) {
                if (memory[i].loaded_time < memory[victim].loaded_time ||
                    (memory[i].loaded_time == memory[victim].loaded_time && i < victim)) {
                    victim = i;
                }
            } else if (algo == LRU) {
                if (memory[i].last_used_time < memory[victim].last_used_time ||
                    (memory[i].last_used_time == memory[victim].last_used_time && i < victim)) {
                    victim = i;
                }
            }
        }
    }
    return victim;
}

void remove_frames(int pid) {
    for (int i = 0; i < NUM_FRAMES; i++) {
        if (memory[i].valid && memory[i].pid == pid) {
            memory[i].valid = 0;
        }
    }
}

typedef struct {
    int frame_idx;
    int loaded_time;
} FrameInfo;


void print_memory_state(FILE *fout) {
    fprintf(fout, "%-10d", current_time);

    for (int pid = 1; pid <= MAX_PROCS; pid++) {
        FrameInfo frames_of_pid[NUM_FRAMES];
        int count = 0;

        for (int i = 0; i < NUM_FRAMES; i++) {
            if (memory[i].valid && memory[i].pid == pid) {
                frames_of_pid[count].frame_idx = i;
                frames_of_pid[count].loaded_time = memory[i].loaded_time;
                count++;
            }
        }

        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                if (memory[frames_of_pid[i].frame_idx].page > memory[frames_of_pid[j].frame_idx].page) {
                    FrameInfo temp = frames_of_pid[i];
                    frames_of_pid[i] = frames_of_pid[j];
                    frames_of_pid[j] = temp;
                }
            }
        }

        char buffer[64] = "";
        for (int i = 0; i < count; i++) {
            char temp[8];
            if (i > 0) strcat(buffer, ",");
            sprintf(temp, "F%d", frames_of_pid[i].frame_idx);
            strcat(buffer, temp);
        }

        fprintf(fout, "%-23s", buffer);
    }
    fprintf(fout, "\n");
}

void simulate(Algorithm algo, const char* filename, int* memory_limits, int* exec_sequence, int num_execs) {
    memset(memory, 0, sizeof(memory)); // Memoria inicializada a 0
    current_time = 0; 

    output_file = fopen(filename, "w");
    if (!output_file) {
        perror("Erro ao abrir ficheiro de output");
        exit(1);
    }
    fprintf(output_file, "time inst ");
    for (int p = 1; p <= MAX_PROCS; p++) {
        fprintf(output_file, "proc%-15d ", p);
    }
    fprintf(output_file, "\n");


    for (int i = 0; i < num_execs * 2; i += 2) {
        int pid = exec_sequence[i];
        int addr = exec_sequence[i+1];
      
        if (pid < 1 || pid > MAX_PROCS) {
            printf("PID %d inválido (fora do intervalo 1-%d)\n", pid, MAX_PROCS);
            current_time++;
            continue;
        }
        
        int limit = memory_limits[pid - 1];
        int page = addr / FRAME_SIZE;
     
        // Erro de segmentação
        if (addr >= limit) {
            fprintf(output_file, "%-10d", current_time);
            for (int p = 1; p <= MAX_PROCS; p++) {
                if (p == pid) {
                    fprintf(output_file, "%-23s", "SIGSEGV");
                } else {
                    char buffer[32] = "";
                    int first = 1;
                    for (int k = 0; k < NUM_FRAMES; k++) {
                        if (memory[k].valid && memory[k].pid == p) {
                            if (!first) strcat(buffer, ",");
                            char temp[8];
                            sprintf(temp, "F%d", k);
                            strcat(buffer, temp);
                            first = 0;
                        }
                    }
                    fprintf(output_file, "%-23s", buffer);
                }
            }
            fprintf(output_file, "\n");
            remove_frames(pid);
            current_time++;
            continue;
        }
        
        // CACHE HIT
        int frame_idx = find_frame(pid, page);
        if (frame_idx != -1) {
            memory[frame_idx].last_used_time = current_time;

        // CACHE MISS
        } else { // Existe um frame livre
            int free = get_free_frame();
            if (free != -1) {
                memory[free].pid = pid;
                memory[free].page = page;
                memory[free].loaded_time = current_time;
                memory[free].last_used_time = current_time;
                memory[free].valid = 1;
            } else { // Requer Substituição
                int victim = choose_victim(algo);
                memory[victim].pid = pid;
                memory[victim].page = page;
                memory[victim].loaded_time = current_time;
                memory[victim].last_used_time = current_time;
            }
        }
for (int i = 0; i < NUM_FRAMES; i++) {
    if (memory[i].valid) {
        printf("  Frame %d: pid %d, page %d\n", i, memory[i].pid, memory[i].page);
    } else {
        printf("  Frame %d: vazio\n", i);
    }
}
        print_memory_state(output_file);
        current_time++;
    }

    fclose(output_file);
}


int main() {
    create_directories();

    int choice;
    printf("Escolha a simulação a executar (0-5): ");
    scanf("%d", &choice);

    int *memory_limits, *exec_sequence;
    int num_pairs;

    switch(choice) {
        case 0:
            memory_limits = inputP1Mem00;
            exec_sequence = inputP1Exec00;
            num_pairs = sizeof(inputP1Exec00)/sizeof(int)/2;
            break;
        case 1:
            memory_limits = inputP1Mem01;
            exec_sequence = inputP1Exec01;
            num_pairs = sizeof(inputP1Exec01)/sizeof(int)/2;
            break;
        case 2:
            memory_limits = inputP1Mem02;
            exec_sequence = inputP1Exec02;
            num_pairs = sizeof(inputP1Exec02)/sizeof(int)/2;
            break;
        case 3:
            memory_limits = inputP1Mem03;
            exec_sequence = inputP1Exec03;
            num_pairs = sizeof(inputP1Exec03)/sizeof(int)/2; 
            break;
        case 4:
            memory_limits = inputP1Mem04;
            exec_sequence = inputP1Exec04;
            num_pairs = sizeof(inputP1Exec04)/sizeof(int)/2;
            break;
        case 5:
            memory_limits = inputP1Mem05;
            exec_sequence = inputP1Exec05;
            num_pairs = sizeof(inputP1Exec05)/sizeof(int)/2;
            break;
        default:
            printf("Escolha inválida. A usar simulação 0 por defeito.\n");
            memory_limits = inputP1Mem00;
            exec_sequence = inputP1Exec00;
            num_pairs = sizeof(inputP1Exec00)/sizeof(int)/2;
            choice = 0;
    }

    char fifo_filename[50], lru_filename[50];
    sprintf(fifo_filename, "output/fifo/fifo%02d.out", choice);
    sprintf(lru_filename, "output/lru/lru%02d.out", choice);
    simulate(FIFO, fifo_filename, memory_limits, exec_sequence, num_pairs);
    simulate(LRU, lru_filename, memory_limits, exec_sequence, num_pairs);
    return 0;
}