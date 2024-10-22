package frontend.AST;

import frontend.Token.TrueToken;
import frontend.Token.TrueType;

import java.util.ArrayList;
import java.util.List;

public class ConstDecl {
    protected TrueToken constToken;
    protected TrueToken BType;
    protected List<ConstDef> constDefList;
    protected List<TrueToken> COMMAList;
    protected TrueToken SEMICN;

    public ConstDecl(TrueToken constToken,TrueToken BType){
        this.constToken=constToken;
        this.BType=BType;
        constDefList=new ArrayList<>();
        COMMAList=new ArrayList<>();
    }

    public void insertConstDefList(ConstDef constDef){
        this.constDefList.add(constDef);
    }

    public void insertCOMMAList(TrueToken COMMA){
        this.COMMAList.add(COMMA);
    }

    public void setSEMICN(TrueToken SEMICN) {
        this.SEMICN = SEMICN;
    }

    public String outputConstDecl() {
        StringBuilder a= new StringBuilder();
        a.append(constToken.toString());
        a.append(BType.toString());
        for(int i=0;i<constDefList.size();i++){
            a.append(constDefList.get(i).outputConstDef());
            if(i<COMMAList.size()){
                a.append(COMMAList.get(i).toString());
            }
        }
        a.append(SEMICN.toString());
        a.append("<ConstDecl>\n");
        return a.toString();
    }

    public List<ConstDef> getConstDefList() {
        return constDefList;
    }

    public TrueToken getBType() {
        return BType;
    }
}
