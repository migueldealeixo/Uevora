SRC_DIR = src
BUILD_DIR = build

SRC = $(SRC_DIR)/inputs_part1.c $(SRC_DIR)/mem.c

TARGET = $(BUILD_DIR)/simulador

CC = gcc
CFLAGS = -Wall -Wextra -O2

all: $(TARGET)

$(TARGET): $(SRC)
	@mkdir -p $(BUILD_DIR)
	$(CC) $(CFLAGS) -o $@ $^

clean:
	rm -rf $(BUILD_DIR)

.PHONY: all clean
