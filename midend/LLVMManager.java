package midend;

import midend.type.*;
import midend.value.Argument;
import midend.value.BasicBlock;
import midend.value.Function;
import midend.value.GlobalVariable;
import midend.value.Instruction.ConversionType.Trunc;
import midend.value.Instruction.ConversionType.Zext;
import midend.value.Instruction.Jump.Br;
import midend.value.Instruction.Jump.Call;
import midend.value.Instruction.Jump.Ret;
import midend.value.Instruction.Memory.Alloca;
import midend.value.Instruction.Memory.Getelementptr;
import midend.value.Instruction.Memory.Load;
import midend.value.Instruction.Memory.Store;
import midend.value.Instruction.Operate.*;
import symbol.Symbol.*;
import symbol.SymbolTable;
import frontend.AST.*;
import frontend.Token.TrueToken;
import frontend.Token.TrueType;

import java.util.*;

public class LLVMManager {
    protected ASTNode AST;
    protected static List<SymbolTable> SymbolTables=new ArrayList<>();
    protected Module module;
    protected int id;  //作用域记录，只增不减
    public static int presentId;  //当前作用域
    public static int variableId; //变量id
    public static int labelId;   //标签id
    protected Stack<Integer> stackId=new Stack<>();
    protected int strId;
    protected Set<String> pointerSet;
    protected static Boolean breakOrContinue=false;

    public Module getModule() {
        return module;
    }

    public LLVMManager(ASTNode AST){
        this.AST=AST;
        this.module=new Module();
        presentId=1;
        id=1;
        strId=0;
        labelId=0;
        this.pointerSet=new HashSet<>();
    }

    public Boolean loadPointer(String name,Type type){
        if(pointerSet.contains((presentId)+name)||SymbolTables.get(0).getDirectory().containsKey(name.substring(1))){
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value(name,new PointerType(type)));
            getCurBasicBlock().addInstruction(new Load("%"+(variableId++),type,temp));
            return true;
        }else{
            return false;
        }
    }

    public void addSymbol(String Ident, Symbol symbol){
        SymbolTables.get(presentId-1).addDirectory(Ident,symbol);
    }

    public void CompUnitToLLVM(){
        addDeclareFunction();
        SymbolTables.add(new SymbolTable(id,0));
        stackId.add(presentId);
        id++;
        for(Decl decl:AST.getDeclList()){
            DeclToLLVM(decl);
        }
        for(FuncDef funcDef:AST.getFuncDefList()){
            FuncToLLVM(funcDef);
        }
        MainFuncDefToLLVM(AST.getMainFuncDef());
    }

    public void addDeclareFunction() {
        module.addFunctionList(new Function("@getint",new FunctionType(new ArrayList<>(),new IntegerType(),true)));
        module.addFunctionList(new Function("@getchar",new FunctionType(new ArrayList<>(),new IntegerType(),true)));
        ArrayList<Type> paramList=new ArrayList<>();
        paramList.add(new IntegerType());
        module.addFunctionList(new Function("@putint",new FunctionType(paramList,new VoidType(),true)));
        module.addFunctionList(new Function("@putch",new FunctionType(paramList,new VoidType(),true)));
        ArrayList<Type> paramList1=new ArrayList<>();
        paramList1.add(new PointerType(new CharType()));
        module.addFunctionList(new Function("@putstr",new FunctionType(paramList1,new VoidType(),true)));
    }

    public void DeclToLLVM(Decl decl) {
        if(decl.getConstDecl()!=null){
            ConstDeclToLLVM(decl.getConstDecl());
        }else{
            VarDeclToLLVM(decl.getVarDecl());
        }
    }

    public void ConstDeclToLLVM(ConstDecl constDecl) {
        for(ConstDef constDef:constDecl.getConstDefList()){
            ConstDefToLLVM(constDef,constDecl.getBType().getType());
        }
    }

    public BasicBlock getCurBasicBlock(){
        return module.getCurBasicBlock();
    }

    public void ConstDefToLLVM(ConstDef constDef, TrueType BType) {
        if(BType==TrueType.INTTK){
            //Int
            if(constDef.getType()==0){
                //一维数组
                Symbol symbol=new VariableSymbol(constDef.getConstExp().getValue(),SymbolType.ConstIntArray,constDef.getIdent().getName(),1);
                symbol.setId((presentId==1)?"@"+constDef.getIdent().getName():"%"+variableId);
                addSymbol(constDef.getIdent().getName(),symbol);
                if(presentId==1){
                    ArrayList<Integer> temp=getConstExpListValue(constDef.getConstInitVal().getConstExpList());
                    module.addGlobalVariableList(new GlobalVariable("@"+constDef.getIdent().getName(),new ArrayType(new IntegerType(),constDef.getConstExp().getValue()),temp,constDef.getConstExp().getValue(),true));
                    symbol.setValue(temp);
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    ArrayList<Integer> temp=getConstExpListValue(constDef.getConstInitVal().getConstExpList());
                    addConstArray(constDef.getConstInitVal().getConstExpList(),constDef.getConstExp().getValue(),new ArrayType(new IntegerType(),constDef.getConstExp().getValue()),null);
                    symbol.setValue(temp);
                }
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.ConstInt,constDef.getIdent().getName(),0,constDef.getConstInitVal().getValue());
                symbol.setId((presentId==1)?"@"+constDef.getIdent().getName():"%"+variableId);
                addSymbol(constDef.getIdent().getName(),symbol);
                if(presentId==1){
                    module.addGlobalVariableList(new GlobalVariable("@"+constDef.getIdent().getName(),new IntegerType(),constDef.getConstInitVal().getConstExpList().get(0).getValue(),true));
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    addConstVariable(constDef.getConstInitVal().getConstExpList().get(0).getValue(),new IntegerType());
                }
            }
        }else{
            //Char
            if(constDef.getType()==0){
                //一维数组
                Symbol symbol=new VariableSymbol(constDef.getConstExp().getValue(),SymbolType.ConstCharArray,constDef.getIdent().getName(),1);
                symbol.setId((presentId==1)?"@"+constDef.getIdent().getName():"%"+variableId);
                addSymbol(constDef.getIdent().getName(),symbol);
                if(presentId==1){
                    if(constDef.getConstInitVal().getStringConst()!=null){
                        module.addGlobalVariableList(new GlobalVariable("@"+constDef.getIdent().getName(),new ArrayType(new CharType(),constDef.getConstExp().getValue()),getStringConstValue(constDef.getConstInitVal().getStringConst().getName().substring(1,constDef.getConstInitVal().getStringConst().getName().length()-1)),constDef.getConstExp().getValue(),true));
                    }else{
                        module.addGlobalVariableList(new GlobalVariable("@"+constDef.getIdent().getName(),new ArrayType(new CharType(),constDef.getConstExp().getValue()),getConstExpListValue(constDef.getConstInitVal().getConstExpList()),constDef.getConstExp().getValue(),true));
                    }
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    if(constDef.getConstInitVal().getStringConst()!=null){
                        addConstArray(null,constDef.getConstExp().getValue(),new ArrayType(new CharType(),constDef.getConstExp().getValue()),constDef.getConstInitVal().getStringConst().getName());
                    }else{
                        addConstArray(constDef.getConstInitVal().getConstExpList(),constDef.getConstExp().getValue(),new ArrayType(new CharType(),constDef.getConstExp().getValue()),null);
                    }
                }
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.ConstChar,constDef.getIdent().getName(),0,constDef.getConstInitVal().getValue());
                symbol.setId((presentId==1)?"@"+constDef.getIdent().getName():"%"+variableId);
                addSymbol(constDef.getIdent().getName(),symbol);
                if(presentId==1){
                    module.addGlobalVariableList(new GlobalVariable("@"+constDef.getIdent().getName(),new CharType(),constDef.getConstInitVal().getConstExpList().get(0).getValue(),true));
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    addConstVariable(constDef.getConstInitVal().getConstExpList().get(0).getValue(),new CharType());
                }
            }
        }
    }

    public void addConstArray(List<ConstExp> constExpList, int elementNum, Type type,String StringConst){
        getCurBasicBlock().addInstruction(new Alloca("%"+variableId,type,elementNum));
        int arrayBasicAddress=variableId++;
        //first element
        ArrayList<Value> firstOperators=new ArrayList<>();
        firstOperators.add(new Value("%"+arrayBasicAddress,new PointerType(type)));
        firstOperators.add(new Value("0",new IntegerType()));
        firstOperators.add(new Value("0",new IntegerType()));
        getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type,firstOperators));
        if(constExpList!=null){
            for (int i=0;i<constExpList.size();i++) {
                addConstArrayElement(constExpList.get(i).getValue(), i == 0, type.getType());
            }
            for(int i=constExpList.size();i<elementNum;i++){
                addConstArrayElement(0, i == 0, type.getType());
            }
        }else{
            for (int i=1;i<StringConst.length()-1;i++) {
                if(StringConst.charAt(i)=='\\'&&i<StringConst.length()-2&&StringConst.charAt(i+1)=='n'){
                    addConstArrayElement(10,i==1,type.getType());
                    i++;
                    continue;
                }
                addConstArrayElement(StringConst.charAt(i), i == 1, type.getType());
            }
            for(int i=StringConst.length()-2;i<elementNum;i++){
                addConstArrayElement(0, false, type.getType());
            }
        }

    }

    public void addConstArrayElement(int value,Boolean isFirst,Type type){
        if(!isFirst){
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value("%"+ (variableId - 1),new PointerType(type)));
            temp.add(new Value("1",new IntegerType()));
            getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type,temp));
        }
        ArrayList<Value> storeList=new ArrayList<>();
        storeList.add(new Value(String.valueOf(value),type));
        storeList.add(new Value("%"+(variableId-1),new PointerType(type)));
        getCurBasicBlock().addInstruction(new Store(null,null,storeList));
    }

    public void addConstVariable(Integer value,Type type){
        getCurBasicBlock().addInstruction(new Alloca("%"+variableId,type));
        ArrayList<Value> temp=new ArrayList<>();
        temp.add(new Value(String.valueOf(value),type));
        temp.add(new Value("%"+variableId,new PointerType(type)));
        getCurBasicBlock().addInstruction(new Store(null,type,temp));
        variableId++;
    }

    public ArrayList<Integer> getStringConstValue(String name){
        ArrayList<Integer> temp=new ArrayList<>();
        for(int i=0;i<name.length();i++){
            if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='n'){
                temp.add((int)'\n');
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='a'){
                temp.add(7);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='b'){
                temp.add(8);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='t'){
                temp.add(9);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='v'){
                temp.add(11);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='f'){
                temp.add(12);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='"'){
                temp.add(34);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='\''){
                temp.add(39);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='\\'){
                temp.add(92);
                i++;
            }else if(name.charAt(i)=='\\'&&i<name.length()-1&&name.charAt(i+1)=='0'){
                temp.add(0);
                i++;
            }else{
                temp.add((int) name.charAt(i));
            }
        }
        return temp;
    }

    public ArrayList<Integer> getConstExpListValue(List<ConstExp> ConstExpList){
        ArrayList<Integer> temp=new ArrayList<>();
        for(ConstExp constExp:ConstExpList){
            temp.add(constExp.getValue());
        }
        return temp;
    }

    public static int getVarValue(TrueToken Ident,int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(Ident.getName())){
                return symbol.getValue();
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return getVarValue(Ident,SymbolTables.get(presentId-1).getFatherId());
        }
        return 0;
    }

    public static int getArrayValue(TrueToken Ident,int expValue,int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(Ident.getName())){
                return symbol.getValue(expValue);
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return getArrayValue(Ident,expValue,SymbolTables.get(presentId-1).getFatherId());
        }
        return 0;
    }


    public void VarDeclToLLVM(VarDecl varDecl) {
        for(VarDef varDef:varDecl.getVarDefList()){
            VarDefToLLVM(varDef,varDecl.getBType().getType());
        }
    }

    public void VarDefToLLVM(VarDef varDef, TrueType BType) {
        if(BType==TrueType.INTTK){
            //Int
            if(varDef.getLBRACK()!=null){
                //一维数组
                Symbol symbol=new VariableSymbol(varDef.getConstExp().getValue(),SymbolType.IntArray,varDef.getIdent().getName(),1);
                symbol.setId((presentId==1)?"@"+varDef.getIdent().getName():"%"+variableId);
                addSymbol(varDef.getIdent().getName(),symbol);
                if(presentId==1){
                    if(varDef.getInitVal()!=null){
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new ArrayType(new IntegerType(),varDef.getConstExp().getValue()),getExpListValue(varDef.getInitVal().getExpList()),varDef.getConstExp().getValue(),false));
                    }else{
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new ArrayType(new IntegerType(),varDef.getConstExp().getValue()),new ArrayList<>(),varDef.getConstExp().getValue(),false));
                    }
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    if(varDef.getInitVal()!=null){
                        addArray(varDef.getInitVal().getExpList(),varDef.getConstExp().getValue(),new ArrayType(new IntegerType(),varDef.getConstExp().getValue()),null);
                    }else{
                        addEmptyArray(varDef.getConstExp().getValue(),new ArrayType(new IntegerType(),varDef.getConstExp().getValue()));
                    }
                }
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.Int,varDef.getIdent().getName(),0);
                symbol.setId((presentId==1)?"@"+varDef.getIdent().getName():"%"+variableId);
                addSymbol(varDef.getIdent().getName(),symbol);
                if(presentId==1){
                    if(varDef.getInitVal()!=null){
                        int value=varDef.getInitVal().getExpList().get(0).getExpValue();
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new IntegerType(),value,false));
                        symbol.addValue(value);
                    }else{
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new IntegerType(),0,false));
                    }
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    if(varDef.getInitVal()!=null){
                        addVariable(varDef.getInitVal().getExpList().get(0),new IntegerType());
                    }else{
                        addEmptyVariable(new IntegerType());
                    }
                }
            }
        }else{
            //Char
            if(varDef.getLBRACK()!=null){
                //一维数组
                Symbol symbol=new VariableSymbol(varDef.getConstExp().getValue(),SymbolType.CharArray,varDef.getIdent().getName(),1);
                symbol.setId((presentId==1)?"@"+varDef.getIdent().getName():"%"+variableId);
                addSymbol(varDef.getIdent().getName(),symbol);
                if(presentId==1){
                    if(varDef.getInitVal()!=null){
                        if(varDef.getInitVal().getSTRCON()!=null){
                            //是StringConst类型
                            module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new ArrayType(new CharType(),varDef.getConstExp().getValue()),getStringConstValue(varDef.getInitVal().getSTRCON().getName().substring(1,varDef.getInitVal().getSTRCON().getName().length()-1)),varDef.getConstExp().getValue(),false));
                        }else{
                            module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new ArrayType(new CharType(),varDef.getConstExp().getValue()),getExpListValue(varDef.getInitVal().getExpList()),varDef.getConstExp().getValue(),false));
                        }
                    }else{
                        //未定义初值
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new ArrayType(new CharType(),varDef.getConstExp().getValue()),new ArrayList<>(),varDef.getConstExp().getValue(),false));
                    }
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    if(varDef.getInitVal()!=null){
                        if(varDef.getInitVal().getSTRCON()!=null){
                            addArray(null,varDef.getConstExp().getValue(),new ArrayType(new CharType(),varDef.getConstExp().getValue()),varDef.getInitVal().getSTRCON().getName());
                        }else{
                            addArray(varDef.getInitVal().getExpList(),varDef.getConstExp().getValue(),new ArrayType(new CharType(),varDef.getConstExp().getValue()),null);
                        }
                    }else{
                        addEmptyArray(varDef.getConstExp().getValue(),new ArrayType(new CharType(),varDef.getConstExp().getValue()));
                    }
                }
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.Char,varDef.getIdent().getName(),0);
                symbol.setId((presentId==1)?"@"+varDef.getIdent().getName():"%"+variableId);
                addSymbol(varDef.getIdent().getName(),symbol);
                if(presentId==1){
                    if(varDef.getInitVal()!=null){
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new CharType(),varDef.getInitVal().getExpList().get(0).getExpValue(),false));
                    }else{
                        module.addGlobalVariableList(new GlobalVariable("@"+varDef.getIdent().getName(),new CharType(),0,false));
                    }
                }else{
                    pointerSet.add(presentId+"%"+variableId);
                    if(varDef.getInitVal()!=null){
                        addVariable(varDef.getInitVal().getExpList().get(0),new CharType());
                    }else{
                        addEmptyVariable(new CharType());
                    }
                }
            }
        }
    }

    public void addEmptyVariable(Type type) {
        getCurBasicBlock().addInstruction(new Alloca("%"+(variableId++),type));
    }

    public void addEmptyArray(int elementNum, Type type) {
        getCurBasicBlock().addInstruction(new Alloca("%"+(variableId++),type,elementNum));
    }

    public void addVariable(Exp exp,Type type){
        String name="%"+(variableId++);
        getCurBasicBlock().addInstruction(new Alloca(name,type));
        // 优化：试图求出exp，如果能求出，直接给变量赋初始值
//        Integer tryToGetValue=exp.tryToGetValue();
//        if(tryToGetValue!=null){
//            ArrayList<Value> temp=new ArrayList<>();
//            temp.add(new Value(String.valueOf(tryToGetValue),type));
//            temp.add(new Value(name,new PointerType(type)));
//            getCurBasicBlock().addInstruction(new Store(null,type,temp));
//            return;
//        }
        Pair pair=ExpToLLVM(exp);
        if(typeConversion(pair.type,pair.id,type)){
            pair.id="%"+(variableId-1);
        }
        ArrayList<Value> temp=new ArrayList<>();
        temp.add(new Value(pair.id,type));
        temp.add(new Value(name,new PointerType(type)));
        getCurBasicBlock().addInstruction(new Store(null,type,temp));
    }

    public void addArray(List<Exp> expList, int elementNum, Type type,String StringConst){
        //剩余初始化
        getCurBasicBlock().addInstruction(new Alloca("%"+variableId,type,elementNum));
        int arrayBasicAddress=variableId++;
        //first element
        ArrayList<Value> firstOperators=new ArrayList<>();
        firstOperators.add(new Value("%"+arrayBasicAddress,new PointerType(type)));
        firstOperators.add(new Value("0",new IntegerType()));
        firstOperators.add(new Value("0",new IntegerType()));
        getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type,firstOperators));
        String lastElementId="%"+(variableId-1);
        if(expList!=null){
            for (int i=0;i<expList.size();i++) {
                if(i!=0){
                    ArrayList<Value> temp=new ArrayList<>();
                    temp.add(new Value(lastElementId,new PointerType(type.getType())));
                    temp.add(new Value("1",new IntegerType()));
                    getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type.getType(),temp));
                    lastElementId="%"+(variableId-1);
                }
                Pair pair=ExpToLLVM(expList.get(i));
                if(typeConversion(pair.type,pair.id,type.getType())){
                    pair.id="%"+(variableId-1);
                }
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(pair.id,type.getType()));
                temp.add(new Value(lastElementId,new PointerType(type.getType())));
                getCurBasicBlock().addInstruction(new Store(null,null,temp));
            }
            for(int i=expList.size();i<elementNum;i++){
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(lastElementId,new PointerType(type.getType())));
                temp.add(new Value("1",new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type.getType(),temp));
                lastElementId="%"+(variableId-1);
                temp=new ArrayList<>();
                temp.add(new Value("0",type.getType()));
                temp.add(new Value(lastElementId,new PointerType(type.getType())));
                getCurBasicBlock().addInstruction(new Store(null,null,temp));
            }
        }else{
            int hasNum=0;
            for (int i=1;i<StringConst.length()-1;i++,hasNum++) {
                if(i!=1){
                    ArrayList<Value> temp=new ArrayList<>();
                    temp.add(new Value(lastElementId,new PointerType(type.getType())));
                    temp.add(new Value("1",new IntegerType()));
                    getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type.getType(),temp));
                    lastElementId="%"+(variableId-1);
                }
                int charASCII;
                if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='a'){
                    charASCII=7;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='b'){
                    charASCII=8;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='t'){
                    charASCII=9;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='n'){
                    charASCII=10;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='v'){
                    charASCII=11;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='f'){
                    charASCII=12;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='\"'){
                    charASCII=34;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='\''){
                    charASCII=39;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='\\'){
                    charASCII=92;
                    i++;
                }else if(StringConst.charAt(i)=='\\'&&i+1<StringConst.length()-1&&StringConst.charAt(i+1)=='0'){
                    charASCII=0;
                    i++;
                }else{
                    charASCII=StringConst.charAt(i);
                }
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(String.format("%d",charASCII),type.getType()));
                temp.add(new Value(lastElementId,new PointerType(type.getType())));
                getCurBasicBlock().addInstruction(new Store(null,null,temp));
            }
            for(int i=hasNum;i<elementNum;i++){
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(lastElementId,new PointerType(type.getType())));
                temp.add(new Value("1",new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type.getType(),temp));
                lastElementId="%"+(variableId-1);
                temp=new ArrayList<>();
                temp.add(new Value("0",type.getType()));
                temp.add(new Value(lastElementId,new PointerType(type.getType())));
                getCurBasicBlock().addInstruction(new Store(null,null,temp));
            }
        }

    }

    public Pair ExpToLLVM(Exp exp){
        return AddExpToLLVM(exp.getAddExp());
    }

    public Pair AddExpToLLVM(AddExp addExp){
        if(addExp.getMulExpList().size()==1){
            return MulExpToLLVM(addExp.getMulExpList().get(0));
        }
        ArrayList<Value> temp=new ArrayList<>();
        Pair pair1=MulExpToLLVM(addExp.getMulExpList().get(0));
        Pair pair2=MulExpToLLVM(addExp.getMulExpList().get(1));
        if(typeConversion(pair1.type,pair1.id,new IntegerType())){
            pair1.id="%"+(variableId-1);
        }
        if(typeConversion(pair2.type,pair2.id,new IntegerType())){
            pair2.id="%"+(variableId-1);
        }
        temp.add(new Value(pair1.id,new IntegerType()));
        temp.add(new Value(pair2.id,new IntegerType()));
        if(addExp.getTokenList().get(0).getType()==TrueType.PLUS){
            getCurBasicBlock().addInstruction(new Add("%"+(variableId++),new IntegerType(),temp));
        }else{
            getCurBasicBlock().addInstruction(new Sub("%"+(variableId++),new IntegerType(),temp));
        }
        for(int i=2;i<addExp.getMulExpList().size();i++){
            ArrayList<Value> tempList=new ArrayList<>();
            tempList.add(new Value("%"+(variableId-1),new IntegerType()));
            Pair pair3=MulExpToLLVM(addExp.getMulExpList().get(i));
            if(typeConversion(pair3.type,pair3.id,new IntegerType())){
                pair3.id="%"+(variableId-1);
            }
            tempList.add(new Value(pair3.id,new IntegerType()));
            if(addExp.getTokenList().get(i-1).getType()==TrueType.PLUS){
                getCurBasicBlock().addInstruction(new Add("%"+(variableId++),new IntegerType(),tempList));
            }else{
                getCurBasicBlock().addInstruction(new Sub("%"+(variableId++),new IntegerType(),tempList));
            }
        }
        return new Pair("%"+(variableId-1),new IntegerType());
    }

    public Pair MulExpToLLVM(MulExp mulExp){
        if(mulExp.getUnaryExpList().size()==1){
            return UnaryExpToLLVM(mulExp.getUnaryExpList().get(0));
        }
        ArrayList<Value> temp=new ArrayList<>();
        Pair pair1=UnaryExpToLLVM(mulExp.getUnaryExpList().get(0));
        Pair pair2=UnaryExpToLLVM(mulExp.getUnaryExpList().get(1));
        if(typeConversion(pair1.type,pair1.id,new IntegerType())){
            pair1.id="%"+(variableId-1);
        }
        if(typeConversion(pair2.type,pair2.id,new IntegerType())){
            pair2.id="%"+(variableId-1);
        }
        temp.add(new Value(pair1.id,new IntegerType()));
        temp.add(new Value(pair2.id,new IntegerType()));
        if(mulExp.getTokenList().get(0).getType()==TrueType.MULT){
            getCurBasicBlock().addInstruction(new Mul("%"+(variableId++),new IntegerType(),temp));
        }else if(mulExp.getTokenList().get(0).getType()==TrueType.DIV){
            getCurBasicBlock().addInstruction(new Sdiv("%"+(variableId++),new IntegerType(),temp));
        }else{
            getCurBasicBlock().addInstruction(new Srem("%"+(variableId++),new IntegerType(),temp));
        }
        for(int i=2;i<mulExp.getUnaryExpList().size();i++){
            ArrayList<Value> tempList=new ArrayList<>();
            tempList.add(new Value("%"+(variableId-1),new IntegerType()));
            Pair pair3=UnaryExpToLLVM(mulExp.getUnaryExpList().get(i));
            if(typeConversion(pair3.type,pair3.id,new IntegerType())){
                pair3.id="%"+(variableId-1);
            }
            tempList.add(new Value(pair3.id,new IntegerType()));
            if(mulExp.getTokenList().get(i-1).getType()==TrueType.MULT){
                getCurBasicBlock().addInstruction(new Mul("%"+(variableId++),new IntegerType(),tempList));
            }else if(mulExp.getTokenList().get(i-1).getType()==TrueType.DIV){
                getCurBasicBlock().addInstruction(new Sdiv("%"+(variableId++),new IntegerType(),tempList));
            }else{
                getCurBasicBlock().addInstruction(new Srem("%"+(variableId++),new IntegerType(),tempList));
            }
        }
        return new Pair("%"+(variableId-1),new IntegerType());
    }

    public Pair UnaryExpToLLVM(UnaryExp unaryExp){
        if(unaryExp.getPrimaryExp()!=null){
            //PrimaryExp
            return PrimaryExpToLLVM(unaryExp.getPrimaryExp());
        }else if(unaryExp.getIdent()!=null){
            // Ident '(' [FuncRParams] ')'
            ArrayList<Value> temp=new ArrayList<>();
            if(unaryExp.getFuncRParams()!=null){
                temp=FuncRParamsToLLVM("@"+unaryExp.getIdent().getName(),unaryExp.getFuncRParams());
            }
            Type type=getFuncType(unaryExp.getIdent().getName(),presentId);
            getCurBasicBlock().addInstruction(new Call(type instanceof VoidType?null:"%"+variableId,type,"@"+unaryExp.getIdent().getName(),temp));
            return new Pair(type instanceof VoidType?null:"%"+(variableId++),getFuncType(unaryExp.getIdent().getName(),presentId));
        }else{
            //UnaryOp UnaryExp
            Pair pair=UnaryExpToLLVM(unaryExp.getUnaryExp());
            ArrayList<Value> temp=new ArrayList<>();
            if(typeConversion(pair.type,pair.id,new IntegerType())){
                pair.id="%"+(variableId-1);
            }
            temp.add(new Value("0",new IntegerType()));
            temp.add(new Value(pair.id,new IntegerType()));
            if(unaryExp.getUnaryOp().getType()==TrueType.PLUS){
                getCurBasicBlock().addInstruction(new Add("%"+(variableId++),new IntegerType(),temp));
            }else if(unaryExp.getUnaryOp().getType()==TrueType.MINU){
                getCurBasicBlock().addInstruction(new Sub("%"+(variableId++),new IntegerType(),temp));
            }else if(unaryExp.getUnaryOp().getType()==TrueType.NOT){
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new BooleanType(),temp,IcmpType.eq));
                typeConversion(new BooleanType(),"%"+(variableId-1),new IntegerType());
            }
            return new Pair("%"+(variableId-1),new IntegerType());
        }
    }

    public ArrayList<Value> FuncRParamsToLLVM(String name,FuncRParams funcRParams){
        ArrayList<Value> temp=new ArrayList<>();
        List<Argument> arguments=module.getFunction(name).getArgumentList();
        for(int i=0;i<funcRParams.getExpList().size();i++){
            Type argumentsType=arguments.get(i).getType();
            Pair pair=ExpToLLVM(funcRParams.getExpList().get(i));
            if(typeConversion(pair.type,pair.id,argumentsType)){
                pair.id="%"+(variableId-1);
            }
            temp.add(new Value(pair.id,pair.type));
        }
        return temp;
    }

    public Type getFuncType(String name,int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(name)){
                if(symbol.getType()==SymbolType.VoidFunc){
                    return new VoidType();
                }else if(symbol.getType()==SymbolType.IntFunc){
                    return new IntegerType();
                }else if(symbol.getType()==SymbolType.CharFunc){
                    return new CharType();
                }
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return getFuncType(name,SymbolTables.get(presentId-1).getFatherId());
        }else{
            return null;
        }
    }

    public Pair PrimaryExpToLLVM(PrimaryExp primaryExp){
        if(primaryExp.getExp()!=null){
            // '(' Exp ')'
            return ExpToLLVM(primaryExp.getExp());
        }else if(primaryExp.getlVal()!=null){
            // LVal
            return LValToLLVM(primaryExp.getlVal(),true);
        }else if(primaryExp.getNumberToken()!=null){
            // Number
            return new Pair(primaryExp.getNumberToken().getName(),new IntegerType());
        }else{
            // Character
            return new Pair(CharacterToAscii(primaryExp.getCharacterToken().getName().substring(1,primaryExp.getCharacterToken().getName().length()-1)),new CharType());
        }
    }

    public static String CharacterToAscii(String name){
        if(name.length()==1){
            return String.valueOf((int)name.charAt(0));
        }else{
            if(name.equals("\\a")){
                return "7";
            }else if(name.equals("\\b")){
                return "8";
            }else if(name.equals("\\t")){
                return "9";
            }else if(name.equals("\\n")){
                return "10";
            }else if(name.equals("\\v")){
                return "11";
            }else if(name.equals("\\f")){
                return "12";
            }else if(name.equals("\\\"")){
                return "34";
            }else if(name.equals("\\'")){
                return "39";
            }else if(name.equals("\\\\")){
                return "92";
            }else{
                return "0";
            }
        }
    }


    public ArrayList<Integer> getExpListValue(List<Exp> expList){
        ArrayList<Integer> temp=new ArrayList<>();
        for(Exp exp:expList){
            temp.add(exp.getExpValue());
        }
        return temp;
    }

    public void addSymbolFunc(String Ident, Symbol symbol,int Id){
        SymbolTables.get(Id-1).addDirectory(Ident,symbol);
    }

    public void FuncToLLVM(FuncDef funcDef) {
        SymbolType type;
        Type returnType;
        if(funcDef.getFuncType().getType()==TrueType.VOIDTK){
            type=SymbolType.VoidFunc;
            returnType=new VoidType();
        }else if(funcDef.getFuncType().getType()==TrueType.INTTK){
            type=SymbolType.IntFunc;
            returnType=new IntegerType();
        }else{
            type=SymbolType.CharFunc;
            returnType=new CharType();
        }
        Symbol symbol=new FunctionSymbol(type,funcDef.getIdent().getName());
        SymbolTables.add(new SymbolTable(id,presentId));
        presentId=id;
        id++;
        variableId=0;
        stackId.push(presentId);
        if(funcDef.getFuncFParams()!=null&&funcDef.getFuncFParams().getFuncFParamList()!=null){
            for(FuncFParam funcFParam:funcDef.getFuncFParams().getFuncFParamList()){
                symbol.addFuncParams((FuncParamSymbol) FuncFParamToLLVM(funcFParam));
            }
        }
        variableId++;
        int temp=stackId.pop();
        addSymbolFunc(funcDef.getIdent().getName(),symbol,stackId.peek());
        Function function=new Function("@"+funcDef.getIdent().getName(),new FunctionType(getFuncParamsType(symbol.getFuncParams()),returnType,false),getArgumentList(symbol.getFuncParams()));
        module.addFunctionList(function);
        function.addBasicBlock(new BasicBlock(null,null));
        stackId.add(temp);
        for(FuncParamSymbol funcParamSymbol:symbol.getFuncParams()){
            FuncParamsLoad(funcParamSymbol);
            setFuncParamsIdAndLoad(funcParamSymbol.getIdent(),presentId);
        }
        BlockToLLVM(funcDef.getBlock(),false, type == SymbolType.VoidFunc);
        if(type==SymbolType.VoidFunc){
            function.checkReturn();
        }
        stackId.pop();
        presentId=stackId.peek();
    }

    public void FuncParamsLoad(FuncParamSymbol funcParamSymbol) {
        int dimension=funcParamSymbol.getDimension();
        SymbolType type=funcParamSymbol.getType();
        if(dimension==0){
            Type paramType=type==SymbolType.Char?new CharType():new IntegerType();
            getCurBasicBlock().addInstruction(new Alloca("%"+(variableId++),paramType));
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value(funcParamSymbol.getId(),paramType));
            temp.add(new Value("%"+(variableId-1),new PointerType(paramType)));
            getCurBasicBlock().addInstruction(new Store(null,null,temp));
            String id="%"+(variableId-1);
            pointerSet.add(presentId+id);
        }else{
            Type paramType=(funcParamSymbol.getType()==SymbolType.Int||funcParamSymbol.getType()==SymbolType.IntArray||funcParamSymbol.getType()==SymbolType.ConstIntArray||funcParamSymbol.getType()==SymbolType.ConstInt)?new IntegerType():new CharType();
            getCurBasicBlock().addInstruction(new Alloca("%"+(variableId++),new PointerType(paramType)));
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value(funcParamSymbol.getId(),new PointerType(paramType)));
            temp.add(new Value("%"+(variableId-1),new PointerType(new PointerType(paramType))));
            getCurBasicBlock().addInstruction(new Store(null,null,temp));
            String id="%"+(variableId-1);
            pointerSet.add(presentId+id);
        }
    }

    public void BlockToLLVM(Block block,boolean inFor,boolean isVoid){
        for(int i=0;i<block.getBlockItemList().size();i++){
            if(breakOrContinue){
                breakOrContinue=false;
                break;
            }
            BlockItemToLLVM(block.getBlockItemList().get(i),inFor,isVoid);
        }
        if(breakOrContinue)breakOrContinue=false;
//        if(getCurBasicBlock().getInstructionList().isEmpty()){
//            getCurBasicBlock().addInstruction(new Br(null,null,"%b"+(labelId)));
//        }
    }

    public void BlockItemToLLVM(BlockItem blockItem,boolean inFor,boolean isVoid){
        if(blockItem.getDecl()!=null){
            DeclToLLVM(blockItem.getDecl());
        }else{
            StmtToLLVM(blockItem.getStmt(),inFor,isVoid);
        }
    }

    public void StmtToLLVM(Stmt stmt,Boolean inFor,Boolean isVoid){
        int type=stmt.getType();
        switch (type){
            case 1:
                Pair pair=ExpToLLVM(stmt.getExp());
                String expId=pair.id;
                Pair pair1=LValToLLVM(stmt.getlVal(),false);
                if(typeConversion(pair.type,expId,pair1.type)){
                    expId="%"+(variableId-1);
                }
                ArrayList<Value> temp1=new ArrayList<>();
                temp1.add(new Value(expId,pair1.type));
                temp1.add(new Value(pair1.id,new PointerType(pair1.type)));
                getCurBasicBlock().addInstruction(new Store(null,pair1.type,temp1));
                break;
            case 2:
                if(stmt.getExp()!=null){
                    ExpToLLVM(stmt.getExp());
                }
                break;
            case 3:
                BlockToLLVM(stmt.getBlock(),inFor,isVoid);
                break;
            case 4:
                //if else
                getCurBasicBlock().addInstruction(new Br(null,null,"%b"+labelId));
                BasicBlock curBlock=getCurBasicBlock();
                CondToLLVM(stmt.getCond());
                BasicBlock thenStmt=new BasicBlock("%b"+(labelId++),null);
                int startId= module.getCurFunction().getBasicBlockNum();
                curBlock.fillThenStmt(thenStmt);
                module.addNewBasicBlock(thenStmt);
                StmtToLLVM(stmt.getStmt(),inFor,isVoid);
                if(stmt.getStmt().getType()!=3){
                    breakOrContinue=false;
                }
                if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                    getCurBasicBlock().addInstruction(new Br(null,null,"nextBlock"));
                }
                int endId=module.getCurFunction().getBasicBlockNum();
                int start = 0,end=0;
                BasicBlock elseStmt=null;
                if(stmt.getElseStmt()!=null){
                    elseStmt=new BasicBlock("%b"+(labelId++),null);
                    start= module.getCurFunction().getBasicBlockNum();
                    curBlock.fillElseStmt(elseStmt);
                    module.addNewBasicBlock(elseStmt);
                    StmtToLLVM(stmt.getElseStmt(),inFor,isVoid);
                    if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                        getCurBasicBlock().addInstruction(new Br(null,null,"nextBlock"));
                    }
                    end= module.getCurFunction().getBasicBlockNum();
                }else{
                    curBlock.fillElseStmtToNextBlock();
                }
                BasicBlock nextBlock=new BasicBlock("%b"+(labelId++),null);
                module.addNewBasicBlock(nextBlock);
                curBlock.fillNextBlock(nextBlock);
                module.getCurFunction().fillUnCondNextBlock(startId,endId,nextBlock);
                if(elseStmt!=null)module.getCurFunction().fillUnCondNextBlock(start,end,nextBlock);
                break;
            case 5:
                //for
                if(stmt.getForStmt1()!=null){
                    ForStmtToLLVM(stmt.getForStmt1());
                }
                BasicBlock lastBlock=getCurBasicBlock();
                BasicBlock condBlock=getCurBasicBlock();
                String continueBlockId=null,breakBlockId;
                if(stmt.getCond()!=null){
                    if(condBlock.getInstructionList().isEmpty()||!(condBlock.getInstructionList().get(condBlock.getInstructionList().size()-1) instanceof Br||condBlock.getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                        condBlock.addInstruction(new Br(null,null,"%b"+(labelId)));
                    }
                    CondToLLVM(stmt.getCond());
                    condBlock=getCurBasicBlock();
                    continueBlockId=condBlock.getName();
                }else{
                    if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                        getCurBasicBlock().addInstruction(new Br(null,null,"%b"+(labelId)));
                    }
                }
                BasicBlock forStmt=new BasicBlock("%b"+(labelId++),null);
                int startIndex= module.getCurFunction().getBasicBlockNum();
                if(condBlock!=null){
                    condBlock.fillForStmt(forStmt);
                }
                module.addNewBasicBlock(forStmt);
                StmtToLLVM(stmt.getStmt(),true,isVoid);
                if(continueBlockId==null)continueBlockId=forStmt.getName();
                if(stmt.getForStmt2()!=null){
                    if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                        getCurBasicBlock().addInstruction(new Br(null,null,"%b"+labelId));
                    }
                    BasicBlock forStmt2=new BasicBlock("%b"+(labelId++),null);
                    module.addNewBasicBlock(forStmt2);
                    continueBlockId=forStmt2.getName();
                    ForStmtToLLVM(stmt.getForStmt2());
                    if(stmt.getCond()!=null){
                        if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                            getCurBasicBlock().addInstruction(new Br(null,null,condBlock.getName()));
                        }
                    }else{
                        if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                            getCurBasicBlock().addInstruction(new Br(null,null,forStmt.getName()));
                        }
                    }
                }else{
                    if(stmt.getCond()!=null){
                        if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                            getCurBasicBlock().addInstruction(new Br(null,null,condBlock.getName()));
                        }
                    }else{
                        if(getCurBasicBlock().getInstructionList().isEmpty()||!(getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Br||getCurBasicBlock().getInstructionList().get(getCurBasicBlock().getInstructionList().size()-1) instanceof Ret)){
                            getCurBasicBlock().addInstruction(new Br(null,null,forStmt.getName()));
                        }
                    }
                }
                int endIndex=module.getCurFunction().getBasicBlockNum();
                BasicBlock nextForBlock=new BasicBlock("%b"+(labelId++),null);
                //先出现break，再填值,continue同理
                breakBlockId=nextForBlock.getName();
                module.getCurFunction().fillContinueLabel(startIndex,endIndex,continueBlockId);
                module.getCurFunction().fillBreakLabel(startIndex,endIndex,breakBlockId);
                module.addNewBasicBlock(nextForBlock);
                if(stmt.getCond()!=null){
                    lastBlock.fillThenStmt(forStmt);
                    lastBlock.fillNextForBlockStmt(nextForBlock);
                }
                break;
            case 6:
                //break continue
                if(stmt.getBreakOrContinue().getType()==TrueType.BREAKTK){
                    getCurBasicBlock().addInstruction(new Br(null,null,"breakBlockId"));
                    breakOrContinue=true;
//                    module.addNewBasicBlock(new BasicBlock("%b"+(labelId++),null));
                }else{
                    getCurBasicBlock().addInstruction(new Br(null,null,"continueBlockId"));
                    breakOrContinue=true;
//                    module.addNewBasicBlock(new BasicBlock("%b"+(labelId++),null));
                }
                break;
            case 7:
                //return
                ArrayList<Value> temp7=new ArrayList<>();
                if(stmt.getExp()!=null){
                    Pair pair7=ExpToLLVM(stmt.getExp());
                    Type type7=getCurFunctionType();
                    if(typeConversion(pair7.type,pair7.id,type7)){
                        pair7.id="%"+(variableId-1);
                    }
                    temp7.add(new Value(pair7.id,type7));
                }
                getCurBasicBlock().addInstruction(new Ret(null,null,temp7));
                break;
            case 8:
                //getint
                Pair pair8=LValToLLVM(stmt.getlVal(),false);
                getCurBasicBlock().addInstruction(new Call("%"+(variableId++),new IntegerType(),"@getint",new ArrayList<>()));
                typeConversion(new IntegerType(),"%"+(variableId-1),pair8.type);
                ArrayList<Value> temp8=new ArrayList<>();
                temp8.add(new Value("%"+(variableId-1),pair8.type));
                temp8.add(new Value(pair8.id,new PointerType(pair8.type)));
                getCurBasicBlock().addInstruction(new Store(null,null,temp8));
                break;
            case 9:
                //getchar
                Pair pair9=LValToLLVM(stmt.getlVal(),false);
                getCurBasicBlock().addInstruction(new Call("%"+(variableId++),new IntegerType(),"@getchar",new ArrayList<>()));
                typeConversion(new IntegerType(),"%"+(variableId-1),pair9.type);
                ArrayList<Value> temp9=new ArrayList<>();
                temp9.add(new Value("%"+(variableId-1),pair9.type));
                temp9.add(new Value(pair9.id,new PointerType(pair9.type)));
                getCurBasicBlock().addInstruction(new Store(null,null,temp9));
                break;
            case 10:
                //printf
                PrintfToLLVM(stmt.getStringConst().getName(),stmt.getExpList());
                break;
        }
    }

    public void CondToLLVM(Cond cond) {
        LOrExpToLLVM(cond.getlOrExp());
    }

    /**
     * @description:
     * 1.是最后一个LOrExp，正确跳转到thenStmt，错误跳转到elseStmt
     * 2.不是最后一个LOrExp，正确跳转到下一个thenStmt，错误跳转到下一个or中的第一个AndLabel
     **/
    public void LOrExpToLLVM(LOrExp lOrExp){
        BasicBlock curBlock=getCurBasicBlock();
        for(int i=0;i<lOrExp.getlAndExpList().size();i++){
           BasicBlock firstLAndExpBlock=new BasicBlock("%b"+(labelId++),null);
            if(i!=0)curBlock.fillNextOrFirstAndLabel(firstLAndExpBlock.getName());
           curBlock.addLAndExpList(firstLAndExpBlock);
           module.addNewBasicBlock(firstLAndExpBlock);
           LAndExpToLLVM(lOrExp.getlAndExpList().get(i),curBlock);
        }
        curBlock.fillNextOrFirstAndLabelToElseStmt();

    }

    /**
     * @description:
     * 1.是最后一个AndExp，正确跳转到thenStmt，错误跳转到下一个or中的第一个AndLabel
     * 2.不是最后一个AndExp，正确跳转到下一个AndExp，错误跳转到下一个or中的第一个AndLabel
     **/
    public void LAndExpToLLVM(LAndExp lAndExp,BasicBlock curBlock) {
        for(int i=0;i<lAndExp.getEqExpList().size();i++){
            String trueLabel="nextAndLabel";
            String falseLabel="nextOrFirstAndLabel";
            BasicBlock newLAndBlock = null;
            if(i!=0){
                newLAndBlock=new BasicBlock("%b"+(labelId++),null);
                curBlock.addLAndExpList(newLAndBlock);
                module.addNewBasicBlock(newLAndBlock);
            }
            Pair pair=EqExpToLLVM(lAndExp.getEqExpList().get(i));
            if(typeConversion(pair.type,pair.id,new IntegerType())){
                pair.id="%"+(variableId-1);
            }
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value("0",new IntegerType()));
            temp.add(new Value(pair.id,new IntegerType()));
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.ne));
            if(i==lAndExp.getEqExpList().size()-1){
                trueLabel="thenStmt";
            }
            if(i!=0)curBlock.fillNextAndLabel(newLAndBlock.getName());
            getCurBasicBlock().addInstruction(new Br(null,new BooleanType(),"%"+(variableId-1),trueLabel,falseLabel));
        }
    }

    public Pair EqExpToLLVM(EqExp eqExp) {
        if(eqExp.getRelExpList().size()==1){
            return RelExpToLLVM(eqExp.getRelExpList().get(0));
        }
        ArrayList<Value> temp=new ArrayList<>();
        Pair pair1=RelExpToLLVM(eqExp.getRelExpList().get(0));
        Pair pair2=RelExpToLLVM(eqExp.getRelExpList().get(1));
        if(typeConversion(pair1.type,pair1.id,new IntegerType())){
            pair1.id="%"+(variableId-1);
        }
        if(typeConversion(pair2.type,pair2.id,new IntegerType())){
            pair2.id="%"+(variableId-1);
        }
        temp.add(new Value(pair1.id,new IntegerType()));
        temp.add(new Value(pair2.id,new IntegerType()));
        if(eqExp.getTokenList().get(0).getType()==TrueType.EQL){
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.eq));
        }else{
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.ne));
        }
        for(int i=2;i<eqExp.getRelExpList().size();i++){
            ArrayList<Value> tempList=new ArrayList<>();
            tempList.add(new Value("%"+(variableId-1),new IntegerType()));
            Pair pair3=RelExpToLLVM(eqExp.getRelExpList().get(i));
            if(typeConversion(pair3.type,pair3.id,new IntegerType())){
                pair3.id="%"+(variableId-1);
            }
            tempList.add(new Value(pair3.id,new IntegerType()));
            if(eqExp.getTokenList().get(i-1).getType()==TrueType.EQL){
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),tempList,IcmpType.eq));
            }else{
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),tempList,IcmpType.ne));
            }
        }
        return new Pair("%"+(variableId-1),new BooleanType());
    }

    public Pair RelExpToLLVM(RelExp relExp){
        if(relExp.getAddExpList().size()==1){
            return AddExpToLLVM(relExp.getAddExpList().get(0));
        }
        ArrayList<Value> temp=new ArrayList<>();
        Pair pair1=AddExpToLLVM(relExp.getAddExpList().get(0));
        Pair pair2=AddExpToLLVM(relExp.getAddExpList().get(1));
        if(typeConversion(pair1.type,pair1.id,new IntegerType())){
            pair1.id="%"+(variableId-1);
        }
        if(typeConversion(pair2.type,pair2.id,new IntegerType())){
            pair2.id="%"+(variableId-1);
        }
        temp.add(new Value(pair1.id,new IntegerType()));
        temp.add(new Value(pair2.id,new IntegerType()));
        if(relExp.getTokenList().get(0).getType()==TrueType.LSS){
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.slt));
        }else if(relExp.getTokenList().get(0).getType()==TrueType.LEQ){
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.sle));
        }else if(relExp.getTokenList().get(0).getType()==TrueType.GRE){
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.sgt));
        }else{
            getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),temp,IcmpType.sge));
        }
        for(int i=2;i<relExp.getAddExpList().size();i++){
            ArrayList<Value> tempList=new ArrayList<>();
            tempList.add(new Value("%"+(variableId-1),new IntegerType()));
            Pair pair3=AddExpToLLVM(relExp.getAddExpList().get(i));
            if(typeConversion(pair3.type,pair3.id,new IntegerType())){
                pair3.id="%"+(variableId-1);
            }
            tempList.add(new Value(pair3.id,new IntegerType()));
            if(relExp.getTokenList().get(i-1).getType()==TrueType.LSS){
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),tempList,IcmpType.slt));
            }else if(relExp.getTokenList().get(i-1).getType()==TrueType.LEQ){
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),tempList,IcmpType.sle));
            }else if(relExp.getTokenList().get(i-1).getType()==TrueType.GRE){
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),tempList,IcmpType.sgt));
            }else{
                getCurBasicBlock().addInstruction(new Icmp("%"+(variableId++),new IntegerType(),tempList,IcmpType.sge));
            }
        }
        return new Pair("%"+(variableId-1),new BooleanType());
    }

    public void ForStmtToLLVM(ForStmt forStmt1) {
        Pair pair=ExpToLLVM(forStmt1.getExp());
        String expId=pair.id;
        Pair pair1=LValToLLVM(forStmt1.getlVal(),false);
        if(typeConversion(pair.type,expId,pair1.type)){
            expId="%"+(variableId-1);
        }
        ArrayList<Value> temp1=new ArrayList<>();
        temp1.add(new Value(expId,pair1.type));
        temp1.add(new Value(pair1.id,new PointerType(pair1.type)));
        getCurBasicBlock().addInstruction(new Store(null,pair1.type,temp1));
    }

    public Type getCurFunctionType() {
        String name=module.getCurFunctionName();
        if(name.equals("main")){
            return new IntegerType();
        }
        for(Symbol symbol:SymbolTables.get(0).getDirectory().values()){
            if(symbol.getIdent().equals(name)){
                if(symbol.getType()==SymbolType.VoidFunc){
                    return new VoidType();
                }else if(symbol.getType()==SymbolType.IntFunc){
                    return new IntegerType();
                }else if(symbol.getType()==SymbolType.CharFunc){
                    return new CharType();
                }
            }
        }
        return null;
    }


    public void PrintfToLLVM(String stringConst, List<Exp> expList) {
        StringBuilder a=new StringBuilder();
        int expFlag=0;
        for(int i=1;i<stringConst.length()-1;i++){
            if(stringConst.charAt(i)=='%'&&i+2<stringConst.length()&&(stringConst.charAt(i+1)=='c'||stringConst.charAt(i+1)=='d')){
                if(!a.toString().isEmpty()){
                    ArrayList<Integer> temp=getStringConstValue(a.toString());
                    temp.add((int)'\0');
                    module.addGlobalVariableList(new GlobalVariable("@.str."+(strId++),new ArrayType(new CharType(),temp.size()),temp,temp.size(),true));
                    a=new StringBuilder();
                    ArrayList<Value> tempGet=new ArrayList<>();
                    tempGet.add(new Value("@.str."+(strId-1),new PointerType(new ArrayType(new CharType(),temp.size()))));
                    tempGet.add(new Value("0",new IntegerType()));
                    tempGet.add(new Value("0",new IntegerType()));
                    getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),new ArrayType(new CharType(),temp.size()),tempGet));
                    ArrayList<Value> callTemp=new ArrayList<>();
                    callTemp.add(new Value("%"+(variableId-1),new PointerType(new CharType())));
                    getCurBasicBlock().addInstruction(new Call(null,new VoidType(),"@putstr",callTemp));
                }
                if(stringConst.charAt(i+1)=='c'){
                    // %c
                    Pair pair=ExpToLLVM(expList.get(expFlag++));
                    if(loadPointer(pair.id,pair.type)){
                        pair.id="%"+(variableId-1);
                    }
                    if(typeConversion(pair.type,pair.id,new IntegerType())){
                        pair.id="%"+(variableId-1);
                    }
                    ArrayList<Value> temp=new ArrayList<>();
                    temp.add(new Value(pair.id,new IntegerType()));
                    getCurBasicBlock().addInstruction(new Call(null,new VoidType(),"@putch",temp));
                }else{
                    // %d
                    Pair pair=ExpToLLVM(expList.get(expFlag++));
                    if(loadPointer(pair.id,pair.type)){
                        pair.id="%"+(variableId-1);
                    }
                    if(typeConversion(pair.type,pair.id,new IntegerType())){
                        pair.id="%"+(variableId-1);
                    }
                    ArrayList<Value> temp=new ArrayList<>();
                    temp.add(new Value(pair.id,new IntegerType()));
                    getCurBasicBlock().addInstruction(new Call(null,new VoidType(),"@putint",temp));
                }
                i++;
            }else{
                a.append(stringConst.charAt(i));
            }
        }
        if(!a.toString().isEmpty()){
            ArrayList<Integer> temp=getStringConstValue(a.toString());
            temp.add((int)'\0');
            module.addGlobalVariableList(new GlobalVariable("@.str."+(strId++),new ArrayType(new CharType(),temp.size()),temp,temp.size(),true));
            ArrayList<Value> tempGet=new ArrayList<>();
            tempGet.add(new Value("@.str."+(strId-1),new PointerType(new ArrayType(new CharType(),temp.size()))));
            tempGet.add(new Value("0",new IntegerType()));
            tempGet.add(new Value("0",new IntegerType()));
            getCurBasicBlock().addInstruction(new Getelementptr("%" + (variableId++), new ArrayType(new CharType(), temp.size()), tempGet));
            ArrayList<Value> callTemp=new ArrayList<>();
            callTemp.add(new Value("%"+(variableId-1),new PointerType(new CharType())));
            getCurBasicBlock().addInstruction(new Call(null,new VoidType(),"@putstr",callTemp));
        }
    }

    public Boolean typeConversion(Type type1,String id1,Type type2){
        if(type1.getClass()==type2.getClass()){
            return false;
        }else if(type1 instanceof CharType||type1 instanceof BooleanType){
            //char - > int : zext || i1 - >int : zext
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value(id1,type1));
            temp.add(new Value(null,type2));
            getCurBasicBlock().addInstruction(new Zext("%"+(variableId++),null,temp));
        }else{
            //int - > char : trunc
            ArrayList<Value> temp=new ArrayList<>();
            if(id1.charAt(0)!='%'){
                id1= String.valueOf(Integer.parseInt(id1)&0x7f);
            }
            temp.add(new Value(id1,type1));
            temp.add(new Value(null,type2));
            getCurBasicBlock().addInstruction(new Trunc("%"+(variableId++),null,temp));
        }
        return true;
    }

    public Pair LValToLLVM(LVal lVal,Boolean isRight){
        if(lVal.getExp()==null){
            //变量或数组
            Symbol symbol=getVariable(lVal.getIdent().getName(),presentId);
            String id=symbol.getId();
            Type type;
            if(symbol.getType()==SymbolType.Int||symbol.getType()==SymbolType.ConstInt){
                type=new IntegerType();
            }else if(!(symbol instanceof FuncParamSymbol) &&(symbol.getType()==SymbolType.IntArray||symbol.getType()==SymbolType.ConstIntArray)){
                // int array
                type=new ArrayType(new IntegerType(), symbol.getElementNum());
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(id,new PointerType(type)));
                temp.add(new Value("0",new IntegerType()));
                temp.add(new Value("0",new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type,temp));
                id="%"+(variableId-1);
                type=new PointerType(new IntegerType());
            }else if(!(symbol instanceof FuncParamSymbol) &&(symbol.getType()==SymbolType.CharArray||symbol.getType()==SymbolType.ConstCharArray)){
                // char array
                type=new ArrayType(new CharType(), symbol.getElementNum());
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(id,new PointerType(type)));
                temp.add(new Value("0",new IntegerType()));
                temp.add(new Value("0",new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type,temp));
                id="%"+(variableId-1);
                type=new PointerType(new CharType());
            }else if(symbol.getType()==SymbolType.Char||symbol.getType()==SymbolType.ConstChar){
                // char
                type=new CharType();
            }else{
                if(symbol.getDimension()==1){
                    //数组
                    if(symbol.getType()==SymbolType.IntArray||symbol.getType()==SymbolType.ConstIntArray){
                        type=new PointerType(new IntegerType());
                    }else{
                        type=new PointerType(new CharType());
                    }
                }else{
                    //变量
                    if(symbol.getType()==SymbolType.Int||symbol.getType()==SymbolType.ConstInt){
                        type=new IntegerType();
                    }else{
                        type=new CharType();
                    }
                }
            }
            if(isRight&&loadPointer(id,type)){
                id="%"+(variableId-1);
            }
            if(symbol instanceof FuncParamSymbol&&type instanceof PointerType){
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(id,type));
                temp.add(new Value("0",new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type.getType(),temp));
                id="%"+(variableId-1);
            }
            return new Pair(id,type);
        }else{
            //数组取值
            Pair pair=ExpToLLVM(lVal.getExp());
            Symbol symbol=getVariable(lVal.getIdent().getName(),presentId);
            Type type=(symbol.getType()==SymbolType.Int||symbol.getType()==SymbolType.IntArray||symbol.getType()==SymbolType.ConstIntArray||symbol.getType()==SymbolType.ConstInt)?new IntegerType():new CharType();
            if(symbol instanceof FuncParamSymbol&&!((FuncParamSymbol) symbol).getIsLoad()){
                getCurBasicBlock().addInstruction(new Alloca("%"+(variableId++),new PointerType(type)));
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(symbol.getId(),new PointerType(type)));
                temp.add(new Value("%"+(variableId-1),new PointerType(new PointerType(type))));
                getCurBasicBlock().addInstruction(new Store(null,null,temp));
                setFuncParamsIdAndLoad(lVal.getIdent().getName(),presentId);
                String id="%"+(variableId-1);
                pointerSet.add(presentId+id);
                loadPointer(id,new PointerType(type));
                temp = new ArrayList<>();
                temp.add(new Value("%"+(variableId-1),new PointerType(type)));
                if(typeConversion(pair.type,pair.id,new IntegerType())){
                    pair.id="%"+(variableId-1);
                }
                temp.add(new Value(pair.id,new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++), type, temp));
                temp=new ArrayList<>();
                temp.add(new Value("%"+(variableId-1),new PointerType(type)));
                getCurBasicBlock().addInstruction(new Load("%"+(variableId++),type,temp));
                return new Pair("%"+(variableId-1),type);
            }else if(symbol instanceof FuncParamSymbol&&((FuncParamSymbol) symbol).getIsLoad()){
                ArrayList<Value> temp=new ArrayList<>();
                temp.add(new Value(symbol.getId(),new PointerType(new PointerType(type))));
                getCurBasicBlock().addInstruction(new Load("%"+(variableId++),new PointerType(type),temp));
                temp=new ArrayList<>();
                temp.add(new Value("%"+(variableId-1),new PointerType(type)));
                if(typeConversion(pair.type,pair.id,new IntegerType())){
                    pair.id="%"+(variableId-1);
                }
                temp.add(new Value(pair.id,new IntegerType()));
                getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),type,temp));
                if(isRight){
                    temp=new ArrayList<>();
                    temp.add(new Value("%"+(variableId-1),new PointerType(type)));
                    getCurBasicBlock().addInstruction(new Load("%"+(variableId++),type,temp));
                }
                return new Pair("%"+(variableId-1),type);
            }
            ArrayList<Value> temp=new ArrayList<>();
            temp.add(new Value(symbol.getId(),new PointerType(new ArrayType(type,symbol.getElementNum()))));
            temp.add(new Value("0",new IntegerType()));
            if(typeConversion(pair.type,pair.id,new IntegerType())){
                pair.id="%"+(variableId-1);
            }
            temp.add(new Value(pair.id,new IntegerType()));
            getCurBasicBlock().addInstruction(new Getelementptr("%"+(variableId++),
                    new ArrayType(type,symbol.getElementNum()),
                    temp
                    ));
            temp=new ArrayList<>();
            temp.add(new Value("%"+(variableId-1),new PointerType(type)));
            if(isRight){
                getCurBasicBlock().addInstruction(new Load("%"+(variableId++),type,temp));
            }
            return new Pair("%"+(variableId-1),type);
        }
    }

    public void setFuncParamsIdAndLoad(String name, int presentId) {
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(name)){
                symbol.setId("%"+(variableId-1));
                symbol.setLoad();
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            getVariable(name,SymbolTables.get(presentId-1).getFatherId());
        }
    }

    public Symbol getVariable(String name,int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(name)){
                return symbol;
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return getVariable(name,SymbolTables.get(presentId-1).getFatherId());
        }else{
            return null;
        }
    }

    public ArrayList<Argument> getArgumentList(List<FuncParamSymbol> funcParamSymbolList){
        ArrayList<Argument> temp=new ArrayList<>();
        int i=0;
        for(FuncParamSymbol funcParamSymbol:funcParamSymbolList){
            if(funcParamSymbol.getType()==SymbolType.Char){
                temp.add(new Argument(null,new CharType(),i));
            }else if(funcParamSymbol.getType()==SymbolType.Int){
                temp.add(new Argument(null,new IntegerType(),i));
            }else if(funcParamSymbol.getType()==SymbolType.CharArray){
                temp.add(new Argument(null,new PointerType(new CharType()),i));
            }else if(funcParamSymbol.getType()==SymbolType.IntArray){
                temp.add(new Argument(null,new PointerType(new IntegerType()),i));
            }
            i++;
        }
        return temp;
    }

    /*
       获得函数参数的类型
     */
    public ArrayList<Type> getFuncParamsType(List<FuncParamSymbol> funcParamSymbolList){
        ArrayList<Type> temp=new ArrayList<>();
        for(FuncParamSymbol funcParamSymbol:funcParamSymbolList){
            if(funcParamSymbol.getType()==SymbolType.Char){
                temp.add(new CharType());
            }else if(funcParamSymbol.getType()==SymbolType.Int){
                temp.add(new IntegerType());
            }else if(funcParamSymbol.getType()==SymbolType.CharArray){
                temp.add(new PointerType(new CharType()));
            }else if(funcParamSymbol.getType()==SymbolType.IntArray){
                temp.add(new PointerType(new IntegerType()));
            }
        }
        return temp;
    }

    public Symbol FuncFParamToLLVM(FuncFParam funcFParam){
        SymbolType type=funcFParam.handleSymbolType();
        int dimension;
        if(type==SymbolType.Char||type==SymbolType.Int){
            dimension=0;
        }else{
            dimension=1;
            }
        Symbol symbol=new FuncParamSymbol(type,funcFParam.getIdent().getName(),dimension,"%"+(variableId++));
        addSymbol(funcFParam.getIdent().getName(),symbol);
        return symbol;
    }


    public void MainFuncDefToLLVM(MainFuncDef mainFuncDef) {
        SymbolTables.add(new SymbolTable(id,presentId));
        presentId=id;
        id++;
        stackId.add(presentId);
        variableId=1;
        Function function=new Function("@main",new FunctionType(new ArrayList<>(),new IntegerType(),false),new ArrayList<>());
        module.addFunctionList(function);
        function.addBasicBlock(new BasicBlock(null,null));
        BlockToLLVM(mainFuncDef.getBlock(),false,false);
        stackId.pop();
    }

    public String outputLLVM() {
        return module.toString();
    }
}
