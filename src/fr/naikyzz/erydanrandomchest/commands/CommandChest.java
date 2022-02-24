package fr.naikyzz.erydanrandomchest.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import fr.naikyzz.erydanrandomchest.ErydanRandomChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class CommandChest implements CommandExecutor, Listener {

    private final ErydanRandomChest main;

    public CommandChest(ErydanRandomChest erc) {
        this.main = erc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (sender.hasPermission("rc.use")) {

            if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
                if (cmd.getName().equalsIgnoreCase("rc")) {
                    if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                        sender.sendMessage("§eLa commande est: §c/rc §c<Xmax> <Xmin> <Ymax> <Ymin> !");
                        return false;
                    }

                    if (args.length == 0 || args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) { //reload le plugin
                        sender.sendMessage(main.getConfig().getString("messages.reload").replace("&", "§"));
                        main.reloadConfig();
                        return false;
                    }
                }

                if (sender instanceof Player) {
                    if (cmd.getName().equalsIgnoreCase("rcp")) {
                        if (args.length > 0) {
                            sender.sendMessage("§eLa commande est: §c/rc §c<Xmax> <Xmin> <Ymax> <Ymin> !");
                            return false;
                        }
                        Location locChest = ((Player) sender).getLocation();
                        Faction fac = Board.getInstance().getFactionAt(new FLocation(locChest));

                        spawnChest(locChest, fac);
                    }
                }

                if (cmd.getName().equalsIgnoreCase("rcr")) {
                    if (args.length == 0) {
                        sender.sendMessage("§eLa commande est: §c/rcr §c<Xmax> <Xmin> <Ymax> <Ymin> !");
                        return false;
                    }

                    if (args.length != 4) {
                        sender.sendMessage("§eVous devez entrer 4 coordonnés !");
                        sender.sendMessage("§eLa commande est: §c/rcr §c<Xmax> <Xmin> <Ymax> <Ymin> !");
                        return false;
                    }

                    if (args.length >= 1 && args.length < 5) { // met les arguments dans un string builder

                        StringBuilder bc = new StringBuilder();
                        String[] tab;

                        for (String part : args) { //COORDONNES
                            bc.append(part + " ");
                        }

                        bc.setLength(bc.length() - 1);
                        tab = bc.toString().split(" ");


                        for (int i = 0; i < tab.length; i++) {
                            if (tab[i].charAt(0) == '-'){
                                String nbNeg = tab[i].replace("-","");
                                if (!isNumeric(nbNeg)){
                                    sender.sendMessage("§eVous devez entrer des nombres !");
                                    return false;
                                }
                            } else if (!isNumeric(tab[i]) && tab[i].trim().charAt(0) != '-') { //TROUVER TECHNIQUE POUR GERER LES NOMBRES NEGATIFS StringBuilder pour enelver le - ? replace ?
                                    sender.sendMessage("§eVous devez entrer des nombres !");
                                    return false;
                                }

                            if (Integer.parseInt(tab[i].trim()) > Integer.parseInt(main.getConfig().getString("chest.maxWorld")) || Integer.parseInt(tab[i].trim()) < Integer.parseInt(main.getConfig().getString("chest.minWorld"))) {
                                sender.sendMessage("§eLes limites du monde sont de : §c" + main.getConfig().getString("chest.maxWorld") + " §epar §c" + main.getConfig().getString("chest.minWorld") + " §e!");
                                return false;
                            }
                        }

                        int xmax = Integer.parseInt(tab[0].trim());
                        int xmin = Integer.parseInt(tab[1].trim());
                        int zmax = Integer.parseInt(tab[2].trim());
                        int zmin = Integer.parseInt(tab[3].trim());

                        Location locChest = getLocation(xmax, xmin, zmax, zmin, sender);
                        Faction fac = Board.getInstance().getFactionAt(new FLocation(locChest));

                        while (!(fac.getTag().contains("Wilderness") || fac.getTag().contains("WarZone") || fac.getTag().contains("SafeZone"))) {
                            xmin -= 1;
                            xmax += 1;
                            zmin -= 1;
                            zmax += 1;
                            locChest = getLocation(xmax, xmin, zmax, zmin, sender);
                            fac = Board.getInstance().getFactionAt(new FLocation(locChest));
                        }

                        spawnChest(locChest, fac); // fait spawn un coffre

                    } else sender.sendMessage("§eVous devez entrer 4 coordonnés !");
                }
            }
        }
        return true;
    }

    public void spawnChest(Location locChest, Faction fac) {
        int nbItem = 0;
        Random rdm = new Random();

        if (fac.getTag().contains("Wilderness") || fac.getTag().contains("WarZone") || fac.getTag().contains("SafeZone")) {
            locChest.getBlock().setType(Material.CHEST);
            Chest chest = (Chest) locChest.getBlock().getState();
            Inventory invChest = chest.getInventory();
            Bukkit.broadcastMessage("§aUn coffre a spawn en §ex: §c" + locChest.getBlockX() + " §ey: §c" + locChest.getBlockY() + " §ez: §c" + locChest.getBlockZ() + " §e! §aFoncez !");

            for (String key : main.getConfig().getConfigurationSection("chest.item").getKeys(false)) {//get config / Mettre items dans le coffre
                try {
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
        }
    }


    private Location getLocation(int xmax, int xmin, int zmax, int zmin, CommandSender cms) {
        double x, y, z;
        x = getCoord(xmax, xmin, cms);
        z = getCoord(zmax, zmin, cms);
        y = getHighestBlock(Bukkit.getWorld("world"), (int) x, (int) z);
        Location locChest;
        Location locBlock = new Location(Bukkit.getWorld("world"), x, y, z);
        cms.sendMessage("pass");
        cms.sendMessage(String.valueOf(locBlock.getBlock().getType()));
        while (locBlock.getBlock().getType() == Material.STATIONARY_WATER || locBlock.getBlock().getType() == Material.STATIONARY_LAVA) { //check que ce soit pas de l'eau ou de la lave
            x = getCoord(xmax += 10, xmin += 10, cms);
            z = getCoord(zmax += 10, zmin += 10, cms);
            locBlock = new Location(Bukkit.getWorld("world"), x, y - 1, z);
            y = getHighestBlock(Bukkit.getWorld("world"), (int) x, (int) z);
        }

        if (locBlock.getBlock().getType() == Material.LONG_GRASS
                || locBlock.getBlock().getType() == Material.RED_ROSE
                || locBlock.getBlock().getType() == Material.YELLOW_FLOWER){
            x = locBlock.getBlockX();
            y = locBlock.getBlockY();
            z = locBlock.getBlockZ();

            locChest = new Location(Bukkit.getWorld("world"), x, y, z);
            return locChest;
        }

        if (locBlock.getBlock().getType() == Material.DOUBLE_PLANT){
            x = locBlock.getBlockX();
            y = locBlock.getBlockY();
            z = locBlock.getBlockZ();

            locChest = new Location(Bukkit.getWorld("world"), x, y - 1, z);
            return locChest;
        }

        locChest = new Location(Bukkit.getWorld("world"), x, y + 1, z);

        if (xmax < xmin || zmax < zmin) {
            cms.sendMessage("§eXmax et Ymax doivent être plus grands que Xmin et Ymin !");
            return null;
        }

        return locChest;
    }

    public int getCoord(int max, int min, CommandSender cms) {
        int x;
        int between;
        int rand;
        if (!isEquals(max, min)) {
            if (isHighter(max, min)) {
                if (max >= 0 && min >= 0) {    //101 101
                    between = (max) - (min);
                    rand = (int) getRandom(between);
                    x = rand + min;
                    return x;
                } else if (max <= 0 && min <= 0) { //-100  -300
                    between = max - min;
                    rand = (int) getRandom(between);   //10
                    x = max - rand;
                    return x;
                } else if (max >= 0 && min <= 0) { // 1000   -200
                    between = max + Math.abs(min);
                    rand = (int) getRandom(between); //1100
                    x = min + rand;
                    return x;
                }
            }
        } else if (isEquals(max, min)) {
            x = max;
            return x;
        }
        return 0;
    }

    public boolean isHighter(int a, int b) {
        return a > b;
    }

    public boolean isEquals(int a, int b) {
        return a == b;
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
