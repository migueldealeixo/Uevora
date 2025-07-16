# Nome do compilador
CC = gcc

# Diretórios
SRC_DIR = src
INCLUDE_DIR = include
BUILD_DIR = build

# Ficheiros fonte
SRC_QUEUE = $(SRC_DIR)/queue.c
SRC_A = $(SRC_DIR)/sim_a.c
SRC_B = $(SRC_DIR)/sim_b.c

# Flags de compilação
CFLAGS = -Wall -Wextra -I$(INCLUDE_DIR)

# Objetos compilados
OBJ_QUEUE = $(BUILD_DIR)/queue.o
OBJ_A = $(BUILD_DIR)/sim_a.o
OBJ_B = $(BUILD_DIR)/sim_b.o

# Criar a pasta build, se não existir
$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)

# Compilar a biblioteca queue
$(OBJ_QUEUE): $(SRC_QUEUE) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

# Compilar sim_a (alínea a)
$(OBJ_A): $(SRC_A) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

sim_a: $(OBJ_QUEUE) $(OBJ_A)
	$(CC) $(CFLAGS) $^ -o $(BUILD_DIR)/sim_a

# Compilar sim_b (alínea b)
$(OBJ_B): $(SRC_B) | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

sim_b: $(OBJ_QUEUE) $(OBJ_B)
	$(CC) $(CFLAGS) $^ -o $(BUILD_DIR)/sim_b

# Compilar tudo
all: sim_a sim_b

# Executar sim_a
run_a: sim_a
	./$(BUILD_DIR)/sim_a

# Executar sim_b
run_b: sim_b
	./$(BUILD_DIR)/sim_b

# Limpar ficheiros compilados
clean:
	rm -rf $(BUILD_DIR)
