package bit.minisys.minicc;

import bit.minisys.minicc.antlrgen.CLexer;
import bit.minisys.minicc.antlrgen.CParser;
import bit.minisys.minicc.icgen.BzICBuilder;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.ncgen.BzCodeGen;
import bit.minisys.minicc.parser.BzCListener;
import bit.minisys.minicc.parser.ast.ASTNode;
import bit.minisys.minicc.scanner.CScanner;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class BzMiniCC {
    public static void main(String[] args) throws Exception {
        // 1. 设置变量
        String inputFile = "test/nc_tests/0_BubbleSort.c";
        System.out.println("Start to compile ...");
        // 1. 词法分析
        System.out.println("Scanning...");
        String tokenOutFile = MiniCCUtil.removeAllExt(inputFile) + ".tokens";       // 输出文件名
        CharStream inputStream = CharStreams.fromFileName(inputFile);               // 输入字符流
        CLexer lexer = new CLexer(inputStream);                                     // C 语言词法分析器
        CommonTokenStream tokens = new CommonTokenStream(lexer);                    // Token 流
        CScanner scanner = new CScanner();                                          // C 语言词法分析器
        scanner.saveLexerTemps(tokenOutFile, tokens);                               // 保存中间结果
        // 2. 语法分析
        System.out.println("Parsing...");
        String astOutFile = MiniCCUtil.removeAllExt(inputFile) + ".ast.json";
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        BzCListener listener = new BzCListener(astOutFile);
        walker.walk(listener, tree);
        // 3. 语义分析 && 中间代码生成
        String icOutputFile = MiniCCUtil.removeAllExt(inputFile) + ".ic";
        String errorOutputFile = MiniCCUtil.removeAllExt(inputFile) + ".error";
        String symbolOutputFile = MiniCCUtil.removeAllExt(inputFile) + ".symbol";
        BzICBuilder icBuilder = new BzICBuilder();
        icBuilder.setOutFiles(icOutputFile, errorOutputFile, symbolOutputFile);
        ASTNode node = listener.NodeStack.peek();
        icBuilder.run(node);
        // 4. 目标代码生成
        String asmOutputFile = MiniCCUtil.removeAllExt(inputFile) + ".asm";
        BzCodeGen codeGen = new BzCodeGen("x86", icBuilder, asmOutputFile);
        codeGen.asmOutputFile = asmOutputFile;
        codeGen.run();
        // 5. 编译完成
        System.out.println("Compiling completed!");
    }
}
