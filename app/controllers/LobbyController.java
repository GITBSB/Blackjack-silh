package controllers;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import de.htwg.blackjack.Blackjack;
import de.htwg.blackjack.controller.IController;
import models.WebSocketActor;
import play.Logger;
import play.mvc.Controller;
import play.mvc.LegacyWebSocket;
import play.mvc.WebSocket;
import views.html.rules;
import play.mvc.Result;

public class LobbyController extends Controller {
	
	public static ActorRef player1 = null;
	public static ActorRef player2 = null;
	public static ActorRef player3 = null;
	
	public static Map<ActorRef,String> playermap = new HashMap<ActorRef,String>();
	
	static IController controller = Blackjack.getInstance().getController();
	
	public static String getJson(String command,ActorRef out) {
		String playername = playermap.get(out);
		return controller.getJson(command+":"+playername);
	}
	
	public LegacyWebSocket<String> createWebSocket(String playername) {
//		if (playermap.containsValue(playername)) {
//			return null;
//		}
		if (player1 == null || player2 == null || player3 == null) {
			int playernumber = 0;
			if (player1 == null) {
				playernumber = 1;
			} else if (player2 == null) {
				playernumber = 2;
			} else if (player3 == null) {
				playernumber = 3;
			}
			LegacyWebSocket<String> socket = WebSocket.withActor(WebSocketActor::props);
			switch(playernumber) {
			case 1:
				playermap.put(player1, playername);
				break;
			case 2:
				playermap.put(player2, playername);
				break;
			case 3:
				playermap.put(player3, playername);
				break;
			}
			return socket;
		} else {
			return null;
		}
	}
	
	public static void updatePlayers(String message,ActorRef out) {
		String json = controllers.LobbyController.getJson(message.toString(),out);
		if(player1 != null) {
			player1.tell(json, player1);
		}
		if(player2 != null) {
			player2.tell(json, player2);
		}
		if(player3 != null) {
			player3.tell(json, player3);
		}
	}
	
	public static void removePlayer(ActorRef player) {
		String playername = playermap.get(player);
		controller.removePlayer(playername);
		playermap.remove(player);
		if(player1.equals(player)) {
			player1 = null;
			if(player2 != null) {
				player2.tell("0", player2);
			}
			if(player3 != null) {
				player3.tell("0", player3);
			}
		} else if(player2.equals(player)) {
			player2 = null;
			if(player1 != null) {
				player1.tell("1", player1);
			}
			if(player3 != null) {
				player3.tell("1", player3);
			}
		} else if(player3.equals(player)) {
			player3 = null;
			if(player1 != null) {
				player1.tell("2", player1);
			}
			if(player2 != null) {
				player2.tell("2", player2);
			}
		}
	}
}
