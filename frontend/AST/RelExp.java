package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class RelExp {
    protected List<AddExp> addExpList;
    protected List<TrueToken> tokenList;

    public RelExp(){
        this.tokenList=new ArrayList<>();
        this.addExpList=new ArrayList<>();
    }

    public void insertLAddExpList(AddExp addExp){
        this.addExpList.add(addExp);
    }

    public void insertOrList(TrueToken token){
        this.tokenList.add(token);
    }

    public String outputRelExp() {
        StringBuilder a=new StringBuilder();
//        a.append(addExpList.get(0).outputAddExp());
//        if(addExpList.size()!=1){
//            a.append("<RelExp>\n");
//        }
//        for(int i=1;i<addExpList.size();i++){
//            a.append(tokenList.get(i-1).toString());
//            a.append(addExpList.get(i).outputAddExp());
//        }
        for(int i=0;i<addExpList.size();i++){
            a.append(addExpList.get(i).outputAddExp());
            if(i<tokenList.size()){
                a.append("<RelExp>\n");
                a.append(tokenList.get(i).toString());
            }
        }
        a.append("<RelExp>\n");
        return a.toString();
    }

    public List<AddExp> getAddExpList() {
        return addExpList;
    }
}
