package JORG.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import JORG.lexical.LexicalScanner;
import JORG.lexical.Token;
import JORG.lexical.TokenCategory;

public class Compiler {
    public static void main(String args[]) throws FileNotFoundException {
        if(args.length != 1){
            System.out.println("Informe apenas, ou ao menos, um arquivo para ser compilado.");
            return;
        }

        try{
            String lineRow, line, filePath = args[0];
            LexicalScanner lexicalScanner = new LexicalScanner();
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()){
                line = scanner.nextLine();
                lineRow = "%4d  ";
                System.out.println(String.format(lineRow, lexicalScanner.getRow()) + line);

                line += '\n';

                lexicalScanner.restartPos();

                while(true){
                    lexicalScanner.nextColumn();
                    Token current = lexicalScanner.nextToken(line);

                    if(current == null) break;
                    else System.out.println(current.toString());
                }

                lexicalScanner.nextRow();
            }

            lineRow = "%4d  ";
            System.out.println(String.format(lineRow, lexicalScanner.getRow()));
            Token EOFToken = new Token("EOF", TokenCategory.EOF_TOKEN, lexicalScanner.getRow(), lexicalScanner.getColumn());
            System.out.println(EOFToken.toString());
            
            scanner.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
