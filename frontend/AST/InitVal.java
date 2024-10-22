package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class InitVal {
    protected List<Exp> expList;
    protected TrueToken LBRACE;
    protected List<TrueToken> COMMAList;
    protected TrueToken RBRACE;
    protected TrueToken STRCON;

    public InitVal(){
        this.expList=new ArrayList<>();
        this.COMMAList=new ArrayList<>();
    }

    public void setLBRACE(TrueToken LBRACE) {
        this.LBRACE = LBRACE;
    }

    public void setRBRACE(TrueToken RBRACE) {
        this.RBRACE = RBRACE;
    }

    public void setSTRCON(TrueToken STRCON) {
        this.STRCON = STRCON;
    }

    public void insertCOMMAList(TrueToken token){
        this.COMMAList.add(token);
    }

    public void insertExpList(Exp exp){
        this.expList.add(exp);
    }

    public String outputInitVal() {
        StringBuilder a=new StringBuilder();
        if(LBRACE!=null){
            a.append(LBRACE.toString());
            for(int i=0;i<expList.size();i++){
                a.append(expList.get(i).outputExp());
                if(i<COMMAList.size()){
                    a.append(COMMAList.get(i).toString());
                }
            }
            a.append(RBRACE.toString());
        }else if(STRCON!=null){
            a.append(STRCON.toString());
        }else{
            a.append(expList.get(0).outputExp());
        }
        a.append("<InitVal>\n");
        return a.toString();
    }

    public List<Exp> getExpList() {
        return expList;
    }
}
