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
  private PrintWriter writer;
  private final List<Stmt> statements;
  // Wasm is destined to export a function, kind of like C with its "main".
  // The compiler should have a boiler plate "main" function declaration.
  // The instructions from the lox code would fit inside that function.
  // The function should be run in node (java can run it in a subprocess)
  // after it's been transformed from wat to wasm using the wabbit toolchain.

  Compiler(List<Stmt> statements) throws IOException {
    this.statements = statements;
    String path = "." + File.separator + "output.wat";
    this.writer = new PrintWriter(path, "UTF-8");
  }

  void compile() {
    writeLine("(module");
    indent();
    // TODO: use codegen to make JS file to coordinate function signatures and names
    writeLine("(import \"console\" \"logFloat\" (func $logFloat (param f32)))");
    writeLine("(import \"console\" \"logString\" (func $logString (param i32 i32)))");
    writeLine("(import \"js\" \"mem\" (memory 1))");
    writeLine("(func (export \"main\")");
    indent();
    for (Stmt statement : this.statements) {
      compile(statement);
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
  public Void visitPrintStmt(Print stmt) {
    if (stmt.expression instanceof Expr.Binary) {
    } else if (stmt.expression instanceof Expr.Literal) {
      compile(stmt.expression);
      Object value = ((Literal) stmt.expression).value;
      if (value instanceof String) {
        writeLine("call $logString");
      } else { // assuming integer
        writeLine("call $logFloat");
      }
    }

    return null;
  }

  @Override
  public Object visitLiteralExpr(Literal expr) {
    if (expr.value instanceof String) {
    } else { // assuming integer
      writeLine("f32.const " + expr.value.toString());
    }
    return null;
  }
}
