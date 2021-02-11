package com.martin.tube.repository;


import com.martin.tube.model.View;
import com.martin.tube.model.id.ViewId;
import org.springframework.data.repository.CrudRepository;

public interface ViewRepository extends CrudRepository<View, ViewId> {
}
