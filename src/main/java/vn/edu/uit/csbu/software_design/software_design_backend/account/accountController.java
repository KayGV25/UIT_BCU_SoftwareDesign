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


/**
 * REST controller for managing user accounts.
 * Provides endpoints for account creation, login, retrieval, updates, and deletion.
 * Handles security validations and communicates with the {@code accountService}.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/api/account")
public class accountController {

    /**
     * The Account service.
     */
    @Autowired
    accountService accountService;

    /**
     * The Security.
     */
    Security security;

    /**
     * Retrieves account information by name.
     *
     * @param name the name of the account to retrieve             (must not contain SQL injection patterns)
     * @return Response Entity containing the account information or a 404 status if not found
     */
    @GetMapping("")
    public ResponseEntity<accountModel> findAccount(@RequestParam String name) {
        if(Security.containsSQLInjection(name)){
            return ResponseEntity.badRequest().body(null);
        }
        Optional<accountModel> account = accountService.getAccount(name);
        return account.map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Registers a new account.
     *
     * @param account the request body containing account details, including username and password                (must not contain SQL injection patterns)
     * @return Response Entity with a success message or an error response if validation fails
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
    @PostMapping("/register")
    public ResponseEntity<String> createAccount(@RequestBody accountRequest account) throws NoSuchAlgorithmException {
        if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
            return ResponseEntity.badRequest().body(null);
        }
        return accountService.addAccount(account);
    }

    /**
     * Authenticates a user and generates a login token.
     *
     * @param account the request body containing account credentials (username and password)
     * @return Response Entity containing the login response or an error if authentication fails
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
    @PostMapping("/login")
    public ResponseEntity<accountResponseDTO> login(@RequestBody accountRequest account) throws NoSuchAlgorithmException {
        // if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
        //     return ResponseEntity.badRequest().body(null);
        // }
        ResponseEntity<accountResponseDTO> response = accountService.login(account);
        System.out.println("Login response: " + response.getBody());  // Log the response body
        return response;
    }

    /**
     * Retrieves the stream key for the authenticated user.
     *
     * @param token the authorization token provided in the request header
     * @return Response Entity containing the list of following accounts or an error response if unauthorized
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
    @GetMapping("/auth/following")
    public ResponseEntity<accountResponseDTO> getFollowing(@RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getFollowing(token);
    }

    /**
     * This Java function retrieves the follower count for a user account using the provided
     * authorization token.
     *
     * @param token The `token` parameter in the `getFollowerCount` method is a string that represents the authorization token passed in the request header. This token is used for authentication and authorization purposes to ensure that the user making the request is allowed to access the follower count information.
     * @return The method `getFollowerCount` in the controller class is returning a `ResponseEntity` object containing an `accountResponseDTO` object.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    @GetMapping("/auth/follower")
    public ResponseEntity<accountResponseDTO> getFollowerCount(@RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getFollower(token);
    }


    /**
     * Retrieves the stream key for the authenticated user.
     *
     * @param token the authorization token provided in the request header
     * @return Response Entity containing the stream key or an error response if unauthorized
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
    @GetMapping("/auth/streamkey")
    public ResponseEntity<accountResponseDTO> getStreamKey(@RequestHeader("Authorization") String token) throws NoSuchAlgorithmException {
        return accountService.getStreamKey(token);
    }

    /**
     * Updates user account information based on the specified type parameter.
     *
     * @param token   the authorization token provided in the request header
     * @param type    the type of update to perform (e.g., "streamkey", "title", "description", etc.)
     * @param account the request body containing the data for the update                (must not contain SQL injection patterns)
     * @return Response Entity with the status of the update operation
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
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

    /**
     * Adds or removes a stream from the user's following list.
     *
     * @param streamId the ID of the stream to follow or unfollow
     * @param account  the request body containing the user's credentials (username and password)                (must not contain SQL injection patterns)
     * @return Response Entity with the status of the follow or unfollow operation
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
    @PutMapping("/auth/follow/streamId")
    public ResponseEntity<String> follow(@PathVariable String streamId, @RequestBody accountRequest account) throws NoSuchAlgorithmException{
        if(Security.containsSQLInjection(account.name()) || Security.containsSQLInjection(account.password())){
            return ResponseEntity.badRequest().body(null);
        }
        return accountService.addToFollowing(account, streamId);
    }

    /**
     * Deletes the authenticated user's account.
     *
     * @param token the authorization token provided in the request header
     * @return Response Entity with the status of the account deletion operation
     * @throws NoSuchAlgorithmException if a required hashing algorithm is unavailable
     */
    @DeleteMapping("/auth/delete")
    public ResponseEntity<accountResponseDTO> delete(@RequestHeader("Authorization") String token) throws NoSuchAlgorithmException{
        return accountService.deleteAccount(token);
    }
}
