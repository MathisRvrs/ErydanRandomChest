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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.*;

import java.util.Random;

public class CommandChest implements CommandExecutor, Listener {

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
                    Faction fac = Board.getInstance().getFactionAt(new FLocation(locChest));
                    int nbItem = 0;

                    if (fac.getTag().contains("Wilderness") || fac.getTag().contains("WarZone") || fac.getTag().contains("SafeZone")) {
                        locChest.getBlock().setType(Material.CHEST);
                        Chest chest = (Chest) locChest.getBlock().getState();
                        Inventory invChest = chest.getInventory();
                        Bukkit.broadcastMessage("§aUn coffre a spawn en §ex: §c" + x + " §ey: §c" + y + " §ez: §c" + z + " §e! §aFoncez !");

                        for (String key : main.getConfig().getConfigurationSection("chest.item").getKeys(false)) {//get config / Mettre items dans le coffre
                            try {

                                Bukkit.broadcastMessage(fac.getTag());

                                if (nbItem < Integer.parseInt(main.getConfig().getString("chest.maxitemloot"))) {
                                    int id = Integer.valueOf(key);
                                    int chance = main.getConfig().getInt("chest.item." + id + ".chance");
                                    double random = getRandom(100);
                                    String mat = main.getConfig().getString("chest.item." + id + ".material");
                                    int amount = Integer.parseInt(main.getConfig().getString("chest.item." + id + ".amount"));
                                    ItemStack item = new ItemStack(Material.getMaterial(mat), amount);

                                    if (chance >= random) {
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
                                            int slot = rdm.nextInt(27);
                                            while (invChest.getItem(slot) != null) {
                                                slot = rdm.nextInt(27);
                                            }
                                            invChest.setItem(slot, item); //ajout item si enchanté
                                        }

                                        if (main.getConfig().getString("chest.item." + id + ".sub").equalsIgnoreCase("true")) {
                                            int subname = main.getConfig().getInt("chest.item." + id + ".subname");
                                            ItemStack metaItem = new ItemStack(Material.getMaterial(mat), Integer.parseInt(String.valueOf(amount).trim()), (short) Integer.parseInt(String.valueOf(subname).trim()));
                                            int slot = rdm.nextInt(27);
                                            while (invChest.getItem(slot) != null) {
                                                slot = rdm.nextInt(27);
                                            }
                                            invChest.setItem(slot, metaItem);

                                        } else if (main.getConfig().getString("chest.item." + id + ".enchanted").equalsIgnoreCase("false")) {
                                            int slot = rdm.nextInt(27);
                                            while (invChest.getItem(slot) != null) {
                                                slot = rdm.nextInt(27);
                                            }
                                            invChest.setItem(slot, item);
                                        }
                                    }
                                }

                                nbItem++;
                            } catch (NumberFormatException e) { //erreur config
                                System.out.println("ERROR: Il faut entrer un nombre après chest.item. !");
                            }

                        }
                    } else {
                        Bukkit.broadcastMessage("§eUne faction se trouvait au point de spawn, recommencez !");
                        return true;
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

    public double getRandom(int max) {
        return Math.floor(Math.random() * max) + 1;
    }

}
