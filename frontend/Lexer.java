package frontend;

import frontend.Token.ErrorToken;
import frontend.Token.ErrorType;
import frontend.Token.TrueType;
import frontend.Token.TrueToken;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static frontend.Token.TrueToken.checkToken;

public class Lexer{
    public static Boolean isError=false;

    /*错误输出列表*/
    private static List<ErrorToken> errorList=new ArrayList<>();

    /*正确输出列表*/
    private static List<TrueToken> TokenList=new ArrayList<>();

    public static List<TrueToken> getTokenList() {
        return TokenList;
    }

    public static List<ErrorToken> getErrorList() {
        return errorList;
    }

    private static Set<Integer> ErrorLineNumber=new HashSet<>();

    public static Set<Integer> getErrorLineNumber() {
        return ErrorLineNumber;
    }

    public static Boolean analyze(String line, Boolean isInAnnotation, int lineNumber){
        int lineLength=line.length();

        for(int i=0;i<lineLength;i++){
            if(isInAnnotation){
                if(line.charAt(i)=='*'&&i<lineLength-1&&line.charAt(i+1)=='/'){
                    isInAnnotation=false;
                    i++;
                    continue;
                }else{
                    continue;
                }
            }
            char temp=line.charAt(i);
            char next='\0';
            if(i<lineLength-1)next=line.charAt(i+1);
            if(temp=='!'){
                if(next!='='){
                    TrueToken token=new TrueToken("!", TrueType.NOT,lineNumber);
                    TokenList.add(token);
                }else{
                    i++;
                    TrueToken token=new TrueToken("!=", TrueType.NEQ,lineNumber);
                    TokenList.add(token);
                }
            }else if(temp=='&'){
                if(next=='&'){
                    i++;
                    TrueToken token=new TrueToken("&&", TrueType.AND,lineNumber);
                    TokenList.add(token);
                }else{
                    ErrorToken token=new ErrorToken(lineNumber, ErrorType.a);
                    errorList.add(token);
                    TrueToken token1=new TrueToken("&&", TrueType.AND,lineNumber);
                    TokenList.add(token1);
                    //TODO:为什么注掉这行代码就不会出现RuntimeError了呢，我也不懂，反正如果出现了验证一下就是了
                    ErrorLineNumber.add(lineNumber);
                }
            }else if(temp=='|'){
                if(next=='|'){
                    i++;
                    TrueToken token=new TrueToken("||", TrueType.OR,lineNumber);
                    TokenList.add(token);
                }else{
                    ErrorToken token=new ErrorToken(lineNumber, ErrorType.a);
                    errorList.add(token);
                    TrueToken token1=new TrueToken("||", TrueType.OR,lineNumber);
                    TokenList.add(token1);
                    ErrorLineNumber.add(lineNumber);
                }
            }else if(temp=='+'){
                TrueToken token=new TrueToken("+", TrueType.PLUS,lineNumber);
                TokenList.add(token);
            }else if(temp=='-'){
                TrueToken token=new TrueToken("-", TrueType.MINU,lineNumber);
                TokenList.add(token);
            }else if(temp=='*'){
                TrueToken token=new TrueToken("*", TrueType.MULT,lineNumber);
                TokenList.add(token);
            }else if(temp=='/'){
                if(next=='/'){//单行注释
                    break;
                }else if(next=='*'){//多行注释
                    isInAnnotation=true;
                    i++;
                    continue;
                }
                TrueToken token=new TrueToken("/", TrueType.DIV,lineNumber);
                TokenList.add(token);
            }else if(temp=='%'){
                TrueToken token=new TrueToken("%", TrueType.MOD,lineNumber);
                TokenList.add(token);
            }else if(temp=='<'){
                if(next=='='){
                    i++;
                    TrueToken token=new TrueToken("<=", TrueType.LEQ,lineNumber);
                    TokenList.add(token);
                }else{
                    TrueToken token=new TrueToken("<", TrueType.LSS,lineNumber);
                    TokenList.add(token);
                }
            }else if(temp=='>'){
                if(next=='='){
                    i++;
                    TrueToken token=new TrueToken(">=", TrueType.GEQ,lineNumber);
                    TokenList.add(token);
                }else{
                    TrueToken token=new TrueToken(">", TrueType.GRE,lineNumber);
                    TokenList.add(token);
                }
            }else if(temp=='='){
                if(next=='='){
                    i++;
                    TrueToken token=new TrueToken("==", TrueType.EQL,lineNumber);
                    TokenList.add(token);
                }else{
                    TrueToken token=new TrueToken("=", TrueType.ASSIGN,lineNumber);
                    TokenList.add(token);
                }
            }else if(temp==';'){
                TrueToken token=new TrueToken(";", TrueType.SEMICN,lineNumber);
                TokenList.add(token);
            }else if(temp==','){
                TrueToken token=new TrueToken(",", TrueType.COMMA,lineNumber);
                TokenList.add(token);
            }else if(temp=='('){
                TrueToken token=new TrueToken("(", TrueType.LPARENT,lineNumber);
                TokenList.add(token);
            }else if(temp==')'){
                TrueToken token=new TrueToken(")", TrueType.RPARENT,lineNumber);
                TokenList.add(token);
            }else if(temp=='['){
                TrueToken token=new TrueToken("[", TrueType.LBRACK,lineNumber);
                TokenList.add(token);
            }else if(temp==']'){
                TrueToken token=new TrueToken("]", TrueType.RBRACK,lineNumber);
                TokenList.add(token);
            }else if(temp=='{'){
                TrueToken token=new TrueToken("{", TrueType.LBRACE,lineNumber);
                TokenList.add(token);
            }else if(temp=='}'){
                TrueToken token=new TrueToken("}", TrueType.RBRACE,lineNumber);
                TokenList.add(token);
            }else if(temp=='\"'){//字符串StringConst
                StringBuilder save= new StringBuilder("\"");
                for(int j=i+1;j<lineLength;j++){
                    if(line.charAt(j)=='\"'){
                        save.append("\"");
                        TrueToken token=new TrueToken(save.toString(), TrueType.STRCON,lineNumber);
                        TokenList.add(token);
                        i=j;
                        break;
                    }
                    save.append(line.charAt(j));
                }
            }else if(temp=='_'||Character.isLetter(temp)){//标识符或变量名Ident
                StringBuilder save= new StringBuilder(String.valueOf(temp));
                if(i+1<lineLength){
                    int j;
                    for(j=i+1;j<lineLength;j++){
                        char b=line.charAt(j);
                        if(b=='_'||Character.isLetter(b)||Character.isDigit(b)){
                            save.append(b);
                        }else{
                            break;
                        }
                    }
                    i=j-1;
                }
                //检查是否是标识符
                TrueToken token=new TrueToken(save.toString(), checkToken(save.toString()),lineNumber);
                TokenList.add(token);
            }else if(Character.isDigit(temp)) {//数字IntConst
                StringBuilder save= new StringBuilder();
                int j,flag=0;
                for(j=i;j<lineLength;j++){
                    char b=line.charAt(j);
                    if(Character.isDigit(b)){
                        save.append(b);
                    }else{
                        i=j-1;
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    i=j;
                }
                TrueToken token=new TrueToken(save.toString(), TrueType.INTCON,lineNumber);
                TokenList.add(token);
            }else if(temp=='\''){//字符CharConst
                StringBuilder save = new StringBuilder("'");
                if(next!='\0'){
                    if(next=='\\'){
                        if(i+2<lineLength){
                            char d=line.charAt(i+2);
                            if((d=='a'||d=='b'||d=='t'||d=='n'||d=='v'||d=='f'||d=='"'||d=='\''||d=='\\'||d=='0')&&i+3<lineLength&&line.charAt(i+3)=='\''){
                                save.append(next);
                                save.append(d);
                                save.append('\'');
                                i+=3;
                                TrueToken token=new TrueToken(save.toString(), TrueType.CHRCON,lineNumber);
                                TokenList.add(token);
                            }
                        }
                    }else if(next>=32&&next<=126&&i+2<lineLength&&line.charAt(i+2)=='\''){
                        save.append(next);
                        save.append('\'');
                        i+=2;
                        TrueToken token=new TrueToken(save.toString(), TrueType.CHRCON,lineNumber);
                        TokenList.add(token);
                    }
                }
            }
        }
        return isInAnnotation;
    }

    public static String outTrueToken(){
        StringBuilder a= new StringBuilder();
        for(TrueToken i:TokenList){
            a.append(i.toString());
        }
        return a.toString();
    }

    public static String outFalseToken(){
        StringBuilder a= new StringBuilder();
        for(ErrorToken i:errorList){
            a.append(i.toString());
        }
        return a.toString();
    }
}