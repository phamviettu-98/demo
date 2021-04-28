package com.example.demo.Resposity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.User;

import java.util.List;

@Repository
public interface UserRespository extends JpaRepository<User, Integer> {
    Boolean existsByUsernameAndPassword(String username, String password);

    Boolean existsByUsername(String usernme);


    User findByUsername(String username);

    List<User> findAllByUsernameAndPassword(String username, String password);
}
