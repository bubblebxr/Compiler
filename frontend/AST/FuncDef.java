package frontend.AST;

import frontend.Token.TrueToken;

public class FuncDef {
    protected TrueToken FuncType;
    protected TrueToken Ident;
    protected TrueToken LPARENT;
    protected FuncFParams funcFParams;
    protected TrueToken RPARENT;
    protected Block block;

    public FuncDef(TrueToken FuncType,TrueToken Ident,TrueToken LPARENT){
        this.FuncType=FuncType;
        this.Ident=Ident;
        this.LPARENT=LPARENT;
    }

    public void setFuncFParams(FuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }

    public void setRPARENT(TrueToken RPARENT) {
        this.RPARENT = RPARENT;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String outputFuncDef() {
        StringBuilder a=new StringBuilder();
        a.append(FuncType.toString());
        a.append("<FuncType>\n");
        a.append(Ident.toString());
        a.append(LPARENT.toString());
        if(funcFParams!=null){
            a.append(funcFParams.outputFuncFParams());
        }
        a.append(RPARENT.toString());
        a.append(block.outputBlock());
        a.append("<FuncDef>\n");
        return a.toString();
    }

    public TrueToken getIdent() {
        return Ident;
    }

    public TrueToken getFuncType() {
        return FuncType;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public Block getBlock() {
        return block;
    }
}
