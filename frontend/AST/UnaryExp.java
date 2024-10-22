package frontend.AST;

import frontend.Token.TrueToken;

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
}
