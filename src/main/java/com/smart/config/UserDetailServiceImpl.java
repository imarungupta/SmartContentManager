package com.smart.config;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //fetching user from database
        User userByEmail = userRepository.getUserByUserName(username);

        if(userByEmail==null){
            throw new UsernameNotFoundException("User could not be found !!");
        }
        // Now pass this user to CustomerUserDetail to extract all the detail of user which we have just got from DB
        //CustomerUserDetail customerUserDetail = new CustomerUserDetail(userByEmail); // Or we can write in inline as given below
        return new CustomerUserDetail(userByEmail);
    }
}
