package net.krinsoft.chat.commands;

import com.pneumaticraft.commandhandler.Command;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;

/**
 *
 * @author krinsdeath
 */
public class ChatSuiteCommand extends Command {
    protected ChatCore plugin;

    public ChatSuiteCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void runCommand(CommandSender cs, List<String> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
