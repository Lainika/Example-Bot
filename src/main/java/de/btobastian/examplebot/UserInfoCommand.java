package de.btobastian.examplebot;

import de.btobastian.javacord.entities.Activity;
import de.btobastian.javacord.entities.message.MessageAuthor;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import de.btobastian.javacord.events.message.MessageCreateEvent;
import de.btobastian.javacord.exceptions.MissingPermissionsException;
import de.btobastian.javacord.listeners.message.MessageCreateListener;
import de.btobastian.javacord.utils.ExceptionLogger;

public class UserInfoCommand implements MessageCreateListener {

    /*
     * This command can be used to display information about the user who used the command.
     * It's a good example for the MessageAuthor, MessageBuilder and ExceptionLogger class
     */
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        // Check if the message content equals "!userInfo"
        if (event.getMessage().getContent().equalsIgnoreCase("!userInfo")) {
            MessageAuthor author = event.getMessage().getAuthor();
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("User Info")
                    .addField("Display Name", author.getDisplayName(), true)
                    .addField("Name + Discriminator", author.getDiscriminatedName(), true)
                    .addField("User Id", author.getIdAsString(), true)
                    .setAuthor(author);
            // Keep in mind that a message author can either be a webhook or a normal user
            author.asUser().ifPresent(user -> {
                embed.addField("Online Status", user.getStatus().getStatusString(), true);
                // The User#getActivity() method returns an Optional
                embed.addField("Activity", user.getActivity().map(Activity::getName).orElse("none"), true);
            });
            // Keep in mind that messages can also be sent as private messages
            event.getMessage().getServer()
                    .ifPresent(server -> embed.addField("Server Admin", author.isServerAdmin() ? "yes" : "no", true));
            // Send the embed. It logs every exception, besides missing permissions (you are not allowed to send message in the channel)
            event.getChannel().sendMessage(embed)
                    .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        }
    }

}
