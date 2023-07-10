package bit.minisys.minicc.ncgen;

import bit.minisys.minicc.icgen.BzICBuilder;
import bit.minisys.minicc.icgen.BzLabelGenerator;
import bit.minisys.minicc.icgen.Quat;
import bit.minisys.minicc.icgen.TemporaryValue;
import bit.minisys.minicc.parser.ast.ASTIdentifier;
import bit.minisys.minicc.parser.ast.ASTIntegerConstant;
import bit.minisys.minicc.parser.ast.ASTNode;
import bit.minisys.minicc.parser.ast.ASTStringConstant;
import bit.minisys.minicc.semantic.FuncTable;
import bit.minisys.minicc.semantic.VarRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BzCodeGen {
    public String codeType;                                 // 生成代码类型 x86/risc-v/arm
    public BzICBuilder icBuilder;                           // 四元式生成器
    public String asmOutputFile;                            // 输出文件
    public StringBuilder strBuilder;                        // 生成代码构造器
    public int tab;
    public boolean array;
    public boolean printFlag;
    public boolean printStr;
    public boolean printInt;
    public boolean getInt;
    public boolean getIntFunc;
    public String arrayName;                                // 数组名
    public String funcName;                                 // 函数名
    public Stack<String> paramStack;                        // 参数列表
    public Map<ASTNode, String> tempValue;                   // 临时变量
    public List<String> printScanf;
    private String jumpOP;                                  // 跳转指令

    public BzCodeGen(String type, BzICBuilder icBuilder, String asmOutputFile) {
        // 设置文件输出路径
        this.asmOutputFile = asmOutputFile;
        this.icBuilder = icBuilder;
        this.codeType = type;
        this.strBuilder = new StringBuilder();
        this.tab = 0;
        this.array = false;
        this.printFlag = false;
        this.printStr = false;
        this.printInt = false;
        this.getInt = false;
        this.getIntFunc = false;
        this.funcName = "null";
        this.paramStack = new Stack<>();
        this.tempValue = new HashMap<>();
        this.printScanf = new LinkedList<>();
    }

    // AST 节点访问
    public static String astStr(ASTNode node) {
        if (node == null) {
            return "";
        } else if (node instanceof ASTIdentifier) {
            return ((ASTIdentifier) node).value;
        } else if (node instanceof ASTIntegerConstant) {
            return String.valueOf(((ASTIntegerConstant) node).value);
        } else if (node instanceof TemporaryValue) {
            return ((TemporaryValue) node).name();
        } else if (node instanceof ASTStringConstant) {
            return ((ASTStringConstant) node).value;
        } else if (node instanceof BzLabelGenerator) {
            return ((BzLabelGenerator) node).name();
        } else {
            return "";
        }
    }

    // 生成 Tab
    public void genTab() {
        strBuilder.append("    ".repeat(Math.max(0, tab)));
    }
    // 文件头及参数处理
    public void genX86Include() {
        // 1. 添加文件头
        strBuilder.append(".386\n");
        strBuilder.append(".model flat, stdcall\n");
        strBuilder.append("option casemap : none\n");
        strBuilder.append("includelib msvcrt.lib\n");
        strBuilder.append("includelib ucrt.lib\n");
        strBuilder.append("includelib legacy_stdio_definitions.lib\n");
        // 2. 根据四元式生成代码段处理参数处理
        for (Quat quat : BzICBuilder.quats) {
            // 读到参数时候将其存入参数栈
            if (quat.getOp().equals("param")) {
                ASTNode node = quat.getOpnd1();
                String param = astStr(node);
                paramStack.push(param);
            }
            // 函数调用时候将已经收到的参数弹出
            if (quat.getOp().equals("call")) {
                switch (((ASTIdentifier) (quat.getRes())).value) {
                    // Mars_PrintStr 函数处理
                    case "Mars_PrintStr" -> {
                        if (!printFlag) {
                            printFlag = true;
                            strBuilder.append("printf proto c:dword,:vararg\n");
                        }
                        printStr = true;
                        while (paramStack.size() != 0) {
                            printScanf.add(paramStack.pop());
                        }
                    }
                    // Mars_PrintInt 函数处理
                    case "Mars_PrintInt" -> {
                        if (!printFlag) {
                            printFlag = true;
                            strBuilder.append("printf proto c:dword,:vararg\n");
                        }
                        while (paramStack.size() != 0) {
                            paramStack.pop();
                        }
                        printInt = true;
                    }
                    // Mars_GetInt 函数处理
                    case "Mars_GetInt" -> {
                        if (!getInt) {
                            getInt = true;
                            strBuilder.append("scanf proto c:dword,:vararg\n");
                        }
                        while (paramStack.size() != 0) {
                            paramStack.pop();
                        }
                    }
                    // 一般函数处理
                    default -> {
                        while (paramStack.size() != 0) {
                            paramStack.pop();
                        }
                    }
                }
            }
        }
        // 3. 数据段
        strBuilder.append(".data\n");
        // 4. Mars 函数相关处理
        // 4.1 Mars_PrintInt
        if (printInt) {
            strBuilder.append("Mars_PrintInt byte \"%d\",0ah,0\n");
        }
        // 4.2 Mars_PrintStr
        if (printStr) {
            for (int i = 0; i < printScanf.size(); i++) {
                String str = printScanf.get(i);
                str = str.replace("\\n", "");
                strBuilder.append("Mars_PrintStr").append(i).append(" byte ").append(str).append(",0ah,0\n");
            }
        }
        // 4.3 Mars_GetInt
        if (getInt) {
            strBuilder.append("Mars_GetInt byte \"%d\",0\n");
        }
    }
    // 函数处理
    public void genX86Func() {
        strBuilder.append("\n");
        tab++;
        for (FuncTable funcTable : BzICBuilder.procTable) {
            // 遍历函数列表
            if (funcTable.funcName.equals(funcName)) {
                // 遍历函数中的变量表
                if (funcTable.varTable.size() != 0) {
                    for (VarRecord varTable : funcTable.varTable) {
                        if (varTable.type.equals("VariableDeclarator")) {
                            // 处理变量声明
                            genTab();
                            strBuilder.append("local ").append(varTable.name).append(":dword\n");
                        } else if (varTable.type.equals("ArrayDeclarator")) {
                            // 处理数组声明
                            genTab();
                            strBuilder.append("local ").append(varTable.name).append("[").append(varTable.dimension * varTable.length).append("]").append(":dword\n");
                        }
                    }
                }
            }
        }
    }
    // 代码生成
    public void genX86Code() {
        // 1. 生成文件头部
        genX86Include();
        // 2. 处理全局变量并添加至 .data 下方
        // 3. 生成代码主体
        strBuilder.append(".code\n");
        // 遍历代码四元式
        for (Quat quat : BzICBuilder.quats) {
            // 参数处理
            if (quat.getOp().equals("param")) {
                ASTNode node = quat.getOpnd1();
                String param = astStr(node);
                paramStack.push(param);
            }
            if (quat.getOp().equals("func")) {
                if (!funcName.equals("null")) {
                    genTab();
                    strBuilder.append("ret\n");
                    tab--;
                    genTab();
                    strBuilder.append(funcName).append(" endp\n");
                    if (funcName.equals("main")) {
                        genTab();
                        strBuilder.append("end main\n");
                    }
                }
                funcName = ((ASTIdentifier) (quat.getRes())).value;
                if (!funcName.equals("main")) {
                    strBuilder.append(funcName).append(" proc far C");
                    while (paramStack.size() != 0) {
                        strBuilder.append(" ").append(paramStack.pop()).append(":dword");
                    }
                    genX86Func();
                } else {
                    strBuilder.append(funcName).append(" proc");
                    genX86Func();
                }
            }
            if (quat.getOp().equals("Label")) {
                String type = ((BzLabelGenerator) (quat.getRes())).Type;
                switch (type) {
                    case "Endif", "loopEndLabel" -> {
                        tab--;
                        genTab();
                    }
                    case "If", "loopStartLabel" -> {
                        genTab();
                        tab++;
                    }
                    case "Else", "loopCheckLabel", "loopNextLabel" -> {
                        tab--;
                        genTab();
                        tab++;
                    }
                }
                strBuilder.append(astStr(quat.getRes())).append(":\n");
            }
            if (quat.getOp().equals("<")) {
                jumpOP = "<";
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                genTab();
                strBuilder.append("mov eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("cmp eax, ").append(opnd2).append("\n");
            }
            if (quat.getOp().equals("<=")) {
                jumpOP = "<=";
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                genTab();
                strBuilder.append("mov eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("cmp eax, ").append(opnd2).append("\n");
            }
            if (quat.getOp().equals("[]")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("imul edx, ").append(opnd2).append(", 4\n");
                array = true;
                arrayName = opnd1;
            }
            if (quat.getOp().equals("-=")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("mov eax, ").append(res).append("\n");
                genTab();
                strBuilder.append("sub eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("mov ").append(res).append(", eax\n");
            }
            if (quat.getOp().equals("==")) {
                jumpOP = "==";
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                genTab();
                strBuilder.append("mov eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("cmp eax, ").append(opnd2).append("\n");
            }
            if (quat.getOp().equals("JF")) {
                genTab();
                switch (jumpOP) {
                    case "<" -> strBuilder.append("jnb ").append(astStr(quat.getRes())).append("\n");
                    case "<=" -> strBuilder.append("jnbe ").append(astStr(quat.getRes())).append("\n");
                    case "==" -> strBuilder.append("jnz ").append(astStr(quat.getRes())).append("\n");
                }
            }
            if (quat.getOp().equals("*")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("mov eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("mov ebx, ").append(opnd2).append("\n");
                genTab();
                strBuilder.append("imul eax, ebx\n");
                genTab();
                strBuilder.append("mov ").append(res).append(", eax\n");
            }
            if (quat.getOp().equals("%")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("xor edx, edx\n");
                genTab();
                strBuilder.append("mov eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("mov ebx, ").append(opnd2).append("\n");
                genTab();
                strBuilder.append("div ebx\n");
                genTab();
                strBuilder.append("mov ").append(res).append(", edx\n");
            }
            if (quat.getOp().equals("/")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("xor edx, edx\n");
                genTab();
                strBuilder.append("mov eax, ").append(opnd1).append("\n");
                genTab();
                strBuilder.append("mov ebx, ").append(opnd2).append("\n");
                genTab();
                strBuilder.append("div ebx\n");
                genTab();
                strBuilder.append("mov ").append(res).append(", eax\n");
            }
            if (quat.getOp().equals("-")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                ASTNode res = quat.getRes();
                genTab();
                strBuilder.append("mov eax ,").append(opnd1).append("\n");
                genTab();
                strBuilder.append("mov ebx ,").append(opnd2).append("\n");
                genTab();
                strBuilder.append("sub eax, ebx" + "\n");
                genTab();
                strBuilder.append("mov ").append(((TemporaryValue) res).name()).append(", eax").append("\n");
            }
            if (quat.getOp().equals("=")) {
                if (array) {
                    array = false;
                    String opnd1 = astStr(quat.getOpnd1());
                    String res = astStr(quat.getRes());
                    genTab();
                    strBuilder.append("push ").append(opnd1).append("\n");
                    genTab();
                    strBuilder.append("pop ").append(arrayName).append("[edx]\n");
                } else {
                    String opnd1 = astStr(quat.getOpnd1());
                    String res = astStr(quat.getRes());
                    genTab();
                    strBuilder.append("push ").append(opnd1).append("\n");
                    genTab();
                    strBuilder.append("pop ").append(res).append("\n");
                }
            }
            if (quat.getOp().equals("call")) {
                String name = astStr(quat.getRes());
                switch (name) {
                    case "Mars_PrintStr" -> {
                        String str = paramStack.pop();
                        for (int i = 0; i < printScanf.size(); i++) {
                            String string = printScanf.get(i);
                            if (string.equals(str)) {
                                genTab();
                                strBuilder.append("invoke printf, addr Mars_PrintStr").append(i).append("\n");
                            }
                        }
                    }
                    case "Mars_GetInt" -> getIntFunc = true;
                    case "Mars_PrintInt" -> {
                        while (paramStack.size() != 0) {
                            genTab();
                            strBuilder.append("mov eax,").append(paramStack.pop()).append("\n");
                            genTab();
                            strBuilder.append("invoke printf, addr Mars_PrintInt, eax\n");
                        }
                    }
                    default -> {
                        genTab();
                        strBuilder.append("invoke ").append(name);
                        while (paramStack.size() != 0) {
                            strBuilder.append(", ").append(paramStack.pop()).append("\n");
                        }
                    }
                }
            }
            if (quat.getOp().equals("JMP")) {
                genTab();
                strBuilder.append("jmp ").append(astStr(quat.getRes())).append("\n");
            }
            if (quat.getOp().equals("return")) {
                if (!getIntFunc) {
                    genTab();
                    strBuilder.append("mov ").append(astStr(quat.getRes())).append(", eax\n");
                } else {
                    genTab();
                    strBuilder.append("lea eax, [").append(astStr(quat.getRes())).append("]\n");
                    genTab();
                    strBuilder.append("push eax\n");
                    genTab();
                    strBuilder.append("push offset Mars_GetInt\n");
                    genTab();
                    strBuilder.append("call scanf\n");
                    getIntFunc = false;
                }
            }
            if (quat.getOp().equals("+")) {
                String opnd1 = astStr(quat.getOpnd1());
                String opnd2 = astStr(quat.getOpnd2());
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("mov eax ,").append(opnd1).append("\n");
                genTab();
                strBuilder.append("mov ebx ,").append(opnd2).append("\n");
                genTab();
                strBuilder.append("add eax, ebx" + "\n");
                genTab();
                strBuilder.append("mov ").append(res).append(", eax").append("\n");
            }
            if (quat.getOp().equals("++")) {
                String res = astStr(quat.getRes());
                genTab();
                strBuilder.append("inc ").append(res).append("\n");
            }
            if (quat.getOp().equals("RET")) {
                if (quat.getRes() != null) {
                    genTab();
                    strBuilder.append("mov eax, ").append(astStr(quat.getRes())).append("\n");
                }
            }
        }
    }
    public void run() throws IOException {
        if (codeType.equals("x86")) {
            genX86Code();
            if (!funcName.equals("null")) {
                genTab();
                strBuilder.append("ret\n");
                tab--;
                genTab();
                strBuilder.append(funcName).append(" endp\n");
                if (funcName.equals("main")) {
                    genTab();
                    strBuilder.append("end main\n");
                }
            }
            try {
                FileWriter fileWriter = new FileWriter(new File(asmOutputFile));
                fileWriter.write(strBuilder.toString());
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}