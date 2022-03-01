package JORG.compiler;

import java.io.FileNotFoundException;

import JORG.lexical.LexicalScanner;

import JORG.parser.Parser;
public class Compiler {
    public static void main(String args[]) throws FileNotFoundException {
        if(args.length != 1){
            System.out.println("Informe apenas, ou ao menos, um arquivo para ser compilado.");
            return;
        }

        LexicalScanner lexicalScanner = new LexicalScanner();
        Parser parser = new Parser(lexicalScanner);
        parser.parser(args[0]);
    }
}