package compiler;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexical_analyzer.Lexer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Compiler {    
    public static void main(String[] args) {
        // TODO chamar tudo por aqui, ou não...
        while(true){
            System.out.println("Digite o nome do arquivo a ser compilado: ");
            Scanner input = new Scanner (System.in);
            String fileName = input.nextLine();
            fileName = "./codigosParaTeste/" + fileName;  
            
            try {
                Lexer lexer = new Lexer(fileName);
                break;
            } catch (FileNotFoundException ex) {
                // TODO Qual o melhor jeito de tratar este erro?               
            }
        }
        
    }
    
}
