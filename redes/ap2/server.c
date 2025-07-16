#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <time.h>


#define PORT 1300
#define BUFFER_SIZE 128

void send_datatime(int client_socket){
    time_t rawtime;
    struct tn *timeinfo;
    char buffer[BUFFER_SIZE];


    time(&rawtime);
    timeinfo = localtime(&rawtime);

    strftime(buffer,sizeof(buffer),"%c\n",timeinfo);
    write(client_socket,buffer,strlen(buffer));
}

int main(){
    int server_socket,client_socket;

    struct sockaddr_in server_addr,client_addr;
    socklen_t client_len = sizeof(client_addr);

    if((server_socket = socket(AF_INET,SOCK_STREAM,0))<0){
        
    }
}