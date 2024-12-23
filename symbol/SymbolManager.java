package symbol;

import symbol.Symbol.*;
import frontend.AST.*;
import frontend.Token.ErrorToken;
import frontend.Token.ErrorType;
import frontend.Token.TrueToken;
import frontend.Token.TrueType;

import java.util.*;

public class SymbolManager {
    protected ASTNode AST;
    protected List<ErrorToken> errorList;
    protected List<SymbolTable> SymbolTables;
    protected Set<Integer> ErrorLineNumber;
    protected int id;  //作用域记录，只增不减
    protected int presentId;  //当前作用域
    protected Stack<Integer> stackId;

    public SymbolManager(ASTNode AST,List<ErrorToken> errorList,Set<Integer> ErrorLineNumber) {
        this.AST = AST;
        this.errorList=errorList;
        this.SymbolTables=new ArrayList<>();
        this.stackId=new Stack<>();
        presentId=1;
        id=1;
        this.ErrorLineNumber=ErrorLineNumber;
    }

    public List<ErrorToken> getSymbolErrorList(){
        return errorList;
    }

    public void addSymbol(String Ident, Symbol symbol){
        SymbolTables.get(presentId-1).addDirectory(Ident,symbol);
    }

    public void CompUnitSymbol(){
        SymbolTables.add(new SymbolTable(id,0));
        stackId.add(presentId);
        id++;
        for(Decl decl:AST.getDeclList()){
            DeclSymbol(decl);
        }
        for(FuncDef funcDef:AST.getFuncDefList()){
            FuncDefSymbol(funcDef);
        }
        MainFuncDefSymbol(AST.getMainFuncDef());
    }

    public void DeclSymbol(Decl decl){
        if(decl.getConstDecl()!=null){
            ConstDeclSymbol(decl.getConstDecl());
        }else{
            VarDeclSymbol(decl.getVarDecl());
        }
    }

    public void ConstDeclSymbol(ConstDecl constDecl){
        for(ConstDef constDef:constDecl.getConstDefList()){
            ConstDefSymbol(constDef,constDecl.getBType().getType());
        }
    }

    public void ConstDefSymbol(ConstDef constDef, TrueType BType){
        if(checkNameOverload(constDef.getIdent())){
            return;
        }
        if(BType==TrueType.INTTK){
            //Int
            if(constDef.getType()==0){
                //一维数组
                Symbol symbol=new VariableSymbol(SymbolType.ConstIntArray,constDef.getIdent().getName(),1);
                addSymbol(constDef.getIdent().getName(),symbol);
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.ConstInt,constDef.getIdent().getName(),0);
                addSymbol(constDef.getIdent().getName(),symbol);
            }
        }else{
            //Char
            if(constDef.getType()==0){
                //一维数组
                Symbol symbol=new VariableSymbol(SymbolType.ConstCharArray,constDef.getIdent().getName(),1);
                addSymbol(constDef.getIdent().getName(),symbol);
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.ConstChar,constDef.getIdent().getName(),0);
                addSymbol(constDef.getIdent().getName(),symbol);
            }
        }
        if(constDef.getConstExp()!=null){
            ConstExpError(constDef.getConstExp());
        }
        ConstInitValError(constDef.getConstInitVal());
    }

    public void ConstExpError(ConstExp constExp){
        AddExpError(constExp.getAddExp());
    }

    public void AddExpError(AddExp addExp){
        for(MulExp mulExp:addExp.getMulExpList()){
            MulExpError(mulExp);
        }
    }

    public void MulExpError(MulExp mulExp){
        for(UnaryExp unaryExp:mulExp.getUnaryExpList()){
            UnaryExpError(unaryExp);
        }
    }

    public void UnaryExpError(UnaryExp unaryExp){
        if(unaryExp.getPrimaryExp()!=null){
            PrimaryExpError(unaryExp.getPrimaryExp());
        } else if(unaryExp.getIdent()!=null){
            if(isNotDefine(unaryExp.getIdent().getName(), presentId)&&!ErrorLineNumber.contains(unaryExp.getIdent().getLineNumber())){
                ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.c);
                errorList.add(errorToken);
                ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
            }
            if(unaryExp.getFuncRParams()!=null){
                isValidFunc(unaryExp,presentId);
                FuncRParamsError(unaryExp.getFuncRParams());
            }else{
                // 也有可能是本来有定义确没传任何值
                if(!ErrorLineNumber.contains(unaryExp.getIdent().getLineNumber())){
                    for(Symbol symbol:SymbolTables.get(0).getDirectory().values()) {
                        if (symbol instanceof FunctionSymbol && symbol.getIdent().equals(unaryExp.getIdent().getName())) {
                            if (!symbol.getFuncParams().isEmpty()) {
                                //ERROR:函数参数个数不匹配
                                ErrorToken errorToken = new ErrorToken(unaryExp.getIdent().getLineNumber(), ErrorType.d);
                                errorList.add(errorToken);
                                ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                                return;
                            }
                        }
                    }
                }
            }

        }else if(unaryExp.getUnaryExp()!=null){
            UnaryExpError(unaryExp.getUnaryExp());
        }
    }

    public void FuncRParamsError(FuncRParams funcRParams){
        for(Exp exp:funcRParams.getExpList()){
            ExpError(exp);
        }
    }

    public void PrimaryExpError(PrimaryExp primaryExp){
        if(primaryExp.getExp()!=null){
            ExpError(primaryExp.getExp());
        }else if(primaryExp.getlVal()!=null){
            LValError(primaryExp.getlVal());
        }
    }

    public void ExpError(Exp exp){
        AddExpError(exp.getAddExp());
    }

    public void isValidFunc(UnaryExp unaryExp,int presentId){
        if(ErrorLineNumber.contains(unaryExp.getIdent().getLineNumber())){
            return;
        }
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol instanceof FunctionSymbol&&symbol.getIdent().equals(unaryExp.getIdent().getName())){
                if(!(symbol.getFuncParams().size()==unaryExp.getFuncRParams().getExpList().size())){
                    //ERROR:函数参数个数不匹配
                    ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.d);
                    errorList.add(errorToken);
                    ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                    return;
                }
                //函数参数类型 不匹配
                for(int i=0;i<symbol.getFuncParams().size();i++){
                    int dimension=symbol.getFuncParams().get(i).getDimension();
                    SymbolType type=symbol.getFuncParams().get(i).getType();
                    SymbolType ExpType= getExpType(unaryExp.getFuncRParams().getExpList().get(i));
                    if(dimension==1){
                        //传递数组
                        if(type==SymbolType.CharArray){
                            //char array
                            if(ExpType!=SymbolType.CharArray&&ExpType!=SymbolType.ConstCharArray){
                                ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.e);
                                errorList.add(errorToken);
                                ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                                return;
                            }

                        }else{
                            //int array
                            if(ExpType!=SymbolType.IntArray&&ExpType!=SymbolType.ConstIntArray){
                                ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.e);
                                errorList.add(errorToken);
                                ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                                return;
                            }
                        }
                    }else{
                        //传递变量
                        if(ExpType==SymbolType.CharArray||ExpType==SymbolType.IntArray||ExpType==SymbolType.ConstCharArray||ExpType==SymbolType.ConstIntArray||ExpType==SymbolType.None){
                            ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.e);
                            errorList.add(errorToken);
                            ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                            return;
                        }
                    }
                }
                return;
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            isValidFunc(unaryExp,SymbolTables.get(presentId-1).getFatherId());
        }
    }

    public SymbolType getExpType(Exp exp){
        return getAddExpType(exp.getAddExp());
    }

    public SymbolType getAddExpType(AddExp addExp){
        return getMulExpType(addExp.getMulExpList().get(0));
    }

    public SymbolType getMulExpType(MulExp mulExp){
        return getUnaryExpType(mulExp.getUnaryExpList().get(0));
    }

    public SymbolType getUnaryExpType(UnaryExp unaryExp){
        if(unaryExp.getPrimaryExp()!=null){
            return getPrimaryExpType(unaryExp.getPrimaryExp());
        }else if(unaryExp.getIdent()!=null){
            return getFuncType(unaryExp.getIdent().getName(),presentId);
        }else{
            return getUnaryExpType(unaryExp.getUnaryExp());
        }
    }

    public SymbolType getFuncType(String Ident,int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(Ident)){
                if(symbol.getType()==SymbolType.CharFunc){
                    return SymbolType.Char;
                }else if(symbol.getType()==SymbolType.IntFunc){
                    return SymbolType.Int;
                }else{
                    return SymbolType.None;
                }
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return getFuncType(Ident,SymbolTables.get(presentId-1).getFatherId());
        }
        return null;
    }

    public SymbolType getPrimaryExpType(PrimaryExp primaryExp){
        int type=primaryExp.getType();
        return switch (type) {
            case 1 -> getExpType(primaryExp.getExp());
            case 2 -> getLValType(primaryExp.getlVal(),presentId);
            case 3 -> SymbolType.Int;
            default -> SymbolType.Char;
        };
    }

    public SymbolType getLValType(LVal lVal,int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(lVal.getIdent().getName())){
                if(lVal.getExp()!=null){
                    return SymbolType.Int;
                }else{
                    return symbol.getType();
                }
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return getLValType(lVal,SymbolTables.get(presentId-1).getFatherId());
        }
        return null;
    }

    public void ConstInitValError(ConstInitVal constInitVal){
        if(!constInitVal.getConstExpList().isEmpty()){
            for(ConstExp constExp:constInitVal.getConstExpList()){
                ConstExpError(constExp);
            }
        }
    }

    public Boolean checkNameOverload(TrueToken Ident){
        if(SymbolTables.get(presentId-1).getNameOverLoad(Ident.getName())&&!ErrorLineNumber.contains(Ident.getLineNumber())){
            ErrorToken errorToken=new ErrorToken(Ident.getLineNumber(), ErrorType.b);
            errorList.add(errorToken);
            ErrorLineNumber.add(Ident.getLineNumber());
            return true;
        }
        return false;
    }

    public void VarDeclSymbol(VarDecl varDecl){
        for(VarDef varDef:varDecl.getVarDefList()){
            VarDefSymbol(varDef,varDecl.getBType().getType());
        }
    }

    public void VarDefSymbol(VarDef varDef,TrueType BType){
        if(checkNameOverload(varDef.getIdent())){
            return;
        }
        if(BType==TrueType.INTTK){
            //Int
            if(varDef.getLBRACK()!=null){
                //一维数组
                Symbol symbol=new VariableSymbol(SymbolType.IntArray,varDef.getIdent().getName(),1);
                addSymbol(varDef.getIdent().getName(),symbol);
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.Int,varDef.getIdent().getName(),0);
                addSymbol(varDef.getIdent().getName(),symbol);
            }
        }else{
            //Char
            if(varDef.getLBRACK()!=null){
                //一维数组
                Symbol symbol=new VariableSymbol(SymbolType.CharArray,varDef.getIdent().getName(),1);
                addSymbol(varDef.getIdent().getName(),symbol);
            }else{
                //普通变量
                Symbol symbol=new VariableSymbol(SymbolType.Char,varDef.getIdent().getName(),0);
                addSymbol(varDef.getIdent().getName(),symbol);
            }
        }
        if(varDef.getConstExp()!=null){
            ConstExpError(varDef.getConstExp());
        }
        if(varDef.getInitVal()!=null){
            InitValError(varDef.getInitVal());
        }
    }

    public void InitValError(InitVal initVal){
        for(Exp exp:initVal.getExpList()){
            ExpError(exp);
        }
    }


    public void FuncDefSymbol(FuncDef funcDef){
        if(checkNameOverload(funcDef.getIdent())){
            return;
        }
        SymbolType type;
        if(funcDef.getFuncType().getType()==TrueType.VOIDTK){
            type=SymbolType.VoidFunc;
        }else if(funcDef.getFuncType().getType()==TrueType.INTTK){
            type=SymbolType.IntFunc;
        }else{
            type=SymbolType.CharFunc;
        }
        Symbol symbol=new FunctionSymbol(type,funcDef.getIdent().getName());
        SymbolTables.add(new SymbolTable(id,presentId));
        presentId=id;
        id++;
        stackId.push(presentId);
        if(funcDef.getFuncFParams()!=null&&funcDef.getFuncFParams().getFuncFParamList()!=null){
            for(FuncFParam funcFParam:funcDef.getFuncFParams().getFuncFParamList()){
                if(checkNameOverload(funcFParam.getIdent())){
                    break;
                }
                symbol.addFuncParams((FuncParamSymbol) FuncFParamSymbol(funcFParam));
            }
        }
        int temp=stackId.pop();
        addSymbolFunc(funcDef.getIdent().getName(),symbol,stackId.peek());
        stackId.add(temp);
        if(BlockError(funcDef.getBlock(),type,false, type == SymbolType.VoidFunc)&&!ErrorLineNumber.contains(funcDef.getBlock().getRBRACE().getLineNumber())){
            ErrorToken errorToken=new ErrorToken(funcDef.getBlock().getRBRACE().getLineNumber(),ErrorType.g);
            errorList.add(errorToken);
            ErrorLineNumber.add(funcDef.getBlock().getRBRACE().getLineNumber());
        }
        stackId.pop();
        presentId=stackId.peek();
    }

    public void addSymbolFunc(String Ident, Symbol symbol,int Id){
        SymbolTables.get(Id-1).addDirectory(Ident,symbol);
    }

    public Boolean BlockError(Block block, SymbolType type,boolean inFor,boolean isVoid){
        for(int i=0;i<block.getBlockItemList().size();i++){
            BlockItemError(block.getBlockItemList().get(i),inFor,isVoid);
            if(i==block.getBlockItemList().size()-1&&(type==SymbolType.CharFunc||type==SymbolType.IntFunc)){
                if(!(block.getBlockItemList().get(i).getStmt()!=null&&block.getBlockItemList().get(i).getStmt().getRETURN()!=null)){
                    return true;
                }
            }
        }
        return block.getBlockItemList().isEmpty() && (type == SymbolType.CharFunc || type == SymbolType.IntFunc);
    }

    private void BlockItemError(BlockItem blockItem,boolean inFor,boolean isVoid) {
        if(blockItem.getDecl()!=null){
            DeclSymbol(blockItem.getDecl());
        }else{
            StmtSymbol(blockItem.getStmt(),inFor,isVoid);
        }
    }

    public void StmtSymbol(Stmt stmt,boolean inFor,boolean isVoid){
        int type=stmt.getType();
        if(type==1||type==8||type==9){
            if(isConst(stmt.getlVal().getIdent().getName(),presentId)&&!ErrorLineNumber.contains(stmt.getlVal().getIdent().getLineNumber())){
                ErrorToken errorToken=new ErrorToken(stmt.getlVal().getIdent().getLineNumber(),ErrorType.h);
                errorList.add(errorToken);
                ErrorLineNumber.add(stmt.getlVal().getIdent().getLineNumber());
            }
            LValError(stmt.getlVal());
            if(type==1){
                ExpError(stmt.getExp());
            }
        }else if(type==2){
            if(stmt.getExp()!=null){
                ExpError(stmt.getExp());
            }
        }else if(type==3){
            SymbolTables.add(new SymbolTable(id,presentId));
            presentId=id;
            id++;
            stackId.push(presentId);
            BlockError(stmt.getBlock(),SymbolType.None,inFor,isVoid);
            stackId.pop();
            presentId=stackId.peek();
        }else if(type==4){
            CondError(stmt.getCond());
            StmtSymbol(stmt.getStmt(),inFor,isVoid);
            if(stmt.getElseStmt()!=null){
                StmtSymbol(stmt.getElseStmt(),inFor,isVoid);
            }
        }else if(type==5){
            if(stmt.getForStmt1()!=null){
                ForStmtSymbol(stmt.getForStmt1());
            }
            if(stmt.getCond()!=null){
                CondError(stmt.getCond());
            }
            if(stmt.getForStmt2()!=null){
                ForStmtSymbol(stmt.getForStmt2());
            }
            StmtSymbol(stmt.getStmt(),true,isVoid);
        }else if(type==6){
            //break/continue在非循环块中使用
            if(!inFor&&!ErrorLineNumber.contains(stmt.getBreakOrContinue().getLineNumber())){
                ErrorToken errorToken=new ErrorToken(stmt.getBreakOrContinue().getLineNumber(),ErrorType.m);
                errorList.add(errorToken);
                ErrorLineNumber.add(stmt.getBreakOrContinue().getLineNumber());
            }
        }else if(type==7){
            if(stmt.getExp()!=null){
                ExpError(stmt.getExp());
                if(isVoid&&!ErrorLineNumber.contains(stmt.getRETURN().getLineNumber())){
                    ErrorToken errorToken=new ErrorToken(stmt.getRETURN().getLineNumber(),ErrorType.f);
                    errorList.add(errorToken);
                    ErrorLineNumber.add(stmt.getRETURN().getLineNumber());
                }
            }
        }else if(type==10){
            int size=stmt.getExpList().size();
            if(size!=StringConstNum(stmt.getStringConst().getName())&&!ErrorLineNumber.contains(stmt.getPRINTF().getLineNumber())){
                ErrorToken errorToken=new ErrorToken(stmt.getPRINTF().getLineNumber(),ErrorType.l);
                errorList.add(errorToken);
                ErrorLineNumber.add(stmt.getPRINTF().getLineNumber());
            }
            for(Exp exp:stmt.getExpList()){
                ExpError(exp);
            }
        }
    }

    public int StringConstNum(String name){
        int count=0;
        for(int i=0;i<name.length();i++){
            if(name.charAt(i)=='%'&&i+1<name.length()&&(name.charAt(i+1)=='c'||name.charAt(i+1)=='d')){
                count++;
            }
        }
        return count;
    }

    public void CondError(Cond cond){
        LOrExpError(cond.getlOrExp());
    }

    public void LOrExpError(LOrExp lOrExp){
        for(LAndExp lAndExp:lOrExp.getlAndExpList()){
            LAndExpError(lAndExp);
        }
    }

    public void LAndExpError(LAndExp lAndExp){
        for(EqExp eqExp:lAndExp.getEqExpList()){
            EqExpError(eqExp);
        }
    }

    public void EqExpError(EqExp eqExp){
        for(RelExp relExp:eqExp.getRelExpList()){
            RelExpError(relExp);
        }
    }

    public void RelExpError(RelExp relExp){
        for(AddExp addExp:relExp.getAddExpList()){
            AddExpError(addExp);
        }
    }

    public void LValError(LVal lVal){
        if(lVal.getExp()!=null){
            ExpError(lVal.getExp());
        }
        if(isNotDefine(lVal.getIdent().getName(), presentId)&&!ErrorLineNumber.contains(lVal.getIdent().getLineNumber())){
            ErrorToken errorToken=new ErrorToken(lVal.getIdent().getLineNumber(),ErrorType.c);
            errorList.add(errorToken);
            ErrorLineNumber.add(lVal.getIdent().getLineNumber());
        }
    }

    public boolean isNotDefine(String Ident, int presentId){
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(Ident)){
                return false;
            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return isNotDefine(Ident,SymbolTables.get(presentId-1).getFatherId());
        }else{
            return true;
        }
    }

    public void ForStmtSymbol(ForStmt forStmt){
        if(isConst(forStmt.getlVal().getIdent().getName(),presentId)&&!ErrorLineNumber.contains(forStmt.getlVal().getIdent().getLineNumber())){
            ErrorToken errorToken=new ErrorToken(forStmt.getlVal().getIdent().getLineNumber(),ErrorType.h);
            errorList.add(errorToken);
            ErrorLineNumber.add(forStmt.getlVal().getIdent().getLineNumber());
        }
        LValError(forStmt.getlVal());
        ExpError(forStmt.getExp());
    }

    public Boolean isConst(String Ident,int presentId){
        //判断是否是常量，如果是返回True
        for(Symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
            if(symbol.getIdent().equals(Ident)){
                return symbol.getType() == SymbolType.ConstChar ||
                        symbol.getType() == SymbolType.ConstInt ||
                        symbol.getType() == SymbolType.ConstCharArray ||
                        symbol.getType() == SymbolType.ConstIntArray;

            }
        }
        if(SymbolTables.get(presentId-1).getFatherId()!=0){
            return isConst(Ident,SymbolTables.get(presentId-1).getFatherId());
        }else{
            return false;
        }
    }

    public Symbol FuncFParamSymbol(FuncFParam funcFParam){
        SymbolType type=funcFParam.handleSymbolType();
        int dimension;
        if(type==SymbolType.Char||type==SymbolType.Int){
            dimension=0;
        }else
            dimension=1;
        Symbol symbol=new FuncParamSymbol(type,funcFParam.getIdent().getName(),dimension);
        addSymbol(funcFParam.getIdent().getName(),symbol);
        return symbol;
    }

    public void MainFuncDefSymbol(MainFuncDef mainFuncDef){
        SymbolTables.add(new SymbolTable(id,presentId));
        presentId=id;
        id++;
        stackId.add(presentId);
        if(BlockError(mainFuncDef.getBlock(),SymbolType.IntFunc,false,false)&&!ErrorLineNumber.contains(mainFuncDef.getBlock().getRBRACE().getLineNumber())){
            ErrorToken errorToken=new ErrorToken(mainFuncDef.getBlock().getRBRACE().getLineNumber(),ErrorType.g);
            errorList.add(errorToken);
            ErrorLineNumber.add(mainFuncDef.getBlock().getRBRACE().getLineNumber());
        }
        stackId.pop();
        //presentId=stackId.peek();
    }

    public String outFalseVisitor() {
        errorList.sort(new Comparator<>() {
            @Override
            public int compare(ErrorToken e1, ErrorToken e2) {
                return Integer.compare(e1.getLineNumber(), e2.getLineNumber());
            }
        });
        StringBuilder a= new StringBuilder();
        for(ErrorToken i:errorList){
            a.append(i.toString());
        }
        return a.toString();
    }

    public String outTrueSymbol() {
        StringBuilder a= new StringBuilder();
        for(SymbolTable symbolTable:SymbolTables){
            int id=symbolTable.id;
            for(Symbol symbol:symbolTable.getDirectory().values()){
                a.append(id).append(" ").append(symbol.toString());
            }
        }
        return a.toString();
    }
}
