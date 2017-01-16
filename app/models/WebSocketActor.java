package models;

import akka.actor.*;
import controllers.LobbyController;

public class WebSocketActor extends UntypedActor {
	
	public static Props props(ActorRef out) {
		if(LobbyController.player1 == null) {
			LobbyController.player1 = out;
		} else if(LobbyController.player2 == null) {
			LobbyController.player2 = out;
		} else if(LobbyController.player3 == null) {
			LobbyController.player3 = out;
		}
		return Props.create(WebSocketActor.class,out);
	}
	
	private final ActorRef out;
	
	public WebSocketActor(ActorRef out) {
		this.out = out;
	}

	public void onReceive(Object message) throws Exception {
		if(message instanceof String) {
			if(message.toString().equals("closed")) {
				LobbyController.removePlayer(this.out);
			} else {
				LobbyController.updatePlayers(message.toString(),this.out);
			}
			//out.tell(controllers.LobbyController.getJson(message.toString()),self());
		}
	}
	
	public void postStop() {
		LobbyController.removePlayer(this.out);
	}
	
}