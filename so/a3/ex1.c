
#include <pthread.h>
#include <stdio.h>
/* Prints x’s to stderr. The parameter is unused. Does not return. */
void* print_xs (void* unused)
{
int y = 0;
while (y <=50) {
fputc ('x', stderr);
y++;
}
return NULL;
}
/* The main program. */
int main ()
{
pthread_t thread_id;
/* Create a new thread. The new thread will run the print_xs() function. */
pthread_create (&thread_id, NULL, &print_xs, NULL);
/* Print s continuously to stderr. */


int z = 10;
while (z > 0) {
fputc ('o', stderr);
z--;
}
return 0;
}