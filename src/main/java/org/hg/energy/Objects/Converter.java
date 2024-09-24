package org.hg.energy.Objects;

import org.bukkit.Location;
import org.hg.energy.Mesh;

import java.util.List;

public class Converter extends Structure {
    private Mesh outputMash;
    private double coefficient;
    private double chance_use;

    /**
     * Представляет структуру, которая перебрасывает энергию из одной сети в другую
     * <br>
     *
     * @param name      имя конвертера
     * @param locations координаты, которым соответствует конвертер
     */
    public Converter(String name, List<Location> locations) {
        super(name, locations);
        this.coefficient = 0;
        this.chance_use = 0;
    }

    /**
     * Вызывает перекачку энергии
     */
    @Override
    public void update() {
        if (super.useCooldown() && super.castChanceWork()) {
            //TODO: сделать перекачку энергии
        }
    }


    /**
     * Шанс срабатывания конвертации
     *
     * @return шанс от 0 до 100%
     */
    public double getChanceUse() {
        return chance_use;
    }

    /**
     * Метод, устанавливающий шанс успешной конвертации
     *
     * @param chance_use шанс от 0 до 100%
     */
    public void setChanceUse(double chance_use) {
        this.chance_use = Math.max(Math.min(100, chance_use), 0);
    }

    /**
     * Получение коэффициента конвертации энергии
     *
     * @return коэффициент
     */
    public double getCoefficient() {
        return coefficient;
    }

    /**
     * Установка коэффициента, с которым структура конвертирует энергию
     *
     * @param coefficient коэффициент конвертации
     */
    public void setCoefficient(double coefficient) {
        this.coefficient = Math.max(coefficient, 0);
    }

    /**
     * Возвращает сеть, в которую перебрасывается энергия
     *
     * @return сеть выхода энергии
     */
    public Mesh getOutputMash() {
        return outputMash;
    }

    /**
     * Задает сеть, в которую нужно отправлять энергию
     * <br>
     * Обновление структуры не будет работать по данной сети
     *
     * @param outputMash структура, в которую нужно отправлять энергию
     */
    public void setOutputMash(Mesh outputMash) {
        this.outputMash = outputMash;
    }
}
