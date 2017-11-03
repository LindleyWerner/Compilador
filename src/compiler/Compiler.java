package compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexical_analyzer.Lexer;
import syntactic_parser.Semantic;
import lexical_analyzer.Token;

/**
 *
 * @author Lindley and Nícolas
 */
public class Compiler {
    
    private final static boolean DEBUG = false;
    
    public static void main(String[] args) {
        // TODO chamar tudo por aqui, ou não...
        while(true){
            System.out.println("Digite o nome do arquivo a ser compilado: ");
            Scanner input = new Scanner (System.in);
            String fileName = input.nextLine();
            fileName = "./codigosParaTeste/" + fileName;  
            Semantic semantic;
            try {
                semantic = new Semantic(fileName, DEBUG);
                semantic.start();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, "Arquivo não encontrado.", ex);
            }
            
        }
    }    
}
