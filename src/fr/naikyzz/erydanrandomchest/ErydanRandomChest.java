package fr.naikyzz.erydanrandomchest;

import fr.naikyzz.erydanrandomchest.commands.CommandChest;
import org.bukkit.plugin.java.JavaPlugin;

public class ErydanRandomChest extends JavaPlugin {

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        System.out.println("ErydanRandomChest a bien démarré !");
        getCommand("rc").setExecutor(new CommandChest(this));
        getCommand("rcr").setExecutor(new CommandChest(this));
        getCommand("rcp").setExecutor(new CommandChest(this));
    }

    @Override
    public void onDisable() {
        System.out.println("ErydanRandomChest s'est bien stoppé !");
    }
}
