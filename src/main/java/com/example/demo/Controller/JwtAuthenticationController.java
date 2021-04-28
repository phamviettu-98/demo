package com.example.demo.Controller;

import com.example.demo.Resposity.UserRespository;
import com.example.demo.Util.JwtTokenUtil;
import com.example.demo.entity.User;
import com.example.demo.model.JwtRequest;
import com.example.demo.model.JwtResponse;
import com.example.demo.service.JwtUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRespository userRespository;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> Register(JwtRequest jwtRequest) {
        String encoded = new BCryptPasswordEncoder().encode(jwtRequest.getPassword());
        userRespository.save(new User(jwtRequest.getUsername(), encoded));
        return ResponseEntity.ok(jwtRequest.getUsername() + "-" + jwtRequest.getPassword());
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> CreatAuthorToken(@RequestBody JwtRequest jwtRequest) throws Exception {

        XacThuc(jwtRequest.getUsername(), jwtRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtTokenUtil.creatToken(userDetails);
        return ResponseEntity.ok().body(new JwtResponse(token));

    }

    private void XacThuc(String username, String passworld) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, passworld));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

    }

    @PostMapping("/hello")
    public ResponseEntity<?> checklogin(JwtRequest jwtRequest) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String p = bCryptPasswordEncoder.encode(jwtRequest.getPassword());
        if (userRespository.existsByUsername(jwtRequest.getUsername())) {
            User users = userRespository.findByUsername(jwtRequest.getUsername());

            return ResponseEntity.ok( userDetailsService.loadUserByUsername(jwtRequest.getUsername()));

        }
        return ResponseEntity.ok("Bi loi roi");
    }
    @PostMapping("/admin")
    public ResponseEntity<?> checkloginadmin(JwtRequest jwtRequest) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String p = bCryptPasswordEncoder.encode(jwtRequest.getPassword());
        if (userRespository.existsByUsername(jwtRequest.getUsername())) {
            User users = userRespository.findByUsername(jwtRequest.getUsername());

            return ResponseEntity.ok(users.getRoles().toString());

        }
        return ResponseEntity.ok("Bi loi roi");
    }
    @GetMapping("/shop")
    public ResponseEntity<?> xemtt(HttpServletRequest httpServletRequest){
        String request = httpServletRequest.getHeader("Authorization");
        String token = request.substring(7);

        JwtTokenUtil util = new JwtTokenUtil();
        String name = util.getUsernameFromToken(token);
        Date time = util.getExpirationDateFromtoken(token);
        return ResponseEntity.ok("Username: "+name+ "- EndTime: "+time);
    }
}
