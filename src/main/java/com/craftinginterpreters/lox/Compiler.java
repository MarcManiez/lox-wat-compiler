package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Stmt.Expression;
import com.craftinginterpreters.lox.Stmt.Print;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class Compiler implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private int indentationLevel = 0;
  private int memoryOffset = 0;
  private final List<Stmt> statements;
  private List<String> lines;
  private List<String> memoryLines;
  // Wasm is destined to export a function, kind of like C with its "main".
  // The compiler should have a boiler plate "main" function declaration.
  // The instructions from the lox code would fit inside that function.
  // The function should be run in node (java can run it in a subprocess)
  // after it's been transformed from wat to wasm using the wabbit toolchain.

  Compiler(List<Stmt> statements) {
    this.statements = statements;
    this.lines = new ArrayList<String>();
    this.memoryLines = new ArrayList<String>();
  }

  void compile() throws IOException {
    writeLine("(module");
    indent();
    // TODO: use codegen to make JS file to coordinate function signatures and names
    writeLine("(import \"console\" \"logFloat\" (func $logFloat (param f32)))");
    writeLine("(import \"console\" \"logString\" (func $logString (param i32)))");
    writeLine("(import \"js\" \"mem\" (memory 1))");
    writeLine("(func (export \"main\")");
    indent();
    for (Stmt statement : this.statements) {
      compile(statement);
    }
    outdent();
    writeLine(")");
    for (String line : memoryLines) {
      writeLine(line);
    }
    outdent();
    writeLine(")");
    createFile();
  }

  private Void createFile() throws IOException {
    String path = "." + File.separator + "output.wat";
    PrintWriter writer = new PrintWriter(path, "UTF-8");
    for (String line : lines) {
      writer.println(line);
    }
    writer.close();
    return null;
  }

  private Object compile(Stmt statement) {
    return statement.accept(this);
  }

  private Object compile(Expr expression) {
    return expression.accept(this);
  }

  private String indentation() {
    String spaces = "";
    for (int i = 0; i < indentationLevel; i += 1) {
      spaces += "  ";
    }
    return spaces;
  }

  private void indent() {
    indentationLevel++;
  }

  private void outdent() {
    indentationLevel--;
  }

  private void writeLine(String line) {
    lines.add(indentation() + line);
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    compile(stmt.expression);
    return null;
  }

  @Override
  public Void visitPrintStmt(Print stmt) {
    compile(stmt.expression);
    TokenType expressionType = stmt.expression.accept(new ReturnType());
    if (expressionType.equals(TokenType.STRING)) {
      writeLine("call $logString");
    } else if (expressionType.equals(TokenType.NUMBER)) {
      writeLine("call $logFloat");
    }
    return null;
  }

  @Override
  public Object visitBinaryExpr(Binary expr) {
    // TODO: implement other binary operations
    Interpreter interpreter = new Interpreter();
    Object left = interpreter.evaluate(expr.left);
    Object right = interpreter.evaluate(expr.right);
    switch (expr.operator.type) {
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          writeLine("(f32.add");
          indent();
          compile(expr.left);
          compile(expr.right);
          outdent();
          writeLine(")");
        } else {
          String string = Interpreter.stringify(left) + Interpreter.stringify(right);
          writeStringLine(string);
        }
        break;
    }
    return null;
  }

  @Override
  public Object visitLiteralExpr(Literal expr) {
    if (expr.value instanceof String) {
      writeStringLine(expr.value.toString());
    } else {
      writeLine("(f32.const " + expr.value.toString() + ")");
    }
    return null;
  }

  private Void writeStringLine(String string) {
    writeLine("(i32.const " + memoryOffset + ")");
    String nullTerminatedString = string + "\\00";
    String memoryInstruction =
      "(data (i32.const " + memoryOffset + ") \"" + nullTerminatedString +"\")";
    memoryLines.add(memoryInstruction);
    // TODO: handle byte length instead of symbol length
    memoryOffset += string.length();
    return null;
  }
}
