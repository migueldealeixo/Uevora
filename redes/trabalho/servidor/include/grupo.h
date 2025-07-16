#ifndef GRUPO_H
#define GRUPO_H

#define MAX_GRUPOS 50
#define MAX_MEMBROS 50

typedef struct {
    char nome[50];
    char membros[MAX_MEMBROS][50];
    int num_membros;
} Grupo;

// Funções para manipulação de grupos em ficheiros
int save_grupos(Grupo grupos[], int num_grupos);
int carregar_grupos(Grupo grupos[], int *num_grupos);

#endif // GRUPO_H
