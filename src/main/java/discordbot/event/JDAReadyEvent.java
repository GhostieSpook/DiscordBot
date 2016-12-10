package discordbot.event;

import discordbot.main.DiscordBot;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class JDAReadyEvent extends ListenerAdapter {
	private DiscordBot discordBot;

	public JDAReadyEvent(DiscordBot bot) {
		this.discordBot = bot;
	}


	@Override
	public void onReady(ReadyEvent event) {
		discordBot.markReady();
		System.out.println("[event] Bot is ready!");
	}
}