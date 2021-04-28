package com.example.demo.service;

import com.example.demo.Resposity.UserRespository;
import com.example.demo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    UserRespository userRespository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        if (userRespository.existsByUsername(s)) {
            com.example.demo.entity.User users = userRespository.findByUsername(s);
            if ( users == null){
                throw new UsernameNotFoundException("User not found");
            }
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            List<Role> roles = users.getRoles();
            for( Role role:roles){
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            }

            return new User(s, users.getPassword(),
                    grantedAuthorities);//new ArrayList
        } else {
            throw new UsernameNotFoundException("User not found with username: " + s);
        }
    }
}
