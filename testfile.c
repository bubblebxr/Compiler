int getchar(){
    char c;
    scanf("%c",&c);
    return (int)c;
}
int getint(){
    int t;
    scanf("%d",&t);
    while(getchar()!='\n');
    return t;
}


int a[10];
const int b[2]={1,2};
char aa[10];
const char bb[10]="hello";
int foo2(int a[],int b,char c[],char d){
    return 0;
}
int foo(int a[],int b,char c[],char d){
    foo2(a,b,c,d);
    return 0;
}
int main(){
    const char a[6]="1";
    return 0;
}