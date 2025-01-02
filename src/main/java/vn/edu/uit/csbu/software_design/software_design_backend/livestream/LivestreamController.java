package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import vn.edu.uit.csbu.software_design.software_design_backend.Security;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountModel;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountRepository;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountSecureResponseDTO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * The `LivestreamController` class in Java handles streaming requests, proxies them to an Nginx
 * server, and includes methods for checking stream status and validating stream keys.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stream")
public class LivestreamController {

    /**
     * The Livestream service.
     */
// Livestream manager class
    @Autowired
    LivestreamService livestreamService;
    /**
     * The Account repository.
     */
    @Autowired
    accountRepository accountRepository;

    // Static ip of the nginx server
    @Value("${app.streamserver.ip}")
    private String streamServerIp;

    /**
     * The function `stream` handles streaming requests by validating the stream ID, checking if the
     * stream is live, and proxying the request to an Nginx HLS stream if valid.
     *
     * @param response The `response` parameter in the `stream` method is an object of the `HttpServletResponse` class. It is used to manipulate the HTTP response that will be sent back to the client making the request. In this method, it is used to set headers, set content type, and write the
     * @param streamId The `streamId` parameter in the `stream` method of the `WatchController` class is used to identify the streamer whose video stream is being requested. This parameter is passed as a request parameter in the URL when accessing the `/watch` endpoint.
     */
    @GetMapping("/watch")
    @ResponseStatus(HttpStatus.OK)
    public void stream(HttpServletResponse response,
                    //    @RequestParam String token,
                       @RequestParam String streamId) {
        // Validate the token and streamId (you can use the previous JWT-based validation method)

        // If valid, proxy the request to the actual Nginx HLS stream
        String streamKey;
        // if(Security.containsSQLInjection(streamId)){
        //     response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        // }
        Optional<accountModel> streamer = accountRepository.findByName(streamId);
        // This will be checked in the data base
        if(streamer.isPresent()){
            // Set the streaming key which was achieved by quering the database
            streamKey = streamer.get().getStreamKey();

            if(livestreamService.isStreamLive(streamServerIp, streamKey)){
                String nginxStreamUrl = "http://"+ streamServerIp + ":8088/hls/" + streamKey + ".m3u8";        
                // Set up the response to proxy the stream content
                try (@SuppressWarnings("deprecation")
                    InputStream is = new URL(nginxStreamUrl).openStream();
                    OutputStream os = response.getOutputStream()) {

                    response.setHeader("Access-Control-Allow-Origin", "*"); // Allow all origins
                    response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
        
                    response.setContentType("application/vnd.apple.mpegurl");
                    byte[] buffer = new byte[8192];
                    int bytesRead;
        
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    // e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        }
        else{
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * The function streams content from an Nginx HLS server based on a provided stream key file, with
     * validation and proxying mechanisms in place.
     *
     * @param response      The `response` parameter in the `streaming` method is of type `HttpServletResponse`. It is used to manipulate the HTTP response that will be sent back to the client. In this method, it is used to set headers for allowing cross-origin requests, setting the content type of the response,
     * @param streamKeyFile The `streamKeyFile` parameter in the `streaming` method represents the unique identifier or key associated with the HLS stream that the client is requesting to stream. This identifier is used to construct the URL for the actual Nginx HLS stream that will be proxied to the client.
     */
    @GetMapping("/{streamKeyFile}")
    public void streaming(HttpServletResponse response,
                    //    @RequestParam String token,
                       @PathVariable String streamKeyFile) {
        // Validate the token and streamId (you can use the previous JWT-based validation method)

        // If valid, proxy the request to the actual Nginx HLS stream
        if(Security.containsSQLInjection(streamKeyFile)){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
        String nginxStreamUrl = "http://" + streamServerIp + ":8088/hls/" + streamKeyFile;
        // Set up the response to proxy the stream content
        try (@SuppressWarnings("deprecation")
            InputStream is = new URL(nginxStreamUrl).openStream();
            OutputStream os = response.getOutputStream()) {

            response.setHeader("Access-Control-Allow-Origin", "*"); // Allow all origins
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
            response.setContentType("video/mp2t");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The function checks if a specified stream is live on a livestream service.
     *
     * @param streamName The `streamName` parameter is a path variable that is passed in the URL to identify the specific stream for which we want to check if it is live or not.
     * @return A Boolean value indicating whether the stream with the given streamName is live or not.
     */
    @GetMapping("/isStreamLive/{streamName}")
    public Boolean isStreamLive(@PathVariable String streamName) {
        return livestreamService.isStreamLive(streamServerIp, streamName);
    }


    /**
     * The function `isLive` takes a list of stream names as input and returns a list of booleans
     * indicating whether each stream is live or not.
     *
     * @param streamNameList The `streamNameList` parameter in the `isLive` method is of type `Livestreams`, which is expected to be passed in the request body. It likely contains a list of stream names or identifiers that you want to check for live status.
     * @return A list of boolean values indicating whether each stream in the provided list is live or not.
     */
    @PostMapping("/isStreamsLive")
    public List<Boolean> isLive(@RequestBody Livestreams streamNameList) {
        return livestreamService.isStreamsLive(streamServerIp, streamNameList.streamNames);
    }

    /**
     * This Java function retrieves the current streaming accounts with an optional page parameter.
     *
     * @param page The `page` parameter is used to specify the page number for retrieving the current streaming data. It is an optional parameter, as indicated by `@RequestParam(required = false)`. If the `page` parameter is not provided in the request, the default value of 1 is used.
     * @return The method `getCurrentStreaming` is returning a list of `accountSecureResponseDTO` objects.
     */
    @GetMapping("/streaming")
    public List<accountSecureResponseDTO> getCurrentStreaming(@RequestParam(required = false) Integer page) {
        if(page == null) page = 1;
        else page = page.intValue();
        return livestreamService.getCurrentStreaming(page, streamServerIp);
    }


    /**
     * Validate steam key response entity.
     *
     * @param rtmpBody the rtmp body
     * @return the response entity
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validateSteamKey(@RequestBody MultiValueMap<String, String> rtmpBody) {
        // System.out.println(rtmpBody.getFirst("name"));
        // Logic to validate stream key from a database or in-memory store
        if (livestreamService.isValidStreamKey(rtmpBody.getFirst("name"))) {
            // System.out.println("ok");
            return ResponseEntity.ok("Valid stream key");
        }
        return ResponseEntity.status(403).body("Invalid stream key");
    }
}
