package de.vmoon.moonCore.services;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Set;

public class AlertService {
    private final ProxyServer proxy;

    public AlertService(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public void sendMessageToServers(Set<String> targetServers, String message) {
        Component alertMessage = Component.text("[Alert] ", NamedTextColor.RED)
                .append(Component.text(message, NamedTextColor.WHITE));

        for (String serverName : targetServers) {
            proxy.getServer(serverName).ifPresent(server -> {
                server.getPlayersConnected().forEach(player -> player.sendMessage(alertMessage));
            });
        }
    }

    public void sendMessageToAllServers(String message) {
        Component alertMessage = Component.text("[Alert] ", NamedTextColor.RED)
                .append(Component.text(message, NamedTextColor.WHITE));

        for (var server : proxy.getAllServers()) {
            server.getPlayersConnected().forEach(player -> player.sendMessage(alertMessage));
        }
    }

    public void sendMessageToPlayer(String playerName, String message) {
        proxy.getPlayer(playerName).ifPresentOrElse(
                player -> player.sendMessage(Component.text("[Alert] ", NamedTextColor.RED)
                        .append(Component.text(message, NamedTextColor.WHITE))),
                () -> System.out.println("Spieler '" + playerName + "' nicht gefunden.")
        );
    }
}
