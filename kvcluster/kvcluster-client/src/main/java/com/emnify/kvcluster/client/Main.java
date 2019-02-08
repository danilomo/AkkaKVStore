package com.emnify.kvcluster.client;

import akka.actor.Actor;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.emnify.kvcluster.messages.PutMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Danilo Oliveira
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        int port = 3000;

        Config config = ConfigFactory.parseString(
                "akka.remote.netty.tcp.port=" + port)
                .withFallback(ConfigFactory.load("client"));

        // Create an Akka system
        ActorSystem system = ActorSystem.create("kvstore", config);

        ActorSelection ref = system.
                actorSelection("akka.tcp://kvstore@127.0.0.1:2551/user/frontend");
        
        String[] strs = {"Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal", "Espírito Santo", "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais", "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí", "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul", "Rondônia", "Roraima", "Santa Catarina", "São Paulo", "Sergipe", "Tocantins"};
        
        for(String str: strs){
            ref.tell(new PutMessage<>(str, str), Actor.noSender());
        }
        
        BeanshellConsole.main(args);
        
//        Thread.sleep(10000);
//        System.out.println("Enviando mensagens.");
//
//        ref.tell(new PutMessage<>("um",   "1"), Actor.noSender());
//        ref.tell(new PutMessage<>("dois", "2"), Actor.noSender());
//        ref.tell(new PutMessage<>("tres", "3"), Actor.noSender());
//
//        Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));
//
//        CompletableFuture<Object> future1
//                = ask(ref, "get-contents", 1000).toCompletableFuture();
//        
//        System.err.println(future1.get());

    }
}
