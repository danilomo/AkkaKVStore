package com.emnify.kvcluster.client;

/**
 * @author Danilo Oliveira
 */
public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            errorMain();
        }

        String option = args[0];
        String[] remainingArgs = getRemainingArgs(args);
        switch (option) {
            case "senders":
                SendersMain.main(remainingArgs);
                break;
            case "receivers":
                ReceiversMain.main(remainingArgs);
                break;
            case "console":
                BeanshellConsole.main(remainingArgs);
            default:
                errorMain();
        }
    }

    private static void errorMain() {
        System.err.println("Required usage: java -jar kvmcluster-client.jar senders|receivers|console <args>");
        System.exit(1);
    }

    private static String[] getRemainingArgs(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }

        String[] result = new String[args.length - 1];
        System.arraycopy(args, 1, result, 0, result.length);

        return result;
    }
}
