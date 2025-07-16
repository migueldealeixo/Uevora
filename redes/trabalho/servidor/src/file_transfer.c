#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <stdio.h> 

#define SIZE 1024
#include <pthread.h>

#define FILE_PORT 8080

void *tcp_file_server(void *arg) {
    int sockfd, new_sock;
    struct sockaddr_in server_addr, new_addr;
    socklen_t addr_size;
    char buffer[SIZE];
    char filename[256];

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) {
        perror("TCP socket error");
        pthread_exit(NULL);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(FILE_PORT);
    server_addr.sin_addr.s_addr = INADDR_ANY;

    if (bind(sockfd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("TCP bind error");
        close(sockfd);
        pthread_exit(NULL);
    }

    if (listen(sockfd, 5) < 0) {
        perror("TCP listen error");
        close(sockfd);
        pthread_exit(NULL);
    }

    printf("[TCP] File transfer server listening on port %d...\n", FILE_PORT);

    while (1) {
        addr_size = sizeof(new_addr);
        new_sock = accept(sockfd, (struct sockaddr*)&new_addr, &addr_size);
        if (new_sock < 0) {
            perror("TCP accept error");
            continue;
        }

        // Receber nome do ficheiro
        int n = recv(new_sock, filename, sizeof(filename), 0);
        if (n <= 0) {
            perror("TCP filename receive error");
            close(new_sock);
            continue;
        }
        filename[n] = '\0';

        // Salvar conteúdo
        FILE *fp = fopen(filename, "w");
        if (!fp) {
            perror("Error opening file for writing");
            close(new_sock);
            continue;
        }

        while ((n = recv(new_sock, buffer, SIZE, 0)) > 0) {
            fwrite(buffer, 1, n, fp);
        }

        fclose(fp);
        close(new_sock);
        printf("[TCP] File '%s' received successfully.\n", filename);
    }

    close(sockfd);
    pthread_exit(NULL);
}
