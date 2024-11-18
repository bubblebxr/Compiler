package LLVM.value;

import LLVM.Type;
import LLVM.Value;
import LLVM.value.Instruction.Jump.Ret;

import java.util.ArrayList;

public class Function extends Value {
    protected ArrayList<Argument> argumentList;
    protected ArrayList<BasicBlock> basicBlockList;
    protected Boolean isDeclare;

    /* declare function */
    public Function(String name, Type type){
        super(name,type);
        this.basicBlockList=new ArrayList<>();
        this.isDeclare=true;
    }

    /* define function */
    public Function(String name, Type type,ArrayList<Argument> argumentList){
        super(name,type);
        this.argumentList=argumentList;
        this.basicBlockList=new ArrayList<>();
        this.isDeclare=false;
    }

    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(type.toString(name));
        if(!isDeclare){
            a.append("{\n");
            for(BasicBlock basicBlock:basicBlockList){
                a.append(basicBlock.toString());
            }
            a.append("}\n");
        }
        return a.toString();
    }

    public void addBasicBlock(BasicBlock basicBlock){
        basicBlockList.add(basicBlock);
    }

    public BasicBlock getCurBasicBlock() {
        return basicBlockList.get(basicBlockList.size()-1);
    }

    public void checkReturn() {
        if(!basicBlockList.get(basicBlockList.size()-1).getInstructionList().isEmpty()){
            if(basicBlockList.get(basicBlockList.size()-1).checkReturn()){
                getCurBasicBlock().addInstruction(new Ret(null,null,new ArrayList<>()));
            }
        }else{
            if(basicBlockList.size()==1){
                getCurBasicBlock().addInstruction(new Ret(null,null,new ArrayList<>()));
            } else if(basicBlockList.size()>=2&&basicBlockList.get(basicBlockList.size()-2).checkReturn()){
                basicBlockList.get(basicBlockList.size()-2).addInstruction(new Ret(null,null,new ArrayList<>()));
            }
        }
    }
}
