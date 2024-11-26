package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.edu.uit.csbu.software_design.software_design_backend.Util;

@Service
public class accountService {

    @Autowired
    Util util;
    
    @Autowired
    private accountRepository accountRepository;

    Optional<accountModel> getAccount(String name){
        return accountRepository.findByName(name);
    }

    // void addAccount(accountRequest account){
    //     if(accountRepository.findByName(account.name()).isPresent()){
    //         ResponseEntity.status(HttpStatus.CONFLICT).body("Account with name '\" + account.name() + \"' already exists.");
    //     }
    //     else{
    //         account.pasword();
    //         accountRepository.save(new accountModel(
    //             account.name(), 
    //             util.passwordEncoder().encode(account.pasword()), 
    //             util.passwordEncoder().encode(account.name() + Calendar.getInstance().getTime())
    //         ));
    //         ResponseEntity.ok("Account created");
    //     }
    // }
}
