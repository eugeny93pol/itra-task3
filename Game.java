import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import java.security.SecureRandom;
import java.util.*;

public class Game {

    private static String[] moves;

    public static void main(String[] args) {
        if (!checkArgs(args)) {
            printHint();
            return;
        }

        moves = Arrays.copyOf(args, args.length);

        SecureRandom secureRandom = new SecureRandom();

        int cMove = secureRandom.nextInt(moves.length);

        byte[] key = new byte[16];
        secureRandom.nextBytes(key);

        HmacUtils hmac = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key);
        System.out.println("HMAC: " + hmac.hmacHex("" + (cMove+1)));

        int uMove = getUserMove();
        if (uMove == 0) return;
        else uMove--;

        System.out.println("Your move: " + moves[uMove]);
        System.out.println("Computer move: " + moves[cMove]);

        int half = moves.length / 2;
        int rotation = cMove>half ? moves.length+half-cMove : moves.length-1-half-cMove;
        ArrayList<String> movesRotated = new ArrayList<>(Arrays.asList(moves));
        Collections.rotate(movesRotated, rotation);

        if (movesRotated.indexOf(moves[uMove]) > half) {
            System.out.println("You win!");
        } else if (uMove == cMove){
            System.out.println("=== draw ===");
        } else {
            System.out.println("You lose :(");
        }

        System.out.println("HMAC key: " + Hex.encodeHexString(key));
    }

    private static boolean checkArgs(String[] args) {
        HashSet<String> set = new HashSet<>(Arrays.asList(args));
        return args.length > 2 && args.length %2 == 1 && args.length == set.size();
    }

    private static int getUserMove() {
        printMenu();
        Scanner in = new Scanner(System.in);
        int move;
        String input = in.nextLine();
        try {
            move = Integer.parseInt(input);
            if (move > moves.length || move < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            System.out.println("Input incorrect!");
            move = getUserMove();
        }
        in.close();
        return move;
    }

    private static void printMenu() {
        System.out.println("Available moves:");
        for (int i = 0; i < moves.length; i++) {
            System.out.printf("%d - %s\n",i+1, moves[i]);
        }
        System.out.println("0 - exit");
        System.out.print("Enter your move: ");
    }

    private static void printHint() {
        System.out.println("Command line parameters are incorrect.\n" +
                "Follow these rules to start the application:\n" +
                "1. You must use an odd number of command line arguments.\n" +
                "2. All arguments must be unique\n" +
                "3. At least three arguments.\n" +
                "For example:\n" +
                "\t>java Game A B C D E"
        );
    }
}
