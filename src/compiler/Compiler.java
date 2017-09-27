package compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import lexical_analyzer.Lexer;
import lexical_analyzer.Token;

/**
 *
 * @author Lindley and Nícolas
 */
public class Compiler {    
    public static void main(String[] args) {
        // TODO chamar tudo por aqui, ou não...
        Lexer lexer;
        while(true){
            System.out.println("Digite o nome do arquivo a ser compilado: ");
            Scanner input = new Scanner (System.in);
            String fileName = input.nextLine();
            fileName = "./codigosParaTeste/" + fileName;  
            
            try {
                lexer = new Lexer(fileName);
                break;
            } catch (FileNotFoundException ex) {
                // TODO Qual o melhor jeito de tratar este erro?               
            }
        }
        System.out.println("\nFluxo de Tokens\n");
        while(true){
            try {
                // Get tokens
                Token tk = lexer.scan();
                // If it returns null is end of file
                if(tk == null) break;
                
                System.out.println(tk.toString());
            } catch (IOException ex) {
                // TODO Fazer o quê com este erro?
                System.out.println("Erro");
                break;
            }
        }
        lexer.howManyErrors();
        lexer.printTabelaSimbolos();
    }    
}
