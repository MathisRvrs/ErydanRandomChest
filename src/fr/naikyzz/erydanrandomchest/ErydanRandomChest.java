package fr.naikyzz.erydanrandomchest;

import fr.naikyzz.erydanrandomchest.commands.CommandChest;
import org.bukkit.plugin.java.JavaPlugin;

public class ErydanRandomChest extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("ErydanRandomChest a bien démarré !");
        getCommand("rc").setExecutor(new CommandChest());
    }

    @Override
    public void onDisable() {
        System.out.println("ErydanRandomChest s'est bien stoppé !");
    }
}
