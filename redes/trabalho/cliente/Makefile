# Compilador e flags
CC = gcc
CFLAGS = -Wall -Wextra -Iinclude
LDFLAGS =
TARGET = build/client

# Diretórios
OBJ_DIR = obj
BUILD_DIR = build

# Ficheiros fonte
SRC = \
    src/cliente.c \
    src/protocol.c \
    src/utils.c \
    main.c

# Ficheiros objeto (convertidos para obj/*.o)
OBJ = $(SRC:.c=.o)
OBJ := $(addprefix $(OBJ_DIR)/, $(OBJ))

# Regra principal
all: $(TARGET)

# Compilar o executável
$(TARGET): $(OBJ)
	@mkdir -p $(BUILD_DIR)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

# Compilar .c para obj/.o
$(OBJ_DIR)/%.o: %.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

# Limpeza
clean:
	rm -rf $(OBJ_DIR) $(BUILD_DIR)

# Execução
run: $(TARGET)
	./$(TARGET) 127.0.0.1 meu_user

.PHONY: all clean run
