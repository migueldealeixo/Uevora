#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <time.h>
#include <sys/select.h>
#include <sys/time.h>
#include <sys/socket.h>
#include <netinet/in.h> 
#include <sys/time.h>

#include "protocol.h"
#include "utils.h"
#include "auth.h"

#define PORT 12345
#define MAX_CLIENTS 100
#define BUFFER_SIZE 1024
#define TIMEOUT 3000
#define MAX_GROUPS 50
#define MAX_MEMBROS 50


int tcp_sockfd;
#define MAX_FILE_CLIENTS 10
int file_clients[MAX_FILE_CLIENTS] ={0};

typedef struct {
    char nome[50];
    char membros[MAX_MEMBROS][50];
    int num_membros;
} Grupo;

typedef struct {
    struct sockaddr_in addr;
    char username[50];
    time_t last_seen;
    int auth;
} Cliente;

Grupo grupos[MAX_GROUPS];
int num_grupos = 0;
Cliente cliente[MAX_CLIENTS];
int num_clientes = 0;

int encontrar_grupo(const char *nome) {
    for (int i = 0; i < num_grupos; i++) {
        if (strcmp(grupos[i].nome, nome) == 0)
            return i;
    }
    return -1;
}

int esta_no_grupo(int grupo_idx, const char *username) {
    for (int i = 0; i < grupos[grupo_idx].num_membros; i++) {
        if (strcmp(grupos[grupo_idx].membros[i], username) == 0)
            return 1;
    }
    return 0;
}

void adicionar_ao_grupo(int grupo_idx, const char *username) {
    if (!esta_no_grupo(grupo_idx, username) && grupos[grupo_idx].num_membros < MAX_MEMBROS) {
        strncpy(grupos[grupo_idx].membros[grupos[grupo_idx].num_membros++], username, 50);
    }
}

void remover_do_grupo(int grupo_idx, const char *username) {
    for (int i = 0; i < grupos[grupo_idx].num_membros; i++) {
        if (strcmp(grupos[grupo_idx].membros[i], username) == 0) {
            memmove(&grupos[grupo_idx].membros[i], &grupos[grupo_idx].membros[i + 1],
                    (grupos[grupo_idx].num_membros - i - 1) * sizeof(grupos[grupo_idx].membros[i]));
            grupos[grupo_idx].num_membros--;
            break;
        }
    }
}
void salvar_grupos() {
    FILE *fp = fopen("data/groups.txt", "w");
    if (!fp) {
        perror("Erro ao abrir data/groups.txt para escrita");
        return;
    }

    for (int i = 0; i < num_grupos; i++) {
        fprintf(fp, "%s:", grupos[i].nome);
        for (int j = 0; j < grupos[i].num_membros; j++) {
            fprintf(fp, "%s", grupos[i].membros[j]);
            if (j < grupos[i].num_membros - 1)
                fprintf(fp, ",");
        }
        fprintf(fp, "\n");
    }

    fclose(fp);
}

void carregar_grupos() {
    FILE *fp = fopen("data/groups.txt", "r");
    if (!fp) return;

    char linha[256];
    while (fgets(linha, sizeof(linha), fp)) {
        linha[strcspn(linha, "\n")] = '\0';

        char *token = strtok(linha, ":");
        if (!token) continue;

        Grupo g;
        strncpy(g.nome, token, 50);
        g.num_membros = 0;

        token = strtok(NULL, ":");
        if (token) {
            char *membro = strtok(token, ",");
            while (membro && g.num_membros < MAX_MEMBROS) {
                strncpy(g.membros[g.num_membros++], membro, 50);
                membro = strtok(NULL, ",");
            }
        }

        if (num_grupos < MAX_GROUPS) {
            grupos[num_grupos++] = g;
        }
    }

    fclose(fp);
}

void send_response(int sockfd, struct sockaddr_in addr, const char *msg) {
    sendto(sockfd, msg, strlen(msg), 0, (struct sockaddr*)&addr, sizeof(addr));
}

void broadcast(int sockfd, const char *msg, Cliente *sender) {
    for (int i = 0; i < num_clientes; i++) {
        if (cliente[i].auth && (!sender || memcmp(&cliente[i].addr, &sender->addr, sizeof(sender->addr)) != 0)) {
            send_response(sockfd, cliente[i].addr, msg);
        }
    }
}

void handle_message(int sockfd, struct sockaddr_in client_addr, char *raw_msg) {
    Message msg;
    if (!parse_message(raw_msg, &msg)) {
        send_response(sockfd, client_addr, "ERROR|Servidor| |Formato invalido");
        return;
    }

    trim(msg.sender);
    trim(msg.receiver);
    trim(msg.content);
    log_activity("Recebido comando", msg.type, client_addr);

    if (strcmp(msg.type, "REGISTER") == 0) {
        if (strlen(msg.sender) == 0 || strlen(msg.content) == 0) {
            send_response(sockfd, client_addr, "ERROR|Servidor| |Formato invalido");
        } else {
            int result = register_user(msg.sender, msg.content);
            if (result == -1)
                send_response(sockfd, client_addr, "ERROR|Servidor| |Usuario ja existe");
            else if (result == 0)
                send_response(sockfd, client_addr, "ERROR|Servidor| |Erro ao registrar");
            else
                send_response(sockfd, client_addr, "OK|Servidor| |Registro concluido");
        }
    }
    else if (strcmp(msg.type, "CONNECT") == 0) {
        if (!check_credentials(msg.sender, msg.content)) {
            send_response(sockfd, client_addr, "ERROR|Servidor| |Credenciais invalidas");
            return;
        }

        for (int i = 0; i < num_clientes; i++) {
            if (strcmp(cliente[i].username, msg.sender) == 0) {
                send_response(sockfd, client_addr, "ERROR|Servidor| |Usuario ja conectado");
                return;
            }
        }

        if (num_clientes < MAX_CLIENTS) {
            Cliente novo;
            novo.addr = client_addr;
            strncpy(novo.username, msg.sender, sizeof(novo.username) - 1);
            novo.username[sizeof(novo.username) - 1] = '\0';
            novo.last_seen = time(NULL);
            novo.auth = 1;
            cliente[num_clientes++] = novo;

            send_response(sockfd, client_addr, "OK|Servidor| |Autenticado com sucesso");

            Message sysmsg = {.type = "SYSTEM"};
            snprintf(sysmsg.content, sizeof(sysmsg.content), "%.200s entrou no chat", msg.sender);
            strncpy(sysmsg.sender, "Servidor", MAX_FIELD);
            sysmsg.receiver[0] = '\0';

            char formatted[BUFFER_SIZE];
            format_message(&sysmsg, formatted);
            broadcast(sockfd, formatted, &novo);
        } else {
            send_response(sockfd, client_addr, "ERROR|Servidor| |Servidor cheio");
        }
    }
    else if (strcmp(msg.type, "MESSAGE") == 0) {
        char formatted[BUFFER_SIZE];
        format_message(&msg, formatted);
        broadcast(sockfd, formatted, NULL);
    }
    else if (strcmp(msg.type, "PRIVATE") == 0) {
        int found = 0;
        for (int i = 0; i < num_clientes; i++) {
            if (strcmp(cliente[i].username, msg.receiver) == 0 && cliente[i].auth) {
                char formatted[BUFFER_SIZE];
                format_message(&msg, formatted);
                send_response(sockfd, cliente[i].addr, formatted);
                found = 1;
                break;
            }
        }
        if (!found) {
            char error_msg[BUFFER_SIZE];
            snprintf(error_msg, sizeof(error_msg), "ERROR|Servidor| |Usuário %s não encontrado", msg.receiver);
            send_response(sockfd, client_addr, error_msg);
        }
    }
    else if (strcmp(msg.type, "JOIN") == 0) {
        int idx = encontrar_grupo(msg.receiver);
        if (idx == -1 && num_grupos < MAX_GROUPS) {
            Grupo novo;
            strncpy(novo.nome, msg.receiver, 50);
            novo.num_membros = 0;
            grupos[num_grupos++] = novo;
            idx = num_grupos - 1;
        }
        if (idx != -1) {
            adicionar_ao_grupo(idx, msg.sender);
            salvar_grupos();
            char resp[BUFFER_SIZE];
            snprintf(resp, sizeof(resp), "OK|Servidor| |Entrou no grupo %s", grupos[idx].nome);
            send_response(sockfd, client_addr, resp);
        }
    }
    else if (strcmp(msg.type, "LEAVE") == 0) {
        int idx = encontrar_grupo(msg.receiver);
        if (idx != -1) {
            remover_do_grupo(idx, msg.sender);
            salvar_grupos();
            char resp[BUFFER_SIZE];
            snprintf(resp, sizeof(resp), "OK|Servidor| |Saiu do grupo %s", grupos[idx].nome);
            send_response(sockfd, client_addr, resp);
        } else {
            send_response(sockfd, client_addr, "ERROR|Servidor| |Grupo nao encontrado");
        }
    }
    else if (strcmp(msg.type, "GROUP") == 0) {
        int idx = encontrar_grupo(msg.receiver);
        if (idx == -1) {
            send_response(sockfd, client_addr, "ERROR|Servidor| |Grupo nao encontrado");
            return;
        }

        if (!esta_no_grupo(idx, msg.sender)) {
            send_response(sockfd, client_addr, "ERROR|Servidor| |Nao pertence ao grupo");
            return;
        }

        char formatted[BUFFER_SIZE];
        format_message(&msg, formatted);

        for (int i = 0; i < grupos[idx].num_membros; i++) {
            const char *dest = grupos[idx].membros[i];
            for (int j = 0; j < num_clientes; j++) {
                if (strcmp(cliente[j].username, dest) == 0 && cliente[j].auth) {
                    send_response(sockfd, cliente[j].addr, formatted);
                    break;
                }
            }
        }
    }
    else if (strcmp(msg.type, "DISCONNECT") == 0) {
        for (int i = 0; i < num_clientes; i++) {
            if (memcmp(&cliente[i].addr, &client_addr, sizeof(client_addr)) == 0) {
                Message sysmsg = {.type = "SYSTEM"};
                snprintf(sysmsg.content, MAX_FIELD, "%s saiu do chat", cliente[i].username);
                strncpy(sysmsg.sender, "Servidor", MAX_FIELD);
                sysmsg.receiver[0] = '\0';

                char formatted[BUFFER_SIZE];
                format_message(&sysmsg, formatted);
                broadcast(sockfd, formatted, NULL);

                memmove(&cliente[i], &cliente[i + 1], (num_clientes - i - 1) * sizeof(Cliente));
                num_clientes--;
                break;
            }
        }
    }
    else if (strcmp(msg.type, "SEND_FILE") == 0) {
        char notif[BUFFER_SIZE];
        snprintf(notif, sizeof(notif), "FILE_READY|%s|%s|%s", msg.sender, msg.receiver, msg.content);
        
        for (int i = 0; i < num_clientes; i++) {
            if (strcmp(cliente[i].username, msg.receiver) == 0) {
                send_response(sockfd, cliente[i].addr, notif);
                break;
            }
        }
    
        send_response(sockfd, client_addr, "OK|Servidor| |Inicie envio TCP para porta 8080");
    }
    
    else {
        send_response(sockfd, client_addr, "ERROR|Servidor| |Comando desconhecido");
    }
}

void check_timeouts(int sockfd) {
    time_t now = time(NULL);
    for (int i = 0; i < num_clientes; i++) {
        if (difftime(now, cliente[i].last_seen) > TIMEOUT) {
            Message sysmsg = {.type = "SYSTEM"};
            snprintf(sysmsg.content, MAX_FIELD, "%s foi desconectado por inatividade", cliente[i].username);
            strncpy(sysmsg.sender, "Servidor", MAX_FIELD);
            sysmsg.receiver[0] = '\0';

            char formatted[BUFFER_SIZE];
            format_message(&sysmsg, formatted);
            broadcast(sockfd, formatted, NULL);

            memmove(&cliente[i], &cliente[i + 1], (num_clientes - i - 1) * sizeof(Cliente));
            num_clientes--;
            i--;
        }
    }
}





int main() {
    int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    struct sockaddr_in servaddr = {
        .sin_family = AF_INET,
        .sin_port = htons(PORT),
        .sin_addr.s_addr = INADDR_ANY
    };

    bind(sockfd, (struct sockaddr*)&servaddr, sizeof(servaddr));
    log_activity("Servidor iniciado", "Sistema", servaddr);
    carregar_grupos();

    while (1) {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        char buffer[BUFFER_SIZE];

        fd_set readfds;
        FD_ZERO(&readfds);
        FD_SET(sockfd, &readfds);

        struct timeval tv = {.tv_sec = 1, .tv_usec = 0};

        if (select(sockfd + 1, &readfds, NULL, NULL, &tv) > 0) {
            int n = recvfrom(sockfd, buffer, BUFFER_SIZE - 1, 0, (struct sockaddr*)&client_addr, &addr_len);
            if (n > 0) {
                buffer[n] = '\0';
                handle_message(sockfd, client_addr, buffer);
            }
        }

        check_timeouts(sockfd);
    }

    close(sockfd);
    return 0;
}
