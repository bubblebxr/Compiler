package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class LOrExp {
    protected List<LAndExp> lAndExpList;
    protected List<TrueToken> orList;

    public LOrExp(){
        this.orList=new ArrayList<>();
        this.lAndExpList=new ArrayList<>();
    }

    public void insertLAndExpList(LAndExp lAndExp){
        this.lAndExpList.add(lAndExp);
    }

    public void insertOrList(TrueToken token){
        this.orList.add(token);
    }

    public String outputLOrExp() {
        StringBuilder a=new StringBuilder();
//        a.append(lAndExpList.get(0).outputLAndExp());
//        if(lAndExpList.size()!=1){
//            a.append("<LOrExp>\n");
//        }
//        for(int i=1;i<lAndExpList.size();i++){
//            a.append(orList.get(i-1).toString());
//            a.append(lAndExpList.get(i).outputLAndExp());
//        }
        for(int i=0;i<lAndExpList.size();i++){
            a.append(lAndExpList.get(i).outputLAndExp());
            if(i<orList.size()){
                a.append("<LOrExp>\n");
                a.append(orList.get(i).toString());
            }
        }
        a.append("<LOrExp>\n");
        return a.toString();
    }

    public List<LAndExp> getlAndExpList() {
        return lAndExpList;
    }
}
