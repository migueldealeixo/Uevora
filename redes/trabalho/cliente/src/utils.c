#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <arpa/inet.h>
#include "utils.h"

void trim(char *str) {
    if (!str || strlen(str) == 0) return;

    // Trim início
    char *start = str;
    while (isspace((unsigned char)*start)) start++;

    // Trim fim
    char *end = str + strlen(str) - 1;
    while (end > start && isspace((unsigned char)*end)) end--;

    *(end + 1) = '\0';

    if (start != str)
        memmove(str, start, strlen(start) + 1);
}

void log_activity(const char *msg, const char *context, struct sockaddr_in addr) {
    char ip[INET_ADDRSTRLEN];
    inet_ntop(AF_INET, &(addr.sin_addr), ip, INET_ADDRSTRLEN);
    int port = ntohs(addr.sin_port);

    printf("[LOG] %s [%s] de %s:%d\n", msg, context, ip, port);
}
