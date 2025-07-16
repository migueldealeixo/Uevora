#include <stdio.h>
#include "include/cliente.h"

int main(int argc, char *argv[]) {
    if (argc != 3) {
        fprintf(stderr, "Uso: %s <IP_servidor> <username>\n", argv[0]);
        return 1;
    }

    const char *server_ip = argv[1];
    const char *username = argv[2];

    init_client(server_ip);
    start_interface(username);
    close_client();


    /*
    TCP
    
    */

    return 0;
}
