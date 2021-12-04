package cbm.compiler;

import java.io.FileNotFoundException;

public class Compiler {
    public static void main(String args[]) throws FileNotFoundException {
        if(args.length != 1){
            System.out.println("Informe ao menos um arquivo para ser compilado.");
            return;
        }

        System.out.println("Um arquivo foi informado");
    }
}
