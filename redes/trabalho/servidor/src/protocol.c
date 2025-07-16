#include <stdio.h>
#include <string.h>
#include "protocol.h"

int parse_message(char *raw, Message *msg) {
    printf("[DEBUG] Raw input: '%s'\n", raw); 

    
    char *token = strtok(raw, "|");
    if (!token) return 0;
    strncpy(msg->type, token, MAX_FIELD - 1);
    msg->type[MAX_FIELD - 1] = '\0'; 

    token = strtok(NULL, "|");
    if (!token) return 0;
    strncpy(msg->sender, token, MAX_FIELD - 1);
    msg->sender[MAX_FIELD - 1] = '\0';

    token = strtok(NULL, "|");
    if (!token) return 0;
    strncpy(msg->receiver, token, MAX_FIELD - 1);
    msg->receiver[MAX_FIELD - 1] = '\0';

    token = strtok(NULL, "|");
    if (!token) return 0;
    strncpy(msg->content, token, MAX_FIELD - 1);
    msg->content[MAX_FIELD - 1] = '\0';

    printf("[DEBUG] Mensagem interpretada:\n");
    printf("  Tipo:     '%s'\n", msg->type);
    printf("  Sender:   '%s'\n", msg->sender);
    printf("  Receiver: '%s'\n", msg->receiver);
    printf("  Content:  '%s'\n", msg->content);
    return 1;
}

void format_message(const Message *msg, char *buffer) {
    snprintf(
        buffer, 
        (MAX_FIELD - 1) * 4 + 4, 
        "%s|%s|%s|%s", 
        msg->type, 
        msg->sender, 
        msg->receiver, 
        msg->content
    );
    printf("[DEBUG] Mensagem formatada: '%s'\n", buffer);
}