#ifndef UTILS_H
#define UTILS_H

#include <netinet/in.h>

void trim(char *str);
void get_timestamp(char *buffer, size_t buf_size);
void log_activity(const char *action, const char *username, struct sockaddr_in addr);

#endif
