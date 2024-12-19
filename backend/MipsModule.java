package backend;


import backend.Instruction.Memory.Lw;
import backend.Instruction.Memory.Sw;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Addi;
import backend.Instruction.Operate.Move;
import backend.reg.MipsMem;
import backend.value.*;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import static backend.MipsGenerator.curBlockIndex;
import static backend.MipsGenerator.curFuncIndex;

/**
 * @className: MipsModule
 * @author: bxr
 * @date: 2024/11/30 20:03
 * @description: mips module -- ir module
 */

public class MipsModule {
    protected ArrayList<MipsFunction> functionList;
    // 专门存储全局变量
    protected ArrayList<MipsGlobalVariable> varList;
    // 专门存储print str
    protected ArrayList<MipsStr> printStrList;
    // 专门存储const global，不用输出到.data，找不到变量时直接从里面取出来
    protected Map<String, MipsConstGlobalVariable> constGlobalList;
    //  存储在所有函数之前需要输出对sp指令等
    protected ArrayList<MipsInstruction> instructionList;
    // 用于确定在调用函数之前需要开辟多少sp存储全部的全局变量,没有使用的局部变量在LocalRegister中处理
    public static int spNumToCall=0;

    public MipsModule(){
        this.functionList=new ArrayList<>();
        this.varList=new ArrayList<>();
        this.printStrList=new ArrayList<>();
        this.constGlobalList=new HashMap<>();
        this.instructionList=new ArrayList<>();
    }

    public void addPrintStr(MipsStr str){
        printStrList.add(str);

    }

    @Override
    public String toString(){
        StringBuilder a=new StringBuilder();
        a.append(".data:\n");
        for(MipsGlobalVariable variable:varList){
            a.append("    ").append(variable.toString()).append("\n");
        }
        for(MipsConstGlobalVariable variable:constGlobalList.values()){
            a.append("    ").append(variable.toString()).append("\n");
        }
        for(MipsStr str:printStrList){
            a.append("    ").append(str.toString()).append("\n");
        }
        a.append(".text:\n");
        for(MipsInstruction mipsInstruction:instructionList){
            a.append("    ").append(mipsInstruction.toString()).append("\n");
        }
        for(int i=functionList.size()-1;i>=0;i--){
            a.append(functionList.get(i).toString());
        }
        return a.toString();
    }

    public void addConstGlobal(MipsConstGlobalVariable str) {
        constGlobalList.put(str.getName(),str);
    }

    public void addGlobalVariable(MipsGlobalVariable str) {
        varList.add(str);
    }

    public void addFunction(MipsFunction function){
        functionList.add(function);
    }

    /**
     * @description: 获取局部mips寄存器
     * @date: 2024/12/4 9:15
     **/
    public MipsMem getEmptyLocalReg(Boolean isChar){
        return functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().getEmptyReg(isChar,functionList.get(curFuncIndex).spNumForFunc);
    }

    /**
     * @description: 获取栈从而存储函数的传参
     * @date: 2024/12/13 20:14
     **/
    public MipsMem getSpToSaveParams(){
        return functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().getSpToSaveParams(functionList.get(curFuncIndex).spNumForFunc);
    }

    /**
     * @description: 获取全局mips寄存器
     * @date: 2024/12/4 9:17
     **/
    public MipsMem getEmptyGlobalReg(Boolean isChar){
        return functionList.get(curFuncIndex).getReg().getEmptyReg(isChar,functionList.get(curFuncIndex).spNumForFunc);
    }

    /**
     * @description: 为数组开辟sp栈空间
     * @date: 2024/12/6 13:27
     **/
    public MipsMem getArrayMem(Boolean isChar,int elementNum){
        return functionList.get(curFuncIndex).getReg().getArrayMem(isChar,elementNum,functionList.get(curFuncIndex).spNumForFunc);
    }

    /**
     * @description: 将全局寄存器的对应关系存入
     * @date: 2024/12/4 17:32
     **/
    public void putGlobalRel(String RegName,MipsMem mipsMem){
        functionList.get(curFuncIndex).getReg().putGlobalRel(RegName,mipsMem);
    }

    /**
     * @description: 将局部寄存器的对应关系存入
     * @date: 2024/12/4 17:32
     **/
    public void putLocalRel(String regName, MipsMem mipsMem) {
        functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().putRel(regName,mipsMem);
    }

    /**
     * @description: 获取ir寄存器和mips or 栈之间的关系
     * @date: 2024/12/4 17:46
     **/
    public MipsMem getRel(String irReg){
        MipsMem temp=functionList.get(curFuncIndex).getReg().checkRel(irReg);
        if(temp!=null){
            return temp;
        }else{
            return functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().checkRel(irReg);
        }
    }

    public ArrayList<MipsInstruction> storeGlobal(){
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        temp.addAll(functionList.get(curFuncIndex).getReg().storeGlobal());
        temp.addAll(functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().storeGlobal());
        temp.add(0,new Addi("$sp","$sp",Integer.toString(-spNumToCall)));
        return temp;
    }

    public void returnReg(String name){
//        functionList.get(curFuncIndex).getReg().returnReg(name);
        functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().returnReg(name);
    }

    public Boolean checkAtMain() {
        return functionList.get(curFuncIndex).getMain();
    }

    public Integer getConstGlobalValue(String name) {
        if(constGlobalList.containsKey(name)){
            return constGlobalList.get(name).getFirstValue();
        }else{
            return null;
        }
    }

    public Integer getGlobalVariableValue(String name,int index){
        for(MipsGlobalVariable variable:varList){
            if(variable.getName().equals(name)){
                return variable.getGlobalVariableValue(index);
            }
        }
        return null;
    }

    public void addInstruction(MipsInstruction instruction) {
        instructionList.add(instruction);
    }

    public ArrayList<MipsInstruction> loadGlobal() {
        ArrayList<MipsInstruction> temp=new ArrayList<>();
        temp.addAll(functionList.get(curFuncIndex).getReg().loadGlobal());
        temp.addAll(functionList.get(curFuncIndex).getBlockList().get(curBlockIndex).getReg().loadGlobal());
        return temp;
    }

    public void updateSp(int offset){
        functionList.get(curFuncIndex).updateSp(offset);
    }

    public String getNextLabel() {
        if(curBlockIndex<functionList.get(curFuncIndex).getBlockList().size()-1){
            return functionList.get(curFuncIndex).getBlockList().get(curBlockIndex+1).getName();
        }else{
            return "";
        }
    }

    public void deleteNeedlessLw() {
        for(MipsFunction function:functionList){
            for(MipsBasicBlock block:function.getBlockList()){
                for(int i=0;i<block.getInstructionList().size();i++){
                    if(block.getInstructionList().get(i) instanceof Sw
                    &&i<block.getInstructionList().size()-1
                    &&block.getInstructionList().get(i+1) instanceof Lw){
                        if(block.getInstructionList().get(i).getOffset()==block.getInstructionList().get(i+1).getOffset()
                        &&((Sw) block.getInstructionList().get(i)).getReg().equals(((Lw) block.getInstructionList().get(i+1)).getReg())){
                            if(!((Sw) block.getInstructionList().get(i)).getRegister().equals(((Lw) block.getInstructionList().get(i+1)).getRegister())){
                                block.getInstructionList().add(i+1,new Move(((Lw) block.getInstructionList().get(i+1)).getRegister(),((Sw) block.getInstructionList().get(i)).getRegister()));
                                block.getInstructionList().remove(i+2);
                            }else{
                                block.getInstructionList().remove(i+1);
                            }
                        }
                    }
                }
            }
        }
    }
}
