package bit.minisys.minicc.parser;
import bit.minisys.minicc.antlrgen.CLexer;
import bit.minisys.minicc.antlrgen.CParser;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.scanner.CScanner;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


public class BzParser implements IMiniCCParser {
    @Override
    public String run(String iFile) throws Exception {
        System.out.println("Parsing...");
        // 1. 源文件
        String inputFile = MiniCCUtil.removeAllExt(iFile) + ".c";
        // 2. 输出文件
        String oFile = MiniCCUtil.removeAllExt(inputFile) + ".ast.json";
        // 3. 输入流
        CharStream inputStream = CharStreams.fromFileName(inputFile);
        // 4. 词法分析器
        CLexer lexer = new CLexer(inputStream);
        // 5. Token 流
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 6. 语法分析器
        CParser parser = new CParser(tokens);
        // 7. 语法树
        ParseTree tree = parser.compilationUnit();
        // 8. 语法树遍历器
        ParseTreeWalker walker = new ParseTreeWalker();
        // 9. 语法树监听者
        BzCListener listener = new BzCListener(oFile);
        // 10. 遍历语法树
        walker.walk(listener, tree);
        return oFile;
    }
}
