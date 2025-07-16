#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include "cliente.h"
#include "protocol.h"

#define BUFFER_SIZE 1024

static int sockfd;
static struct sockaddr_in server_addr;
static char global_username[50];

void init_client(const char *server_ip) {
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        perror("Erro ao criar o socket");
        exit(EXIT_FAILURE);
    }

    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(12345);
    inet_pton(AF_INET, server_ip, &server_addr.sin_addr);
}

void close_client() {
    close(sockfd);
}

void send_command(const char *message) {
    sendto(sockfd, message, strlen(message), 0, (struct sockaddr*)&server_addr, sizeof(server_addr));
}

// Thread de recebimento de mensagens
void *receive_loop(void *arg) {
    char buffer[BUFFER_SIZE];
    Message msg;
    socklen_t addr_len = sizeof(server_addr);

    while (1) {
        int n = recvfrom(sockfd, buffer, BUFFER_SIZE - 1, 0, (struct sockaddr*)&server_addr, &addr_len);
        if (n > 0) {
            buffer[n] = '\0';

            if (parse_message(buffer, &msg)) {
                if (strcmp(msg.sender, global_username) != 0) {
                    printf("\n%s->%s\n> ", msg.sender, msg.content);
                }
            } else {
                printf("\n[Servidor]: %s\n> ", buffer);
            }

            fflush(stdout);
        }
    }

    return NULL;
}

void start_interface(const char *username) {
    strncpy(global_username, username, sizeof(global_username) - 1);

    pthread_t recv_thread;
    pthread_create(&recv_thread, NULL, receive_loop, NULL);

    char input[BUFFER_SIZE];

    while (1) {
        printf("> ");
        fflush(stdout);

        if (!fgets(input, sizeof(input), stdin))
            break;

        input[strcspn(input, "\n")] = '\0';

        if (strncmp(input, "/register", 9) == 0) {
            char user[50], pass[50];
            if (sscanf(input, "/register %s %s", user, pass) == 2) {
                char msg[BUFFER_SIZE];
                snprintf(msg, sizeof(msg), "REGISTER|%s| |%s", user, pass);
                send_command(msg);
            } else {
                printf("Uso: /register <usuario> <senha>\n");
            }

        } else if (strncmp(input, "/connect", 8) == 0) {
            char user[50], pass[50];
            if (sscanf(input, "/connect %s %s", user, pass) == 2) {
                strncpy(global_username, user, sizeof(global_username) - 1);
                char msg[BUFFER_SIZE];
                snprintf(msg, sizeof(msg), "CONNECT|%s| |%s", user, pass);
                send_command(msg);
            } else {
                printf("Uso: /connect <usuario> <senha>\n");
            }

        } else if (strncmp(input, "/msg", 4) == 0) {
            char msg_content[900];
            strncpy(msg_content, input + 5, sizeof(msg_content) - 1);
            msg_content[sizeof(msg_content) - 1] = '\0';

            printf("\nVocê: %s\n> ", msg_content); 
            fflush(stdout);

            char msg[BUFFER_SIZE];
            snprintf(msg, sizeof(msg), "MESSAGE|%s| |%s", global_username, msg_content);
            send_command(msg);

        } else if (strncmp(input, "/priv", 5) == 0) {
            char dest[50], content[900];
            if (sscanf(input, "/priv %s %[^\n]", dest, content) == 2) {
                printf("\nVocê para %s: %s\n> ", dest, content); 
                fflush(stdout);

                char msg[BUFFER_SIZE];
                snprintf(msg, sizeof(msg), "PRIVATE|%s|%s|%s", global_username, dest, content);
                send_command(msg);
            } else {
                printf("Uso correto: /priv <destinatario> <mensagem>\n");
            }

        } else if (strncmp(input, "/group", 6) == 0) {
            char group[50], content[900];
            if (sscanf(input, "/group %s %[^\n]", group, content) == 2) {
                printf("\nVocê no grupo %s: %s\n> ", group, content); 
                fflush(stdout);

                char msg[BUFFER_SIZE];
                snprintf(msg, sizeof(msg), "GROUP|%s|%s|%s", global_username, group, content);
                send_command(msg);
            } else {
                printf("Uso correto: /group <nome_do_grupo> <mensagem>\n");
            }

        } else if (strncmp(input, "/join", 5) == 0) {
            char group[50];
            if (sscanf(input, "/join %s", group) == 1) {
                char msg[BUFFER_SIZE];
                snprintf(msg, sizeof(msg), "JOIN|%s|%s| ", global_username, group);
                send_command(msg);
            } else {
                printf("Uso correto: /join <nome_do_grupo>\n");
            }

        } else if (strncmp(input, "/leave", 6) == 0) {
            char group[50];
            if (sscanf(input, "/leave %s", group) == 1) {
                char msg[BUFFER_SIZE];
                snprintf(msg, sizeof(msg), "LEAVE|%s|%s| ", global_username, group);
                send_command(msg);
            } else {
                printf("Uso correto: /leave <nome_do_grupo>\n");
            }

        } else if (strncmp(input, "/quit", 5) == 0) {
            char msg[BUFFER_SIZE];
            snprintf(msg, sizeof(msg), "DISCONNECT|%s| |", global_username);
            send_command(msg);
            break;

        } else {
            printf("Comando inválido. Use /register, /connect, /msg, /priv, /group, /join, /leave ou /quit.\n");
        }
    }

    pthread_cancel(recv_thread);
    pthread_join(recv_thread, NULL);
    close_client();
}
