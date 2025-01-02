package vn.edu.uit.csbu.software_design.software_design_backend.account;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
import vn.edu.uit.csbu.software_design.software_design_backend.jwt.JWTUtil;

/**
 * The `accountService` class in Java provides various methods for managing user accounts, including
 * authentication, account creation, updating account details, and deleting accounts.
 */
@Service
public class accountService {
    /**
     * The Security.
     */
    Security security = new Security();
    
    @Autowired
    private accountRepository accountRepository;

    @Autowired
    private followingRepository followingRepository;

    @Value("$secret")
    private String secret;

    /**
     * The function `getAccountToken` retrieves an account model based on a token by extracting the
     * name from the token and querying the account repository.
     *
     * @param token A string representing a token.
     * @return An Optional object containing an accountModel is being returned.
     */
    Optional<accountModel> getAccountToken(String token){
        return accountRepository.findByName(JWTUtil.extractName(token));
    }

    /**
     * The function `getAccount` returns an `Optional` containing an `accountModel` found by name in
     * the `accountRepository`.
     *
     * @param name The `name` parameter is a String representing the name of the account that you want to retrieve from the account repository.
     * @return An Optional object containing an accountModel is being returned.
     */
    Optional<accountModel> getAccount(String name){
        return accountRepository.findByName(name);
    }

    /**
     * The function `getStreamKey` retrieves the stream key for a user account based on a provided
     * token.
     *
     * @param token A token used for authentication and authorization.
     * @return The method `getStreamKey` returns a `ResponseEntity` containing an `accountResponseDTO` object. If the account is found in the database, it returns a successful response with the stream key in the body. If the account is not found or the token is invalid, it returns a bad request response with an error message in the body.
     */
    ResponseEntity<accountResponseDTO> getStreamKey(String token){
        Optional<accountModel> dbAccount = accountRepository.findByName(JWTUtil.extractName(token));
        if(dbAccount.isPresent()){
            return ResponseEntity.ok().body(new accountResponseDTO(dbAccount.get().getStreamKey(), null, accountResponseType.DATA));
        }
        return ResponseEntity.badRequest().body(new accountResponseDTO("Invalid Token or Account not Found", null, accountResponseType.RESPONSE));
    }

    /**
     * The function `addAccount` checks if an account with a given name already exists, and if not,
     * creates a new account with a unique UUID, password, and stream key.
     *
     * @param account The `addAccount` method takes an `accountRequest` object as a parameter. The `accountRequest` object likely contains information about a new account being created, such as the account name and password.
     * @return The method `addAccount` returns a `ResponseEntity<String>`. If an account with the same name already exists, it returns a response with status code 409 (CONFLICT) and a message indicating that the account already exists. If a new account is successfully created, it returns a response with status code 200 (OK) and a message indicating that the account was created.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    ResponseEntity<String> addAccount(accountRequest account) throws NoSuchAlgorithmException{
        if(accountRepository.findByName(account.name()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account with name " + account.name() + " already exists.");
        }
        else{
            String pass = security.toHexString(security.getSHA(account.name()+account.password()+secret));
            String streamKey = security.getHashedStringOfLength(account.name() + Calendar.getInstance().getTimeInMillis(), 32);
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

    /**
     * The login function checks if the provided account credentials are valid and returns a response
     * with a JWT token if successful.
     *
     * @param account The `account` parameter in the `login` method is of type `accountRequest`, which likely contains information such as the user's name and password for authentication. The method first attempts to find a corresponding account in the database based on the provided name. If the account is found, it then checks
     * @return The method `login` returns a `ResponseEntity` containing an `accountResponseDTO`. The response can be one of the following: 1. If the account is found in the database and the password matches, it returns a successful response with a token in the `accountResponseDTO`. 2. If the password is incorrect, it returns a `FORBIDDEN` status with a message "Wrong password
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    ResponseEntity<accountResponseDTO> login(accountRequest account) throws NoSuchAlgorithmException{
        Optional<accountModel> dbAccount = accountRepository.findByName(account.name());
        if(dbAccount.isPresent()){
            String reqPassHashed = security.toHexString(security.getSHA(account.name()+account.password()+secret));
            if(reqPassHashed.equals(dbAccount.get().getPassword())){
                String token = JWTUtil.generateToken(dbAccount.get().getName());
                return ResponseEntity.ok(new accountResponseDTO(token, null, accountResponseType.DATA));
            }
            else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new accountResponseDTO("Wrong password", null, accountResponseType.RESPONSE));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new accountResponseDTO("No Account Found", null, accountResponseType.RESPONSE));
    }

    /**
     * This Java function updates the stream key for an account based on a provided token and returns a
     * response entity with the updated information.
     *
     * @param token A unique identifier used to authenticate and authorize the user.
     * @return The method `updateStreamKey` returns a `ResponseEntity` object containing an `accountResponseDTO` with a message indicating whether the stream key was updated successfully or not. If the account corresponding to the provided token is found in the database, the method returns a response with status code 200 (OK) and a message "Streamkey Updated". If the account is not found, it returns a
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    ResponseEntity<accountResponseDTO> updateStreamKey(String token) throws NoSuchAlgorithmException {
        Optional<accountModel> dbAccount = getAccountToken(token);
        if(dbAccount.isPresent()){
            String streamKey = security.getHashedStringOfLength(dbAccount.get().getName() + Calendar.getInstance().getTimeInMillis(), 32);
            dbAccount.get().setStreamKey(streamKey);
            accountRepository.save(dbAccount.get());
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Streamkey Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("Streamkey Updated", null, accountResponseType.RESPONSE));
    }

    /**
     * This Java function retrieves the list of accounts that a user is following based on the provided
     * token.
     *
     * @param token The `getFollowing` method takes a token as a parameter. This token is used to retrieve the account information associated with it from the database. If the account is found, the method then retrieves the list of accounts that the current account is following and returns them in a response entity.
     * @return The method `getFollowing` returns a `ResponseEntity` object containing an `accountResponseDTO` object. The `accountResponseDTO` object includes a list of accounts that the current user is following, along with a response message indicating success or failure. If the user account is found in the database, the method returns a response with HTTP status OK and the list of following accounts. If the user
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    ResponseEntity<accountResponseDTO> getFollowing(String token) throws NoSuchAlgorithmException{
        Optional<accountModel> dbAccount = getAccountToken(token);
        if(dbAccount.isPresent()){
            ArrayList<accountModel> followingList = new ArrayList<>();
            for(String id:dbAccount.get().getFollowingStreamId()){
                Optional<accountModel> following = accountRepository.findById(id);
                if(following.isPresent()){
                    followingList.add(following.get());
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO(followingList, null, accountResponseType.DATA));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found", null, accountResponseType.RESPONSE));
    }

    /**
     * The function `getFollower` retrieves follower information for an account based on a provided
     * token.
     * 
     * @param token A token used for authentication and authorization purposes.
     * @return The method `getFollower` is returning a `ResponseEntity` object with a generic type of
     * `accountResponseDTO`. The response entity contains either a successful response with HTTP status
     * OK and a body containing the follower count retrieved from the database, or a bad request
     * response with a message indicating that no account was found.
     */
    ResponseEntity<accountResponseDTO> getFollower(String token) throws NoSuchAlgorithmException{
        Optional<accountModel> dbAccount = getAccountToken(token);
        if(dbAccount.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO(dbAccount.get().getFollowerCount().toString(), null, accountResponseType.DATA));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found", null, accountResponseType.RESPONSE));
    }

    /**
     * This Java function adds or removes a streamer from a user's following list based on
     * authentication and returns a corresponding response.
     *
     * @param account  The `account` parameter in the `addToFollowing` method is of type `accountRequest`, which likely contains information about a user account such as the account name and password.
     * @param streamId The `streamId` parameter in the `addToFollowing` method represents the unique identifier of the streamer that the account wants to follow or unfollow. This identifier is used to associate the account with the streamer in the database when adding or removing a following relationship.
     * @return The method `addToFollowing` returns a `ResponseEntity<String>`. The method checks if the account exists in the database, verifies the password, and then either adds or removes a streamer from the following list. The method returns a response entity with a message indicating whether the streamer was followed or unfollowed, or if there was an issue such as a wrong password or no account found.
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
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

    /**
     * The function `updateTitle` takes a token and optional data to update the title of an account,
     * checking for XSS attacks before saving the changes and returning a response.
     *
     * @param token A token is a piece of data that is used to authenticate a user and provide access to a system or service. It is often used in web applications to verify the identity of a user before allowing them to perform certain actions. In this context, the token is likely a JWT (JSON Web Token)
     * @param data  The `data` parameter in the `updateTitle` method is an optional string that represents the new title that you want to update for an account. If the `data` parameter is present and not empty, the method will update the title of the account with the provided data. If the `data
     * @return The method `updateTitle` returns a `ResponseEntity` containing an `accountResponseDTO`. The response can have different HTTP status codes and messages based on the conditions checked in the method. If the update is successful, it returns a response with status code 200 (OK) and a message "Title Updated". If there is a security concern with XSS detected in the data, it returns a response
     */
    public ResponseEntity<accountResponseDTO> updateTitle(String token, Optional<String> data) {
        Optional<accountModel> dbAccount = getAccount(JWTUtil.extractName(token));
        if(dbAccount.isPresent() && data.isPresent()){
            if(Security.containsXSS(data.get())) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new accountResponseDTO("XSS detected", null, accountResponseType.RESPONSE));
            accountModel accountModel = dbAccount.get();
            accountModel.setTitle(data.get());
            accountRepository.save(accountModel);
            return ResponseEntity.status(HttpStatus.OK).body(new accountResponseDTO("Title Updated", null, accountResponseType.RESPONSE));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new accountResponseDTO("No Account Found or Title data is null", null, accountResponseType.RESPONSE));
    }

    /**
     * The function `updateDescription` takes a token and optional data to update the description of an
     * account, checking for XSS attacks before saving the changes.
     *
     * @param token A token is a piece of data that is used to authenticate a user and provide access to a system or service. It is often used in web applications to verify the identity of a user before allowing them to perform certain actions. In this context, the token is likely a JWT (JSON Web Token)
     * @param data  The `data` parameter in the `updateDescription` method is an optional string that represents the new description that will be updated for the account. It is passed as an optional to handle cases where the description data may be null. If the `data` parameter is present and not null, the method
     * @return The method `updateDescription` returns a `ResponseEntity` containing an `accountResponseDTO` object. The response can have different HTTP status codes and messages based on the conditions met in the method:
     */
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

    /**
     * The function `updatePassword` takes a token and optional password data, updates the password for
     * the account associated with the token, and returns a response entity with the result.
     *
     * @param token A token used for authentication and authorization purposes.
     * @param data  The `data` parameter in the `updatePassword` method represents the new password that the user wants to update in their account. It is an optional parameter, meaning it may or may not be present when the method is called. If present, the method will update the password for the account associated with
     * @return A `ResponseEntity` object with a `accountResponseDTO` containing a message indicating whether the password was updated successfully or if there was an issue (such as XSS detection or missing data). The HTTP status code of the response varies based on the outcome (OK for successful update, NOT_ACCEPTABLE for XSS detection, and BAD_REQUEST for missing account or password data).
     */
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

    /**
     * The function `updateUsername` takes a token and optional data to update the username in the
     * database, handling XSS detection and existing username conflicts.
     *
     * @param token A token is a piece of data that is used to authenticate a user and provide access to a system or service. In this context, the token is likely a JWT (JSON Web Token) that contains information about the user's identity and permissions. It is extracted using the `JWTUtil.extractName(token
     * @param data  The `data` parameter in the `updateUsername` method represents the new username that you want to update for the account. It is an optional parameter, meaning it may or may not have a value. If it has a value, the method will attempt to update the username of the account with the
     * @return This method returns a `ResponseEntity` containing an `accountResponseDTO` object. The response can have different HTTP status codes and corresponding messages based on the conditions checked in the method:
     */
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

    /**
     * The `deleteAccount` function deletes an account and related data if it exists based on the
     * provided token.
     *
     * @param token The `token` parameter in the `deleteAccount` method is a string that represents the authentication token used to identify the user account that is being deleted. This token is typically generated during the user authentication process and is used to authorize and authenticate the user's actions.
     * @return The method `deleteAccount` returns a `ResponseEntity` object with a generic type of `accountResponseDTO`. The response body contains a message indicating whether the account was successfully deleted or if no account was found. The HTTP status code of the response is either 200 (OK) if the account was deleted, or 404 (NOT FOUND) if no account was found.
     */
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
