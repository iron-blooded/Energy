package org.hg.energy.Interface;

public interface Window {
    _ShareData getObject();

    /**
     * Задает то, может ли инвентарь обновляться, или лучше не стоит
     * <br>
     * Пример: там где перетаскиваются предметы, лучше выключить
     */
    boolean needUpdate();
}
