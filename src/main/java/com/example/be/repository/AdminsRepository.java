package com.example.be.repository;

import com.example.be.model.Admins;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminsRepository extends JpaRepository<Admins, Integer> {
    @Query("SELECT a FROM Admins a WHERE a.deletedAt IS NULL")
    Page<Admins> findAllNotDeleted(Pageable pageable);

    @Query("SELECT a FROM Admins a WHERE a.adminId = :id AND a.deletedAt IS NULL")
    Admins findByIdNotDeleted(@Param("id") Integer id);

    @Query("SELECT a FROM Admins a WHERE a.user.userId = :userId AND a.deletedAt IS NULL")
    Optional<Admins> findByUserId(@Param("userId") Integer userId);
}