package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class FuncFParams {
    protected List<FuncFParam> funcFParamList;
    protected List<TrueToken> COMMAList;

    public FuncFParams(){
        this.funcFParamList=new ArrayList<>();
        this.COMMAList=new ArrayList<>();
    }

    public void insertFuncFParamList(FuncFParam funcFParam){
        this.funcFParamList.add(funcFParam);
    }

    public void insertCOMMAList(TrueToken token){
        this.COMMAList.add(token);
    }

    public String outputFuncFParams() {
        StringBuilder a=new StringBuilder();
        for(int i=0;i<funcFParamList.size();i++){
            a.append(funcFParamList.get(i).outputFuncFParam());
            if(i<COMMAList.size()){
                a.append(COMMAList.get(i).toString());
            }
        }
        a.append("<FuncFParams>\n");
        return a.toString();
    }
}
