#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>

int main(){
    
    if (fork() ==0){ /*This is the child process*/
		printf("i am the child with pid %d \n", getpid());
        printf("i am the parent with ppid %d \n", getppid());
        sleep(1);
	exit(0);  /* Should never get here, terminate*/
	}else{
        printf("I am the parent and my id is %d", getpid());
        sleep(30);
    }

}
