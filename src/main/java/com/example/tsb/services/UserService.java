package com.example.tsb.services;

import com.example.tsb.models.Role;
import com.example.tsb.models.User;
import com.example.tsb.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public boolean createUser(User user){
        if(userRepository.findByEmail(user.getUsername()).isPresent()){
            return false;
        }
        user.setActive(true);
        user.getRoles().add(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User user1=userRepository.save(user);
        System.out.println("Создание пользователя: "+user1);
        System.out.println(user.getRoles());
        return true;
    }
    public boolean changeUser(String oldEmail, String firstname, String lastname, String phone, String newEmail){

        if((!newEmail.equals(oldEmail)) && (userRepository.findByEmail(newEmail).isPresent())){
            return false;
        }
        User user=userRepository.getUserByEmail(oldEmail);
        user.setEmail(newEmail);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPhoneNumber(phone);
        userRepository.save(user);
        System.out.println("Изменение пользователя: "+user);
        return true;
    }
    public void changeUser(Long id, boolean active, String email,
                           String firstname, String lastname, String password, String phoneNumber, String roles){
        User user=userRepository.getById(id);
        user.setActive(active);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        if(!Objects.equals(password, "")) {

            user.setPassword(passwordEncoder.encode(password));
        }
        user.setPhoneNumber(phoneNumber);
        Set<Role> setRoles=new HashSet<>();
        roles=roles.substring(1,roles.length()-1);
        String[] arrRoles=roles.split(", ");
        for(String s:arrRoles){
            if(s.equals("ROLE_ADMIN")){
                setRoles.add(Role.ROLE_ADMIN);
            } else if (s.equals("ROLE_USER")) {
                setRoles.add(Role.ROLE_USER);
            } else if (s.equals("ROLE_SUPERADMIN")) {
                setRoles.add(Role.ROLE_SUPERADMIN);
            }

        }
        user.setRoles(setRoles);
        userRepository.save(user);
    }

    public User getUser(String username){
        return userRepository.getUserByEmail(username);
    }
    public User getUserById(Long id){
        return userRepository.getById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public boolean changePassword(String passwordOld, String passwordNew, String username) {
        User user=userRepository.getUserByEmail(username);
        if(passwordEncoder.matches(passwordOld, user.getPassword())){
            user.setPassword(passwordEncoder.encode(passwordNew));
            userRepository.save(user);
            return true;
        }
        else {
            return false;
        }
    }
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
