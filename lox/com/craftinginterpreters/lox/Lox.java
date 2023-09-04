package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64); 
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  // start jlox with the file path
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    // Indicate an error 
    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
  }

  // start jlox without any arguments and prompt to enter the executable code line by line
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for(;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) break; //ctrl + D => EOF => readline() will return null
      run(line);
      hadError = false; //reset the flag to proceed with the next line
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // for now, just print the tokens
    System.out.println("Scanning - Tokens:");
    for (Token token : tokens) {
      System.out.println(token);
    }

    Parser parser = new Parser(tokens);
    //Expr expression = parser.parse();
    List<Stmt> statements = parser.parse();
    // Stop if there was a syntax error.
    if (hadError) return;

    // Resolver
    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(statements);
    // Stop if there was a resolution error.
    if (hadError) return;

    // System.out.println("----------");
    // System.out.println("Parsing - Ast printer output:");
    // System.out.println(new AstPrinter().print(expression));

    System.out.println("----------");
    System.out.println("Execution - Expression interpreter output:");
    interpreter.interpret(statements);
  }

  // ***Error handling***
  static void error(int line, String message) {
    report(line, "", message);
  }
  
  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }
}
