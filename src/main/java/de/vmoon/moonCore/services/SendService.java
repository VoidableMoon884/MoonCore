package de.vmoon.moonCore.services;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SendService {
    private final ProxyServer proxy;

    public SendService(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public boolean sendTarget(String target, String destination, Player player) {
        Optional<Player> targetPlayer = proxy.getPlayer(target);
        Optional<RegisteredServer> targetServer = proxy.getServer(target);
        Optional<Player> destinationPlayer = proxy.getPlayer(destination);
        Optional<RegisteredServer> destinationServer = proxy.getServer(destination);

        if (targetPlayer.isPresent()) {
            return sendPlayerToTarget(targetPlayer.get(), destinationServer, destinationPlayer, player);
        } else if (targetServer.isPresent()) {
            return sendPlayersFromServerToTarget(targetServer.get(), destinationServer, destinationPlayer, player);
        }
        return false;
    }

    private boolean sendPlayerToTarget(Player targetPlayer, Optional<RegisteredServer> destinationServer,
                                       Optional<Player> destinationPlayer, Player player) {
        if (destinationServer.isPresent()) {
            targetPlayer.createConnectionRequest(destinationServer.get()).fireAndForget();
            player.sendMessage(Component.text("Spieler " + targetPlayer.getUsername() + " wurde auf " + destinationServer.get().getServerInfo().getName() + " gesendet.", NamedTextColor.GREEN));
            return true;
        } else if (destinationPlayer.isPresent() && destinationPlayer.get().getCurrentServer().isPresent()) {
            targetPlayer.createConnectionRequest(destinationPlayer.get().getCurrentServer().get().getServer()).fireAndForget();
            player.sendMessage(Component.text("Spieler " + targetPlayer.getUsername() + " wurde zu " + destinationPlayer.get().getUsername() + " geschickt.", NamedTextColor.GREEN));
            return true;
        }
        return false;
    }

    private boolean sendPlayersFromServerToTarget(RegisteredServer targetServer, Optional<RegisteredServer> destinationServer,
                                                  Optional<Player> destinationPlayer, Player player) {
        for (Player p : proxy.getAllPlayers().stream()
                .filter(p -> p.getCurrentServer().isPresent() && p.getCurrentServer().get().getServer().equals(targetServer))
                .collect(Collectors.toList())) {
            if (destinationServer.isPresent()) {
                p.createConnectionRequest(destinationServer.get()).fireAndForget();
            } else if (destinationPlayer.isPresent() && destinationPlayer.get().getCurrentServer().isPresent()) {
                p.createConnectionRequest(destinationPlayer.get().getCurrentServer().get().getServer()).fireAndForget();
            }
        }
        player.sendMessage(Component.text("Alle Spieler von " + targetServer.getServerInfo().getName() + " wurden zu " +
                (destinationServer.isPresent() ? destinationServer.get().getServerInfo().getName() : destinationPlayer.get().getUsername()) +
                " gesendet.", NamedTextColor.GREEN));
        return true;
    }

    public List<String> getSuggestions(SimpleCommand.Invocation invocation) {
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
