/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emnify.kvcluster.backend;

import akka.actor.ActorRef;

/**
 * @author Danilo Oliveira
 */
public class TaketimeoutMessage {
    private final ActorRef actor;

    public TaketimeoutMessage(ActorRef actor) {
        this.actor = actor;
    }

    public ActorRef actor() {
        return actor;
    }


}
