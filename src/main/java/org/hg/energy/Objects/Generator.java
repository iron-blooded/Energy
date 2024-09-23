package org.hg.energy.Objects;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Generator extends Structure {
    private double amount_energy_produced = 0;
    private double period_work = 0;
    private double period_use = 0;
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
        period_use++;
        if (period_use > period_work) {
            period_use = 0;
            if (Math.random() * 100 > chance_use) {
                //TODO: сделать добавление в меш энергии
            }
        }
    }

    /**
     * @return Возвращает период, за который генератор генерирует энергию
     */
    public double getPeriod() {
        return period_work;
    }

    /**
     * @param number Устанавливает период, за который генератор генерирует энергию
     */
    public void setPeriod(double number) {
        period_work = Math.max(number, 0);
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
