#include <stdio.h>
#include <string.h>
#include <time.h>
#include <arpa/inet.h>
#include "utils.h"

void trim(char *str) {
    char *end;
    while (*str == ' ') str++;
    end = str + strlen(str) - 1;
    while (end > str && (*end == ' ' || *end == '\n')) end--;
    *(end+1) = '\0';
}

void get_timestamp(char *buffer, size_t buf_size) {
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    strftime(buffer, buf_size, "%Y-%m-%d %H:%M:%S", tm_info);
}

void log_activity(const char *action, const char *username, struct sockaddr_in addr) {
    char timestamp[20];
    char ip[INET_ADDRSTRLEN];

    get_timestamp(timestamp, sizeof(timestamp));
    inet_ntop(AF_INET, &addr.sin_addr, ip, INET_ADDRSTRLEN);

    printf("[%s] [%s] %s: %s\n", 
           timestamp,
           ip,
           username ? username : "Sistema",
           action);
}
