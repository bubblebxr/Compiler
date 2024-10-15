package frontend.AST;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    protected List<Decl> declList;
    protected List<FuncDef> funcDefList=new ArrayList<>();
    protected  MainFuncDef mainFuncDef;

    public ASTNode(){
        this.declList=new ArrayList<>();
        this.funcDefList=new ArrayList<>();
    }

    public void insertDeclList(Decl decl){
        this.declList.add(decl);
    }

    public void insertFuncDefList(FuncDef funcDef){this.funcDefList.add(funcDef);}

    public void setMainFuncDef(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }

    public String outputASTNode(){
        StringBuilder a= new StringBuilder();
        for(Decl decl:declList){
            a.append(decl.outputDecl());
        }
        for(FuncDef funcDef:funcDefList){
            a.append(funcDef.outputFuncDef());
        }
        a.append(mainFuncDef.outputMainFuncDef());
        a.append("<CompUnit>\n");
        return a.toString();
    }
}
