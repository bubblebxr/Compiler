package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class LAndExp {
    protected List<EqExp> eqExpList;
    protected List<TrueToken> andList;

    public LAndExp(){
        this.andList=new ArrayList<>();
        this.eqExpList=new ArrayList<>();
    }

    public void insertEqExpList(EqExp eqExp){
        this.eqExpList.add(eqExp);
    }

    public void insertAndList(TrueToken token){
        this.andList.add(token);
    }

    public String outputLAndExp() {
        StringBuilder a=new StringBuilder();
//        a.append(eqExpList.get(0).outputEqExp());
//        if(eqExpList.size()!=1){
//            a.append("<LAndExp>\n");
//        }
//        for(int i=1;i<eqExpList.size();i++){
//            a.append(andList.get(i-1).toString());
//            a.append(eqExpList.get(i).outputEqExp());
//        }
        for(int i=0;i<eqExpList.size();i++){
            a.append(eqExpList.get(i).outputEqExp());
            if(i<andList.size()){
                a.append("<LAndExp>\n");
                a.append(andList.get(i).toString());
            }
        }
        a.append("<LAndExp>\n");
        return a.toString();
    }
}
