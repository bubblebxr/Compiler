package backend;
import backend.Instruction.MipsInstruction;
import backend.Instruction.Operate.Addi;
import backend.reg.MipsMem;
import backend.value.MipsConstGlobalVariable;
import backend.value.MipsFunction;
import backend.value.MipsGlobalVariable;
import backend.value.MipsStr;
import midend.Module;
import midend.value.Function;
import midend.value.GlobalVariable;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;


/**
 * @className: MipsGenerator
 * @author: bxr
 * @date: 2024/11/21 14:56
 * @description: 生成mips的module，用于输出目标代码mips
 */

public class MipsGenerator {

    protected Module irModule;
    public static MipsModule mipsModule=new MipsModule();
    public static int curFuncIndex;
    public static int curBlockIndex;

    public MipsGenerator(Module irModule){
        this.irModule=irModule;
    }


    public String outputMips(){
        return mipsModule.toString();
    }

    /**
     * @description: 判断是否是2的次方

     **/
    public static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * @description: 计算是2的几次方
     **/
    public static int getPowerOfTwo(int n) {
        int power = 0;
        while (n > 1) {
            n >>= 1;
            power++;
        }
        return power;
    }

    public void generateMipsModule() {
        // 添加print部分中str
        addPrintStr();

        // 添加全局常量到list中，用到时直接查找而不选择存放在.data中
        addConstGlobal();

        //添加全局变量到varList中
        addGlobalVariable();

        // 初始化各个function
        initFunction();

        // 通过function中的ir生成mips
        genMipsFromIr();

        // 遍历所有instruction，如果有存地址然后马上取址的，可以用move替代
        deleteNeedlessLw();
    }

    /**
     * @description: 遍历所有instruction，如果有存地址然后马上取址的，可以用move替代
     * @date: 2024/12/19 9:42
     **/
    public void deleteNeedlessLw() {
        mipsModule.deleteNeedlessLw();
    }


    /**
     * @description: 根据生成的llvm代码翻译为mips
     * @date: 2024/12/3 20:30
     **/
    public void genMipsFromIr() {
        int i=0;
        for(MipsFunction function:mipsModule.functionList){
            curFuncIndex=i++;
            function.genMipsFromIr();
        }
    }

    /**
     * @description: 增加全局变量到.data段中
     * @date: 2024/12/3 20:30
     **/
    public void addGlobalVariable() {
        for(GlobalVariable variable: irModule.getGlobalVariableList()){
            MipsGlobalVariable str=variable.genMipsGlobalVariable();
            if(str!=null){
                mipsModule.addGlobalVariable(str);
            }
        }
    }

    /**
     * @description: 为了便于优化，现将function初始化
     * @date: 2024/12/3 20:30
     **/
    public void initFunction() {
        for(Function function:irModule.getFunctionList()){
            if(!function.getDeclare()){
                mipsModule.addFunction(new MipsFunction(function));
            }
        }
    }

    /**
     * @description: 添加所有的print str
     * @date: 2024/12/3 17:13
     **/
    public void addPrintStr() {
        for(GlobalVariable variable: irModule.getGlobalVariableList()){
            MipsStr str=variable.genMipsStr();
            if(str!=null){
                mipsModule.addPrintStr(str);
            }
        }
    }

    /**
     * @description: 添加所有const global到list中
     * @date: 2024/12/3 17:47
     **/
    public void addConstGlobal(){
        for(GlobalVariable variable: irModule.getGlobalVariableList()){
            MipsConstGlobalVariable str=variable.genMipsConstGlobal();
            if(str!=null){
                mipsModule.addConstGlobal(str);
            }
        }
    }

    /**
     * @description: 获取局部mips寄存器
     * @date: 2024/12/4 9:15
     **/
    public static MipsMem getEmptyLocalReg(Boolean isChar){
        return mipsModule.getEmptyLocalReg(isChar);
    }

    /**
     * @description: 获取栈从而存储函数的传参
     * @date: 2024/12/13 20:16
     **/
    public static MipsMem getSpToSaveParams(){
        return mipsModule.getSpToSaveParams();
    }

    /**
     * @description: 获取全局mips寄存器
     * @date: 2024/12/4 9:17
     **/
    public static MipsMem getEmptyGlobalReg(Boolean isChar){
        return mipsModule.getEmptyGlobalReg(isChar);
    }

    /**
     * @description: 为数组开辟sp栈空间
     * @date: 2024/12/6 13:28
     **/
    public static MipsMem getArrayMem(Boolean isChar,int elementNum){
        return mipsModule.getArrayMem(isChar,elementNum);
    }

    /**
     * @description: 将全局寄存器的对应关系存入
     * @date: 2024/12/4 10:41
     **/
    public static void putGlobalRel(String RegName,MipsMem mipsMem){
        mipsModule.putGlobalRel(RegName,mipsMem);
    }

    /**
     * @description: 将局部寄存器的对应关系存入
     * @date: 2024/12/4 10:41
     **/
    public static void putLocalRel(String RegName,MipsMem mipsMem){
        mipsModule.putLocalRel(RegName,mipsMem);
    }

    /**
     * @description: 获取ir寄存器和mips or 栈之间的关系
     * @date: 2024/12/4 17:46
     **/
    public static MipsMem getRel(String irRegName){
        return mipsModule.getRel(irRegName);
    }

    /**
     * @description: 回收寄存器
     * @date: 2024/12/5 10:53
     **/
    public static void returnReg(String name){
        mipsModule.returnReg(name);
    }

    /**
     * @description: 检查当前函数是否是main函数
     * @date: 2024/12/5 11:09
     **/
    public static Boolean checkAtMain(){
        return mipsModule.checkAtMain();
    }

    public static Integer getConstGlobalValue(String name){
        return mipsModule.getConstGlobalValue(name);
    }

    /**
     * @description: 获取全局变量的值
     * @date: 2024/12/6 13:04
     **/
    public static Integer getGlobalVariableValue(String name,int index){
        return mipsModule.getGlobalVariableValue(name.substring(1),index);
    }

    /**
     * @description: 在调用函数时，将所有正在使用的全局寄存器都保存在sp栈中
     * @date: 2024/12/6 22:40
     **/
    public static ArrayList<MipsInstruction> storeGlobal(){
        return mipsModule.storeGlobal();
    }

    /**
     * @description: 在调用函数结束后，将之前正在使用的全局寄存器都load出来
     * @date: 2024/12/6 22:43
     **/
    public static ArrayList<MipsInstruction> loadGlobal(){
        return mipsModule.loadGlobal();
    }

    /**
     * @description: 更新当前函数的sp指针
     * @date: 2024/12/8 21:36
     **/
    public static void updateSp(int offset){
        mipsModule.updateSp(offset);
    }

    /**
     * @description: 获取下一个block的标签
     * @date: 2024/12/18 17:42
     **/
    public static String getNextLabel(){
        return mipsModule.getNextLabel();
    }
}
