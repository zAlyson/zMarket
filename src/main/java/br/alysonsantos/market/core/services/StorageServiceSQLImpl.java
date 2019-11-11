package br.alysonsantos.market.core.services;

import br.alysonsantos.market.StoreMarket;
import br.alysonsantos.market.core.models.Product;
import br.alysonsantos.market.utils.Serializer;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.*;
import java.util.logging.Level;

public class StorageServiceSQLImpl implements StorageServiceSQL {
    private final SQLiteDataSource dataSource;

    public StorageServiceSQLImpl() {
        dataSource = new SQLiteDataSource();

        dataSource.setUrl("jdbc:sqlite:" + new File(StoreMarket.getInstance().getDataFolder(), "BaseDeDados.db"));
    }

    @Override
    public void init() {
        createDatabase();
        addOwnerColumn();
    }

    @Override
    public Map<Integer, Product> getProducts() {
        Map<Integer, Product> productMap = new HashMap<>();
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT * FROM " + TABLE + ";")) {
                try (ResultSet rs = ps.executeQuery()) {
                    int failed = 0;
                    while (rs.next()) {

                        if (rs.getBoolean(ISPRIVATE)) {
                            Product product = new Product(rs.getInt(ID), rs.getLong(LONG), rs.getDouble(PRICE), true, rs.getString(OWNER), rs.getString(TARGET), Serializer.deserializeItemStack(rs.getString(ITEMSTACK)));
                            product.setPrivate(true);
                            productMap.put(rs.getInt(ID), product);
                        } else {
                            productMap.put(rs.getInt(ID), new Product(rs.getInt(ID), rs.getLong(LONG), rs.getDouble(PRICE), rs.getString(OWNER), Serializer.deserializeItemStack(rs.getString(ITEMSTACK))));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log("Ocurreu um erro ao carregar as máquinas.", e);
        }
        return productMap;
    }

    @Override
    public CompletableFuture<Void> save(Product product) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = dataSource.getConnection()) {
                try (PreparedStatement ps = c.prepareStatement("INSERT OR REPLACE INTO " + TABLE +
                        " (" + ID + "," + ITEMSTACK + "," + OWNER + "," + LONG + "," + PRICE + "," + ISPRIVATE + "," + TARGET + ") " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, product.getID());
                    ps.setString(2, Serializer.serializeItemStack(product.getItemStack()));
                    ps.setString(3, product.getOwner());
                    ps.setLong(4, product.getTempo());
                    ps.setDouble(5, product.getPrice());

                    if (product.isPrivate()) {
                        ps.setBoolean(6, true);
                        ps.setString(7, product.getTarget());
                        ps.executeUpdate();
                        return;
                    }

                    ps.setBoolean(6, false);
                    ps.setString(7, "null");
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                log("Ocurreu um erro ao guardar o " + product + ".", e);
            }
        }, StoreMarket.getInstance().getExecutor());
    }

    @Override
    public CompletableFuture<Void> update(Product product) {
        return null;
    }

    @Override
    public CompletableFuture<Void> delete(Product product) {
        return CompletableFuture.runAsync(() -> {
            try (Connection c = dataSource.getConnection()) {
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM " + TABLE + " WHERE " + ID + " = ?;")) {
                    ps.setInt(1, product.getID());
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                log("Ocurreu um erro ao apagar o " + product + ".", e);
            }
        }, StoreMarket.getInstance().getExecutor());
    }

    private void addOwnerColumn() {
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(TABLE_ADD_OWNER_COLUMN)) {
                ps.executeUpdate();
            }
        } catch (SQLException ignore) {
            // Column already presend in the database
        } catch (Exception e) {
            log("Ocurreu um erro ao adicionar os novos campos a tabela.", e);
        }
    }

    private void createDatabase() {
        try (Connection c = dataSource.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(TABLE_CREATE)) {
                ps.executeUpdate();
            }
        } catch (Exception e) {
            log("Ocurreu um erro ao criar a tabela.", e);
        }
    }

    @Override
    public void stop() {
    }

    private void log(Level level, String message) {
        StoreMarket.getInstance().getLogger().log(level, "[SqlLite] " + message);
    }

    private void log(String message, Throwable thrown) {
        StoreMarket.getInstance().getLogger().log(Level.SEVERE, "[SqlLite] " + message, thrown);
    }
}
