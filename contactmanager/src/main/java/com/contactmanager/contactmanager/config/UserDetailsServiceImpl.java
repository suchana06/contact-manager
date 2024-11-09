
package com.contactmanager.contactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contactmanager.contactmanager.entities.User;
import com.contactmanager.contactmanager.repository.HomeRepository;

public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private HomeRepository hm;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.hm.getUserByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("username not present in database");
        }
        CustomUserDetails cd = new CustomUserDetails(user);
        return cd;
    }

}
