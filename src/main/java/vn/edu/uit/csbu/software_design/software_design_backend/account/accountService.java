package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.edu.uit.csbu.software_design.software_design_backend.Security;

// import vn.edu.uit.csbu.software_design.software_design_backend.Util;

@Service
public class accountService {
    Security security = new Security();
    
    @Autowired
    private accountRepository accountRepository;

    Optional<accountModel> getAccount(String name){
        return accountRepository.findByName(name);
    }

    ResponseEntity<String> addAccount(accountRequest account) throws NoSuchAlgorithmException{
        if(accountRepository.findByName(account.name()).isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Account with name " + account.name() + " already exists.");
            }
        else{
            String pass = security.toHexString(security.getSHA(account.password()));
            String streamKey = security.getHashedStringOfLength(new String(account.name() + Calendar.getInstance().getTimeInMillis()), 32);
            String uuid = UUID.randomUUID().toString();
            System.out.println(uuid);
            System.out.println(pass);
            System.out.println(streamKey);
            accountModel accountModel = new accountModel(
                uuid,
                account.name(),
                pass, 
                streamKey
            );
            accountRepository.save(accountModel);
            return ResponseEntity.ok("Account created");
        }
    }

    ResponseEntity<String> login(accountRequest account) throws NoSuchAlgorithmException{
        Optional<accountModel> dbAccount = getAccount(account.name());
        if(dbAccount.isPresent()){
            String reqPassHashed = security.toHexString(security.getSHA(account.password()));
            if(reqPassHashed.equals(dbAccount.get().getPassword())){
                return ResponseEntity.ok("");
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No account found");
    }
}
