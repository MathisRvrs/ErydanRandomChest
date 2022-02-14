package fr.naikyzz.erydanrandomchest.commands;

import fr.naikyzz.erydanrandomchest.ErydanRandomChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.Random;

public class CommandChest implements CommandExecutor {

    private final ErydanRandomChest main;

    public CommandChest(ErydanRandomChest erc) {
        this.main = erc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("rc")) {
                if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage("§eLa commande est: §c/rc §c<Xmax> <Xmin> <Ymax> <Ymin> !");
                    return false;
                }

                if (args.length == 0 || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                    sender.sendMessage(main.getConfig().getString("messages.reload").replace("&", "§"));
                    main.reloadConfig();
                    return false;
                }

                if (args.length >= 1) {
                    StringBuilder bc = new StringBuilder();
                    String[] tab;

                    for (String part : args) { //COORDONNES
                        bc.append(part + " ");
                    }

                    sender.sendMessage(bc.toString()); //AFFICHAGE COORDS
                    bc.setLength(bc.length() - 1);
                    tab = bc.toString().split(" ");

                    Random rdm = new Random();

                    int x, y, z;
                    x = rdm.nextInt(Integer.parseInt(tab[0]) - Integer.parseInt(tab[1]) + Integer.parseInt(tab[1]));
                    z = rdm.nextInt(Integer.parseInt(tab[2]) - Integer.parseInt(tab[3].trim()) + Integer.parseInt(tab[3].trim()));
                    y = getHighestBlock(Bukkit.getWorld("world"), x, z);

                    Location locChest = new Location(Bukkit.getWorld("world"), x, y + 1, z);
                    locChest.getBlock().setType(Material.CHEST);
                    Bukkit.broadcastMessage("§aUn coffre a spawn en §ex: §c" + x + " §ey: §c" + y + " §ez: §c" + z + " §e! §aFoncez !");

                    Chest chest = (Chest) locChest.getBlock().getState();
                    Inventory invChest = chest.getInventory();

                    for (String key : main.getConfig().getConfigurationSection("chest.item").getKeys(false)) {//get config
                        try {
                            int id = Integer.valueOf(key);
                            String mat = main.getConfig().getString("chest.item." + id + ".material");
                            int amount = Integer.parseInt(main.getConfig().getString("chest.item." + id + ".amount"));
                            ItemStack item = new ItemStack(Material.getMaterial(mat), amount);

                            if (main.getConfig().getString("chest.item." + id + ".enchanted").equalsIgnoreCase("true")) { //Si enchantements
                                StringBuilder sb = new StringBuilder();
                                String[] enchant_nb;
                                String[] enchant_names;
                                sb.append(main.getConfig().getString("chest.item." + id + ".enchants"));

                                enchant_names = sb.toString().split("-");
                                for (int i = 0; i < enchant_names.length - 1; i++) { //Ajout des enchantements
                                    sb = new StringBuilder();
                                    sb.append(enchant_names[i + 1]);
                                    enchant_nb = sb.toString().split(" ");
                                    item.addEnchantment(Enchantment.getByName(enchant_nb[0].trim()), Integer.parseInt(enchant_nb[1].trim()));
                                }
                                invChest.setItem(rdm.nextInt(27), item); //ajout item si enchanté
                            }

                            if (main.getConfig().getString("chest.item." + id + ".sub").equalsIgnoreCase("true")) {
                                int subname = main.getConfig().getInt("chest.item." + id + ".subname");
                                ItemStack metaItem = new ItemStack(Material.getMaterial(mat), Integer.parseInt(String.valueOf(amount).trim()), (short) Integer.parseInt(String.valueOf(subname).trim()));
                                invChest.setItem(rdm.nextInt(27), metaItem);
                            } else if (main.getConfig().getString("chest.item." + id + ".enchanted").equalsIgnoreCase("false")) {
                                invChest.setItem(rdm.nextInt(27), item);
                            }

                        } catch (NumberFormatException e) { //erreur config
                            System.out.println("ERROR: Il faut entrer un nombre après chest.item. !");
                        }
                    }
                }
            }
        }
        return true;
    }

    public int getHighestBlock(World world, int x, int z) { //get le block le plus haut

        for (int i = 150; i != 0; i--) {
            if (new Location(world, x, i, z).getBlock().getType() != Material.AIR) {
                return i;
            }
        }
        return 0;
    }
}
