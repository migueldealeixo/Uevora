// protocol.h
#ifndef PROTOCOL_H
#define PROTOCOL_H

#define MAX_FIELD 256

typedef struct {
    char type[MAX_FIELD];
    char sender[MAX_FIELD];
    char receiver[MAX_FIELD];
    char content[MAX_FIELD];
    int file_port;
} Message;

int parse_message(char *raw, Message *msg);
void format_message(const Message *msg, char *buffer);

#endif
