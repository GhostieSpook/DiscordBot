package discordbot.command.fun;

import discordbot.command.CooldownScale;
import discordbot.command.ICommandCooldown;
import discordbot.core.AbstractCommand;
import discordbot.games.SlotMachine;
import discordbot.games.slotmachine.Slot;
import discordbot.main.Config;
import discordbot.main.NovaBot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.TimerTask;

/**
 * Created on 23-8-2016
 */
public class SlotMachineCommand extends AbstractCommand implements ICommandCooldown {

	public final long SPIN_INTERVAL = 2000L;

	public SlotMachineCommand(NovaBot bot) {
		super(bot);
	}

	@Override
	public long getCooldownDuration() {
		return 30L;
	}

	@Override
	public CooldownScale getCooldownScale() {
		return CooldownScale.USER;
	}

	@Override
	public String getDescription() {
		return "Feeling lucky? try the slotmachine! You might just win a hand full of air!";
	}

	@Override
	public String getCommand() {
		return "slot";
	}

	@Override
	public String[] getUsage() {
		return new String[]{
				"slot      //displays info and payout table",
				"slot play //plays the game"
		};
	}

	@Override
	public String[] getAliases() {
		return new String[]{};
	}

	@Override
	public String execute(String[] args, IChannel channel, IUser author) {
		if (args.length >= 1 && args[0].equals("play")) {
			final SlotMachine slotMachine = new SlotMachine();
			final IMessage msg = bot.out.sendMessage(channel, slotMachine.toString());
			bot.timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					try {
						if (slotMachine.gameInProgress()) {
							slotMachine.spin();
						}
						String gameresult = "";
						if (!slotMachine.gameInProgress()) {
							Slot slot = slotMachine.winSlot();
							if (slot != null) {
								gameresult = "You rolled 3 **" + slot.getName() + "** and won **" + slot.getTriplePayout() + "**";
							} else {
								gameresult = "Aw you lose, better luck next time!";
							}
							if (msg != null) {
								bot.out.editMessage(msg, slotMachine.toString() + Config.EOL + gameresult);
							} else {
								bot.out.sendMessage(channel, slotMachine.toString() + Config.EOL + gameresult);
							}
							this.cancel();
						} else {
							if (msg != null) {
								bot.out.editMessage(msg, slotMachine.toString());
							} else {
								bot.out.sendMessage(channel, slotMachine.toString());
							}
						}
					} catch (Exception e) {
						bot.out.sendErrorToMe(e, "slotmachine", author.getID(), "channel", channel.mention(), bot);
						this.cancel();
					}
				}
			}, 1000L, SPIN_INTERVAL);
		} else {
			String ret = "The slotmachine!" + Config.EOL;
			ret += "payout is as follows: " + Config.EOL;
			for (Slot s : Slot.values()) {
				ret += String.format("%1$s %1$s %1$s = %2$s" + Config.EOL, s.getEmote(), s.getTriplePayout());
			}
			ret += "type **slot play** to give it a shot!";
			return ret;
		}
		return "";
	}
}