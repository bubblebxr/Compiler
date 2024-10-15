package frontend.AST;

public class BlockItem {
    protected Decl decl;
    protected Stmt stmt;

    public BlockItem(){
        this.decl=null;
        this.stmt=null;
    }

    public void setDecl(Decl decl) {
        this.decl = decl;
    }

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    public String outputBlockItem() {
        StringBuilder a=new StringBuilder();
        if(decl!=null){
            a.append(decl.outputDecl());
        }else{
            a.append(stmt.outputStmt());
        }
        return a.toString();
    }
}
