package de.vmoon.moonCore.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SendCommand implements SimpleCommand {
    private final ProxyServer proxy;

    public SendCommand(ProxyServer proxy) {
        this.proxy = proxy;
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

        Optional<Player> targetPlayer = proxy.getPlayer(target);
        Optional<RegisteredServer> targetServer = proxy.getServer(target);
        Optional<Player> destinationPlayer = proxy.getPlayer(destination);
        Optional<RegisteredServer> destinationServer = proxy.getServer(destination);

        if (targetPlayer.isPresent()) {
            if (destinationServer.isPresent()) {
                targetPlayer.get().createConnectionRequest(destinationServer.get()).fireAndForget();
                player.sendMessage(Component.text("Spieler " + target + " wurde auf " + destination + " gesendet.", NamedTextColor.GREEN));
            } else if (destinationPlayer.isPresent() && destinationPlayer.get().getCurrentServer().isPresent()) {
                targetPlayer.get().createConnectionRequest(destinationPlayer.get().getCurrentServer().get().getServer()).fireAndForget();
                player.sendMessage(Component.text("Spieler " + target + " wurde zu " + destination + " geschickt.", NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text("Zielserver oder Zielspieler nicht gefunden.", NamedTextColor.RED));
            }
        } else if (targetServer.isPresent()) {
            for (Player p : proxy.getAllPlayers().stream()
                    .filter(p -> p.getCurrentServer().isPresent() && p.getCurrentServer().get().getServer().equals(targetServer.get()))
                    .collect(Collectors.toList())) {
                if (destinationServer.isPresent()) {
                    p.createConnectionRequest(destinationServer.get()).fireAndForget();
                } else if (destinationPlayer.isPresent() && destinationPlayer.get().getCurrentServer().isPresent()) {
                    p.createConnectionRequest(destinationPlayer.get().getCurrentServer().get().getServer()).fireAndForget();
                }
            }
            player.sendMessage(Component.text("Alle Spieler von " + target + " wurden zu " + destination + " gesendet.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Ziel nicht gefunden.", NamedTextColor.RED));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        String input = args.length > 0 ? args[args.length - 1].toLowerCase() : "";

        List<String> options = proxy.getAllPlayers().stream()
                .map(Player::getUsername)
                .collect(Collectors.toList());

        options.addAll(proxy.getAllServers().stream()
                .map(server -> server.getServerInfo().getName())
                .collect(Collectors.toList()));

        // Falls noch nichts eingegeben wurde, alle Vorschläge anzeigen
        if (input.isEmpty()) {
            return options;
        }

        // Nur Vorschläge zurückgeben, die mit der Eingabe beginnen
        return options.stream()
                .filter(name -> name.toLowerCase().startsWith(input))
                .collect(Collectors.toList());
    }

}
