package net.oldschoolminecraft.cs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
            selectedBlocks.put(event.getPlayer(), event.getClickedBlock());
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
}
