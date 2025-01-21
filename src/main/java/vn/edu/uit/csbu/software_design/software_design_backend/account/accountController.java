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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/account")
public class accountController {

    @Autowired
    accountService accountService;

    Security security;

    @Operation(summary = "Retrieve account information by name", description = "Fetches account details based on the provided account name.")
    @GetMapping("")
    public ResponseEntity<accountModel> findAccount(
            @Parameter(description = "The name of the account to retrieve. Must not contain SQL injection patterns.") 
            @RequestParam String name) {
        if (Security.containsSQLInjection(name)) {
            return ResponseEntity.badRequest().body(null);
        }
        Optional<accountModel> account = accountService.getAccount(name);
        return account.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Register a new account", description = "Creates a new account with the provided details including username and password.")
    @PostMapping("/register")
    public ResponseEntity<String> createAccount(
            @Parameter(description = "Account details including username and password. Must not contain SQL injection patterns.") 
            @RequestBody accountRequest account) throws NoSuchAlgorithmException {
        if (Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())) {
            return ResponseEntity.badRequest().body(null);
        }
        return accountService.addAccount(account);
    }

    @Operation(summary = "Authenticate user and generate login token", description = "Validates user credentials and generates an authentication token.")
    @PostMapping("/login")
    public ResponseEntity<accountResponseDTO> login(
            @Parameter(description = "Account credentials including username and password.") 
            @RequestBody accountRequest account) throws NoSuchAlgorithmException {
        ResponseEntity<accountResponseDTO> response = accountService.login(account);
        System.out.println("Login response: " + response.getBody());
        return response;
    }

    @Operation(summary = "Retrieve the list of accounts that the authenticated user is following", description = "Fetches the list of users that the authenticated user is following.")
    @GetMapping("/auth/following")
    public ResponseEntity<accountResponseDTO> getFollowing(
            @Parameter(description = "The authorization token provided in the request header.") 
            @RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getFollowing(token);
    }

    @Operation(summary = "Retrieve the follower count of the authenticated user", description = "Fetches the count of followers for the authenticated user.")
    @GetMapping("/auth/follower/{streamId}")
    public ResponseEntity<accountResponseDTO> getFollowerCount(
            @Parameter(description = "The stream ID to get the follower count.") 
            @PathVariable String streamId,
            @Parameter(description = "The authorization token provided in the request header.") 
            @RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getFollower(streamId);
    }

    @Operation(summary = "Retrieve the stream key for the authenticated user", description = "Fetches the stream key of the authenticated user.")
    @GetMapping("/auth/streamkey")
    public ResponseEntity<accountResponseDTO> getStreamKey(
            @Parameter(description = "The authorization token provided in the request header.") 
            @RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getStreamKey(token);
    }

    @Operation(summary = "Update user account information", description = "Updates user account information based on the specified type (e.g., stream key, title, description).")
    @PutMapping("/auth/update/{type}")
    public ResponseEntity<accountResponseDTO> update(
            @Parameter(description = "The authorization token provided in the request header.") 
            @RequestHeader("Authorization") String token, 
            @Parameter(description = "The type of update to perform (e.g., streamkey, title, description, password, username).") 
            @PathVariable String type, 
            @Parameter(description = "Account data for the update. Includes fields like new password, username, etc.") 
            @RequestBody accountRequest account) throws NoSuchAlgorithmException {
        if (Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())) {
            return ResponseEntity.badRequest().body(new accountResponseDTO("SQL injection detected", null, accountResponseType.RESPONSE));
        }

        if (account.data().isPresent() && Security.containsSQLInjection(account.data().get())) {
            return ResponseEntity.badRequest().body(new accountResponseDTO("SQL injection detected", null, accountResponseType.RESPONSE));
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

    @Operation(summary = "Follow or unfollow a stream", description = "Adds or removes a stream from the user's following list based on the stream ID.")
    @PutMapping("/auth/follow/{streamId}")
    public ResponseEntity<String> follow(
            @Parameter(description = "The stream ID to follow or unfollow.") 
            @PathVariable String streamId, 
            @Parameter(description = "Account credentials including username. Must not contain SQL injection patterns.") 
            @RequestBody accountRequest account) throws NoSuchAlgorithmException {
        if (Security.containsSQLInjection(account.name())) {
            return ResponseEntity.badRequest().body(null);
        }
        return accountService.addToFollowing(account, streamId);
    }

    @Operation(summary = "Delete the authenticated user's account", description = "Deletes the authenticated user's account.")
    @DeleteMapping("/auth/delete")
    public ResponseEntity<accountResponseDTO> delete(
            @Parameter(description = "The authorization token provided in the request header.") 
            @RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.deleteAccount(token);
    }
}
