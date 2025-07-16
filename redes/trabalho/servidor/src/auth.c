#include <stdio.h>
#include <string.h>
#include "auth.h"

#include "grupo.h"

#define MAX_GROUPS 50
#define USER_FILE "data/users.txt"
#define GROUP_FILE "data/groups.txt"

int check_credentials(const char *username, const char *password) {
    FILE *fp = fopen(USER_FILE, "r");
    if (!fp) return 0;

    char line[100];
    while (fgets(line, sizeof(line), fp)) {
        line[strcspn(line, "\n")] = '\0';
        char saved_user[50], saved_pass[50];
        if (sscanf(line, "%[^:]:%s", saved_user, saved_pass) != 2) continue;
        if (strcmp(saved_user, username) == 0 && strcmp(saved_pass, password) == 0) {
            fclose(fp);
            return 1;
        }
    }
    fclose(fp);
    return 0;
}

int register_user(const char *username, const char *password) {
    FILE *fp = fopen(USER_FILE, "a+");
    if (!fp) return 0;

    char line[100];
    while (fgets(line, sizeof(line), fp)) {
        char saved_user[50];
        sscanf(line, "%[^:]", saved_user);
        if (strcmp(saved_user, username) == 0) {
            fclose(fp);
            return -1; 
        }
    }

    fprintf(fp, "%s:%s\n", username, password);
    fclose(fp);
    return 1; // Sucesso
}
