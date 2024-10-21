package org.hg.energy.Interface;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.bukkit.ChatColor.*;

public enum _Icons {
    empty(Material.AIR, "", ""),
    ХранилищеЭнергии(
            Material.BARREL,
            BLUE + "Хранилище энергии",
            WHITE + "Хранит энергию, и больше ничего\n" +
                    WHITE + "{}",
            (shareData) -> {
                if (shareData.getLocation() != null) {
                    shareData.setStructure(new Container(
                            "fumo",
                            Collections.singletonList(shareData.getLocation())
                    ));
                    return new SettingsStructure(shareData).getInventory();
                } else if (shareData.getStructure() != null) {
                    return new SettingsStructure(shareData).getInventory();
                }
                return null;
            }
    ),
    Генератор(
            Material.IRON_BLOCK,
            GOLD + "Генератор" + BLACK + "Бензиновый Генератор",
            WHITE + "Генерирует энергию, потребляя ресурсы\n" +
                    WHITE + "{}",
            (shareData) -> {
                if (shareData.getLocation() != null) {
                    shareData.setStructure(new Generator(
                            "fumo",
                            Collections.singletonList(shareData.getLocation())
                    ));
                    return new SettingsStructure(shareData).getInventory();
                } else if (shareData.getStructure() != null) {
                    return new SettingsStructure(shareData).getInventory();
                }
                return null;
            }
    ),
    Конвертер(
            Material.BEACON,
            AQUA + "Конвертер",
            WHITE + "Так же можно назвать трансформатором.\n" +
                    WHITE + "Преобразует энергию одной сети в энергию другой сети\n" +
                    WHITE + "с заданным коофициентом.\n" +
                    WHITE + "{}",
            (shareData) -> {
                if (shareData.getLocation() != null) {
                    shareData.setStructure(new Converter(
                            "fumo",
                            Collections.singletonList(shareData.getLocation())
                    ));
                    return new SettingsStructure(shareData).getInventory();
                } else if (shareData.getStructure() != null) {
                    return new SettingsStructure(shareData).getInventory();
                }
                return null;
            }
    ),
    Фабрикатор(
            Material.SMOKER,
            LIGHT_PURPLE + "Фабрикатор",
            WHITE + "Производит из одних ресурсов другие\n" +
                    WHITE + "потребляя при этом энергию.\n" +
                    WHITE + "{}",
            (shareData) -> {
                if (shareData.getLocation() != null) {
                    shareData.setStructure(new Fabrication(
                            "fumo",
                            Collections.singletonList(shareData.getLocation())
                    ));
                    return new SettingsStructure(shareData).getInventory();
                } else if (shareData.getStructure() != null) {
                    return new SettingsStructure(shareData).getInventory();
                }
                return null;
            }
    ),
    ИзменитьИмяСтруктуры(
            Material.BIRCH_SIGN,
            WHITE + "Изменить имя структуры",
            GOLD + "Текущее имя: {}",
            shareData -> {
                if (shareData.getStructure() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getStructure().setName(string);
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ВызватьРаботуСтруктуры(
            Material.CHAIN_COMMAND_BLOCK,
            WHITE + "Вызвать работу структуры",
            GRAY + "" + ITALIC + "Не дожидаясь кулдаунов и не проверяя, хватает ли энергии\n"
                    + WHITE + "Техническая функция\n"
                    + WHITE + "Создана для ивентеров\n",
            shareData -> {
                if (shareData.getStructure() != null) {
                    shareData.getStructure().work();
                    return new SettingsStructure(shareData).getInventory();
                }
                return null;
            }
    ),
    ШансРаботыСтруктуры(
            Material.COMPARATOR,
            WHITE + "Задать шанс, с которым структура выполнит работу",
            GRAY + "" + ITALIC + "(Если работа у структуры вообще есть)\n" +
                    GREEN + "Текущий шанс: {}%",
            shareData -> {
                if (shareData.getStructure() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getStructure().setChanceWork(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    КулдаунРаботыСтруктуры(
            Material.CLOCK,
            DARK_AQUA + "Задать кулдаун структуры",
            YELLOW + "Текущий кулдаун: {} сек.\n" +
                    GOLD + "Осталось до срабатывания структуры: {} сек.",
            shareData -> {
                if (shareData.getStructure() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getStructure().setCooldownRequired(Integer.parseInt(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ЗадатьКулдаун(
            Material.COMMAND_BLOCK,
            RED + "Задать время (кулдаун) до срабатывания структуры",
            WHITE + "Техническая функция\n" +
                    WHITE + "Создана для ивентеров",
            shareData -> {
                if (shareData.getStructure() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getStructure().setCooldown(Integer.parseInt(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ЗадатьБлокиСтруктуры(
            Material.BLAZE_ROD,
            GOLD + "Задать блоки, которым соответсвует структура",
            WHITE + "Блоки не должны быть слишком далеко друг от друга"
            //TODO: выбор игроком блоков, которым соответствует структура
    ),
    ПрисоеденитьСетькКСтруктуре(
            Material.STRING,
            AQUA + "Присоединить к структуре сеть",
            GRAY + "" + ITALIC + "(Только одну)\n" +
                    WHITE + "Текущая сеть: {}\n"
                    + WHITE + "Энергия сети: {}",
            shareData -> {
                if (shareData.getStructure() != null) {
                    return new ListMeshes(shareData).getInventory();
                }
                return null;
            }
    ),
    СтраницаНазад(
            getFlag(true),
            AQUA + "На страницу назад",
            "",
            shareData -> {
                if (shareData.getHolder() instanceof Pagination pagination) {
                    pagination.setPage(pagination.getPage() - 1);
                    return shareData.getHolder().getInventory();
                }
                return null;
            }
    ),
    СтраницаВперед(
            getFlag(false),
            AQUA + "На страницу вперед",
            "",
            shareData -> {
                if (shareData.getHolder() instanceof Pagination pagination) {
                    pagination.setPage(pagination.getPage() + 1);
                    return shareData.getHolder().getInventory();
                }
                return null;
            }
    ),
    ЗадатьКоличествоЭнергии(
            Material.BARREL,
            GREEN + "Задать хранимое количество энергии",
            WHITE + "Установка объема, который может хранить структура\n" +
                    AQUA + "Текущий объем: {} {}",
            shareData -> {
                if (shareData.getStructure() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getStructure().setVolume(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }

    ),
    УдалитьСтруктуру(
            Material.BARRIER,
            RED + "Удалить структуру",
            GOLD + "Внимание! Действие произойдет\n" +
                    GOLD + "без повторного подтверждения!",
            shareData -> {
                if (shareData.getStructure() != null) {
                    Mesh mesh = shareData.getMesh();
                    if (mesh == null) {
                        mesh = shareData.getStructure().getMesh();
                    }
                    shareData.getStructure().disconnectToMesh();
                    if (mesh != null) {
                        return new SettingsMesh(
                                new _ShareData(mesh, null, null, shareData.getPlugin())
                        ).getInventory();
                    }
                }
                return null;
            }
    ),
    УдалитьСеть(
            Material.BARRIER,
            RED + "Удалить сеть",
            GOLD + "Внимание! Действие произойдет\n" +
                    GOLD + "без повторного подтверждения!\n" +
                    RED + "Это приведет к удалению всех\n" +
                    RED + "подключенных элементов!",
            shareData -> {
                if (shareData.getMesh() != null) {
                    shareData.getPlugin().meshes.remove(shareData.getMesh());
                    return new ListMeshes(
                            new _ShareData(null, null, null, shareData.getPlugin())
                    ).getInventory();
                }
                return null;
            }

    ),
    ЗадатьРасход(
            Material.BLAST_FURNACE,
            DARK_BLUE + "Задать кол-во энергии забираемое конвертером",
            GRAY + "" + ITALIC + "Количество энергии, которое за раз\n" +
                    GRAY + ITALIC + "забирает конвертер из родительской сети\n" +
                    WHITE + "Текущее количество: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Converter converter) {
                    new TextBox(shareData, string -> {
                        try {
                            converter.setAmount(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ВыходнаяСеть(
            Material.NETHER_STAR,
            GOLD + "Задать выходную сеть",
            WHITE + "Текущая выходная сеть: {}\n" +
                    WHITE + "Энергия в выходной сети: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Converter converter) {
                    shareData.setBoolean(true);
                    return new ListMeshes(shareData).getInventory();
                }
                return null;
            }
    ),
    КоофициентКонвертации(
            Material.DAYLIGHT_DETECTOR,
            DARK_AQUA + "Задать коофициент конвертации",
            GRAY + "" + ITALIC + "Например, 0.94 = каждая еденица энергии на входе\n" +
                    GRAY + ITALIC + "преобразуется в 0.94 едениц энергии на выходе\n"
                    + RESET + WHITE + "Текущий коофициент: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Converter converter) {
                    new TextBox(shareData, string -> {
                        try {
                            converter.setCoefficient(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ДистаницяМатериал(
            Material.OBSERVER,
            GREEN + "Задать радиус ресурса",
            AQUA + "Задает дистанцию, на которой\n" +
                    AQUA + "cтруктура будет брать ресурсы.\n" +
                    GRAY + ITALIC + "Не более 16 блоков\n" + RESET +
                    RED + "Текущее значение: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Generator generator) {
                    new TextBox(shareData, string -> {
                        try {
                            generator.setDistanceMaterial(Integer.parseInt(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                } else if (shareData.getStructure() instanceof Fabrication fabrication) {
                    new TextBox(shareData, string -> {
                        try {
                            fabrication.setDistanceMaterial(Integer.parseInt(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    КоличествоЭнергииНаВыходе(
            Material.LIGHT,
            GOLD + "Задать количество производимой энергии",
            WHITE + "Текущее значение: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Generator generator) {
                    new TextBox(shareData, string -> {
                        try {
                            generator.setAmountEnergyProduced(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    СписокПотребляемыхРесурсов(
            Material.HOPPER,
            GOLD + "Задать список потребляемых предметов",
            "",
            shareData -> {
                if (shareData.getStructure() instanceof Generator generator) {
                    return new ListItems(
                            shareData,
                            generator::getMaterials,
                            generator::addMaterial,
                            generator::removeMaterial
                    ).getInventory();
                } else if (shareData.getStructure() instanceof Fabrication fabrication) {
                    return new ListItems(
                            shareData,
                            fabrication::getMaterials,
                            fabrication::addMaterial,
                            fabrication::removeMaterial
                    ).getInventory();
                }
                return null;
            }
    ),
    СписокПроизводимыхПредметов(
            Material.DROPPER,
            RED + "Задать список производимых предметов",
            "",
            shareData -> {
                if (shareData.getStructure() instanceof Fabrication fabrication) {
                    return new ListItems(
                            shareData,
                            () -> new ArrayList<>(fabrication.getProducts().keySet()),
                            () -> new ArrayList<>(fabrication.getProducts().values()),
                            fabrication::addProduct,
                            itemStack -> fabrication.addProduct(itemStack, 0),
                            fabrication::removeProduct
                    ).getInventory();
                }
                return null;
            }
    ),
    ЦенаПроизводства(
            Material.BLAST_FURNACE,
            DARK_AQUA + "Задать цену производства",
            GRAY + "" + ITALIC + "Количество энергии, которое при производстве\n" +
                    GRAY + ITALIC + "потребляет фабрикатор из родительской сети\n" +
                    WHITE + "Текущее количество: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Fabrication fabrication) {
                    new TextBox(shareData, string -> {
                        try {
                            fabrication.setPrice(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ЛогикаПроизводства(
            Material.KNOWLEDGE_BOOK,
            LIGHT_PURPLE + "Задать логику производства",
            GRAY + "" + ITALIC + "Устанавливает, по какой логике\n" +
                    GRAY + ITALIC + "фабрикатор должен производить предметы\n" +
                    WHITE + "Задано: {}",
            shareData -> {
                if (shareData.getStructure() instanceof Fabrication fabricator) {
                    List<MultiProduct> list = List.of(MultiProduct.values());
                    int pos = list.indexOf(fabricator.getMultiProduct());
                    pos++;
                    if (pos > list.size() - 1) {
                        pos = 0;
                    }
                    fabricator.setMultiProduct(list.get(pos));
                    return new SettingsStructure(shareData).getInventory();
                }
                return null;
            }
    ),
    ИмяСети(
            Material.BIRCH_SIGN,
            WHITE + "Изменить имя сети",
            GOLD + "Текущее имя: {}",
            shareData -> {
                if (shareData.getMesh() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getMesh().setDisplayName(string);
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ИмяЭнергии(
            Material.BIRCH_SIGN,
            WHITE + "Изменить имя энергии",
            GRAY + "" + ITALIC + "Название энергии, которая\n" +
                    GRAY + ITALIC + "используется в данной сети\n" + RESET +
                    GOLD + "Текущее название: {}",
            shareData -> {
                if (shareData.getMesh() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getMesh().setEnergyName(string);
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    СписокСтруктур(
            Material.BARREL,
            WHITE + "Показать список структур в сети",
            "",
            shareData -> {
                if (shareData.getMesh() != null) {
                    return new ListItems(
                            new _ShareData(shareData.getMesh(), null, null, shareData.getPlugin()),
                            () -> {
                                List<ItemStack> items = new ArrayList<>();
                                for (Structure structure : shareData.getMesh().getStructures()) {
                                    if (structure instanceof Container container) {
                                        items.add(_Icons.ХранилищеЭнергии.getItem(
                                                "Имя: " + structure.getName(), "", structure.getUuid()));
                                    } else if (structure instanceof Converter converter) {
                                        items.add(_Icons.Конвертер.getItem(
                                                "Имя: " + structure.getName(), "", structure.getUuid()));
                                    } else if (structure instanceof Fabrication fabrication) {
                                        items.add(_Icons.Фабрикатор.getItem(
                                                "Имя: " + structure.getName(), "", structure.getUuid()));
                                    } else if (structure instanceof Generator generator) {
                                        items.add(_Icons.Генератор.getItem(
                                                "Имя: " + structure.getName(), "", structure.getUuid()));
                                    }
                                }
                                return items;
                            },
                            itemStack -> {},
                            itemStack -> {
                                UUID uuid = getUUID(itemStack);
                                if (uuid != null) {
                                    shareData.getPlugin().getStructures().stream()
                                            .filter(structure -> structure.getUuid().equals(uuid))
                                            .findFirst()
                                            .ifPresent(Structure::disconnectToMesh);
                                }
                            }
                    ).getInventory();
                }
                return null;
            }
    ),
    КоличествоХранимойЭнергии(
            Material.REDSTONE,
            RED + "Задать количество хранимой энергии",
            WHITE + "На момент входа в настройки\n"
                    + WHITE + "сеть хранит: {} {}",
            shareData -> {
                if (shareData.getMesh() != null) {
                    new TextBox(shareData, string -> {
                        try {
                            shareData.getMesh().setEnergyCount(Double.parseDouble(string));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    МаксимальноеКоличествоЭнергии(
            Material.GUNPOWDER,
            AQUA + "Максимальное кол-во энергии",
            WHITE + "Максимум энергии, которое\n" +
                    WHITE + "может вместить в себя сеть\n"
                    + WHITE + "составляет: {} {}",
            shareData -> null
    ),
    ДобавитьЭнергию(
            Material.CLAY_BALL,
            WHITE + "Добавить энергию в сеть",
            GRAY + "" + ITALIC + "Задавать можно и отрицательные числа",
            shareData -> {
                if (shareData.getMesh() != null) {
                    Mesh mesh = shareData.getMesh();
                    new TextBox(shareData, string -> {
                        try {
                            mesh.setEnergyCount(Math.max(0, Math.min(
                                    mesh.getEnergyLimit(), mesh.getEnergyCount() + Double.parseDouble(string))));
                            return true;
                        } catch (Exception ignored) {
                        }
                        return false;
                    }).apply();
                }
                return null;
            }
    ),
    ВыключитьСеть(
            Material.GREEN_CONCRETE,
            GREEN + "Сеть включена",
            WHITE + "Нажмите, что бы выключить.\n" +
                    GRAY + ITALIC + "При отключенной сети, привязанные\n"
                    + GRAY + ITALIC + "структуры перестанут обновляться (работать)",
            shareData -> {
                if (shareData.getMesh() != null) {
                    shareData.getMesh().setEnabled(false);
                    return new SettingsMesh(shareData).getInventory();
                }
                return null;
            }
    ),
    ВключитьСеть(
            Material.RED_CONCRETE,
            GREEN + "Сеть выключена",
            WHITE + "Нажмите, что бы включить.\n" +
                    GRAY + ITALIC + "При отключенной сети, привязанные\n"
                    + GRAY + ITALIC + "структуры перестанут обновляться (работать)",
            shareData -> {
                if (shareData.getMesh() != null) {
                    shareData.getMesh().setEnabled(true);
                    return new SettingsMesh(shareData).getInventory();
                }
                return null;
            }
    ),
    ВыключитьСтруктуру(
            Material.GREEN_CONCRETE,
            GREEN + "Структура включена",
            WHITE + "Нажмите, что бы выключить.",
            shareData -> {
                if (shareData.getStructure() != null) {
                    shareData.getStructure().setEnabled(false);
                    return shareData.getHolder().getInventory();
                }
                return null;
            }
    ),
    ВключитьСтруктуру(
            Material.RED_CONCRETE,
            GREEN + "Структура выключена",
            WHITE + "Нажмите, что бы включить.",
            shareData -> {
                if (shareData.getStructure() != null) {
                    shareData.getStructure().setEnabled(true);
                    return shareData.getHolder().getInventory();
                }
                return null;
            }
    ),
    ИконкаСети(
            Material.NETHER_STAR,
            LIGHT_PURPLE + "Сеть",
            GOLD + "Имя: {}\n" +
                    GRAY + "Энергия: {}",
            shareData -> {
                if (shareData.getStructure() != null && shareData.getMesh() != null) {
                    if (shareData.isBoolean() && shareData.getStructure() instanceof Converter converter) {
                        converter.setOutputMesh(shareData.getMesh());
                    } else {
                        shareData.getMesh().addStructure(shareData.getStructure());
                    }
                    return new SettingsStructure(shareData).getInventory();
                } else if (shareData.getMesh() != null) {
                    return new SettingsMesh(shareData).getInventory();
                }
                return null;
            }
    ),
    СоздатьСеть(
            Material.COOKIE,
            GREEN + "Создать новую сеть",
            WHITE + "И привязать ее к структуре\n" +
                    GRAY + "(Если возможно)",
            shareData -> {
                Mesh mesh = new Mesh("new_mesh", "energy");
                if (shareData.getStructure() != null) {
                    if (shareData.isBoolean() && shareData.getStructure() instanceof Converter converter) {
                        converter.setOutputMesh(mesh);
                    } else {
                        mesh.addStructure(shareData.getStructure());
                    }
                }
                shareData.getPlugin().meshes.add(mesh);
                return new SettingsMesh(new _ShareData(mesh, null, null, shareData.getPlugin())).getInventory();
            }
    ),
    ДобавитьПредмет(
            Material.SUNFLOWER,
            GOLD + "Добавить",
            WHITE + "Кликните предметом, что бы\n" +
                    WHITE + "добавить предмет",
            shareData -> {
                if (shareData.getCursorItem() != null && shareData.getHolder() instanceof ListItems holder) {
                    holder.addItem(shareData.getCursorItem());
                    return holder.getInventory();
                }
                return null;
            }
    ),
    УдалитьПредмет(
            Material.BARRIER,
            RED + "Подтвердить удаление",
            RED + "Нажмите, если подтверждаете\n" +
                    RED + "удаление предмета",
            shareData -> {
                if (shareData.getHolder() instanceof ListItems holder) {
                    holder.deleteItem();
                    return holder.getInventory();
                }
                return null;
            }
    ),
    ЗадатьЗначениеПредмету(
            Material.STRUCTURE_VOID,
            WHITE + "Задать значение предмету",
            WHITE + "Нажмите, что бы задать\n" +
                    WHITE + "значение предмету.\n" +
                    GOLD + "Текущее значение: {}",
            shareData -> {
                if (shareData.getHolder() instanceof ListItems holder) {
                    new TextBox(shareData, string -> {
                        holder.setNumber(holder.getInsertedItem(shareData.getClickItem()), Double.parseDouble(string));
                        return true;
                    }).apply();
                }
                return null;
            }
    ),
    ЗапретитьРедактироватьИгрокам(
            Material.EMERALD_BLOCK,
            RED + "Запретить игрокам доступ",
            WHITE + "Запретить простым игрокам заходить\n" +
                    WHITE + "в интерфейс",
            shareData -> {
                if (shareData.getStructure() != null) {
                    shareData.getStructure().setCanPlayerEdit(false);
                    return shareData.getHolder().getInventory();
                }
                return null;
            }
    ),
    РазрешитьРедактироватьИгрокам(
            Material.REDSTONE_BLOCK,
            GREEN + "Разрешить игрокам доступ",
            WHITE + "Разрешить простым игрокам заходить\n" +
                    WHITE + "в интерфейс",
            shareData -> {
                if (shareData.getStructure() != null) {
                    shareData.getStructure().setCanPlayerEdit(true);
                    return shareData.getHolder().getInventory();
                }
                return null;
            }
    );

    private final ItemStack item;
    private final String name;
    private final Function<_ShareData, Inventory> function;
    private String lore;
    private UUID uuid;

    _Icons(Material material, String name, String lore) {
        this(material, name, lore, shareData -> null);
    }

    _Icons(Material material, String name, String lore, Function<_ShareData, Inventory> function) {
        this(new ItemStack(material), name, lore, function);
    }

    _Icons(ItemStack item, String name, String lore, Function<_ShareData, Inventory> function) {
        this.item = item.clone();
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
     * Проверить, является ли предмет выходцем из иконок
     */
    public static _Icons isSimilar(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return _Icons.empty;
        }
        return getOrdinal(item);
    }

    public static void setUUID(ItemMeta itemMeta, UUID uuid) {
        if (uuid == null || itemMeta == null) {
            return;
        }
        itemMeta.getPersistentDataContainer().set(new NamespacedKey("energy", "uuid"), PersistentDataType.STRING,
                                                  uuid.toString()
                                                 );
    }

    public static UUID getUUID(ItemStack itemStack) {
        if (itemStack != null && itemStack.getItemMeta() != null) {
            return getUUID(itemStack.getItemMeta());
        }
        return null;
    }

    private static UUID getUUID(ItemMeta itemMeta) {
        if (itemMeta == null || itemMeta.getPersistentDataContainer().isEmpty()) {
            return null;
        }
        String result = itemMeta.getPersistentDataContainer().get(
                new NamespacedKey("energy", "uuid"),
                PersistentDataType.STRING
                                                                 );
        if (result != null) {
            return UUID.fromString(result);
        }
        return UUID.randomUUID();
    }

    private static ItemStack getFlag(boolean left) {
        // Создаем ItemStack с типом GRAY_BANNER
        ItemStack flag = new ItemStack(Material.GRAY_BANNER);
        ItemMeta meta = flag.getItemMeta();
        BannerMeta bannerMeta = (BannerMeta) meta;
        if (left) {
            bannerMeta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.STRIPE_LEFT));
        } else {
            bannerMeta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.STRIPE_RIGHT));
        }
        bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.CURLY_BORDER));
        bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM));
        bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP));
        bannerMeta.addPattern(new Pattern(DyeColor.ORANGE, PatternType.STRIPE_MIDDLE));
        bannerMeta.addPattern(new Pattern(DyeColor.GRAY, PatternType.CURLY_BORDER));
        bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        flag.setItemMeta(bannerMeta);
        return flag;
    }

    private static void setOrdinal(ItemMeta itemMeta, int num) {
        if (itemMeta == null) {
            return;
        }
        itemMeta.getPersistentDataContainer().set(
                new NamespacedKey("energy", "ordinal"), PersistentDataType.INTEGER, num);
    }

    public static _Icons getOrdinal(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return _Icons.empty;
        }
        int num = 0;
        try {
            num = itemStack.getItemMeta().getPersistentDataContainer().get(
                    new NamespacedKey("energy", "ordinal"),
                    PersistentDataType.INTEGER
                                                                          );
        } catch (Exception ignored) {
        }
        return _Icons.values()[num];
    }

    public Material getMaterial() {
        return this.item.getType();
    }

    public String getName() {
        return name;
    }

    /**
     * Использовать заложенную в иконку функцию
     */
    public void use(Player player, _ShareData data) {
        if (data.getClickType() == ClickType.RIGHT) {
            switch (getOrdinal(data.getClickItem())) {
                case Генератор:
                case Фабрикатор:
                case Конвертер:
                case ХранилищеЭнергии:
                    player.teleport(
                            data.getStructure().getLocations().get(0),
                            PlayerTeleportEvent.TeleportCause.COMMAND
                                   );
                    player.openInventory(data.getHolder().getInventory());
                    Location location = data.getStructure().getLocations().get(0);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            "x: %.2f, y: %.2f, z: %.2f".formatted(location.getX(), location.getY(), location.getZ())));
                    return;
                default:
                    break;
            }
        }
        Inventory inventory = this.function.apply(data);
        if (inventory != null) {
            player.openInventory(inventory);
        }
    }

    public ItemStack getItem(String arg1) {
        return this.getItem(arg1, "{{empty2}}", null);
    }

    public ItemStack getItem(String arg1, String arg2) {
        return this.getItem(arg1, arg2, null);
    }

    public ItemStack getItem(String arg1, String arg2, UUID uuid) {
        String lore = this.lore.replaceFirst("[{][}]", arg1);
        String name = this.name.replaceFirst("[{][}]", arg1);
        lore = lore.replaceFirst("[{][}]", arg2);
        ItemStack item = this.item.clone();
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setLore(List.of(lore.split("\n")));
            itemMeta.setDisplayName(name);
            setUUID(itemMeta, uuid);
            setOrdinal(itemMeta, this.ordinal());
            item.setItemMeta(itemMeta);
        }
        return item;
    }
}