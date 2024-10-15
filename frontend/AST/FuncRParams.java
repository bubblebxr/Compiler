package frontend.AST;

import frontend.Token.TrueToken;
import java.util.ArrayList;
import java.util.List;

public class FuncRParams {
    protected List<Exp> expList;
    protected List<TrueToken> COMMAList;

    public FuncRParams(){
        this.expList=new ArrayList<>();
        this.COMMAList=new ArrayList<>();
    }

    public void insertExpList(Exp exp){
        this.expList.add(exp);
    }

    public void insertCOMMAList(TrueToken token){
        this.COMMAList.add(token);
    }

    public String outputFuncRParams() {
        StringBuilder a=new StringBuilder();
        for(int i=0;i<expList.size();i++){
            a.append(expList.get(i).outputExp());
            if(i<COMMAList.size()){
                a.append(COMMAList.get(i).toString());
            }
        }
        a.append("<FuncRParams>\n");
        return a.toString();
    }
}
