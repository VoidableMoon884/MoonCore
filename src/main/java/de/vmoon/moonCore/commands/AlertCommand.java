package de.vmoon.moonCore.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.vmoon.moonCore.services.AlertService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlertCommand implements SimpleCommand {
    private final ProxyServer proxy;
    private final AlertService alertService;
    private final Set<String> targetServers = new HashSet<>();

    public AlertCommand(ProxyServer proxy) {
        this.proxy = proxy;
        this.alertService = new AlertService(proxy); // Initialize AlertService
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player) || !player.hasPermission("mooncore.alert")) {
            invocation.source().sendMessage(Component.text("Du hast keine Berechtigung für diesen Befehl.", NamedTextColor.RED));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length < 1) {
            player.sendMessage(Component.text("Verwendung: /alert [servers/send/player/restart]", NamedTextColor.RED));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "servers" -> {
                targetServers.clear();
                for (int i = 1; i < args.length; i++) {
                    if (args[i].equalsIgnoreCase("ALL")) {
                        proxy.getAllServers().forEach(server -> targetServers.add(server.getServerInfo().getName()));
                    } else if (proxy.getServer(args[i]).isPresent()) {
                        targetServers.add(args[i]);
                    } else {
                        player.sendMessage(Component.text("Server '" + args[i] + "' nicht gefunden.", NamedTextColor.RED));
                    }
                }
                player.sendMessage(Component.text("Zielserver für Alerts gesetzt: " + String.join(", ", targetServers), NamedTextColor.GREEN));
            }
            case "send" -> {
                if (targetServers.isEmpty()) {
                    player.sendMessage(Component.text("Keine Zielserver festgelegt! Nutze /alert servers [Server1, Server2] zuerst.", NamedTextColor.RED));
                    return;
                }
                if (args.length < 2) {
                    player.sendMessage(Component.text("Verwendung: /alert send [Nachricht]", NamedTextColor.RED));
                    return;
                }
                String message = String.join(" ", List.of(args).subList(1, args.length));
                alertService.sendMessageToServers(targetServers, message);
            }
            case "player" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("Verwendung: /alert player [Spielername] [Nachricht]", NamedTextColor.RED));
                    return;
                }
                String playerName = args[1];
                String message = String.join(" ", List.of(args).subList(2, args.length));
                alertService.sendMessageToPlayer(playerName, message);
                player.sendMessage(Component.text("Alert an " + playerName + " gesendet!", NamedTextColor.GREEN));
            }
            case "restart" -> {
                alertService.sendMessageToAllServers("Das Netzwerk wird in Kürze neugestartet. Bitte speichert eure Fortschritte!");
            }
            default -> player.sendMessage(Component.text("Unbekannte Option: " + args[0], NamedTextColor.RED));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 0) {
            return List.of("servers", "send", "player", "restart");
        }
        if (args.length == 1) {
            return List.of("servers", "send", "player", "restart").stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "servers":
                    return proxy.getAllServers().stream()
                            .map(server -> server.getServerInfo().getName())
                            .filter(name -> name.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                case "player":
                    return proxy.getAllPlayers().stream()
                            .map(Player::getUsername)
                            .filter(name -> name.startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
            }
        }

        return List.of();
    }
}
