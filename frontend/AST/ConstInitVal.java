package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class ConstInitVal {
    protected TrueToken LBRACE;
    protected List<ConstExp> constExpList;
    protected List<TrueToken> COMMAList;
    protected TrueToken RBRACE;
    /*2:StringConst，1：普通变量，0：一维数组*/
    protected int type;
    protected TrueToken StringConst;

    public ConstInitVal(int type,TrueToken LBRACE){
        this.type=type;
        this.LBRACE=LBRACE;
        this.constExpList=new ArrayList<>();
        this.COMMAList=new ArrayList<>();
    }

    public ConstInitVal(int type){
        this.type=type;
        this.constExpList=new ArrayList<>();
    }

    public void insertConstExpList(ConstExp constExp){
        this.constExpList.add(constExp);
    }

    public void insertCOMMAList(TrueToken COMMA){
        this.COMMAList.add(COMMA);
    }

    public void setRBRACE(TrueToken RBRACE) {
        this.RBRACE = RBRACE;
    }

    public void setType(int type) {
        this.type =type;
    }

    public void setStringConst(TrueToken stringConst) {
        StringConst = stringConst;
    }

    public String outputConstInitVal() {
        StringBuilder a=new StringBuilder();
        if(LBRACE!=null){
            a.append(LBRACE.toString());
            for(int i=0;i<constExpList.size();i++){
                a.append(constExpList.get(i).outputConstExp());
                if(i<COMMAList.size()){
                    a.append(COMMAList.get(i).toString());
                }
            }
            a.append(RBRACE.toString());
        }else if(StringConst!=null){
            a.append(StringConst.toString());
        }else{
            a.append(constExpList.get(0).outputConstExp());
        }
        a.append("<ConstInitVal>\n");
        return a.toString();
    }

    public List<ConstExp> getConstExpList() {
        return constExpList;
    }
}
