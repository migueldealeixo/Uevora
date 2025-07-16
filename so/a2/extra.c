/*// simple C program for distributing the sum of squares between X and Y ....*/
// and passing the result from child processes to parent process
#include <pthread.h>
#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>
long int sumfunction(int x, int y){
long int sum=0;
for(int i = x; i<y;i++){
sum+=i*i;
}
return sum;
}
int main()
{
long int x, y, result; // Initialize variables here, if needed
int fd1[2], fd2[2], i, status;
pid_t pid1, pid2;
printf("Enter an integer: ");
scanf("%d", &x);
printf("Enter another integer: ");
scanf("%d", &y);
pipe(fd1); //new pipe
//other pipes .....
pid1 = fork(); // fork
if(pid1 == 0){
printf("\n Starting sum of squares 1st child process \n");
// call sumfunction ....
write(fd1[1], &x,sizeof(x)); // sending result from child
exit(0);
}
pid2 = fork(); // another fork
if(pid2 == 0){
printf("\n Starting sum of squares 2nd child process \n");
// call sumfunction ....
write(fd2[1], &x,sizeof(x)); // sending result from child
exit(0);
}
// Parent
waitpid(pid1,&status,WEXITED); // waits for the end of the child process
read(fd1[0], &x, sizeof(x)); // reads pipe
result=x;
// Parent
waitpid(pid2,&status,WEXITED); // waits for other process.....
read(fd2[0], &x, sizeof(x)); // reads other pipes etc ...
result+=x;
printf("\n final result is %ld \n",result);
}