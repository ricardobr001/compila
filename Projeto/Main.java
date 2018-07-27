import java.io.*;
import Lexer.*;
import AST.*;

public class Main {
    public static void main( String []args ) {

        File file;
        FileReader stream;
        int numChRead;

        // Flag
        // true imprime erros do java
        // false não imprime erros do java, somente erros da little
        boolean DEBUG = true;

        if (args.length != 1){
            System.out.println("Usage:\njava Main <input> [output]");
            System.out.println("<input> is the file to be compiled.");
            System.out.println("[output] is the file where the generated code will be stored.");
            System.out.println("\n<input> is required.\n[output] is optional.");
        }
        else {
            file = new File(args[0]);
            if ( ! file.exists() || ! file.canRead() ) {
                System.out.println("Either the file " + args[0] + " does not exist or it cannot be read.");
                System.out.println("Compilation terminated...");
                return;
            }
            try {
                stream = new FileReader(file);
            } catch ( FileNotFoundException e ) {
                System.out.println("Something wrong: file '" + args[0] + "' does not exist anymore.");
                System.out.println("Compilation terminated...");
                return;
            }

            // one more character for '\0' at the end that will be added by the
            // compiler
            char []input = new char[ (int ) file.length() + 1 ];

            try {
                numChRead = stream.read( input, 0, (int ) file.length() );
            } catch ( IOException e ) {
                System.out.println("Error reading file " + args[0] + ".");
                System.out.println("Compilation terminated...");
                return;
            }

            if ( numChRead != file.length() ) {
                System.out.println("Read error.");
                System.out.println("Compilation terminated...");
                return;
            }
            try {
                stream.close();
            } catch ( IOException e ) {
                System.out.println("Error in handling the file " + args[0] + ".");
                System.out.println("Compilation terminated...");
                return;
            }

            String outputFileName;

            if ( args.length == 2 ){
                outputFileName = args[1];
            }
            else {
                outputFileName = args[0];
                int lastIndex;

                if ( (lastIndex = outputFileName.lastIndexOf('.')) == -1 ){
                    lastIndex = outputFileName.length();
                }

                StringBuffer sb = new StringBuffer(outputFileName.substring(0, lastIndex));
                sb.append(".c");
                outputFileName = sb.toString();
            }

            // Cria o arquivo .c para imprimir o código compilado nele
            FileOutputStream  outputStream;
            try { 
               outputStream = new FileOutputStream(outputFileName);
            } catch ( IOException e ) {
                System.out.println("File " + args[1] + " was not found or can't be created.");
                return;
            }

            // Print no arquivo
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            PW pw = new PW(printWriter);
            Compiler compiler = new Compiler();

            // Print no terminal
            // PW pw = new PW(new PrintWriter(System.out, true));
            // Compiler compiler = new Compiler();

            if (!DEBUG){
                try {
                    PgmBody p = compiler.compile(input);
                    p.genC(pw);
                } catch (RuntimeException e){
                    return;
                }
            }
            else {
                PgmBody p = compiler.compile(input);
                p.genC(pw);
            }
        }
    }
}
