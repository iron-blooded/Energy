package org.hg.energy.Objects;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Generator extends Structure {
    private double amount_energy_produced = 0;
    private double chance_use = 0;

    /**
     * Представляет собой структуру генератора, который предоставляет возможность производить энергию в сеть
     *
     * @param name      имя генератора, которое будет отображаться людям
     * @param locations список локаций, которым соответствует данная структура
     */
    public Generator(@NotNull String name, @NotNull List<Location> locations) {
        super(name, locations);
    }

    /**
     * @return Число, соответствующее шансу срабатывания генератора от 0 до 100%
     */
    public double getChanceUse() {
        return chance_use;
    }

    /**
     * Метод, устанавливающий шанс срабатывания генератора от 0 до 100%
     *
     * @param chance_use параметр, отвечающий за шанс срабатывания генератора
     */
    public void setChanceUse(double chance_use) {
        this.chance_use = Math.max(Math.min(100, chance_use), 0);
    }

    /**
     * При вызове увеличивает счетчик периода до срабатывания на единицу.
     * <br>
     * Как только счетчик доходит до значения, которое требуется для срабатывания, бросается шанс на то, что энергия
     * будет произведена.
     * <br>
     * Переполнение обнуляет счетчик
     */
    @Override
    public void update() {
        if (super.useCooldown() && super.castChanceWork()) {
            //TODO: сделать добавление в меш энергии
        }
    }


    /**
     * @return Возвращает количество генерируемой энергии за период
     */
    public double getAmountEnergyProduced() {
        return amount_energy_produced;
    }

    /**
     * @param amount Устанавливает количество генерируемой энергии за период
     */
    public void setAmountEnergyProduced(double amount) {
        amount_energy_produced = Math.max(amount, 0);
    }
}
