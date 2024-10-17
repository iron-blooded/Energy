package org.hg.energy.Interface;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class TextBox {
    private final Function<String, Boolean> function;
    private _ShareData data;
    private Player player;

    public TextBox(_ShareData data, Function<String, Boolean> function) {
        this.data = data;
        this.function = function;
        this.player = data.getPlayer();
    }

    public void apply() {
        player.closeInventory();
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Введите в чат значение");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "(введенное значение не будет отправлено в чат");
        data.getPlugin().textBoxMap.put(player, this);
    }

    public boolean useFunction(String value) {
        try {
            return function.apply(value);
        } catch (Exception ignored) {}
        return false;
    }

    public _ShareData getData() {
        return data;
    }
}
