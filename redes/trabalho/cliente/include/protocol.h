#ifndef PROTOCOL_H
#define PROTOCOL_H

#define MAX_FIELD 256
#define TYPE_GROUP "GROUP"
typedef struct {
    char type[MAX_FIELD];
    char sender[MAX_FIELD];
    char receiver[MAX_FIELD];
    char content[MAX_FIELD];
} Message;

/**
 * Formata uma struct Message numa string do protocolo.
 * Exemplo: "MESSAGE|joao| |Olá a todos"
 */
void format_message(const Message *msg, char *buffer);

/**
 * Faz parsing de uma string do protocolo para uma struct Message.
 * Retorna 1 se for válido, 0 caso contrário.
 */
int parse_message(const char *raw, Message *msg);

#endif
