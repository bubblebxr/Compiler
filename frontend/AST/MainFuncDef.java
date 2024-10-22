package frontend.AST;

import frontend.Token.TrueToken;
import frontend.Token.TrueType;

public class MainFuncDef {
    protected TrueToken INT;
    protected TrueToken MAIN;
    protected TrueToken LPARENT;
    protected TrueToken RPARENT;
    protected Block block;

    public MainFuncDef(TrueToken t1,TrueToken t2,TrueToken t3){
        this.INT=t1;
        this.MAIN=t2;
        this.LPARENT=t3;
    }

    public void setRPARENT(TrueToken RPARENT) {
        this.RPARENT = RPARENT;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String outputMainFuncDef() {
        StringBuilder a=new StringBuilder();
        a.append(INT.toString());
        a.append(MAIN.toString());
        a.append(LPARENT.toString());
        a.append(RPARENT.toString());
        a.append(block.outputBlock());
        a.append("<MainFuncDef>\n");
        return a.toString();
    }

    public Block getBlock() {
        return block;
    }


}
