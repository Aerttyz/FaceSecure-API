package com.face.secure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.face.secure.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByName(String user);
}
