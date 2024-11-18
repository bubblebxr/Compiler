package frontend.AST;

import frontend.Token.TrueToken;
import frontend.Token.TrueType;

import java.util.ArrayList;
import java.util.List;

public class MulExp {
    protected List<UnaryExp> unaryExpList;
    /*('*' | '/' | '%') 符号存储*/
    protected List<TrueToken> tokenList;

    public MulExp(){
        this.unaryExpList=new ArrayList<>();
        this.tokenList=new ArrayList<>();
    }

    public void insertUnaryExpList(UnaryExp unaryExp){
        this.unaryExpList.add(unaryExp);
    }

    public void insertTokenList(TrueToken token){
        this.tokenList.add(token);
    }

    public String outputMulExp() {
        StringBuilder a=new StringBuilder();
//        a.append(unaryExpList.get(0).outputUnaryExp());
//        if(unaryExpList.size()!=1){
//            a.append("<MulExp>\n");
//        }
//        for(int i=1;i<unaryExpList.size();i++){
//            a.append(tokenList.get(i-1).toString());
//            a.append(unaryExpList.get(i).outputUnaryExp());
//        }
        for(int i=0;i<unaryExpList.size();i++){
            a.append(unaryExpList.get(i).outputUnaryExp());
            if(i<tokenList.size()){
                a.append("<MulExp>\n");
                a.append(tokenList.get(i).toString());
            }
        }
        a.append("<MulExp>\n");
        return a.toString();
    }

    public List<UnaryExp> getUnaryExpList() {
        return unaryExpList;
    }

    public int getMulExpValue() {
        int temp=unaryExpList.get(0).getUnaryExpValue();
        for(int i=1;i<unaryExpList.size();i++){
            TrueType type=tokenList.get(i-1).getType();
            if(type==TrueType.MULT){
                //*
                temp+=unaryExpList.get(i).getUnaryExpValue();
            }else if(type==TrueType.DIV){
                // /
                temp-=unaryExpList.get(i).getUnaryExpValue();
            }else{
                // %
                temp%=unaryExpList.get(i).getUnaryExpValue();
            }
        }
        return temp;
    }

    public List<TrueToken> getTokenList() {
        return tokenList;
    }
}
