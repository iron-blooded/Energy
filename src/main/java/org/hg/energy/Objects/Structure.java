package org.hg.energy.Objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.hg.energy.FunctionsTemperature;
import org.hg.energy.Mesh;
import org.hg.scorchingsun.process.editTemp.calculate;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.BinaryOperator;

import static org.hg.energy.Objects._LitBlocks.lit;

public abstract class Structure implements Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID uuid;
    private String name;
    private int cooldown_required;
    private int cooldown;
    private double chance;
    private double volume = 0;
    private int priority = 9494;
    private Map<Block, Material> locations = new HashMap<>();
    private Mesh mesh;
    private boolean enabled = true;
    private double chance_break = 0;
    private boolean can_player_edit = false;
    private int p_cooldown_required = 0;
    private int p_cooldown = 0;
    private SimpleEntry<Sound, Float> sound_success = new SimpleEntry<>(Sound.BLOCK_IRON_DOOR_OPEN, -1f);
    private SimpleEntry<Sound, Float> sound_error = new SimpleEntry<>(Sound.BLOCK_IRON_DOOR_OPEN, -1f);
    private calculate temperature = new calculate(0, Double::sum);
    private String good_job = "";

    /**
     * Представляет собой структуру
     *
     * @param name      имя структуры
     * @param locations каким блокам соответствует структура
     */
    public Structure(@NotNull String name, @NotNull List<Location> locations) {
        this.name = name;
        setLocations(locations);
        this.uuid = UUID.randomUUID();
        this.cooldown_required = 0;
        this.cooldown = 0;
        this.chance = 100;
    }

    /**
     * Стандартная для структуры серелизация
     */
    public void defaultSerialize(java.io.ObjectOutputStream stream)
    throws IOException {
        stream.writeObject(uuid);
        stream.writeUTF(name);
        stream.writeInt(cooldown_required);
        stream.writeInt(cooldown);
        stream.writeDouble(chance);
        stream.writeDouble(volume);
        stream.writeInt(priority);

        stream.writeInt(locations.size());
        for (Map.Entry<Block, Material> entry : locations.entrySet()) {
            stream.writeUTF(new Gson().toJson(entry.getKey().getLocation().serialize()));
            stream.writeObject(entry.getValue());
        }
        stream.writeObject(mesh);
        stream.writeBoolean(enabled);
        stream.writeDouble(chance_break);
        stream.writeBoolean(can_player_edit);
        stream.writeChar('0');
        stream.writeInt(3); //Версия базы данных
        // v3
        stream.writeUTF(getGood_job());
        // v2
        stream.writeUTF(
                Arrays.stream(FunctionsTemperature.values())
                        .filter(functionsTemperature -> functionsTemperature.getOperator().equals(getTemperature().getMath()))
                        .findFirst()
                        .map(FunctionsTemperature::name)
                        .orElse("")
                       );
        stream.writeDouble(getTemperature().getNumber());
        // v1
        stream.writeUTF(getSound_success().getKey().name());
        stream.writeFloat(getSound_success().getValue());
        stream.writeUTF(getSound_error().getKey().name());
        stream.writeFloat(getSound_error().getValue());
        // v0
        stream.writeInt(p_cooldown);
        stream.writeInt(p_cooldown_required);
        stream.writeChar(ChatColor.COLOR_CHAR); // окончание дефолтной структуры
    }

    /**
     * Стандартная для структуры десерелизация
     */
    public void defaultSerialize(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException {
        uuid = (UUID) stream.readObject();
        name = stream.readUTF();
        cooldown_required = stream.readInt();
        cooldown = stream.readInt();
        chance = stream.readDouble();
        volume = stream.readDouble();
        priority = stream.readInt();
        int size = stream.readInt();
        locations = new HashMap<>();
        for (int i = 0; i < size; i++) {
            locations.put(
                    deserilazeLocation(new Gson().fromJson(
                            stream.readUTF(),
                            new TypeToken<Map<String, Object>>() {}.getType()
                                                          )).getBlock(), (Material) stream.readObject()
                         );
        }
        mesh = (Mesh) stream.readObject();
        enabled = stream.readBoolean();
        chance_break = stream.readDouble();
        can_player_edit = stream.readBoolean();
        try {
            switch (stream.readChar()) {
                case '0':
                    int version = stream.readInt();
                    switch (version) {
                        case 3:
                            good_job = stream.readUTF();
                        case 2:
                            BinaryOperator<Double> operator = Double::sum;
                            String op = stream.readUTF();
                            for (FunctionsTemperature o : FunctionsTemperature.values()) {
                                if (o.name().equals(op)) {
                                    operator = o.getOperator();
                                }
                            }
                            temperature = new calculate(
                                    stream.readDouble(),
                                    operator
                            );
                        case 1:
                            sound_success = new SimpleEntry<>(Sound.valueOf(stream.readUTF()), stream.readFloat());
                            sound_error = new SimpleEntry<>(Sound.valueOf(stream.readUTF()), stream.readFloat());
                        case 0:
                            p_cooldown = stream.readInt();
                            p_cooldown_required = stream.readInt();
                            break;
                    }
                    break;
                case ChatColor.COLOR_CHAR:
                default:
                    return;
            }
            while (stream.readChar() != ChatColor.COLOR_CHAR) {
                continue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Серелизация
     */
    @Serial
    private void writeObject(java.io.ObjectOutputStream stream)
    throws IOException {
        this.defaultSerialize(stream);
    }

    /**
     * Десерелизация
     */
    @Serial
    private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException {
        this.defaultSerialize(stream);
    }


    /**
     * Получить серверную команду, которую вызывает структура при успешной сработке
     */
    public @NotNull String getGood_job() {
        if (good_job == null) {
            return "";
        } else {
            return good_job;
        }
    }

    /**
     * Задать серверную команду, которую вызывает структура при успешной сработке
     */
    public void setGood_job(String good_job) {
        this.good_job = good_job;
    }


    private Location deserilazeLocation(Map<String, Object> serializedLocation) {
        World world = Bukkit.getWorld(serializedLocation.get("world").toString());
        double x = (double) serializedLocation.get("x");
        double y = (double) serializedLocation.get("y");
        double z = (double) serializedLocation.get("z");
        float yaw = Float.parseFloat(serializedLocation.get("yaw").toString());
        float pitch = Float.parseFloat(serializedLocation.get("pitch").toString());

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Задать значение температуры для данной структуры
     */
    public void setTemperatureValue(@NotNull Double num) {
        this.temperature.setNumber(num);
    }

    /**
     * Задать способ, которым будет рассчитываться температура
     */
    public void setTemperatureOperator(@NotNull BinaryOperator<Double> operator) {
        this.temperature.setMath(operator);
    }

    /**
     * Получить температуру, которую излучает структура
     */
    public calculate getTemperature() {
        if (temperature == null || temperature.getMath() == null) {
            return new calculate(0, Double::sum);
        }
        return temperature;
    }

    /**
     * Получить звук успешной работы
     */
    public SimpleEntry<Sound, Float> getSound_success() {
        if (sound_success == null || sound_success.getValue() == null || sound_success.getKey() == null) {
            return new SimpleEntry<>(Sound.BLOCK_IRON_DOOR_OPEN, -1f);
        }
        return sound_success;
    }

    /**
     * Задать звук успешной работы
     */
    public void setSound_success(@NotNull SimpleEntry<Sound, Float> sound_success) {
        this.sound_success = sound_success;
    }

    /**
     * Получить звук ошибки при работе
     */
    public SimpleEntry<Sound, Float> getSound_error() {
        if (sound_error == null || sound_error.getValue() == null || sound_error.getKey() == null) {
            return new SimpleEntry<>(Sound.BLOCK_IRON_DOOR_OPEN, -1f);
        }
        return sound_error;
    }

    /**
     * Задать звук ошибки при работе
     */
    public void setSound_error(@NotNull SimpleEntry<Sound, Float> sound_error) {
        this.sound_error = sound_error;
    }

    /**
     * Получить кулдаун, применяющийся для оператора, который должен вызывать работу структуры
     */
    public int getCooldownForPlayer() {
        return this.p_cooldown_required;
    }

    /**
     * Устанавливает кулдаун, применяющийся для оператора, который должен вызывать работу структуры
     */
    public void setCooldownForPlayer(int cooldown) {
        this.p_cooldown_required = Math.max(0, cooldown);
    }

    /**
     * Получить, сколько осталось игроку до возможности вызывать работу
     */
    public int getStayCooldownForPlayer() {
        return this.p_cooldown;
    }

    public boolean useCooldownForPlayer() {
        if (this.getStayCooldownForPlayer() <= 0 && this.getCooldownForPlayer() != 0) {
            this.p_cooldown = this.getCooldownForPlayer();
            this.cooldown = 0;
            return true;
        }
        return false;
    }

    /**
     * Получить шанс того, что структура сломается
     */
    public double getChanceBreak() {
        return chance_break;
    }

    /**
     * Задать шанс поломки
     */
    public void setChanceBreak(double chance_break) {
        this.chance_break = Math.max(0, Math.min(100, chance_break));
    }

    /**
     * Использовать шанс поломки
     * <br>
     * Если сломается - возвращается false и работа структуры должна отмениться
     */
    public boolean useChanceBreak() {
        if (new Random().nextDouble() * 100 <= getChanceBreak()) {
            this.setCanPlayerEdit(false);
            this.setEnabled(false);
            Location location = getLocations().get(0);
            location.getWorld().playSound(location, Sound.ENTITY_ITEM_BREAK, 2f, 0.6f);
            return false;
        }
        return true;
    }

    /**
     * Может ли игрок редактировать структуру
     */
    public boolean isCanPlayerEdit() {
        return can_player_edit;
    }

    /**
     * Задать, может ли игрок редактировать структуру
     */
    public void setCanPlayerEdit(boolean can_player_edit) {
        this.can_player_edit = can_player_edit;
    }


    /**
     * Представляет собой метод, который отвечает за работу структуры
     * <br>
     * При этом не должны учитываться такие параметры как шанс работы и кулдаун
     * <br>
     * Структура должна переопределить данный класс
     *
     * @return успешно ли отработала структура
     */
    public boolean work() {
        return true;
    }

    /**
     * Получить приоритет, с которым должна выполнятся структура в сети. 0 - первые, 1 - вторые и т.д.
     * <br>
     * Каждая наследующая структура должна установить приоритет, иначе будет ошибка
     */
    public int getPriority() {
        if (priority == 9494) {
            throw new RuntimeException("Назначь приоритет!");
        }
        return this.priority;
    }

    /**
     * Задать приоритет, с которым будет выполняться структура в сети
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Возвращает шанс, с которым структура будет успешно выполнять свои действия
     * <br>
     * Работает, только если разработчик учел это свойство
     *
     * @return число от 0 до 100%
     */
    public double getChanceWork() {
        return chance;
    }

    /**
     * Устанавливает шанс, с которым структура успешно сработает
     *
     * @param chance число от 0 до 100%
     */
    public void setChanceWork(double chance) {
        this.chance = Math.max(Math.min(chance, 100), 0);
    }

    /**
     * Позволяет сделать проверку на то, сработала ли структура
     *
     * @return True - если сработала, False - если нет
     */
    public boolean castChanceWork() {
        return Math.random() * 100 <= getChanceWork();
    }

    /**
     * Получить время, требуемое для перезарядки структуры
     * <br>
     * По умолчанию равняется нулю, если не было переопределено
     *
     * @return время перезарядки (в секундах)
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * Задать, сколько осталось до срабатывания структуры
     */
    public void setCooldown(int cooldown) {
        this.cooldown = Math.max(0, getMaxCooldown() - cooldown);
    }

    /**
     * Получить требуемый куладун
     */
    public int getMaxCooldown() {
        return this.cooldown_required;
    }

    /**
     * Установить требуемое время перезарядки структуры
     *
     * @param cooldown_required время перезарядки (в секундах)
     */
    public void setCooldownRequired(int cooldown_required) {
        this.cooldown_required = Math.max(-1, cooldown_required);
    }

    /**
     * Используется для проверки, кончилась ли перезарядка у структуры
     * <br>
     * В то же время отнимает счетчик перезарядки, так что данная структура должна использоваться в функции update()
     *
     * @return True - если перезарядка кончилась, False - если структура еще на перезарядке
     */
    public boolean useCooldown() {
        if (cooldown_required != -1 && cooldown >= cooldown_required) {
            cooldown = 0;
            return true;
        } else if (cooldown_required != -1) {
            cooldown++;
        }
        return false;
    }

    /**
     * Возвращает уникальный номер структуры
     *
     * @return уникальный номер структуры
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return Возвращает имя структуры
     */
    public String getName() {
        return this.name;
    }

    /**
     * Устанавливает имя структуры
     *
     * @param name новое имя
     */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    /**
     * @param location Добавляет блок, которому соответствует структура данного генератора
     */
    public boolean addLocation(@NotNull Location location) {
        boolean validate = false;
        for (Block block : locations.keySet()) {
            if (block.getLocation().distance(location) < 5) {
                validate = true;
            }
        }
        if (!validate || locations.size() >= 16) {
            return false;
        }
        locations.put(location.getBlock(), location.getBlock().getType());
        return true;
    }

    /**
     * @param location Удаляет координаты из списка соответствий структуре
     * @return успешно было ли удаление структуры
     */
    public boolean removeLocation(@NotNull Location location) {
        if (locations.size() > 1) {
            locations.remove(location.getBlock());
            return true;
        }
        return false;
    }

    /**
     * @return Возвращает список локаций, которым соответствует структура данного генератора
     */
    public List<Location> getLocations() {
        List<Location> loc = new ArrayList<>();
        for (Block block : locations.keySet()) {
            loc.add(block.getLocation().clone().add(0.5, 0.5, 0.5));
        }
        return loc;
    }

    /**
     * @param locations Задает список локаций, которым соответствует структура данного генератора
     * @throws NullPointerException Выкидывается, когда на запись подается пустой массив локаций
     */
    public void setLocations(@NotNull List<Location> locations) {
        if (locations.isEmpty()) {
            throw new NullPointerException();
        }
        this.locations = new HashMap<>();
        for (Location location : locations) {
            if (location.getWorld() != null) {
                this.locations.put(location.getBlock(), location.getBlock().getType());
            }
        }
    }

    /**
     * Вызывается сетью для проверки соответствия блоков структуры заданным
     */
    public void checkLocationsMatching() {
        for (Block block1 : locations.keySet()) {
            boolean validate = false;
            for (Block block2 : locations.keySet()) {
                if (block1.getLocation().distance(block2.getLocation()) < 5) {
                    validate = true;
                }
            }
            if (!block1.getType().equals(this.locations.get(block1))) {
                validate = false;
            }
            if (!validate) {
                block1.getLocation().createExplosion(0);
                this.disconnectToMesh();
                return;
            }
        }
    }


    /**
     * Присоединяет структуру к сети
     *
     * @param mesh сеть, к которой нужно присоединить структуру
     */
    public void connectToMesh(@NotNull Mesh mesh) {
        this.mesh = mesh;
    }

    /**
     * Отсоединить структуру от сети
     */
    public void disconnectToMesh() {
        this.mesh.removeStructure(this);
        this.mesh = null;
    }

    /**
     * Получить сеть, к которой присоединена структура
     *
     * @return ссылка на сеть
     */
    public Mesh getMesh() {
        return mesh;
    }

    /**
     * Структура выполнит работу, и воспроизведет звук
     */
    public void useWorkAndSound() {
        SimpleEntry<Sound, Float> sound;
        if (useChanceBreak() && work()) {
            sound = getSound_success();
            lit(this, true);
            if (!Objects.equals(getGood_job(), "") && getGood_job() != null) {
                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getGood_job());
                } catch (CommandException e) {
                    for (Player player : Bukkit.getOnlinePlayers().stream()
                            .filter(p -> getLocations().stream()
                                    .anyMatch(loc -> p.getLocation().distance(loc) < 10))
                            .toList()) {
                        player.sendMessage(ChatColor.RED + "Ошибка при выполнении команды! \n" + getName());
                    }
                }
            }
        } else {
            sound = getSound_error();
            lit(this, false);
        }
        if (sound.getValue() >= 0) {
            getLocations().get(0).getWorld().playSound(
                    getLocations().get(0),
                    sound.getKey(),
                    SoundCategory.PLAYERS,
                    1f,
                    sound.getValue()
                                                      );
        }
    }

    /**
     * Вызывает работу структуры
     * <br>
     * Тем, кому нужна механика работы структуры, необходимо переопределить логику работы самостоятельно
     */
    public void update() {
        if (isEnabled()) {
            this.p_cooldown = Math.max(0, p_cooldown - 1);
            if (useCooldown() && castChanceWork()) {
                useWorkAndSound();
                return;
            }
        }
        lit(this, false);
    }

    /**
     * Получение объема, который может хранить структура
     */
    public double getVolume() {
        return this.volume;
    }

    /**
     * Установка объема
     *
     * @param volume новый объем
     */
    public void setVolume(double volume) {
        this.volume = Math.max(volume, 0);
    }


    /**
     * Включена ли структура
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Задать, включена структура или нет
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Получить копию с другим uuid и локацией
     */
    public Structure cloneWithNewUUID(Location location) {
        try {
            Structure cloned = (Structure) this.clone();
            cloned.uuid = UUID.randomUUID();
            cloned.setLocations(Collections.singletonList(location));
            cloned.connectToMesh(new Mesh("empty", "empty"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(cloned);
            dataOutput.close();
            String str = Base64Coder.encodeLines(outputStream.toByteArray());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            cloned = (Structure) dataInput.readObject();
            return cloned;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
