package com.emnify.kvcluster.frontend;

import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.Arrays;

/**
 *
 * @author Danilo Oliveira
 */
public class FrontendMain {

    public static void main(String[] args) {
        
        System.out.println(Arrays.toString(args));
        
        int port = 0;
        
        if(args.length > 0){
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ne) {
                System.err.println("Invalid port.");
                System.exit(1);
            }
        }

        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load("frontend"));

        ActorSystem system = ActorSystem.create("kvstore", config);
        system.actorOf(Props.create(FrontendActor.class), "frontend");
    }
}
