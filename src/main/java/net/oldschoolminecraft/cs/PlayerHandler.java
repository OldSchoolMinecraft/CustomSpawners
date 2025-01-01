package net.oldschoolminecraft.cs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class PlayerHandler extends PlayerListener
{
    private final HashMap<Player, Block> selectedBlocks = new HashMap<>();

    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
            event.getClickedBlock().getType() == Material.MOB_SPAWNER)
        {
            Block clicked = event.getClickedBlock();
            BlockState state = clicked.getState();
            if (!(state instanceof CreatureSpawner)) return; // don't allow selection of non-spawner blocks
            String creature = CustomSpawners.capitalCase(((CreatureSpawner)state).getCreatureType().toString());
            selectedBlocks.put(event.getPlayer(), clicked);
            event.getPlayer().sendMessage(ChatColor.GREEN + "You have selected a " + creature + " spawner!");
        }
    }

    public void onPlayerMove(PlayerMoveEvent event)
    {
        Block selectedBlock = selectedBlocks.get(event.getPlayer());
        if (selectedBlock == null) return; // no selected block
        if (event.getTo().distance(selectedBlock.getLocation()) > 25)
        {
            selectedBlocks.remove(event.getPlayer());
            event.getPlayer().sendMessage(ChatColor.RED + "Your spawner has been de-selected because you moved too far away!");
        }
    }

    public boolean hasBlockSelected(Player player)
    {
        return selectedBlocks.containsKey(player);
    }

    public Block getSelectedBlock(Player player)
    {
        return selectedBlocks.get(player);
    }

    public void deselectBlock(Player player)
    {
        selectedBlocks.remove(player);
    }
}
