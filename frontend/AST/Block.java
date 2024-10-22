package frontend.AST;

import frontend.Token.TrueToken;

import java.util.ArrayList;
import java.util.List;

public class Block {
    protected TrueToken LBRACE;
    protected List<BlockItem> blockItemList;
    protected TrueToken RBRACE;

    public Block(){
        this.blockItemList=new ArrayList<>();
    }

    public void setLBRACE(TrueToken LBRACE) {
        this.LBRACE = LBRACE;
    }

    public void setRBRACE(TrueToken RBRACE) {
        this.RBRACE = RBRACE;
    }

    public void insertBlockItemList(BlockItem item){
        this.blockItemList.add(item);
    }

    public String outputBlock() {
        StringBuilder a=new StringBuilder();
        a.append(LBRACE.toString());
        for(BlockItem blockItem:blockItemList){
            a.append(blockItem.outputBlockItem());
        }
        a.append(RBRACE.toString());
        a.append("<Block>\n");
        return a.toString();
    }

    public List<BlockItem> getBlockItemList() {
        return blockItemList;
    }

    public TrueToken getRBRACE() {
        return RBRACE;
    }
}
