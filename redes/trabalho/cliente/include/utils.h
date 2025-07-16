#ifndef UTILS_H
#define UTILS_H

#include <netinet/in.h>

/**
 * Remove espaços em branco no início e fim da string (in-place).
 */
void trim(char *str);

/**
 * Mostra atividade no terminal com contexto (pode ser usado em debug).
 */
void log_activity(const char *msg, const char *context, struct sockaddr_in addr);

#endif
