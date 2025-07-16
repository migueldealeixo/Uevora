# Nome do executável
TARGET_SERVER = server

# Caminhos
SRC_DIR = src
BUILD_DIR = build
INCLUDE_DIR = include  # Diretório de includes

# Ficheiros fonte
SRC_SERVER = $(SRC_DIR)/server.c
SRC_UTILS = $(SRC_DIR)/utils.c
SRC_AUTH = $(SRC_DIR)/auth.c
SRC_PROTOCOL = $(SRC_DIR)/protocol.c

# Diretório de saída
BUILD_SERVER = $(BUILD_DIR)/$(TARGET_SERVER)

# Compilador e flags
CC = gcc
CFLAGS = -Wall -Wextra -O2 -I$(INCLUDE_DIR)

# Regra principal
all: $(BUILD_SERVER)

# Compilar servidor
$(BUILD_SERVER): $(SRC_SERVER) $(SRC_UTILS) $(SRC_AUTH) $(SRC_PROTOCOL)
	@mkdir -p $(BUILD_DIR)
	$(CC) $(CFLAGS) $(SRC_SERVER) $(SRC_UTILS) $(SRC_AUTH) $(SRC_PROTOCOL) -o $(BUILD_SERVER)

# Limpar
clean:
	rm -rf $(BUILD_DIR)

.PHONY: all clean
