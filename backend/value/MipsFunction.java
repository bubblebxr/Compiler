package backend.value;


import backend.MipsGenerator;
import backend.reg.GlobalRegister;
import midend.value.BasicBlock;
import midend.value.Function;

import java.util.ArrayList;

/**
 * @className: MipsFunction
 * @author: bxr
 * @date: 2024/11/21 16:05
 * @description: mips function
 */

public class MipsFunction {
    protected Function irFunction;
    protected String name;
    protected Boolean isMain;
    protected GlobalRegister reg;
    protected ArrayList<MipsBasicBlock> blockList;

    public MipsFunction(Function irFunction){
        this.reg=new GlobalRegister();
        this.name=irFunction.getName().substring(1);
        isMain= this.name.equals("main");
        this.irFunction=irFunction;
        this.blockList =new ArrayList<>();
        for(BasicBlock block:irFunction.getBasicBlockList()){
            blockList.add(new MipsBasicBlock(block));
        }
    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder(name).append(":\n");
        for(MipsBasicBlock block: blockList){
            a.append(block.toString()).append("\n");
        }
        return a.toString();
    }

    public String getName() {
        return name;
    }

    public GlobalRegister getReg() {
        return reg;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void genMipsFromIr() {
        int i=0;
        for(MipsBasicBlock block:blockList){
            MipsGenerator.curBlockIndex=i++;
            block.genMipsFromIr();
        }
    }

    public ArrayList<MipsBasicBlock> getBlockList() {
        return blockList;
    }


}
