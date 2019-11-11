package br.alysonsantos.market.core.services;

import br.alysonsantos.market.core.models.Product;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public interface StorageService extends Service {

    Map<Integer, Product> getProducts();

    CompletableFuture<Void> save(final Product product);

    CompletableFuture<Void> update(final Product product);

    CompletableFuture<Void> delete(final Product product);
}
