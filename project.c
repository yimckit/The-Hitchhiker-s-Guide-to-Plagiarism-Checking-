#include <dirent.h> 
#include <stdio.h> 

int main()  
{  
    printf("HI\n");  
   //show there is five file 
    DIR *d;
    struct dirent *dir;
    d = opendir("/mnt/c/Users/User/Desktop/SCC110/Project3TestFiles/.");
 if (d) {
        int x = 0;
        
        while ((dir = readdir(d)) != NULL) {
            // The first .. and . are skipped or any file with "." are also skipped
            if (dir->d_name[0] != '.'){
                printf("%s\n",dir->d_name);
                x += 1;
            };
            
        };
        printf("Total file(s) processed %i\n",x);
        closedir(d);
    }
    return(0);
}
//open the file
//count the number 
