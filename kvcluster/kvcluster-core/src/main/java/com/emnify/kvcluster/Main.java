package com.emnify.kvcluster;

import com.emnify.kvcluster.backend.BackendMain;
import com.emnify.kvcluster.frontend.FrontendMain;

/**
 *
 * @author Danilo Oliveira
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        if(args.length < 1){
            errorMain();
        }
        
        String option = args[0];
        String[] remainingArgs = getRemainingArgs(args);
        switch(option){
            case "backend":
                BackendMain.main(remainingArgs);
                break;
            case "frontend":
                FrontendMain.main(remainingArgs);                
                break;
            default:
                errorMain();
        }
    }

    private static void errorMain() {
        System.err.println("Required usage: java -jar kvmcluster-core.jar frontend|backend [PORT]");
        System.exit(1);
    }

    private static String[] getRemainingArgs(String[] args) {
        if(args.length <= 1){
            return new String[0];
        }
        
        String[] result = new String[args.length-1];
        System.arraycopy(args, 1, result, 0, result.length);
        
        return result;
    }
}
