#ifdef CLIENTE_H
#define CLIENTE_H

#include <netinet/in.h>

#define BUFFER_SIZE 1024

void init_client(const char *server_ip);
void close_client();
void send_command(const char *message);
void start_interface(const char *username);

#endif