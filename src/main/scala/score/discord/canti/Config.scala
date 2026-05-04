package score.discord.canti

import net.dv8tion.jda.api.entities.User
import score.discord.canti.wrappers.jda.ID

class Config(
  val token: String,
  val owner: ID[User],
  val hasMessageIntent: Boolean,
  val hasGuildMembersIntent: Boolean,
  val hasPresencesIntent: Boolean,
  val watchedUser: Option[ID[User]]
)

object Config:
  def load(config: com.typesafe.config.Config) =
    Config(
      token = config.getString("token").nn,
      owner = ID[User](config.getLong("owner")),
      hasMessageIntent = config.getBoolean("has_message_intent"),
      hasGuildMembersIntent = config.getBoolean("has_guild_members_intent"),
      hasPresencesIntent =
        if config.hasPath("has_presences_intent") then config.getBoolean("has_presences_intent")
        else false,
      watchedUser =
        if config.hasPath("watched_user") then Some(ID[User](config.getLong("watched_user")))
        else None
    )
