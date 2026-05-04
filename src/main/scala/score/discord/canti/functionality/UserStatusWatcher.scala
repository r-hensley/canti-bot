package score.discord.canti.functionality

import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent
import net.dv8tion.jda.api.hooks.EventListener
import score.discord.canti.wrappers.NullWrappers.*
import score.discord.canti.wrappers.jda.ID

/** Watches the online status of a specific Discord user and suspends bot activity while that user
  * is online.
  *
  * Requires the `GUILD_PRESENCES` privileged gateway intent to be enabled both in the Discord
  * Developer Portal and in the bot configuration (`has_presences_intent = true`). Without it,
  * no `UserUpdateOnlineStatusEvent` events are dispatched and the bot will never be suspended.
  *
  * @param watchedUserId
  *   the ID of the user whose presence should gate bot activity
  */
class UserStatusWatcher(watchedUserId: ID[User]) extends EventListener:
  private val logger = loggerOf[UserStatusWatcher]

  @volatile private var _suspended = false

  /** Returns true when the bot should cease activity (i.e. the watched user is online). */
  def suspended: Boolean = _suspended

  override def onEvent(event: GenericEvent): Unit = event match
    case ev: UserUpdateOnlineStatusEvent
        if ev.getUser.nn.getIdLong == watchedUserId.value =>
      val newStatus = ev.getNewOnlineStatus.nn
      // Treat OFFLINE and UNKNOWN as not-online so that a transient UNKNOWN status
      // (e.g. during reconnects) does not incorrectly suppress bot activity.
      val nowOnline = newStatus != OnlineStatus.OFFLINE && newStatus != OnlineStatus.UNKNOWN
      if nowOnline != _suspended then
        _suspended = nowOnline
        if nowOnline then
          logger.info("Watched user is online; suspending bot activity.")
        else
          logger.info("Watched user is offline; resuming bot activity.")
    case _ =>
end UserStatusWatcher
