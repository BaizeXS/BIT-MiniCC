package bit.minisys.minicc.scanner;

import bit.minisys.minicc.antlrgen.CLexer;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CScanner implements IMiniCCScanner {
    public void saveLexerTemps(String oFile, CommonTokenStream tokenStream) {
        tokenStream.fill();
        FileWriter fWriter = null;
        try {
            File destFile = new File(oFile);
            fWriter = new FileWriter(destFile);
            // 写入目标文件
            for (Token token : tokenStream.getTokens()) {
                fWriter.write(token.toString()+"\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fWriter != null) {
                    fWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String run(String iFile) throws Exception {
        System.out.println("Scanning...");
        // 0. 设置输出文件
        String oFile = MiniCCUtil.removeAllExt(iFile) + ".tokens";
        // 1. 设置字符输入流
        CharStream input = CharStreams.fromFileName(iFile);
        // 2. 设置 C 语言词法分析器
        CLexer lexer = new CLexer(input);
        // 3. 遍历令牌
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 4. 写入文件
        saveLexerTemps(oFile, tokens);
        // 5. 输出完成词法分析
        System.out.println("已完成词法分析！");
        return oFile;
    }
}
