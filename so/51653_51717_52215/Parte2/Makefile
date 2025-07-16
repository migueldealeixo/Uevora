CC = gcc
CFLAGS = -Wall -Wextra -Iinclude

SOURCES = $(wildcard src/*.c)

OBJECTS = $(SOURCES:src/%.c=build/%.o)

TARGET = build/simulador

all: create_dir $(TARGET)

create_dir:
	mkdir -p build

$(TARGET): $(OBJECTS)
	$(CC) $(CFLAGS) $^ -o $@

build/%.o: src/%.c
	$(CC) $(CFLAGS) -c $< -o $@

clean:
	rm -rf build
	