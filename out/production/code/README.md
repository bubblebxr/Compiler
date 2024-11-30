# 编译器总体架构设计

## 参考编译器介绍



## 编译器总体设计



## 词法分析设计

```
.
├── Compiler.java
├── config.json
└── frontend
         └──Token
         	├──ErrorToken  #用于处理错误的类别
         	├──ErrorType   #enum错误类别
         	├──TrueToken   #用于处理正确的Token
         	└──TrueType    #enum 正确Token类别
         └── Lexer.java
```

### 编码前的设计

> 大致思路：如题可知，要求识别出源程序的所有单词，如果错误的源程序需要判断出错误类别和所在行号。所以初步判断可以进行“读两次”，即不判断正误，读完所有程序，记录下单词类别、名字和行号，再根据第一次保存下来的数据读第二次判断程序是否正误。

由此，首先处理读入，由于要记录行号，为了方便处理，首先通过`BufferedReader`读取字符输入流，然后通过`readline()`逐行读取，每次调用返回文件中的一行存入`line`，进而记录行号。

```java
try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
    String line;
    while ((line = reader.readLine()) != null) {
        isInAnnotation=analyze(line,isInAnnotation,lineCount);
        lineCount++;
    }
} catch (IOException e) {
    System.out.println(e);
}
```

然后开始进行“第一次”的逐行读，每次调用`analyze`函数传入每行的字符串，进入`Lexer.java`，首先定义两个静态列表，即`errorList`和`TokenList`，为之后的错误输出和正确输出做准备，两个列表的元素分别为`ErrorToken`和`TrueToken`，他们的定义根据之后将要输出的内容而定，具体如下：

```java
/*ErrorToken.java*/
public class ErrorToken {
    protected int lineNumber;
    protected ErrorType type;
}

/*TrueToken.java*/
public class TrueToken {
    protected String name;
    protected TrueType type;
    public static Map<String, TrueType> tokenMap=new HashMap<>();  //记录不变的字符串对应的类别
}
```

开始`analyze`函数，逐个分析单词的类别。

- 对于`+`，`-`，`*`，`/`，`%`类似的单字符单词，可以直接在逐个字符分析时直接分辨出来，加入的`TokenList`中。
- 对于`<=`，`!=`类似的双字符单词，可以通过多读一个字符判断，对于`!`和`!=`这样的终结符号串有相同的首符号集，需要进行特殊判断。
- 对于数值常量，可以通过判断当前字符是否是数字，如果是则逐一往后读直至不是数字，即可分别出`INTCON`。
- 对于字符串常量，一定是以""开始和结尾的，由于字符串常量中单引号、双引号不会作为转义字符出现，也不会直接出现；所以一旦读到“，便继续读到下一个”即可。
- 对于字符常量就麻烦了一些，单个字符既可以包括ASCII，也包括转义字符，需要特判是否以`\`为开头。
- 对于`Ident`和保留关键字很难分辨，所以需要先读入后特殊判别。

![image-20240925110416309](https://gitee.com/bxrbxr/images/raw/master/imgs/202409251104417.png)

对于注释，可以参照上面分别识别`//`和`/*`，对于多行注释，在`Compiler.java`入口函数特殊定义了`isInAnnotation`用于存储是否在多行注释中，并在`analyze`函数已进入特判是否在多行注释中，如果在就只判断一行中是否存在`*/`，如果不存在直接跳过，如果存在则修改`isInAnnotation`的值，继续处理注释后的程序。

```java
while ((line = reader.readLine()) != null) {
	isInAnnotation=analyze(line,isInAnnotation,lineCount);
	lineCount++;
}
```

### 编码后的设计

> 都写完了所谓的“第一遍”了才发现错误类型只需要识别这两种，恨不好好读题。那就直接读一遍在出现`&`和`|`时特判好了。。。
>
> ```java
> 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp // a
> 
> 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp // a
> ```

对于多行注释的处理需要每次更新`isInAnnotation`啊，并且跳出多行注释后后面的程序也要继续读！！！怎么就直接`break`了。

```java
if(isInAnnotation){
    if(line.charAt(i)=='*'&&i<lineLength-1&&line.charAt(i+1)=='/'){
        isInAnnotation=false;
        i++;
        continue;
    }else{
        continue;
    }
}
```

对于字符常量，当字符为`'`或转义字符时需要特殊处理，比如：

```c#
char str[10] = {'3', '\''};
```

相对于之前直接简单粗暴的判断下一个`'`，这样的样例就过不了了。

所以需要分开，首先判断是否是转义字符，如果是则继续读对应的下一个，如果不是只能再继续读一个字符。

```java
if(next=='\\'){
    if(i+2<lineLength){
        char d=line.charAt(i+2);
        if((d=='a'||d=='b'||d=='t'||d=='n'||d=='v'||d=='f'||d=='"'||d=='\''||d=='\\'||d=='0')&&i+3<lineLength&&line.charAt(i+3)=='\''){
            save.append(next);
            save.append(d);
            save.append('\'');
            i+=3;
            TrueToken token=new TrueToken(save.toString(), TrueType.CHRCON);
            TokenList.add(token);
        }
        }
    }else if(next>=32&&next<=126&&i+2<lineLength&&line.charAt(i+2)=='\''){
        save.append(next);
        save.append('\'');
        i+=2;
        TrueToken token=new TrueToken(save.toString(), TrueType.CHRCON);
        TokenList.add(token);
}
```

对于判断是关键字还是标识符，先通过下列方法提取出`name`：

```java
StringBuilder save= new StringBuilder("\"");
for(int j=i+1;j<lineLength;j++){
    if(line.charAt(j)=='\"'){
        save.append("\"");
        TrueToken token=new TrueToken(save.toString(), TrueType.STRCON);
        TokenList.add(token);
        i=j;
        break;
    }
    save.append(line.charAt(j));
}
```

然后根据预先存储好的`String`与`Type`的`Map`辨别是否属于关键字即可。

```java
public static TrueType checkToken(String s){
    if(tokenMap.containsKey(s)){
        return tokenMap.get(s);
    }
    return TrueType.IDENFR;
}
```

## 语法分析设计

```
.
├── Compiler.java
├── config.json
└── frontend
         ├──Token
         ├──AST
         ├──Lexer.java
         └── Parser.java
```

### 编码前的设计

> 大致思路：如题可知，要求根据识别出源程序的所有单词，构建AST抽象语法树并进行错误处理。所以，应当先从编译单元`CompUnit`入手，为每个语法成分创建一个类，然后根据语法逐个构建。

首先，我们先分析一下语法中可能出现的成分：

- 对于类似于`CompUnit → {Decl} {FuncDef} MainFuncDef `这样的具有`{}`代表可循环0~无数次，将采用循环，每次判断是否符合特定可选项的`FIRST`集合，如果不符合将进行至下一步骤，伪代码如下所示：

  ```java
  public void createCompUnit(){
      while(now() in FIRST(Decl)){
          createDecl();
      }
      while(now() in FIRST(FuncDef)){
          createFuncDef();
      }
      createMainFuncDef();
  }
  ```

- 对于类似`ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal`这样的具有`[]`表示可选项的，将采用判断，每次判断是否符合可选项的`FIRST`结合，如果不符合直接跳过可选项，伪代码如下所示：

  ```java
  public void createConstDef(){
      //前面省略
      if(now() in FIRST(ConstExp)){
          createConstExp();
      }
      //后面省略
  }
  ```

然后，为了便于我们获得错误处理时的行号，对于第一次作业的词法分析设计，每次存储正确的Token时，还需增加保存`lineNumber`便于语法分析时使用。

```java
public class TrueToken{
    protected String name;
    protected TrueType type;
    protected int lineNumber;
    public static Map<String, TrueType> tokenMap=new HashMap<>();
}
```

然后，在`Compiler.java`中开始语法分析和输出，其中`CompUnit()`函数为语法分析入口，`outTrueParser()`和`outFalseParser()`为正确输出和错误输出的入口。

```java
Parser parser=new Parser(getErrorList(),getTokenList());
parser.CompUnit();
if(!isError){
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(TrueResultPath))) {
        writer.write(parser.outTrueParser());
    } catch (IOException e) {
        System.out.println(e);
    }
}else{
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ErrorResultPath))) {
        writer.write(parser.outFalseParser());
    } catch (IOException e) {
        System.out.println(e);
    }
}
```

首先利用递归下降子程序法分析语法分析，用`present`预先记录当前读到哪个`token`，然后设计`now()`、`preRead()`、`prePreRead()`读取`Token`，设计`nowType()`、`preReadType()`、`prePreReadType()`读取`Token`的类别。

对于`CompUnit`，包含0\~多次的`Decl`、0\~多次的`FuncDef`和一个`MainFuncDef`。

则设计`CompUnit`类如下：

```java
public class ASTNode {
    protected List<Decl> declList;
    protected List<FuncDef> funcDefList=new ArrayList<>();
    protected  MainFuncDef mainFuncDef;
}
```

为处理`CompUnit`类，应得出`Decl`和`FuncDef`的`FIRST`集合，由于由于第一项的`FIRST`的集合交集不为空，所以采用预读的方式，`FuncDef`的第三项一定为左括号，`MainFuncDef`的第二项一定为关键字`main`，`Decl`一定包含`const`来区分这三个非终结符，如下所示：

```java
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
```

如果识别出了这三个非终结符，则继续调用`create+非终结符()`函数继续以类似的方式进行处理。

**特殊处理：**

1. ==递归下降子程序无法处理左递归==

   对于`AddExp → MulExp | AddExp ('+' | '−') MulExp `，根据PPT所给的提示，可以转化为

   ```java
   MulExp { ('+' | '-') MulExp }//循环
   MulExp| MulExp ('+' | '-') AddExp//右递归
   ```

   两种形式，这里我更偏向于第一种。则处理方式转化为了，先预先处理一个`MulExp`，然后根据当前`Token`是否是`('+' | '-')`来判断是否跳出循环，如果在循环内则读入符号，进行再处理一个`MulExp`。

   ```java
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
   ```

   对于`RelExp`、`EqExp`、`LAndExp`、`LOrExp`、`MulExp`应同理解决。

2. ==递归下降子程序无法处理回溯==

   对于`Stmt`这样的非终结符，

   ```java
   Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
   | [Exp] ';' //有无Exp两种情况
   | Block
   | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
   | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省，1种情况 2.
   ForStmt与Cond中缺省一个，3种情况 3. ForStmt与Cond中缺省两个，3种情况 4. ForStmt与Cond全部
   缺省，1种情况
   | 'break' ';' | 'continue' ';'
   | 'return' [Exp] ';' // 1.有Exp 2.无Exp
   | LVal '=' 'getint''('')'';'
   | LVal '=' 'getchar''('')'';'
   | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp
   ```

   对于第1,8,9种情况，均以`LVal`第一个非终结符，为了避免回溯，可以先统一处理`LVal`后，采用预读的方式，如果`preRead()`为`getint`或`getchar`，则按对应的方式处理；如果不是，则按第一种方式处理。

   ```java
   if(isLVal()){
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
           }
           if(nowType()==TrueType.SEMICN){
               stmt.setSEMICN(now());
               present++;
           }else{
               ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
               errorList.add(token);
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
           }
           if(nowType()==TrueType.SEMICN){
               stmt.setSEMICN(now());
               present++;
           }else{
               ErrorToken token=new ErrorToken(beforeLineNum(), ErrorType.i);
               errorList.add(token);
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
           }
       }
       return stmt;
   }
   ```

由此，AST抽象语法树构造完成，存储在`Parser`对象中。

**输出**

==输出正确的语法分析结果到`parser.txt`中：==

我们要对AST树进行树的后序遍历，由于树的节点都是不同的，不能直接粗暴的遍历，所以我们应该对于每个节点（即我所创造的对于每个终结符的类）写专门的output函数，定义在该类中，要输出的先后顺序（应当遵循后序遍历）。

比如，对于`CompUnit`，后序遍历的顺序是，先按顺序输出所有`Decl`，再按顺序输出所有`FuncDef`，再输出`MainFuncDef`。那么`outputCompUnit`函数应为：

```java
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
```

然后在依次调用`outputDecl`，`outputFuncdef`和`outputMainFuncDef`。

==输出错误处理到`error.txt`中：==

错误输出就比较简单了，在进行语法分析时顺便分析一下，分号、右中括号、右括号是不是齐的，不齐全就增加到`errorList`中，然后根据行号从小到大排序后输出。

### 编码后的设计

编码后发现将左递归修改为循环后实际上更改了未终结符的类型，如果参照之前的处理，那么应当在处理`addExp`中，所有的都是非终结符`MulExp`，实际上存储的`mulExpList`中，只有最后一个是`MulExp`，其余都是`addExp`。所以应当对将消除左递归的语法都进行如下修改：

```java
public String outputAddExp() {
        StringBuilder a=new StringBuilder();
//        a.append(mulExpList.get(0).outputMulExp());
//        if(mulExpList.size()!=1){
//            a.append("<AddExp>\n");
//        }
//        for(int i=1;i<mulExpList.size();i++){
//            a.append(tokenList.get(i-1).toString());
//            a.append(mulExpList.get(i).outputMulExp());
//        }
        for(int i=0;i<mulExpList.size();i++){
            a.append(mulExpList.get(i).outputMulExp());
            if(i<tokenList.size()){
                a.append("<AddExp>\n");
                a.append(tokenList.get(i).toString());
            }
        }
        a.append("<AddExp>\n");
        return a.toString();
    }
```

## 错误处理设计

```
.
├── Compiler.java
├── config.json
├── frontend
         ├──Token
         ├──AST
         ├──Lexer.java
         └── Parser.java
└──Vistor
	├──symbol
	├──SymbolManager
	└──SymbolTable
```

### 编码前的设计

#### 建立符号表

首先，符号应该如何储存。我的想法是建立一个Symbol类，其中存储Symbol的类型和名字。

```java
public symbol{
    protected SymbolType type;
    protected String Ident;
}
```

对于较为典型的Symbol类型，建立子类，比如`FuncParamSymbol`（函数参数符号），`FunctionSymbol`（函数符号），`VariableSymbol`（变量&常量）。

- 对于`VariableSymbol`，应该额外存储其是一维数组或变量。

- 对于`FunctionSymbol`，应该额外存储函数参数的变量的列表，以`FuncParamSymbol`为元素。

然后，建立符号表。应包含当前符号表的id，外层符号表id，存储所有的在本作用域中的符号`Map<String, symbol> directory`。这样，将所有符号表按id顺序存储到以`SymbolTable`为元素的列表中。

以上均为对于符号表的前置存储工作。现在开始描述如何维护符号表的列表。我们首先定义一个`presentId`作为指针指向当前作用域编号，然后对词法分析生成的AST进行前序遍历，如果进入到了一个新的作用域中，就将当前作用域id压入栈中，同时为这个新的作用域编号；当出某个作用域时，则将最后一个作用域id弹出栈作为当前作用域id。当遇见新的符号时，就将该符号加入到该作用域id对应的符号表中。

然后，还需要细化这一过程，首先是何时到了新的作用域，即出现`{}`的地方：

- 在建立符号表最开始，由于全局作用域也为一个作用域，应在进行任何处理前先将全局作用域id压入栈中
- Block

之后的工作就与词法分析类似，都是根据树的前序遍历调用非终结符对应的符号分析的函数，以`CompUnit`为例：

```java
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
```

#### 错误处理

##### b型错误

该错误为名字重定义。如果在同一作用域中出现了与当前名字相同的Ident即为错误。所以，只需遍历一遍当前作用域id所指的符号表的`directory`即可。

```java
public Boolean checkNameOverload(TrueToken Ident){
    if(SymbolTables.get(presentId-1).getNameOverLoad(Ident.getName())&&!ErrorLineNumber.contains(Ident.getLineNumber())){
        ErrorToken errorToken=new ErrorToken(Ident.getLineNumber(), ErrorType.b);
        errorList.add(errorToken);
        ErrorLineNumber.add(Ident.getLineNumber());
        return true;
    }
    return false;
}
```

##### c型错误

该错误为未定义的名字。由于可能会出现不同作用域同名覆盖问题，所以应从当前作用域开始寻找，如果没找到就继续从其作用域寻找，直到全局作用域为止。

```java
public boolean isNotDefine(String Ident, int presentId){
    for(symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
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
```

##### d型错误

该错误为函数参数个数不匹配。此时计算调用函数传入的变量数和事先存储在`FunctionSymbol`中的个数比对，如果不一致则错误。

```java
if(!(symbol.getFuncParams().size()==unaryExp.getFuncRParams().getExpList().size())){
    //ERROR:函数参数个数不匹配
    ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.d);
    errorList.add(errorToken);
    ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
    return;
}
```

##### e型错误

应该逐一比对事先存储的Symbol类型和传入的变量类型。

##### f型错误

对于`Void`类型的函数，应该事先设置字段`isVoid`，并伴随着维护符号表一直作为参数传递到对应函数中，如果解析到`return`则立即加入错误。

```java
if(isVoid&&!ErrorLineNumber.contains(stmt.getRETURN().getLineNumber())){
    ErrorToken errorToken=new ErrorToken(stmt.getRETURN().getLineNumber(),ErrorType.f);
    errorList.add(errorToken);
    ErrorLineNumber.add(stmt.getRETURN().getLineNumber());
}
```

##### g型错误

对于`Char`或`Int`类型的函数，在处理`Block`时特殊判断最后一个`BlockItem`，如果不是`Stmt`类型或不含有`return`字段，则立即报错。

```java
if(i==block.getBlockItemList().size()-1&&(type==SymbolType.CharFunc||type==SymbolType.IntFunc)){
    if(!(block.getBlockItemList().get(i).getStmt()!=null&&block.getBlockItemList().get(i).getStmt().getRETURN()!=null)){
        return true;
    }
}
```

##### h型错误

对于每一个引用变量的式子，都要判断其是否是常量。由于可能会出现不同作用域同名覆盖问题，所以应从当前作用域开始寻找，如果没找到就继续从其作用域寻找，直到全局作用域为止。如果是常量则报错。

```java
public Boolean isConst(String Ident,int presentId){
        //判断是否是常量，如果是返回True
    for(symbol symbol:SymbolTables.get(presentId-1).getDirectory().values()){
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
```

##### I型错误

只需当解析到`print`时解析`StringConst`中变量的个数与`exp`的个数是否一致。

##### m型错误

判断当前作用域是不是在for循环中。

### 编码后的设计

#### 多行错误处理问题

由于可能出现一个错误连锁反应多个错误的问题，应只报最先出现的错误。所以建立一个Set存储当前出现的错误行，如果已经发现是当前行是有错误的，就不在进行错误处理。

#### e型错误

由于传入参数是`exp`，判断exp并非易事，但是只有相同类型才能进行`exp`中计算，且`int`,`char`混用不会产生不匹配问题，所以这里只判断`exp`中第一个的类型与事先存入的symbol进行比较。

```java
for(int i=0;i<symbol.getFuncParams().size();i++){
    int dimension=symbol.getFuncParams().get(i).getDimension();
    SymbolType type=symbol.getFuncParams().get(i).getType();
    SymbolType ExpType= getExpType(unaryExp.getFuncRParams().getExpList().get(i));
    if(dimension==1){
        //传递数组
        if(type==SymbolType.CharArray){
            //char array
            if(ExpType!=SymbolType.CharArray){
                ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.e);
                errorList.add(errorToken);
                ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                return;
            }

        }else{
            //int array
            if(ExpType!=SymbolType.IntArray){
                ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.e);
                errorList.add(errorToken);
                ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
                return;
            }
        }
    }else{
        //传递变量
        if(ExpType==SymbolType.CharArray||ExpType==SymbolType.IntArray){
            ErrorToken errorToken=new ErrorToken(unaryExp.getIdent().getLineNumber(),ErrorType.e);
            errorList.add(errorToken);
            ErrorLineNumber.add(unaryExp.getIdent().getLineNumber());
            return;
        }
    }
}
```

> 小问题：
>
> - 其实应该在创建一个`symbol`的子类`ArraySymbol`，因为错误处理中出现了很多次需要比较是变量还是数组的问题。
> - 词法分析中一行中最后是`IntConst`就会导致指针没有改变返回处理数字，所以应该特判一下。
> - 语法分析中解析Stmt时，对于[exp];是以最后的分号作为应不应该解析exp的，这导致出现了没有;就解析失败的问题。

## 代码生成设计



## 代码优化设计

