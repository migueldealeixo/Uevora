#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "protocol.h"

void format_message(const Message *msg, char *buffer) {
    snprintf(buffer, MAX_FIELD * 4, "%s|%s|%s|%s",
             msg->type,
             msg->sender,
             msg->receiver,
             msg->content);
}

int parse_message(const char *raw, Message *msg) {
    char temp[1024];
    strncpy(temp, raw, sizeof(temp));
    temp[sizeof(temp) - 1] = '\0';

    char *type = strtok(temp, "|");
    char *sender = strtok(NULL, "|");
    char *receiver = strtok(NULL, "|");
    char *content = strtok(NULL, "");  // Pode retornar NULL 

    if (!type || !sender || !receiver) {
        return 0; // Falta campo obrigatório
    }

    strncpy(msg->type, type, sizeof(msg->type) - 1);
    strncpy(msg->sender, sender, sizeof(msg->sender) - 1);
    strncpy(msg->receiver, receiver, sizeof(msg->receiver) - 1);
    msg->type[sizeof(msg->type) - 1] = '\0';
    msg->sender[sizeof(msg->sender) - 1] = '\0';
    msg->receiver[sizeof(msg->receiver) - 1] = '\0';

    if (content) {
        strncpy(msg->content, content, sizeof(msg->content) - 1);
        msg->content[sizeof(msg->content) - 1] = '\0';
    } else {
        msg->content[0] = '\0'; // campo vazio
    }

    return 1;
}
