package compiler;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import syntactic_parser.Syntatic;

/**
 *
 * @author Lindley and Nícolas
 */
public class Compiler {

    private final static boolean DEBUG = false;

    public static void main(String[] args) {
        // TODO chamar tudo por aqui, ou não...
        while (true) {
            System.out.println("Digite o nome do arquivo a ser compilado: ");
            Scanner input = new Scanner(System.in);
            String fileName = input.nextLine();
            fileName = "./codigosParaTeste/" + fileName;
            Syntatic syntatic;
            try {
                syntatic = new Syntatic(fileName, DEBUG);
                syntatic.start();
            } catch (FileNotFoundException ex) {
                //Nada a fazer, mensagem de erro já foi escrita
                //Logger.getLogger(Compiler.class.getName()).log(Level.SEVERE, "Arquivo não encontrado.", ex);
            }

        }
    }
}
