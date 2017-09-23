package compiler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            String fileName = "teste1.txt";//input.nextLine();
            fileName = "./codigosParaTeste/" + fileName;  
            
            try {
                lexer = new Lexer(fileName);
                break;
            } catch (FileNotFoundException ex) {
                // TODO Qual o melhor jeito de tratar este erro?               
            }
        }
        while(true){
            try {
                // Get tokens
                Token tk = lexer.scan();
                // If it returns null is end of file
                if(tk == null) break;
                // TODO as vezes printa o nome e as vezes o número, está correto? 
                System.out.println(tk.toString());
            } catch (IOException ex) {
                // TODO Fazer o quê com este erro?
                System.out.println("Erro");
                break;
            }
        }
    }    
}
