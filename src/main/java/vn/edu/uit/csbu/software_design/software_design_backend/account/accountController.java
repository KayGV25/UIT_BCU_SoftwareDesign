package vn.edu.uit.csbu.software_design.software_design_backend.account;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.uit.csbu.software_design.software_design_backend.Security;

import java.security.NoSuchAlgorithmException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/account")
public class accountController {
    
    @Autowired
    accountService accountService;

    Security security;

    @GetMapping("")
    public ResponseEntity<accountModel> findAccount(@RequestParam String name) {
        Optional<accountModel> account = accountService.getAccount(name);
        return account.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    @PostMapping("/register")
    public ResponseEntity<String> createAccount(@RequestBody accountRequest account) throws NoSuchAlgorithmException {
        return accountService.addAccount(account);
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> postMethodName(@RequestBody accountRequest account) throws NoSuchAlgorithmException {
        return accountService.login(account);
    }

    @PutMapping("/update/{type}")
    public ResponseEntity<String> putMethodName(@PathVariable String type, @RequestBody accountRequest account) throws NoSuchAlgorithmException{
        if(type.equals("streamkey")){
            return accountService.updateStreamKey(account);
        }
        // else if(type.equals("password")){
        //     return accountService.updatePassword(account);
        // }
        else{
            return ResponseEntity.badRequest().body("Invalid type");
        
        }
    }
    
}
