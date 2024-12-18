package frontend.AST;

public class Exp {
    protected AddExp addExp;

    public void setAddExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public String outputExp() {
        StringBuilder a=new StringBuilder();
        a.append(addExp.outputAddExp());
        a.append("<Exp>\n");
        return a.toString();
    }

    public AddExp getAddExp() {
        return addExp;
    }

    public int getExpValue(){
        return addExp.getAddExpValue();
    }

    /**
     * @description: 优化代码，如果能计算出来的话就计算出来，不能就返回null
     * @date: 2024/12/18 18:03
     **/
    public Integer tryToGetValue(){
        return addExp.tryToGetValue();
    }
}
