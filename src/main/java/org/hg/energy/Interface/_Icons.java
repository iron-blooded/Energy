package org.hg.energy.Interface;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Structure;

import java.util.List;
import java.util.function.Function;

import static org.bukkit.ChatColor.*;

public enum _Icons {
    ХранилищеЭнергии(
            Material.BARREL,
            BLUE + "Хранилище энергии",
            WHITE + "Хранит энергию, и больше ничего"
    ),
    Генератор(
            Material.IRON_BLOCK,
            GOLD + "Генератор",
            WHITE + "Генерирует энергию, потребляя ресурсы"
    ),
    Конвертер(
            Material.BEACON,
            AQUA + "Конвертер",
            WHITE + """
                    Так же можно назвать трансформатором.
                    Преобразует энергию одной сети в энергию другой сети
                    с заданным коофициентом.
                    """
    ),
    Фабрикатор(
            Material.SMOKER,
            LIGHT_PURPLE + "Фабрикатор",
            WHITE + """
                    Производит из одних ресурсов другие
                    потребляя при этом энергию.
                    """
    ),
    ИзменитьИмяСтруктуры(
            Material.BIRCH_SIGN,
            WHITE + "Изменить имя структуры",
            "Текущее имя: {}"
    ),
    ВызватьРаботуСтруктуры(
            Material.CHAIN_COMMAND_BLOCK,
            WHITE + "Вызвать работу структуры",
            GRAY + "" + ITALIC + "Не дожидаясь кулдаунов и не проверяя, хватает ли энергии"
                    + RESET + WHITE + """
                    Техническая функция
                    Создана для ивентеров
                    """

    ),
    ШансРаботыСтруктуры(
            Material.COMPARATOR,
            WHITE + "Задать шанс, с которым структура выполнит работу",
            GRAY + "" + ITALIC + "(Если работа у структуры вообще есть)" +
                    RESET + GREEN + "Текущий шанс: {}%"
    ),
    ИспользоатьШансРаботыСтруктуры(
            Material.COMMAND_BLOCK,
            RED + "Пробросить шанс, что структура сработала",
            WHITE + """
                    Техническая функция
                    Создана для ивентеров
                    """
    ),
    КулдаунРаботыСтруктуры(
            Material.CLOCK,
            DARK_AQUA + "Задать кулдаун структуры",
            YELLOW + "Текущий кулдаун: {} сек." + RESET +
                    GOLD + "Осталось до срабатывания структуры: {} сек."
    ),
    ЗадатьКулдаун(
            Material.COMMAND_BLOCK,
            RED + "Задать время (кулдаун) до срабатывания структуры",
            WHITE + """
                    Техническая функция
                    Создана для ивентеров
                    """
    ),
    ЗадатьБлокиСтруктуры(
            Material.BLAZE_ROD,
            GOLD + "Задать блоки, которым соответсвует структура",
            WHITE + "Блоки не должны быть слишком далеко друг от друга"
    ),
    ПрисоеденитьСтруктуру(
            Material.STRING,
            AQUA + "Присоеденить структуру к сети",
            GRAY + "" + ITALIC + "(Только к одной)" +
                    RESET + WHITE + """
                    Текущая сеть: {}
                    Энергия сети: {}
                    """

    ),
    ЗадатьКоличествоЭнергии(
            Material.BARREL,
            GREEN + "Задать хранимое количесвто энергии",
            WHITE + "Установка объема, который может хранить структура" +
                    RESET + AQUA + "Текущий объем: {}"

    ),
    УдалитьСтруктуру(
            Material.BARREL,
            RED + "Удалить структуру",
            GOLD + """
                    Внимание! Действие произойдет
                    без повторного подтверждения!
                    """

    ),
    ЗадатьРасход(
            Material.BLAST_FURNACE,
            DARK_BLUE + "Задать кол-во энергии забираемое конвертером",
            GRAY + "" + ITALIC + """
                    Количество энергии, которое за раз
                    забирает конвертер из родительской сети
                    """ + RESET + WHITE + "Текущее количество: {}"
    ),
    ВыходнаяСеть(
            Material.NETHER_STAR,
            GOLD + "Задать выходную сеть",
            WHITE + """
                    Текущая выходная сеть: {}
                    Энергия в выходной сети: {}
                    """
    ),
    КоофициентКонвертации(
            Material.DAYLIGHT_DETECTOR,
            DARK_AQUA + "Задать коофициент конвертации",
            GRAY + "" + ITALIC + """
                    Например, 0.94 = каждая еденица энергии на входе
                    преобразуется в 0.94 едениц энергии на выходе
                    """ + RESET + WHITE + "Текущий коофициент: {}"
    ),
    ДистаницяМатериал(
            Material.OBSERVER,
            GREEN + "Текущий коофициент: {}",
            AQUA + """
                    Задает дистанцию, на которой
                    генератор будет брать ресурсы.""" + RESET +
                    GRAY + ITALIC + "Не более 16 блоков" + RESET +
                    RED + "Текущее значение: {}"
    ),
    СписокПотребляемыхРесурсов(
            Material.HOPPER,
            GOLD + "Задать список потребляемых предметов",
            ""
    ),
    СписокПроизводимыхПредметов(
            Material.DROPPER,
            RED + "Задать список производимых предметов",
            ""
    ),
    ЦенаПроизводства(
            Material.BLAST_FURNACE,
            DARK_AQUA + "Задать цену производства",
            GRAY + "" + ITALIC + """
                    Количество энергии, которое при производстве
                    потребляет фабрикатор из родительской сети""" + RESET +
                    WHITE + "Текущее количество: {}"
    ),
    ЛогикаПроизводства(
            Material.KNOWLEDGE_BOOK,
            LIGHT_PURPLE + "Задать логику производства",
            GRAY + "" + ITALIC + """
                    Устанавливает, по какой логике
                    фабрикатор должен производить предметы""" + RESET +
                    WHITE + "Задано: {}"
    ),
    ИмяСети(
            Material.BIRCH_SIGN,
            WHITE + "Изменить имя сети",
            GOLD + "Текущее имя: {}"
    ),
    ИмяЭнергии(
            Material.BIRCH_SIGN,
            WHITE + "Изменить имя энергии",
            GRAY + "" + ITALIC + """
                    Название энергии, которая
                    используется в данной сети""" + RESET +
                    GOLD + "Текущее название: {}"
    ),
    СписокСтруктур(
            Material.BARREL,
            WHITE + "Показать список структур в сети",
            """
                    На каждый внутри можно нажать
                    и провалиться в меню настройки"""
    ),
    КоличествоХранимойЭнергии(
            Material.REDSTONE,
            RED + "Задать количество хранимой энергии",
            WHITE + """
                    На момент входа в настройки
                    сеть хранит: {} {}"""
    ),
    МаксимальноеКоличествоЭнергии(
            Material.GUNPOWDER,
            AQUA + "Максимальное кол-во энергии",
            WHITE + """
                    Максимум энергии, которое
                    может вместить в себя сеть
                    составляет: {} {}"""
    ),
    ДобавитьЭнергию(
            Material.CLAY_BALL,
            WHITE + "Добавить энергию в сеть",
            GRAY + "" + ITALIC + "Задавать можно и отрицательные числа"
    ),
    ВыключитьСеть(
            Material.GREEN_CONCRETE,
            GREEN + "Сеть включена",
            WHITE + "Нажмите, что бы выключить." +
                    GRAY + ITALIC + """
                    При отключенной сети, привязанные
                    структуры перестанут обновляться (работать)"""
    ),
    ВключитьСеть(
            Material.RED_CONCRETE,
            GREEN + "Сеть выключена",
            WHITE + "Нажмите, что бы включить." +
                    GRAY + ITALIC + """
                    При отключенной сети, привязанные
                    структуры перестанут обновляться (работать)"""
    );

    private ItemStack item;
    private String lore;
    private String name;
    private Function<Object, Void> function = o -> {return null;};

    _Icons(Material material, String name, String lore) {
        this(material, name, lore, o -> {return null;});
    }

    _Icons(Material material, String name, String lore, Function<Object, Void> function) {
        this.item = new ItemStack(material);
        this.lore = lore;
        this.name = name;
        this.function = function;
    }

    /**
     * Калькулятор для вычисления места иконки
     *
     * @param line   линия
     * @param column колонка
     * @return число для setItem
     */
    public static int calculate(int line, int column) {
        line--;
        column--;
        return (9 * line) + column;
    }

    /**
     * Использовать заложенную в иконку функцию
     *
     * @param mesh_or_structure или сеть, или структура, которая будет передана как параметр в функцию иконки
     */
    public void use(Object mesh_or_structure) {
        if (mesh_or_structure instanceof Mesh || mesh_or_structure instanceof Structure) {
            function.apply(mesh_or_structure);
        }
    }

    public ItemStack getItem() {
        return this.getItem("empty", "empty");
    }

    public ItemStack getItem(String arg1) {
        return this.getItem(arg1, "empty");
    }

    public ItemStack getItem(String arg1, String arg2) {
        lore = lore.replaceFirst("[{][}]", arg1);
        lore = lore.replaceFirst("[{][}]", arg2);
        ItemStack item = this.item.clone();
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(List.of(lore.split("\n")));
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return item;
    }
}