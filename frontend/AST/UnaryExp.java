package frontend.AST;

import frontend.Token.TrueToken;
import frontend.Token.TrueType;

public class UnaryExp {
    protected PrimaryExp primaryExp;
    protected UnaryExp unaryExp;
    protected TrueToken Ident;
    protected TrueToken LPARENT;
    protected FuncRParams funcRParams;
    protected TrueToken RPARENT;
    protected TrueToken UnaryOp;

    public void setUnaryOp(TrueToken unaryOp) {
        UnaryOp = unaryOp;
    }

    public void setUnaryExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
    }

    public void setPrimaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public void setLPARENT(TrueToken LPARENT) {
        this.LPARENT = LPARENT;
    }

    public void setFuncRParams(FuncRParams funcRParams) {
        this.funcRParams = funcRParams;
    }

    public void setIdent(TrueToken ident) {
        Ident = ident;
    }

    public void setRPARENT(TrueToken RPARENT) {
        this.RPARENT = RPARENT;
    }

    public String outputUnaryExp() {
        StringBuilder a=new StringBuilder();
        if(primaryExp!=null){
            a.append(primaryExp.outputPrimaryExp());
        }
        if(Ident!=null){
            a.append(Ident.toString());
            a.append(LPARENT.toString());
            if(funcRParams!=null){
                a.append(funcRParams.outputFuncRParams());
            }
            a.append(RPARENT.toString());
        }
        if(unaryExp!=null){
            a.append(UnaryOp.toString());
            a.append("<UnaryOp>\n");
            a.append(unaryExp.outputUnaryExp());
        }
        a.append("<UnaryExp>\n");
        return a.toString();
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public TrueToken getIdent() {
        return Ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public int getUnaryExpValue() {
        if(primaryExp!=null){
            return primaryExp.getPrimaryExpValue();
        }else if(unaryExp!=null){
            TrueType type=UnaryOp.getType();
            if(type==TrueType.PLUS){
                return unaryExp.getUnaryExpValue();
            }else if(type==TrueType.MINU){
                return -unaryExp.getUnaryExpValue();
            }
        }
        return 0;
    }

    public TrueToken getUnaryOp() {
        return UnaryOp;
    }

    public Integer tryToGetValue() {
        if(primaryExp!=null){
            return primaryExp.tryToGetValue();
        }else if(unaryExp!=null){
            TrueType type=UnaryOp.getType();
            if(type==TrueType.PLUS){
                return unaryExp.tryToGetValue();
            }else if(type==TrueType.MINU){
                Integer temp=unaryExp.tryToGetValue();
                if(temp!=null){
                    return -temp;
                }else{
                    return null;
                }
            }
        }
        return null;
    }
}
