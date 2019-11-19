package com.craftinginterpreters.lox;

import com.craftinginterpreters.lox.Expr.Binary;
import com.craftinginterpreters.lox.Expr.Literal;
import com.craftinginterpreters.lox.Stmt.Expression;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class Compiler implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private int indentationLevel = 0;
  private PrintWriter writer;
  private final List<Stmt> statements;
  private final List<String> instructions = new ArrayList<>();
  // Wasm is destined to export a function, kind of like C with its "main".
  // The compiler should have a boiler plate "main" function declaration.
  // The instructions from the lox code would fit inside that function.
  // The function should be run in node (java can run it in a subprocess)
  // after it's been transformed from wat to wasm using the wabbit toolchain.

  Compiler(List<Stmt> statements) {
    this.statements = statements;
  }

  void compile() throws IOException {
    for (Stmt statement : this.statements) {
      compile(statement);
    }

    String path = "." + File.separator + "output.wat";
    writer = new PrintWriter(path, "UTF-8");
    writeLine("(module");
    indent();
    writeLine("(func $main");
    indent();
    for (String instruction : instructions) {
      writeLine(instruction);
    }
    outdent();
    writeLine(")");
    outdent();
    writeLine(")");
    writer.close();
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
    writer.println(indentation() + line);
  }

  @Override
  public Void visitExpressionStmt(Expression stmt) {
    compile(stmt.expression);
    return null;
  }

  @Override
  public Object visitBinaryExpr(Binary expr) {
    compile(expr.left);
    compile(expr.right);

    switch (expr.operator.type) {
      case PLUS:
      instructions.add("Add two things on the stck pls :)");
      break;
    }
    return null;
  }

  @Override
  public Object visitLiteralExpr(Literal expr) {
    instructions.add("Put this on the stack " + expr.value.toString()); // instead of producing an instruction
    return null;
  }
}
