package bit.minisys.minicc.icgen;

import bit.minisys.minicc.semantic.FuncTable;
import bit.minisys.minicc.semantic.VarRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BzSymbolPrinter {
    private final List<VarRecord> scope;
    private List<VarRecord> globalVarTable;
    private List<FuncTable> procTable;


    public BzSymbolPrinter() {
        scope = new LinkedList<>();
        globalVarTable = new LinkedList<>();
        procTable = new LinkedList<>();
    }

    public void setProcTable(List<FuncTable> procTable) {
        this.procTable = procTable;
    }

    public void setGlobalVarTable(List<VarRecord> globalVarRecord) {
        this.globalVarTable = globalVarRecord;
    }

    public void setScope(List<VarRecord> scope) {
        this.globalVarTable = scope;
    }

    public void printProcTable(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("ProcTable\n");
        // 输出函数表
        for (FuncTable funcTable : this.procTable) {
            sb.append("FuncTable\n");
            sb.append(funcTable.specifiers).append(" | ").append(funcTable.funcName).append(" | ").append(funcTable.type).append("\n");
            if (funcTable.varTable.size() != 0) {
                sb.append(funcTable.funcName).append("'s").append(" VariableTable\n");
                for (VarRecord varRecord : funcTable.varTable) {
                    sb.append(varRecord.specifiers).append(" | ").append(varRecord.name).append(" | ").append(varRecord.type).append("\n");
                }
            }
        }
        // 输出全局变量表
        if (globalVarTable.size() != 0) {
            sb.append("globalVarRecord\n");
            for (VarRecord varRecord : globalVarTable) {
                sb.append(varRecord.specifiers).append(" | ").append(varRecord.name).append(" | ").append(varRecord.type).append("\n");
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(new File(fileName));
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}