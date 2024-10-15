package frontend.AST;

public class Decl {
    protected ConstDecl constDecl;
    protected VarDecl varDecl;

    public void setConstDecl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }

    public void setVarDecl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }

    public String outputDecl() {
        StringBuilder a= new StringBuilder();
        if(constDecl!=null){
            a.append(constDecl.outputConstDecl());
        }else{
            a.append(varDecl.outputVarDecl());
        }
        return a.toString();
    }
}
