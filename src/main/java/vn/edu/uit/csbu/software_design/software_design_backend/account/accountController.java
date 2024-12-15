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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
        if(Security.containsSQLInjection(name)){
            return ResponseEntity.badRequest().body(null);
        }
        Optional<accountModel> account = accountService.getAccount(name);
        return account.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    @PostMapping("/register")
    public ResponseEntity<String> createAccount(@RequestBody accountRequest account) throws NoSuchAlgorithmException {
        if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
            return ResponseEntity.badRequest().body(null);
        }
        return accountService.addAccount(account);
    }
    
    @PostMapping("/login")
    public ResponseEntity<accountResponseDTO> postMethodName(@RequestBody accountRequest account) throws NoSuchAlgorithmException {
        // if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
        //     return ResponseEntity.badRequest().body(null);
        // }
        return accountService.login(account);
    }

    @GetMapping("/auth/streamkey")
    public ResponseEntity<accountResponseDTO> getStreamKey(@RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getStreamKey(token);
    }


    @PutMapping("/auth/update/{type}")
    public ResponseEntity<accountResponseDTO> update(@RequestHeader("Authorization") String token, @PathVariable String type, @RequestBody accountRequest account) throws NoSuchAlgorithmException{
        if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
            return ResponseEntity.badRequest().body(new accountResponseDTO("SQL injection deteced", null, accountResponseType.RESPONSE));
        }

        if(account.data().isPresent() && Security.containsSQLInjection(account.data().get())){
            return ResponseEntity.badRequest().body(new accountResponseDTO("SQL injection deteced", null, accountResponseType.RESPONSE));
        }

        switch (type) {
            case "streamkey": return accountService.updateStreamKey(token);
            case "title": return accountService.updateTitle(token, account.data());
            case "description": return accountService.updateDescription(token, account.data());
            case "password": return accountService.updatePassword(token, account.data());
            case "username": return accountService.updateUsername(token, account.data());
            default: return ResponseEntity.badRequest().body(new accountResponseDTO("Invalid Type", null, accountResponseType.RESPONSE));
        }
    }

    @PutMapping("/auth/follow/streamId")
    public ResponseEntity<String> follow(@PathVariable String streamId, @RequestBody accountRequest account) throws NoSuchAlgorithmException{
        if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
            return ResponseEntity.badRequest().body(null);
        }
        return accountService.addToFollowing(account, streamId);
    }

    @DeleteMapping("/auth/delete")
    public ResponseEntity<accountResponseDTO> delete(@RequestHeader("Authorization") String token) throws NoSuchAlgorithmException{
        return accountService.deleteAccount(token);
    }
}
