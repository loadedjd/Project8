import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Put your name here
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to statement string of body of
     *          instruction at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens,
            Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION") : ""
                + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";

        // TODO - fill in body
        tokens.dequeue(); // Instruction
        //save name for reference
        String name = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(name),
                "Error: IDENTIFIER expected, found: \"IS\"");

        Reporter.assertElseFatalError(tokens.front().equals("IS"),
                "Error: Keyword \"IS\" expected, found: \"" + tokens.front()
                        + "\"");
        tokens.dequeue(); // is

        body.parseBlock(tokens);
        Reporter.assertElseFatalError(tokens.front().equals("END"),
                "Error: Keyword \"END\" expected, found: \"" + tokens.front()
                        + "\"");
        tokens.dequeue(); // end
        Reporter.assertElseFatalError(tokens.front().equals(name),
                "Error: IDENTIFIER \"" + tokens.front()
                        + "\" at end of instruction \"" + name
                        + "\" must match instruction name");
        tokens.dequeue(); // Name

        return name;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0 : ""
                + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";

        // TODO - fill in body

        Reporter.assertElseFatalError(tokens.front().equals("PROGRAM"),
                "Keyword \"PROGRAM\" expected, found: \"" + tokens.front()
                        + "\"");
        tokens.dequeue(); // Program

        //save name for reference
        String name = tokens.dequeue(); // name

        Reporter.assertElseFatalError(Tokenizer.isIdentifier(name),
                "Keyword \"IDENTIFIER\" expected, found: \"" + name + "\"");
        this.replaceName(name);

        Reporter.assertElseFatalError(tokens.front().equals("IS"),
                "Keyword \"IS\" expected, found: \"" + tokens.front() + "\"");
        tokens.dequeue(); // is

        Map<String, Statement> context = this.newContext();
        
        while (tokens.front().equals("INSTRUCTION")) {
        	Statement s1 = this.newBody();

            String n = parseInstruction(tokens, s1); // returns name of Instruction
            Reporter.assertElseFatalError(!context.hasKey(n),
                    "\"IDENTIFIER\" expected, found: \"" + n + "\"");
            context.add(n, s1);
        }
        this.replaceContext(context);

        Reporter.assertElseFatalError(tokens.front().equals("BEGIN"),
                "Keyword \"BEGIN\" expected, found: \"" + tokens.front()
                        + "\"");
        tokens.dequeue(); // begin

        Statement s2 = this.newBody();
        s2.parseBlock(tokens);
        this.replaceBody(s2);
        Reporter.assertElseFatalError(tokens.front().equals("END"),
                "Keyword \"END\" expected, found: \"### END OF INPUT ###\"");
        tokens.dequeue(); // end
        Reporter.assertElseFatalError(tokens.front().equals(name),
                "IDENTIFIER \"" + tokens.front() + "\" at end of program \""
                        + name + "\" must match program name");
        tokens.dequeue(); // program name  
        
        Reporter.assertElseFatalError(tokens.length() == 1, "expected end of input");
        
        
    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
