# Trabalho Prático 1 de Sistemas Operativos
Este projeto simula a execução de processos com suporte a conjuntos simples de execuções definidos em inputs.c.
## Estrutura do Projeto
.
├── include
│   ├── processo.h
│   └── queue.h
├── Makefile
├── outputs
│   ├── output00.out
│   ├── output01.out
│   ├── output02.out
│   ├── output03.out
│   ├── output04.out
│   └── output05.out
├── Readme.md
├── src
│   ├── inputs.c
│   ├── main.c
│   ├── processo.c
│   └── queue.c
└── Trabalho1-SO-24-25_v31.pdf

## Compilação e Execução
Para compilar o projeto basta executar: make
Para limpar os ficheiros de compilação basta executar: make clean
Depois de compilar, corre se o simulador com: ./build/simulador