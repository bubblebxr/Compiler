package frontend;

import frontend.AST.*;
import frontend.Token.ErrorToken;
import frontend.Token.ErrorType;
import frontend.Token.TrueToken;
import frontend.Token.TrueType;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Parser {

    /*错误输出列表*/
    protected List<ErrorToken> errorList;

    /*正确输出列表*/
    protected List<TrueToken> TokenList;

    protected Set<Integer> ErrorLineNumber;

    protected int present;

    protected ASTNode AST;

    public Parser(List<ErrorToken> errorList, List<TrueToken> TokenList, Set<Integer> ErrorLineNumber){
        this.errorList=errorList;
        this.TokenList=TokenList;
        AST=new ASTNode();
        this.present=0;
        this.ErrorLineNumber=ErrorLineNumber;
    }

    public ASTNode getASTNode(){
        return AST;
    }

    public TrueType nowType(){
        return present < TokenList.size() ? TokenList.get(present).getType() : null;
    }

    public TrueToken now(){
        return present < TokenList.size() ? TokenList.get(present) : null;
    }

    public TrueType preReadType(){
        return present+1 < TokenList.size() ? TokenList.get(present+1).getType() : null;
    }

    public TrueToken preRead(){
        return present+1 < TokenList.size() ? TokenList.get(present+1) : null;
    }

    public TrueType prePreReadType(){
        return present+2 < TokenList.size() ? TokenList.get(present+2).getType() : null;
    }

    public TrueToken prePreRead(){
        return present+2 < TokenList.size() ? TokenList.get(present+2) : null;
    }

    public TrueToken prePrePreRead(){
        return present+3 < TokenList.size() ? TokenList.get(present+3) : null;
    }

    public Integer beforeLineNum(){
        return present-1>=0 ? TokenList.get(present-1).getLineNumber() :null;
    }

    public void CompUnit() {
        //{Decl}:const
        while(prePreReadType()!=TrueType.LPARENT&& preReadType()!=TrueType.MAINTK){
            AST.insertDeclList(createDecl());
        }
        //{FuncDef}
        while(preReadType()!=TrueType.MAINTK){
            createFuncDef();
        }
        //MainFuncDef
        AST.setMainFuncDef(createMainFuncDef());
    }

    public Decl createDecl(){
        if(nowType()==TrueType.CONSTTK){
            //ConstDecl
            return createConstDecl();
        }else if(nowType()==TrueType.INTTK|| nowType()==TrueType.CHARTK){
            //VarDecl
            return createVarDecl();
        }
        return null;
    }

    public Decl createConstDecl(){
        if(preReadType()==TrueType.INTTK|| preReadType()==TrueType.CHARTK){
            ConstDecl constDecl=new ConstDecl(now(),preRead());
            present+=2;
            constDecl.insertConstDefList(createConstDef());
            while(nowType()==TrueType.COMMA){
                constDecl.insertCOMMAList(now());
                present++;
                constDecl.insertConstDefList(createConstDef());
            }
            if(nowType()==TrueType.SEMICN){
                constDecl.setSEMICN(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }

            Decl decl=new Decl();
            decl.setConstDecl(constDecl);
            return decl;
        }
        return null;
    }

    public ConstDef createConstDef(){
        //ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
        ConstDef constDef=new ConstDef();
        if(nowType()==TrueType.IDENFR){
            constDef.setIdent(now());
            present++;
        }
        if(nowType()==TrueType.LBRACK){
            constDef.setType(0);
            constDef.setLBRACK(now());
            present++;
            constDef.setConstExp(createConstExp());
            if(nowType()==TrueType.RBRACK){
                constDef.setRBRACK(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.k);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
        }else{
            constDef.setType(1);
        }
        if(nowType()==TrueType.ASSIGN){
            constDef.setASSIGN(now());
            present++;
            constDef.setConstInitVal(createConstInitVal(constDef.getType()));
        }

        return constDef;
    }

    public ConstInitVal createConstInitVal(int type){
        if(nowType()==TrueType.STRCON){
            ConstInitVal constInitVal=new ConstInitVal(type);
            constInitVal.setType(2);
            constInitVal.setStringConst(now());
            present++;
            return constInitVal;
        } else if(type==0&&nowType()==TrueType.LBRACE){
            //一维数组初值
            ConstInitVal constInitVal=new ConstInitVal(type,now());
            present++;
            if(nowType()!=TrueType.RBRACE){
                constInitVal.insertConstExpList(createConstExp());
                while(nowType()==TrueType.COMMA){
                    constInitVal.insertCOMMAList(now());
                    present++;
                    constInitVal.insertConstExpList(createConstExp());
                }
            }
            if(nowType()==TrueType.RBRACE){
                constInitVal.setRBRACE(now());
                present++;
            }
            return constInitVal;
        }else if(type==1&&nowType()!=TrueType.LBRACE){
            //常表达式初值
            ConstInitVal constInitVal=new ConstInitVal(type);
            //区分ConstExp和StringConst
            if(nowType()==TrueType.STRCON){
                //StringConst
                constInitVal.setType(2);
                constInitVal.setStringConst(now());
                present++;
                return constInitVal;
            }else{
                constInitVal.insertConstExpList(createConstExp());
                return constInitVal;
            }
        }
        return null;
    }

    public ConstExp createConstExp(){
        ConstExp constExp=new ConstExp();
        constExp.setAddExp(createAddExp());
        return constExp;
    }

    public AddExp createAddExp(){
        //AddExp: MulExp { ('+' | '-') MulExp }
        AddExp addExp=new AddExp();
        addExp.insertMulExpList(createMulExp());
        while(nowType()==TrueType.PLUS||nowType()==TrueType.MINU){
            addExp.insertTokenList(now());
            present++;
            addExp.insertMulExpList(createMulExp());
        }
        return addExp;
    }

    public MulExp  createMulExp(){
        //MulExp: UnaryExp { ('*' | '/' | '%')  UnaryExp }
        MulExp mulExp=new MulExp();
        mulExp.insertUnaryExpList(createUnaryExp());
        while(nowType()==TrueType.MULT||nowType()==TrueType.DIV||nowType()==TrueType.MOD){
            mulExp.insertTokenList(now());
            present++;
            mulExp.insertUnaryExpList(createUnaryExp());
        }
        return mulExp;
    }


    public UnaryExp createUnaryExp(){
        UnaryExp unaryExp=new UnaryExp();
        if(nowType()==TrueType.IDENFR&&preReadType()==TrueType.LPARENT){
            //Ident '(' [FuncRParams] ')'
            unaryExp.setIdent(now());
            unaryExp.setLPARENT(preRead());
            present+=2;
            if(nowType()!=TrueType.RPARENT){
                unaryExp.setFuncRParams(createFuncRParams());
            }
            if(nowType()==TrueType.RPARENT){
                unaryExp.setRPARENT(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
        }else if(nowType()==TrueType.PLUS||nowType()==TrueType.MINU||nowType()==TrueType.NOT){
            //UnaryOp UnaryExp
            unaryExp.setUnaryOp(now());
            present++;
            unaryExp.setUnaryExp(createUnaryExp());
        }else{
            //PrimaryExp
            unaryExp.setPrimaryExp(createPrimaryExp());
        }
        return unaryExp;
    }

    public FuncRParams createFuncRParams(){
        FuncRParams funcRParams=new FuncRParams();
        funcRParams.insertExpList(createExp());
        while(nowType()==TrueType.COMMA){
            funcRParams.insertCOMMAList(now());
            present++;
            funcRParams.insertExpList(createExp());
        }
        return funcRParams;
    }

    public FuncFParams createFuncFParams(){
        FuncFParams funcFParams=new FuncFParams();
        funcFParams.insertFuncFParamList(createFuncParam());
        while(nowType()==TrueType.COMMA){
            funcFParams.insertCOMMAList(now());
            present++;
            funcFParams.insertFuncFParamList(createFuncParam());
        }
        return funcFParams;
    }

    public FuncFParam createFuncParam(){
        FuncFParam funcFParam=new FuncFParam();
        if(nowType()==TrueType.INTTK||nowType()==TrueType.CHARTK){
            funcFParam.setBType(now());
            present++;
        }
        if(nowType()==TrueType.IDENFR){
            funcFParam.setIdent(now());
            present++;
        }
        if(nowType()==TrueType.LBRACK){
            funcFParam.setLBRACK(now());
            present++;
            if(nowType()==TrueType.RBRACK){
                funcFParam.setRBRACK(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.k);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
        }
        return funcFParam;
    }

    public PrimaryExp createPrimaryExp(){
        PrimaryExp primaryExp=new PrimaryExp();
        if(nowType()==TrueType.LPARENT){
            // '(' Exp ')'
            primaryExp.setType(1);
            primaryExp.setLPARENT(now());
            present++;
            primaryExp.setExp(createExp());
            if(nowType()==TrueType.RPARENT){
                primaryExp.setRPARENT(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
        }else if(nowType()==TrueType.IDENFR){
            //LVal
            primaryExp.setType(2);
            primaryExp.setlVal(createLVal());
        }else if(nowType()==TrueType.INTCON){
            //Number
            primaryExp.setType(3);
            primaryExp.setNumberToken(now());
            present++;
        }else if(nowType()==TrueType.CHRCON){
            //Character
            primaryExp.setType(3);
            primaryExp.setCharacterToken(now());
            present++;
        }
        return primaryExp;
    }

    public LVal createLVal(){
        LVal lVal=new LVal();
        lVal.setIdent(now());
        present++;
        if(nowType()==TrueType.LBRACK){
            lVal.setLBRACK(now());
            present++;
            lVal.setExp(createExp());
            if(nowType()==TrueType.RBRACK){
                lVal.setRBRACK(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.k);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
        }
        return lVal;
    }

    public Exp createExp(){
        Exp exp=new Exp();
        exp.setAddExp(createAddExp());
        return exp;
    }


    public Decl createVarDecl(){
        VarDecl varDecl=new VarDecl();
        //BType
        varDecl.setBType(now());
        present++;
        varDecl.insertVarDefList(createVarDef());
        while(nowType()==TrueType.COMMA){
            varDecl.insertTokenList(now());
            present++;
            varDecl.insertVarDefList(createVarDef());
        }
        if(nowType()==TrueType.SEMICN){
            varDecl.setSEMICN(now());
            present++;
        }else{
            ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
            errorList.add(token);
            ErrorLineNumber.add(beforeLineNum());
        }
        Decl decl=new Decl();
        decl.setVarDecl(varDecl);
        return decl;
    }

    public VarDef createVarDef(){
        VarDef varDef=new VarDef();
        //Ident
        varDef.setIdent(now());
        present++;
        //[ '[' ConstExp ']' ]
        if(nowType()==TrueType.LBRACK){
            varDef.setLBRACK(now());
            present++;
            varDef.setConstExp(createConstExp());
            if(nowType()==TrueType.RBRACK){
                varDef.setRBRACK(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.k);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
        }
        if(nowType()==TrueType.ASSIGN){
            varDef.setASSIGN(now());
            present++;
            varDef.setInitVal(createInitVal());
        }
        return varDef;
    }

    public InitVal createInitVal(){
        InitVal initVal=new InitVal();
        if(nowType()==TrueType.LBRACE){
            initVal.setLBRACE(now());
            present++;
            if(nowType()!=TrueType.RBRACE){
                initVal.insertExpList(createExp());
                while(nowType()==TrueType.COMMA){
                    initVal.insertCOMMAList(now());
                    present++;
                    initVal.insertExpList(createExp());
                }
            }
            if(nowType()==TrueType.RBRACE){
                initVal.setRBRACE(now());
                present++;
            }
        }else if(nowType()==TrueType.STRCON){
            initVal.setSTRCON(now());
            present++;
        }else{
            initVal.insertExpList(createExp());
        }
        return initVal;
    }

    public void createFuncDef(){
        FuncDef funcDef=new FuncDef(now(),preRead(),prePreRead());
        present+=3;
        if(nowType()!=TrueType.RPARENT&&nowType()!=TrueType.LBRACE){
            funcDef.setFuncFParams(createFuncFParams());
        }
        if(nowType()==TrueType.RPARENT){
            funcDef.setRPARENT(now());
            present++;
        }else{
            ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
            errorList.add(token);
            ErrorLineNumber.add(beforeLineNum());
        }
        funcDef.setBlock(createBlock());
        AST.insertFuncDefList(funcDef);
    }

    public Block createBlock(){
        Block block=new Block();
        block.setLBRACE(now());
        present++;
        while(nowType()!=TrueType.RBRACE){
            block.insertBlockItemList(createBlockItem());
        }
        block.setRBRACE(now());
        present++;
        return block;
    }

    public BlockItem createBlockItem(){
        BlockItem blockItem=new BlockItem();
        if(nowType()==TrueType.CONSTTK||nowType()==TrueType.INTTK||nowType()==TrueType.CHARTK){
            blockItem.setDecl(createDecl());
        }else{
            blockItem.setStmt(createStmt());
        }
        return blockItem;
    }

    public Stmt createStmt(){
        if(nowType()==TrueType.LBRACE){
            //Block
            return new Stmt(3,createBlock());
        }else if(nowType()==TrueType.IFTK){
            //'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
            Stmt stmt=new Stmt(4);
            stmt.setIF(now());
            stmt.setLPARENT(preRead());
            present+=2;
            stmt.setCond(createCond());
            if(nowType()==TrueType.RPARENT){
                stmt.setRPARENT(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }

            stmt.setStmt(createStmt());
            if(nowType()==TrueType.ELSETK){
                stmt.setELSE(now());
                present++;
                stmt.setElseStmt(createStmt());
            }
            return stmt;
        }else if(nowType()==TrueType.FORTK){
            // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
            Stmt stmt=new Stmt(5);
            stmt.setFOR(now());
            present++;
            stmt.setLPARENT(now());
            present++;
            if(nowType()==TrueType.IDENFR){
                stmt.setForStmt1(createForStmt());
            }
            if(nowType()==TrueType.SEMICN){
                stmt.setForSEMICN1(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            if(nowType() == TrueType.IDENFR ||
                    nowType() == TrueType.PLUS ||
                    nowType() == TrueType.MINU ||
                    nowType()== TrueType.NOT ||
                    nowType() == TrueType.LPARENT ||
                    nowType()==TrueType.CHRCON||
                    nowType() == TrueType.INTCON){
                stmt.setCond(createCond());
            }
            if(nowType()==TrueType.SEMICN){
                stmt.setForSEMICN2(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            if(nowType()==TrueType.IDENFR){
                stmt.setForStmt2(createForStmt());
            }
            if(nowType()==TrueType.RPARENT){
                stmt.setRPARENT(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            stmt.setStmt(createStmt());
            return stmt;
        }else if(nowType()==TrueType.BREAKTK||nowType()==TrueType.CONTINUETK){
            //'break' ';' | 'continue' ';'
            Stmt stmt=new Stmt(6);
            stmt.setBreakOrContinue(now());
            present++;
            if(nowType()==TrueType.SEMICN){
                stmt.setSEMICN(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            return stmt;
        }else if(nowType()==TrueType.RETURNTK){
            // 'return' [Exp] ';'
            Stmt stmt=new Stmt(7);
            stmt.setRETURN(now());
            present++;
            if(nowType() == TrueType.IDENFR ||
                    nowType() == TrueType.PLUS ||
                    nowType() == TrueType.MINU ||
                    nowType()== TrueType.NOT ||
                    nowType() == TrueType.LPARENT ||
                    nowType()==TrueType.CHRCON||
                    nowType() == TrueType.INTCON){
                stmt.setExp(createExp());
            }
            if(nowType()==TrueType.SEMICN){
                stmt.setSEMICN(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            return stmt;
        }else if(nowType()==TrueType.PRINTFTK){
            //'printf''('StringConst {','Exp}')'';'
            Stmt stmt=new Stmt(10,now(),preRead(),prePreRead());
            present+=3;
            while(nowType()==TrueType.COMMA){
                stmt.insertCOMMAList(now());
                present++;
                stmt.insertExpList(createExp());
            }
            if(nowType()==TrueType.RPARENT){
                stmt.setRPARENT(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            if(nowType()==TrueType.SEMICN){
                stmt.setSEMICN(now());
                present++;
            }else{
                ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                errorList.add(token);
                ErrorLineNumber.add(beforeLineNum());
            }
            return  stmt;
        }else if(nowType()==TrueType.IDENFR){
            int flag=0,line=now().getLineNumber();
            for(int j=present;j<TokenList.size()&&TokenList.get(j).getType()!=TrueType.SEMICN&&TokenList.get(j).getLineNumber()==line;j++){
                if (TokenList.get(j).getType() == TrueType.ASSIGN) {
                    flag = 1;
                    break;
                }
            }
            //LVal '=' Exp ';'
            if(isLVal()&&flag==1){
                Stmt stmt=new Stmt();
                stmt.setlVal(createLVal());
                stmt.setASSIGN(now());
                present++;
                if(nowType()==TrueType.GETINTTK){
                    stmt.setType(8);
                    stmt.setGetInt(now());
                    present++;
                    stmt.setLPARENT(now());
                    present++;
                    if(nowType()==TrueType.RPARENT){
                        stmt.setRPARENT(now());
                        present++;
                    }else{
                        ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                        errorList.add(token);
                        ErrorLineNumber.add(beforeLineNum());
                    }
                    if(nowType()==TrueType.SEMICN){
                        stmt.setSEMICN(now());
                        present++;
                    }else{
                        ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                        errorList.add(token);
                        ErrorLineNumber.add(beforeLineNum());
                    }
                }else if(nowType()==TrueType.GETCHARTK){
                    stmt.setType(9);
                    stmt.setGetChar(now());
                    present++;
                    stmt.setLPARENT(now());
                    present++;
                    if(nowType()==TrueType.RPARENT){
                        stmt.setRPARENT(now());
                        present++;
                    }else{
                        ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
                        errorList.add(token);
                        ErrorLineNumber.add(beforeLineNum());
                    }
                    if(nowType()==TrueType.SEMICN){
                        stmt.setSEMICN(now());
                        present++;
                    }else{
                        ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                        errorList.add(token);
                        ErrorLineNumber.add(beforeLineNum());
                    }
                }else{
                    stmt.setType(1);
                    stmt.setExp(createExp());
                    if(nowType()==TrueType.SEMICN){
                        stmt.setSEMICN(now());
                        present++;
                    }else{
                        ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
                        errorList.add(token);
                        ErrorLineNumber.add(beforeLineNum());
                    }
                }
                return stmt;
            }
        }else if(nowType()==TrueType.SEMICN){
            Stmt stmt=new Stmt(2);
            stmt.setSEMICN(now());
            present++;
            return stmt;
        }

        Stmt stmt=new Stmt(2);
//        if(nowType() == TrueType.IDENFR ||
//                nowType() == TrueType.PLUS ||
//                nowType() == TrueType.MINU ||
//                nowType()== TrueType.NOT ||
//                nowType() == TrueType.LPARENT ||
//                nowType()==TrueType.CHRCON||
//                nowType() == TrueType.INTCON){
//
//        }{
            stmt.setExp(createExp());
//        }
        if(nowType()==TrueType.SEMICN){
            stmt.setSEMICN(now());
            present++;
        }else{
            ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
            errorList.add(token);
            ErrorLineNumber.add(beforeLineNum());
        }
//        stmt.setSEMICN(now());
//        System.out.println(nowType());
//        present++;
        return stmt;

    }

    public Boolean isLVal(){
        for(int j=present;j<TokenList.size()&&TokenList.get(j).getType()!=TrueType.SEMICN;j++){
            if(TokenList.get(j).getType()==TrueType.ASSIGN){
                return true;
            }
        }
        return false;
    }

    public Cond createCond(){
        Cond cond=new Cond();
        cond.setlOrExp(createLOrExp());
        return cond;
    }

    public ForStmt createForStmt(){
        ForStmt forStmt=new ForStmt();
        forStmt.setlVal(createLVal());
        forStmt.setASSIGN(now());
        present++;
        forStmt.setExp(createExp());
        return forStmt;
    }

    public LOrExp createLOrExp(){
        //LOrExp: LAndExp { '||' LAndExp }
        LOrExp lOrExp=new LOrExp();
        lOrExp.insertLAndExpList(createLAndExp());
        while(nowType()==TrueType.OR){
            lOrExp.insertOrList(now());
            present++;
            lOrExp.insertLAndExpList(createLAndExp());
        }
        return lOrExp;
    }

    public LAndExp createLAndExp(){
        //LAndExp:EqExp { '&&' EqExp}
        LAndExp lAndExp=new LAndExp();
        lAndExp.insertEqExpList(createEqExp());
        while(nowType()==TrueType.AND){
            lAndExp.insertAndList(now());
            present++;
            lAndExp.insertEqExpList(createEqExp());
        }
        return lAndExp;
    }

    public EqExp createEqExp(){
        //EqExp:RelExp { ('==' | '!=') RelExp}
        EqExp eqExp=new EqExp();
        eqExp.insertRelExpList(createRelExp());
        while(nowType()==TrueType.EQL||nowType()==TrueType.NEQ){
            eqExp.insertTokenList(now());
            present++;
            eqExp.insertRelExpList(createRelExp());
        }
        return eqExp;
    }

    public RelExp createRelExp(){
        //RelExp:AddExp { ('<' | '>' | '<=' | '>=')  RelExp}
        RelExp relExp=new RelExp();
        relExp.insertLAddExpList(createAddExp());
        while(nowType()==TrueType.LSS||nowType()==TrueType.GRE||nowType()==TrueType.LEQ||nowType()==TrueType.GEQ){
            relExp.insertOrList(now());
            present++;
            relExp.insertLAddExpList(createAddExp());
        }
        return relExp;
    }

    public MainFuncDef createMainFuncDef(){
        MainFuncDef mainFuncDef=new MainFuncDef(now(),preRead(),prePreRead());
        present+=3;
        if(nowType()==TrueType.RPARENT){
            mainFuncDef.setRPARENT(now());
            present++;
        }else{
            ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.j);
            errorList.add(token);
            ErrorLineNumber.add(beforeLineNum());
        }
        mainFuncDef.setBlock(createBlock());
        return mainFuncDef;
    }

    public List<ErrorToken> getParserErrorList() {
        return errorList;
    }

    public String outFalseParser() {
        errorList.sort(new Comparator<ErrorToken>() {
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

    public String outTrueParser() {
        return AST.outputASTNode();
    }

    public Set<Integer> getErrorLineNumber() {
        return ErrorLineNumber;
    }
}
