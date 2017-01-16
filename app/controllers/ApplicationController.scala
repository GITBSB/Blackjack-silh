package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc.Controller
import utils.auth.DefaultEnv
import play.api.mvc.Action

import de.htwg.blackjack.Blackjack
import de.htwg.blackjack.entities.impl.Player

import scala.concurrent.Future
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param socialProviderRegistry The social provider registry.
 * @param webJarAssets The webjar assets implementation.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  implicit val webJarAssets: WebJarAssets)
  extends Controller with I18nSupport {

  var controller = Blackjack.getInstance.getController();

  def blackjack(command: String) = silhouette.SecuredAction.async { implicit request =>
    Blackjack.getInstance.getTUI().userinputselection(command);
    Future.successful(Ok(views.html.index(request.identity)))
  }

  def start = silhouette.UserAwareAction.async { implicit request =>
    Future.successful(Ok(views.html.start(webJarAssets)))
  }

  def rules = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.rules(request.identity)))
  }

  def play = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.index(request.identity)))
  }

  def jsonCommand(command: String) = silhouette.SecuredAction { implicit request =>
    Blackjack.getInstance.getTUI().userinputselection(command)
    Ok(controller.json())
  }

  def addNewPlayer(player: String) = silhouette.SecuredAction { implicit request =>
    Blackjack.getInstance().getController().addnewPlayer(player);
    Ok(controller.json())
  }

  /**
   * def rules = silhouette.UserAwareAction { implicit request =>
   * val userName = request.identity match {
   * case Some(identity) => identity.fullName
   * case None => "Guest"
   * }
   * Ok(views.html.start(userName, webJarAssets))
   * }
   */

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.home(request.identity)))
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  def json = Action {
    Ok(controller.json())
  }

}
