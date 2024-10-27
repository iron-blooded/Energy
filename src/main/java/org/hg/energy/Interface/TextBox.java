package org.hg.energy.Interface;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Function;

import static org.bukkit.ChatColor.*;

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
        player.sendMessage(GOLD + "" + ChatColor.BOLD + "Введите в чат новое значение");
        TextComponent textComponent = new TextComponent(WHITE + "Для отмены напишите слово " + BOLD + "cancel");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "cancel"));
        player.sendMessage(textComponent);
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "(Введенное значение не будет отправлено в чат)");
        data.getPlugin().textBoxMap.put(player, this);
    }

    public boolean useFunction(String value) {
        try {
            if (value.equals("cancel")) {
                data.getPlugin().textBoxMap.remove(player);
                player.sendMessage(RED + "Ввод значения отменен");
                return true;
            }
            return function.apply(value);
        } catch (Exception ignored) {
        }
        return false;
    }

    public _ShareData getData() {
        return data;
    }
}
