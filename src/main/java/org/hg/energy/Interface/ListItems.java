package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.hg.energy.Interface._Icons.calculate;

public class ListItems implements InventoryHolder, Pagination, Window {
    Supplier<List<Double>> f_get_numbers_for_items;
    private int page = 0;
    private _ShareData data;
    private Supplier<List<ItemStack>> f_get_items;
    private Consumer<ItemStack> f_add_item;
    private Consumer<ItemStack> f_remove_item;
    private ItemStack item_delete = null;
    private BiConsumer<ItemStack, Double> f_set_number;

    public ListItems(
            _ShareData data,
            Supplier<List<ItemStack>> f_get_items,
            Supplier<List<Double>> f_get_numbers_for_items,
            BiConsumer<ItemStack, Double> f_set_number,
            Consumer<ItemStack> f_add_item,
            Consumer<ItemStack> f_remove_item
                    ) {
        this.data = data;
        this.f_get_items = f_get_items;
        this.f_add_item = f_add_item;
        this.f_remove_item = f_remove_item;
        this.f_set_number = f_set_number;
        this.f_get_numbers_for_items = f_get_numbers_for_items;
    }


    public ListItems(_ShareData data,
                     Supplier<List<ItemStack>> f_get_items,
                     Consumer<ItemStack> f_add_item,
                     Consumer<ItemStack> f_remove_item
                    ) {
        this(
                data,
                f_get_items,
                () -> null,
                (itemStack, aDouble) -> {},
                f_add_item,
                f_remove_item
            );
    }

    private static ItemStack setInsertNumber(ItemStack itemStack, int num) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        setInsertNumber(itemMeta, num);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private static void setInsertNumber(ItemMeta itemMeta, int num) {
        if (itemMeta == null) {
            return;
        }
        itemMeta.getPersistentDataContainer().set(
                new NamespacedKey("energy", "item_pos"), PersistentDataType.INTEGER, num);
    }

    public ItemStack getInsertedItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return null;
        }
        int num = -1;
        try {
            num = itemStack.getItemMeta().getPersistentDataContainer().get(
                    new NamespacedKey("energy", "item_pos"),
                    PersistentDataType.INTEGER
                                                                          );
        } catch (Exception ignored) {
            return null;
        }
        return this.f_get_items.get().get(num);
    }

    @Override
    public @NotNull Inventory getInventory() {
        List<ItemStack> list_items = f_get_items.get();
        list_items.replaceAll(itemStack -> itemStack.isSimilar(this.item_delete) ? _Icons.УдалитьПредмет.getItem("") : itemStack);
        int max_elements = 9 * 5;
        if (this.f_get_numbers_for_items != null && this.f_get_numbers_for_items.get() != null) {
            max_elements /= 3;
        }
        if (this.page < 0 || this.page > list_items.size() / max_elements) {
            this.page = 0;
        }
        Inventory inventory = Bukkit.createInventory(
                this, 9 * 6, ChatColor.GOLD + "Список предметов (" + (this.page + 1) + ")");
        if (!list_items.isEmpty()) {
            list_items = list_items.subList(Math.min(page * max_elements, list_items.size() - 1), Math.min(
                    (page + 1) * max_elements, list_items.size()));
        }
        for (int slot = 0; slot < max_elements && slot < list_items.size(); slot++) {
            ItemStack item = list_items.get(slot);
            if (this.f_get_numbers_for_items != null && this.f_get_numbers_for_items.get() != null) {
                inventory.setItem(((slot / 9)) * 9 + slot, item);
                inventory.setItem(
                        ((slot / 9)) * 9 + slot + 9,
                        setInsertNumber(
                                _Icons.ЗадатьЗначениеПредмету.getItem(String.valueOf(f_get_numbers_for_items.get().get(slot))),
                                slot
                                       )
                                 );
            } else {
                inventory.setItem(slot, item);
            }
        }
        inventory.setItem(calculate(6, 6), _Icons.СтраницаВперед.getItem("", ""));
        if (this.f_add_item != null) {
            inventory.setItem(calculate(6, 5), _Icons.ДобавитьПредмет.getItem("", ""));
        }
        inventory.setItem(calculate(6, 4), _Icons.СтраницаНазад.getItem("", ""));
        return inventory;
    }

    public void addItem(ItemStack item) {
        this.f_add_item.accept(item);
    }

    public void clickItem(ItemStack item) {
        this.item_delete = item.clone();
    }

    public void deleteItem() {
        this.f_remove_item.accept(this.item_delete);
    }

    public void setNumber(ItemStack itemStack, Double oDouble) {
        this.f_set_number.accept(itemStack, oDouble);
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public _ShareData getObject() {
        return data;
    }

}
