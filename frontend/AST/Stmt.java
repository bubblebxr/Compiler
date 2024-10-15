package frontend.AST;

import frontend.Token.TrueToken;
import frontend.Token.TrueType;

import java.util.ArrayList;
import java.util.List;

public class Stmt {
    /*
    1:Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
    2:| [Exp] ';' //有无Exp两种情况
    3:| Block
    4:| 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
    5:| 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省，1种情况 2.
    ForStmt与Cond中缺省一个，3种情况 3. ForStmt与Cond中缺省两个，3种情况 4. ForStmt与Cond全部
    缺省，1种情况
    6:| 'break' ';' | 'continue' ';'
    7:| 'return' [Exp] ';' // 1.有Exp 2.无Exp
    8:| LVal '=' 'getint''('')'';'
    9:| LVal '=' 'getchar''('')'';'
    10:| 'printf''('StringConst {','Exp}')'';' /
     */
    protected int type;

    protected LVal lVal;
    protected TrueToken ASSIGN;

    //3
    protected Block block;

    //4
    protected TrueToken IF;
    protected TrueToken LPARENT;
    protected Cond cond;
    protected TrueToken RPARENT;
    protected Stmt stmt;
    protected TrueToken ELSE;
    protected Stmt elseStmt;

    //5
    protected TrueToken FOR;
    protected ForStmt forStmt1;
    protected TrueToken forSEMICN1;
    protected ForStmt forStmt2;
    protected  TrueToken forSEMICN2;

    //6
    protected TrueToken breakOrContinue;
    protected TrueToken SEMICN;

    //7
    protected TrueToken RETURN;
    protected Exp exp;

    //8
    protected TrueToken getInt;

    //9
    protected TrueToken getChar;

    //10
    protected TrueToken PRINTF;
    protected TrueToken StringConst;
    protected List<TrueToken> COMMAList;
    protected List<Exp> expList;

    public Stmt(int type,TrueToken t1,TrueToken t2,TrueToken t3){
        this.type=type;
        this.PRINTF=t1;
        this.LPARENT=t2;
        this.StringConst=t3;
        this.COMMAList=new ArrayList<>();
        this.expList=new ArrayList<>();
    }

    public Stmt(int type,Block block){
        this.type=type;
        this.block=block;
    }

    public Stmt(int type){
        this.type=type;
    }

    public Stmt(){

    }

    public void insertCOMMAList(TrueToken token){
        this.COMMAList.add(token);
    }

    public void insertExpList(Exp exp){
        this.expList.add(exp);
    }

    public void setIF(TrueToken IF) {
        this.IF = IF;
    }

    public void setLPARENT(TrueToken LPARENT) {
        this.LPARENT = LPARENT;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    public void setELSE(TrueToken ELSE) {
        this.ELSE = ELSE;
    }

    public void setElseStmt(Stmt elseStmt) {
        this.elseStmt = elseStmt;
    }

    public void setRPARENT(TrueToken RPARENT) {
        this.RPARENT = RPARENT;
    }

    public void setFOR(TrueToken FOR) {
        this.FOR = FOR;
    }

    public void setForStmt1(ForStmt forStmt1) {
        this.forStmt1 = forStmt1;
    }

    public void setForSEMICN1(TrueToken forSEMICN1) {
        this.forSEMICN1 = forSEMICN1;
    }

    public void setForStmt2(ForStmt forStmt2) {
        this.forStmt2 = forStmt2;
    }

    public void setForSEMICN2(TrueToken forSEMICN2) {
        this.forSEMICN2 = forSEMICN2;
    }

    public void setSEMICN(TrueToken SEMICN) {
        this.SEMICN = SEMICN;
    }

    public void setBreakOrContinue(TrueToken breakOrContinue) {
        this.breakOrContinue = breakOrContinue;
    }

    public void setRETURN(TrueToken RETURN) {
        this.RETURN = RETURN;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void setlVal(LVal lVal) {
        this.lVal = lVal;
    }

    public void setASSIGN(TrueToken ASSIGN) {
        this.ASSIGN = ASSIGN;
    }

    public void setGetInt(TrueToken getInt) {
        this.getInt = getInt;
    }

    public void setGetChar(TrueToken getChar) {
        this.getChar = getChar;
    }

    public void setType(int type){
        this.type=type;
    }

    public String outputStmt() {
        StringBuilder a=new StringBuilder();
        switch (type){
            case 1:
                a.append(lVal.outputLVal());
                a.append(ASSIGN.toString());
                a.append(exp.outputExp());
                a.append(SEMICN.toString());
                break;
            case 2:
                if(exp!=null){
                    a.append(exp.outputExp());
                }
                a.append(SEMICN);
                break;
            case 3:
                a.append(block.outputBlock());
                break;
            case 4:
                a.append(IF.toString());
                a.append(LPARENT.toString());
                a.append(cond.outputCond());
                a.append(RPARENT.toString());
                a.append(stmt.outputStmt());
                if(ELSE!=null){
                    a.append(ELSE.toString());
                    a.append(elseStmt.outputStmt());
                }
                break;
            case 5:
                a.append(FOR.toString());
                a.append(LPARENT.toString());
                if(forStmt1!=null){
                    a.append(forStmt1.outputForStmt());
                }
                a.append(forSEMICN1.toString());
                if(cond!=null){
                    a.append(cond.outputCond());
                }
                a.append(forSEMICN2.toString());
                if(forStmt2!=null){
                    a.append(forStmt2.outputForStmt());
                }
                a.append(RPARENT.toString());
                a.append(stmt.outputStmt());
                break;
            case 6:
                a.append(breakOrContinue.toString());
                a.append(SEMICN.toString());
                break;
            case 7:
                a.append(RETURN.toString());
                if(exp!=null){
                    a.append(exp.outputExp());
                }
                a.append(SEMICN.toString());
                break;
            case 8:
                a.append(lVal.outputLVal());
                a.append(ASSIGN.toString());
                a.append(getInt.toString());
                a.append(LPARENT.toString());
                a.append(RPARENT.toString());
                a.append(SEMICN.toString());
                break;
            case 9:
                a.append(lVal.outputLVal());
                a.append(ASSIGN.toString());
                a.append(getChar.toString());
                a.append(LPARENT.toString());
                a.append(RPARENT.toString());
                a.append(SEMICN.toString());
                break;
            case 10:
                a.append(PRINTF.toString());
                a.append(LPARENT.toString());
                a.append(StringConst.toString());
                for(int i=0;i<COMMAList.size();i++){
                    a.append(COMMAList.get(i).toString());
                    a.append(expList.get(i).outputExp());
                }
                a.append(RPARENT.toString());
                a.append(SEMICN.toString());
                break;
        }
        a.append("<Stmt>\n");
        return a.toString();
    }
}
