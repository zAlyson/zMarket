package br.alysonsantos.market.core.services;

public interface StorageServiceSQL extends StorageService {
    String TABLE = "market";
    String ID = "id";
    String ITEMSTACK = "nbt";
    String LONG = "tempo";
    String OWNER = "owner";
    String PRICE = "PRICE";
    String ISPRIVATE = "private";
    String TARGET = "target";

    String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
            "`" + ID + "` varchar(10) NOT NULL," +
            "`" + ITEMSTACK + "` varchar(1000) NOT NULL," +
            "`" + PRICE + "` varchar(1000) NOT NULL," +
            "`" + ISPRIVATE + "` boolean NOT NULL," +
            "`" + LONG + "` varchar(1000) NOT NULL," +
            "`" + OWNER + "` varchar(16) NOT NULL," +
            "`" + TARGET + "` varchar(16) NOT NULL," +
            "PRIMARY KEY (`" + ID + "`)" +
            ");";
    String TABLE_ADD_OWNER_COLUMN = "ALTER TABLE `" + TABLE + "` ADD `" + ID + "` varchar(64) NOT NULL default 'Null';";
}
