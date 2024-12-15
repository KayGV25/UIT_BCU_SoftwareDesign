package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.edu.uit.csbu.software_design.software_design_backend.Security;
import vn.edu.uit.csbu.software_design.software_design_backend.following.followingModel;
import vn.edu.uit.csbu.software_design.software_design_backend.following.followingRepository;
import vn.edu.uit.csbu.software_design.software_design_backend.jwt.JWTModel;
import vn.edu.uit.csbu.software_design.software_design_backend.jwt.JWTUtil;

@Service
public class accountService {
    Security security = new Security();
    
    @Autowired
    private accountRepository accountRepository;

    @Autowired
    private followingRepository followingRepository;

    @Value("$secret")
    private String secret;

    Optional<accountModel> getAccountToken(String token){
        return accountRepository.findByName(JWTUtil.extractName(token));
    }
    
    Optional<accountModel> getAccount(String name){
        return accountRepository.findByName(name);
    }
    
    ResponseEntity<accountResponseDTO> getStreamKey(String token){
        Optional<accountModel> dbAccount = accountRepository.findByName(JWTUtil.extractName(token));
        if(dbAccount.isPresent()){
            return ResponseEntity.ok().body(new accountResponseDTO(dbAccount.get().getStreamKey(), null, accountResponseType.DATA));
        }
        return ResponseEntity.badRequest().body(new accountResponseDTO("Invalid Token or Account not Found", null, accountResponseType.RESPONSE));
    }
    // Optional<accountModel> getAccountById(String token){
    //     return accountRepository.findById(JWTUtil.extractName(token));
    // }    

    ResponseEntity<String> addAccount(accountRequest account) throws NoSuchAlgorithmException{
        if(accountRepository.findByName(account.name()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account with name " + account.name() + " already exists.");
        }
        else{
            String pass = security.toHexString(security.getSHA(account.name()+account.password()+secret));
            String streamKey = security.getHashedStringOfLength(new String(account.name() + Calendar.getInstance().getTimeInMillis()), 32);
            String uuid = UUID.randomUUID().toString();
            while(accountRepository.existsById(uuid)){
                uuid = UUID.randomUUID().toString();
            }
            // System.out.println(uuid);
            // System.out.println(pass);
            // System.out.println(streamKey);
            accountModel accountModel = new accountModel(
                uuid,
                account.name(),
                pass, 
                streamKey
            );
            accountRepository.save(accountModel);
            return ResponseEntity.status(HttpStatus.OK).body("Account created");
        }
    }

    ResponseEntity<accountResponseDTO> login(accountRequest account) throws NoSuchAlgorithmException{
        Optional<accountModel> dbAccount = accountRepository.findByName(account.name());
        if(dbAccount.isPresent()){
            String reqPassHashed = security.toHexString(security.getSHA(account.name()+account.password()+secret));
            if(reqPassHashed.equals(dbAccount.get().getPassword())){
                return ResponseEntity.ok(new accountResponseDTO(new JWTModel(JWTUtil.generateToken(dbAccount.get().getName())).token(), null, accountResponseType.DATA));
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new accountResponseDTO("Wrong password", null, accountResponseType.RESPONSE));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new accountResponseDTO("No Account Found", null, accountResponseType.RESPONSE));
    }

    // ResponseEntity<String> updateStreamKey(accountRequest account) throws NoSuchAlgorithmException {
    //     Optional<accountModel> dbAccount = getAccount(account.name());
    //     if(dbAccount.isPresent()){
    //         String reqPassHashed = security.toHexString(security.getSHA(account.name()+account.password()+secret));
    //         if(reqPassHashed.equals(dbAccount.get().getPassword())){
    //             String streamKey = security.getHashedStringOfLength(new String(account.name() + Calendar.getInstance().getTimeInMillis()), 32);
    //             dbAccount.get().setStreamKey(streamKey);
    //             accountRepository.save(dbAccount.get());
    //             return ResponseEntity.ok("Stream key changed");
    //         }
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password");
    //     }
    //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No account found");
    // }

    ResponseEntity<accountResponseDTO> updateStreamKey(String token) throws NoSuchAlgorithmException {
        Optional<accountModel> dbAccount = getAccountToken(token);
        if(dbAccount.isPresent()){
            String streamKey = security.getHashedStringOfLength(new String(dbAccount.get().getName() + Calendar.getInstance().getTimeInMillis()), 32);
            dbAccount.get().setStreamKey(streamKey);
            accountRepository.save(dbAccount.get());
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Streamkey Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("Streamkey Updated", null, accountResponseType.RESPONSE));
    }

    ResponseEntity<String> addToFollowing(accountRequest account, String streamId) throws NoSuchAlgorithmException {
        Optional<accountModel> dbAccount = getAccount(account.name());
        if(dbAccount.isPresent()){
            String reqPassHashed = security.toHexString(security.getSHA(account.name()+account.password()+secret));
            if(reqPassHashed.equals(dbAccount.get().getPassword())){
                if(!followingRepository.existsByStreamerId(streamId)){
                    followingRepository.save(new followingModel(dbAccount.get().getId(), streamId));
                    return ResponseEntity.ok("Followed");
                }
                followingRepository.delete(new followingModel(dbAccount.get().getId(),streamId));
                return ResponseEntity.ok("Unfollowed");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No account found");
    }

    public ResponseEntity<accountResponseDTO> updateTitle(String token, Optional<String> data) {
        Optional<accountModel> dbAccount = getAccount(JWTUtil.extractName(token));
        if(dbAccount.isPresent() && data.isPresent()){
            if(Security.containsXSS(data.get())) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new accountResponseDTO("XSS detected", null, accountResponseType.RESPONSE));;
            accountModel accountModel = dbAccount.get();
            accountModel.setTitle(data.get());
            accountRepository.save(accountModel);
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Title Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found or Title data is null", null, accountResponseType.RESPONSE));
    }

    public ResponseEntity<accountResponseDTO> updateDescription(String token, Optional<String> data) {
        Optional<accountModel> dbAccount = getAccount(JWTUtil.extractName(token));
        if(dbAccount.isPresent() && data.isPresent()){
            if(Security.containsXSS(data.get())) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new accountResponseDTO("XSS detected", null, accountResponseType.RESPONSE));
            accountModel accountModel = dbAccount.get();
            accountModel.setDescription(data.get());
            accountRepository.save(accountModel);
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Description Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found or Description data is null", null, accountResponseType.RESPONSE));
    }

    public ResponseEntity<accountResponseDTO> updatePassword(String token, Optional<String> data) {
        Optional<accountModel> dbAccount = getAccount(JWTUtil.extractName(token));
        if(dbAccount.isPresent() && data.isPresent()){
            if(Security.containsXSS(data.get())) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new accountResponseDTO("XSS detected", null, accountResponseType.RESPONSE));
            accountModel accountModel = dbAccount.get();
            accountModel.setPassword(data.get());
            accountRepository.save(accountModel);
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Password Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found or Password data is null", null, accountResponseType.RESPONSE));

    }

    public ResponseEntity<accountResponseDTO> updateUsername(String token, Optional<String> data) {
        Optional<accountModel> dbAccount = getAccount(JWTUtil.extractName(token));
        if(dbAccount.isPresent() && data.isPresent()){
            if(Security.containsXSS(data.get())) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new accountResponseDTO("XSS detected", null, accountResponseType.RESPONSE));
            if(accountRepository.existsByName(data.get())) return ResponseEntity.status(HttpStatus.CONFLICT).body(new accountResponseDTO("Username already exists", null, accountResponseType.RESPONSE));
            accountModel accountModel = dbAccount.get();
            accountModel.setName(data.get());
            accountRepository.save(accountModel);
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Name Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found or Name data is null", null, accountResponseType.RESPONSE));
    }

    public ResponseEntity<accountResponseDTO> deleteAccount(String token) {
        Optional<accountModel> dbAccount = getAccount(JWTUtil.extractName(token));
        if(dbAccount.isPresent()){
            accountRepository.deleteByName(dbAccount.get().getName());
            followingRepository.deleteAllByAccountId(dbAccount.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Account deleted", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new accountResponseDTO("No account found", null, accountResponseType.RESPONSE));
    }

}
