package com.example.be.repository;

import com.example.be.model.Admins;
import com.example.be.model.Routes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoutesRepository extends JpaRepository<Routes, Integer>, JpaSpecificationExecutor<Routes> {
    @Query("SELECT r FROM Routes r WHERE r.deletedAt IS NULL")
    List<Routes> findAllNotDeleted();

    @Query("SELECT r FROM Routes r WHERE r.routeId = :id AND r.deletedAt IS NULL")
    Routes findByIdNotDeleted(Integer id);

    @Query("SELECT r FROM Routes r WHERE r.deletedAt IS NULL")
    Page<Routes> findAllNotDeleted(Pageable pageable);
}
