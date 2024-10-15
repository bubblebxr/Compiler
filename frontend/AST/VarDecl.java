package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class VarDecl{
    protected TrueToken BType;
    protected List<VarDef> varDefList;
    protected List<TrueToken> TokenList;
    protected TrueToken SEMICN;

    public VarDecl(){
        this.varDefList=new ArrayList<>();
        this.TokenList=new ArrayList<>();
    }

    public void setBType(TrueToken BType) {
        this.BType = BType;
    }

    public void insertVarDefList(VarDef varDef){
        this.varDefList.add(varDef);
    }

    public void insertTokenList(TrueToken token){
        this.TokenList.add(token);
    }

    public void setSEMICN(TrueToken SEMICN) {
        this.SEMICN = SEMICN;
    }

    public String outputVarDecl() {
        StringBuilder a= new StringBuilder();
        a.append(BType.toString());
        for(int i=0;i<varDefList.size();i++){
            a.append(varDefList.get(i).outputVarDef());
            if(i<TokenList.size()){
                a.append(TokenList.get(i).toString());
            }
        }
        a.append(SEMICN.toString());
        a.append("<VarDecl>\n");
        return a.toString();
    }
}
