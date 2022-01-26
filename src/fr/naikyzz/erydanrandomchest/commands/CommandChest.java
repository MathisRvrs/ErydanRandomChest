package fr.naikyzz.erydanrandomchest.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class CommandChest implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("rc")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage("§eLa commande est: §c/rc §c<Xmax> <Xmin> <Ymax> <Ymin> !");
                }
                if (args.length >= 1) {
                    StringBuilder bc = new StringBuilder();
                    String[] tab;

                    for (String part : args) {
                        bc.append(part + " ");
                    }

                    sender.sendMessage(bc.toString());
                    bc.setLength(bc.length() - 1);
                    tab = bc.toString().split(" ");


                    sender.sendMessage("§c§l" + bc.length() + "_" + bc.toString() + "_" + tab.length);

                    for (int i = 0; i < tab.length; i++) { // TESTS
                        if (i != 3) {
                            sender.sendMessage(tab[i] + "tab[" + i + "]" + "_");
                        } else
                            sender.sendMessage(tab[i] + "tab[" + i + "]");
                    }

                    sender.sendMessage("Passage");

                    Random rdm = new Random();

                    int x, y, z;
                    x = rdm.nextInt(Integer.parseInt(tab[0]) - Integer.parseInt(tab[1]) + Integer.parseInt(tab[1]));
                    sender.sendMessage(Integer.toString(x));
                    z = rdm.nextInt(Integer.parseInt(tab[2]) - Integer.parseInt(tab[3].trim()) + Integer.parseInt(tab[3].trim()));
                    sender.sendMessage(Integer.toString(z));
                    y = getHighestBlock(Bukkit.getWorld("world"), x, z);

                    Location chest = new Location(Bukkit.getWorld("world"), x, y + 1, z);
                    chest.getBlock().setType(Material.CHEST);
                    Bukkit.broadcastMessage("§aUn coffre a spawn en §ex: §c" + x + " §ey: §c" + y + " §ez: §c" + z + " §e! §aFoncez !");

                }
            }
        }
        return true;
    }

    public int getHighestBlock(World world, int x, int z) {

        for (int i = 255; i != 0; i--) {
            if (new Location(world, x, i, z).getBlock().getType() != Material.AIR) {
                return i;
            }
        }
        return 0;
    }
}
