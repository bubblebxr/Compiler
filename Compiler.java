import midend.LLVMManager;
import backend.MipsGenerator;
import frontend.Parser;
import symbol.SymbolManager;

import java.io.*;

import static frontend.Lexer.*;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String filePath = "testfile.txt";
        String llvmPath="llvm_ir_优化前.txt";
        String llvmAfterOptimizePath="llvm_ir_优化后.txt";
        String mipsPath="mips.txt";
        String ErrorResultPath="error.txt";
        String TrueAnswer="ans.txt";
        int lineCount=1; //记录行数
        boolean isError=false; //是否存在错误
        Boolean isInAnnotation=false;
        //词法分析
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                isInAnnotation=analyze(line,isInAnnotation,lineCount);
                lineCount++;
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        //语法分析
        Parser parser=new Parser(getErrorList(),getTokenList(),getErrorLineNumber());
        parser.CompUnit();


//        if(!parser.getParserErrorList().isEmpty())isError=true;
//
//        if(!isError){
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(llvmPath))) {
//                writer.write(parser.outTrueParser());
//            } catch (IOException e) {
//                System.out.println(e);
//            }
//        }else{
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ErrorResultPath))) {
//                writer.write(parser.outFalseParser());
//            } catch (IOException e) {
//                System.out.println(e);
//            }
//        }

        //语义分析：符号表
        SymbolManager symbolManager=new SymbolManager(parser.getASTNode(),parser.getParserErrorList(),parser.getErrorLineNumber());
        symbolManager.CompUnitSymbol();

        if(!symbolManager.getSymbolErrorList().isEmpty())isError=true;

        if(!isError){
            //生成llvm
            LLVMManager llvmManager=new LLVMManager(parser.getASTNode());
            llvmManager.CompUnitToLLVM();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(llvmPath))) {
                writer.write(llvmManager.outputLLVM());
            } catch (IOException e) {
                System.out.println(e);
            }

            // 优化llvm
            llvmManager.optimize();
            BufferedWriter writerOP = new BufferedWriter(new FileWriter(llvmAfterOptimizePath));
            writerOP.write(llvmManager.outputLLVM());

            //生成mips
            MipsGenerator mipsGenerator=new MipsGenerator(llvmManager.getModule());
            mipsGenerator.generateMipsModule();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(mipsPath))) {
                writer.write(mipsGenerator.outputMips());
            } catch (IOException e) {
                System.out.println(e);
            }
        }else{
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ErrorResultPath))) {
                writer.write(symbolManager.outFalseVisitor());
            } catch (IOException e) {
                System.out.println(e);
            }
        }



//        compareFiles(isError?ErrorResultPath:llvmPath, TrueAnswer);

    }

    private static void compareFiles(String filePath1, String filePath2) {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
             BufferedReader reader2 = new BufferedReader(new FileReader(filePath2))) {

            String line1, line2;
            int lineNumber = 0;
            boolean filesAreEqual = true;

            while (true) {
                line1 = reader1.readLine();
                line2 = reader2.readLine();
                lineNumber++;

                if (line1 == null && line2 == null) {
                    break;
                }

                if (line1 == null || line2 == null) {
                    filesAreEqual = false;
                    System.out.println("Files differ at line " + lineNumber);
                    break;
                }

                if (!line1.equals(line2)) {
                    filesAreEqual = false;
                    System.out.println("Files differ at line " + lineNumber + ":");
                    System.out.println("File1: " + line1);
                    System.out.println("File2: " + line2);
                    break;
                }
            }

            if (filesAreEqual) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}