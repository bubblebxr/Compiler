int a=1;
char c=2;
int foo(){
    printf("foo pass\n");
    return 4;
}
void fun(int var,char var2){
    printf("In fun %d\n",var);
    printf("In fun %c\n",var2);
    return;
}
int main(){
    int i=getint();
    printf("i=%d\n",i);
    printf("c=%c\n",c);
    printf("a=%d\n",a);
    printf("%d\n",foo());
    fun(a,c);
    printf("after fun c=%c\n",c);
    printf("after fun a=%d\n",a);
    return 0;
}