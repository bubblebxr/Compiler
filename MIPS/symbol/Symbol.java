package MIPS.symbol;


/**
 * @className: Symbol
 * @author: bxr
 * @date: 2024/11/22 21:27
 * @description:
 */

public class Symbol {
    protected String name;
    protected Boolean isChar;
    protected Boolean isGlobal;// 全局变量存放在gp中
    public static int gpPresentNum=0;
    protected int gp;
    protected int sp;// 局部变量存在栈sp上
    public static int spPresentNum=0;
    protected Boolean isInReg;
    protected String MipsRegName;
    protected Boolean isArray;
    protected int elementNum;
    public static int forLocalFunGp;

    /**
     * @description: 初始化全局变量
     * @date: 2024/11/22 22:10
     **/
    public Symbol(String name,Boolean isChar){
        this.name=name;
        this.isChar=isChar;
        this.isGlobal=true;
        this.gp=gpPresentNum;
        gpPresentNum+=4;
        forLocalFunGp=gpPresentNum;
        this.isArray=false;
    }

    /**
     * @description: 为非main函数的函数创造临时存在gp的变量
     * @date: 2024/11/28 8:50
     **/
    public Symbol(Boolean isLocal,String name,Boolean isChar){
        this.name=name;
        this.isChar=isChar;
        this.isGlobal=false;
        this.gp=forLocalFunGp;
        forLocalFunGp+=4;
        this.isArray=false;
    }

    /**
     * @description: 初始化全局常量
     * @date: 2024/11/22 22:10
     **/
    public Symbol(String name,Boolean isChar,Boolean yes){
        this.name=name;
        this.isChar=isChar;
        this.isGlobal=true;
        this.isArray=false;
    }

    /**
     * @description: 为局部变量分配sp
     * @date: 2024/11/22 22:46
     **/
    public Symbol(String name,Boolean isChar,int sp){
        this.name=name;
        this.isChar=isChar;
        this.isGlobal=false;
        this.sp=sp;
        spPresentNum+=4;
        this.isArray=false;
    }

    /**
     * @description: 为数组分配.data位置
     * @date: 2024/11/27 10:21
     **/
    public Symbol(String name,Boolean isChar,int elementNum,Boolean isArray){
        this.name=name;
        this.isChar=isChar;
        this.elementNum=elementNum;
        this.isArray=isArray;
    }

    /**
     * @description: 为局部数组分配栈
     * @date: 2024/11/27 11:11
     **/
    public Symbol(String name,Boolean isChar,int elementNum,int sp){
        this.name=name;
        this.isChar=isChar;
        this.elementNum=elementNum;
        this.isArray=true;
        this.sp=sp;
        if(!isChar){
            spPresentNum+=4*elementNum;
        }else{
            spPresentNum+=(int) Math.ceil((double) elementNum / 8) * 8;
        }
    }

    /**
     * @description: 直接为中间变量分配寄存器
     * @date: 2024/11/27 21:21
     **/
    public Symbol(String name,Boolean isChar,String reg){
        this.name=name;
        this.isChar=isChar;
        this.isInReg=true;
        this.MipsRegName=reg;
    }

    public Boolean getChar() {
        return isChar;
    }

    public int getGp() {
        return gp;
    }

    /**
     * @description: 将该内存存放在了哪个reg存入
     * @date: 2024/11/23 17:15
     **/
    public void putToReg(String reg){
        isInReg=true;
        MipsRegName=reg;
    }

    /**
     * @description: 从mips reg调出
     * @date: 2024/11/23 17:16
     **/
    public void outFromReg(){
        isInReg=false;
    }

    public int getSp() {
        return sp;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }
}
