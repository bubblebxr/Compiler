package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class EqExp {
    protected List<RelExp> relExpList;
    protected List<TrueToken> tokenList;

    public EqExp(){
        this.relExpList=new ArrayList<>();
        this.tokenList=new ArrayList<>();
    }

    public void insertRelExpList(RelExp relExp){
        this.relExpList.add(relExp);
    }

    public void insertTokenList(TrueToken token){
        this.tokenList.add(token);
    }

    public String outputEqExp() {
        StringBuilder a=new StringBuilder();
//        a.append(relExpList.get(0).outputRelExp());
//        if(relExpList.size()!=1){
//            a.append("<EqExp>\n");
//        }
//        for(int i=1;i<relExpList.size();i++){
//            a.append(tokenList.get(i-1).toString());
//            a.append(relExpList.get(i).outputRelExp());
//        }
        for(int i=0;i<relExpList.size();i++){
            a.append(relExpList.get(i).outputRelExp());
            if(i<tokenList.size()){
                a.append("<EqExp>\n");
                a.append(tokenList.get(i).toString());
            }
        }
        a.append("<EqExp>\n");
        return a.toString();
    }

    public List<RelExp> getRelExpList() {
        return relExpList;
    }

    public List<TrueToken> getTokenList() {
        return tokenList;
    }
}
