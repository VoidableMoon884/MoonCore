package de.vmoon.moonCore;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import de.vmoon.moonCore.commands.AlertCommand;
import de.vmoon.moonCore.commands.SendCommand;
import de.vmoon.moonCore.services.SendService;
import org.slf4j.Logger;

@Plugin(
        id = "mooncore",
        name = "MoonCore",
        version = "1.0.1",
        description = "The main Velocity plugin for the MoonNet servers.",
        url = "vmoon.de",
        authors = {"VoidableMoon884"}
)
public class MoonCore {

    @Inject private Logger logger;
    private final ProxyServer proxy;

    @Inject
    public MoonCore(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = proxy.getCommandManager();
        commandManager.register("send", new SendCommand(new SendService(proxy)));
        commandManager.register("alert", new AlertCommand(proxy));

        logger.info("\u001B[32mMoonCore wurde erfolgreich geladen!\u001B[0m");
    }
}
