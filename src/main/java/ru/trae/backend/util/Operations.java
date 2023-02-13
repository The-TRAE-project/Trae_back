package ru.trae.backend.util;

public enum Operations {
    ASSEMBLING("Сборка"),
    CUTTING("Раскрой"),
    GLUING("Склейка"),
    GRINDING("Шлифовка"),
    MILLING("Фрезеровка"),
    SHIPMENT("Отгрузка"),
    PAINTING("Покраска"),
    PACKAGING("Упаковка"),
    PREPARATION_FOR_PAINTING("Подготовка к покраске");

    public final String value;

    Operations(String value) {
        this.value = value;
    }
}
