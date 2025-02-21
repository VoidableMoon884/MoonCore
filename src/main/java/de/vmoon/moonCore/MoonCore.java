package de.vmoon.moonCore;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(
    id = "mooncore",
    name = "MoonCore",
    version = "1.0"
    ,description = "The main Velocity plugin for the MoonNet servers."
    ,url = "vmoon.de"
    ,authors = {"VoidableMoon884"}
)
public class MoonCore {

    @Inject private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
