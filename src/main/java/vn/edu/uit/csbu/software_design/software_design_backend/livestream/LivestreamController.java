package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountModel;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountRepository;

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


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stream")
public class LivestreamController {

    // Livestream manager class
    @Autowired
    LivestreamService livestreamService;
    @Autowired
    accountRepository accountRepository;

    // Static ip of the nginx server
    @Value("${app.streamserver.ip}")
    private String streamServerIp;
    
    @GetMapping("/watch")
    @ResponseStatus(HttpStatus.OK)
    public void stream(HttpServletResponse response,
                    //    @RequestParam String token,
                       @RequestParam String streamId) {
        // Validate the token and streamId (you can use the previous JWT-based validation method)

        // If valid, proxy the request to the actual Nginx HLS stream
        String streamKey;
        Optional<accountModel> streamer = accountRepository.findByName(streamId);
        // This will be checked in the data base
        if(streamer.isPresent()){
            // Set the streaming key which was achieved by quering the database
            streamKey = streamer.get().getStreamKey();

            if(livestreamService.isStreamLive(streamServerIp, streamKey)){
                String nginxStreamUrl = "http://"+ streamServerIp + ":8088/hls/" + streamKey + ".m3u8";
                // System.out.println(nginxStreamUrl);
        
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
            throw new LivestreamNotFoundException();
        }
    }

    @GetMapping("/{streamKeyFile}")
    public void streaming(HttpServletResponse response,
                    //    @RequestParam String token,
                       @PathVariable String streamKeyFile) {
        // Validate the token and streamId (you can use the previous JWT-based validation method)

        // If valid, proxy the request to the actual Nginx HLS stream

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

    @GetMapping("/isStreamLive/{streamName}")
    public Boolean isStreamLive(@PathVariable String streamName) {
        return livestreamService.isStreamLive(streamServerIp, streamName);
    }
    

    @PostMapping("/isStreamsLive")
    public List<Boolean> isLive(@RequestBody Livestreams streamNameList) {
        return livestreamService.isStreamsLive(streamServerIp, streamNameList.streamNames);
    }
    
    
    // For validation from rtmp server
    // Check if the streaming key is in the database
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
