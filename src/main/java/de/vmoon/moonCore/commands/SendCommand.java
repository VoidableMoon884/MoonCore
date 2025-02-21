package de.vmoon.moonCore.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.vmoon.moonCore.services.SendService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class SendCommand implements SimpleCommand {
    private final SendService sendService;

    public SendCommand(SendService sendService) {
        this.sendService = sendService;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player) || !player.hasPermission("mooncore.send")) {
            invocation.source().sendMessage(Component.text("Du hast keine Berechtigung für diesen Befehl.", NamedTextColor.RED));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length != 2) {
            player.sendMessage(Component.text("Verwendung: /send [Spieler/Server] [Spieler/Server]", NamedTextColor.RED));
            return;
        }

        String target = args[0];
        String destination = args[1];

        boolean result = sendService.sendTarget(target, destination, player);
        if (!result) {
            player.sendMessage(Component.text("Ziel nicht gefunden.", NamedTextColor.RED));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return sendService.getSuggestions(invocation);
    }
}
