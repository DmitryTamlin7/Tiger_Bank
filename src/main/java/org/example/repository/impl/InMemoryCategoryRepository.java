package org.example.repository.impl;

import org.example.domain.Category;
import org.example.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCategoryRepository implements CategoryRepository {

    private final Map<Long, Category> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Category save(Category category) {
        if (category.getId() == null){
            category.setId(idGenerator.getAndIncrement());
        }
        storage.put(category.getId(), category);
        return category;
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }


}
