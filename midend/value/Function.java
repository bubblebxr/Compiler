package midend.value;

import midend.Type;
import midend.Value;
import midend.value.Instruction.Jump.Br;
import midend.value.Instruction.Jump.Ret;

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

    @Override
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

    public Boolean getDeclare() {
        return isDeclare;
    }

    public ArrayList<BasicBlock> getBasicBlockList() {
        return basicBlockList;
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
            } else if(basicBlockList.size()>=2&&basicBlockList.get(basicBlockList.size()-1).checkReturn()){
                basicBlockList.get(basicBlockList.size()-1).addInstruction(new Ret(null,null,new ArrayList<>()));
            }
        }
    }

    public void fillContinueLabel(int startIndex, int endIndex,String continueBlockLabel) {
        for(int i=startIndex;i<endIndex;i++){
            for(int j=0;j<basicBlockList.get(i).getInstructionList().size();j++){
                if(basicBlockList.get(i).getInstructionList().get(j) instanceof Br &&basicBlockList.get(i).getInstructionList().get(j).getLabel1().equals("continueBlockId")){
                    basicBlockList.get(i).getInstructionList().get(j).setLabel1(continueBlockLabel);
                }
            }
        }
    }

    public void fillBreakLabel(int startIndex, int endIndex, String breakBlockId) {
        for(int i=startIndex;i<endIndex;i++){
            for(int j=0;j<basicBlockList.get(i).getInstructionList().size();j++){
                if(basicBlockList.get(i).getInstructionList().get(j) instanceof Br &&basicBlockList.get(i).getInstructionList().get(j).getLabel1().equals("breakBlockId")){
                    basicBlockList.get(i).getInstructionList().get(j).setLabel1(breakBlockId);
                }
            }
        }
    }

    public int getBasicBlockNum(){
        return basicBlockList.size();
    }

    public void fillUnCondNextBlock(int startId, int endId, BasicBlock nextBlock) {
        for(int i=startId;i<endId;i++){
            for(int j=0;j<basicBlockList.get(i).getInstructionList().size();j++){
                if(basicBlockList.get(i).getInstructionList().get(j) instanceof Br &&basicBlockList.get(i).getInstructionList().get(j).getLabel1().equals("nextBlock")){
                    basicBlockList.get(i).getInstructionList().get(j).setLabel1(nextBlock.getName());
                }
            }
        }
    }
}
