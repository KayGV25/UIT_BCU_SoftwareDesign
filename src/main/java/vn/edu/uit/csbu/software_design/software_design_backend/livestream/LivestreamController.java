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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stream")
public class LivestreamController {

    @Autowired
    LivestreamService livestreamService;

    @Autowired
    accountRepository accountRepository;

    @Value("${app.streamserver.ip}")
    private String streamServerIp;

    @Operation(summary = "Stream a video from the Nginx HLS server", description = "Proxies the video stream content from the Nginx HLS server based on the provided streamId.")
    @GetMapping("/watch")
    @ResponseStatus(HttpStatus.OK)
    public void stream(
            @Parameter(description = "The HTTP response object to send the stream content back to the client.") 
            HttpServletResponse response,
            @Parameter(description = "The unique identifier for the stream, used to look up the stream key and verify stream validity.") 
            @RequestParam String streamId) {

        Optional<accountModel> streamer = accountRepository.findByName(streamId);
        if(streamer.isPresent()){
            String streamKey = streamer.get().getStreamKey();
            if(livestreamService.isStreamLive(streamServerIp, streamKey)){
                String nginxStreamUrl = "http://"+ streamServerIp + ":8088/hls/" + streamKey + ".m3u8";
                try (@SuppressWarnings("deprecation")
                     InputStream is = new URL(nginxStreamUrl).openStream();
                     OutputStream os = response.getOutputStream()) {

                    response.setHeader("Access-Control-Allow-Origin", "*"); 
                    response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                    response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
                    response.setContentType("application/vnd.apple.mpegurl");

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Operation(summary = "Stream content from an Nginx HLS server using the stream key file", description = "Streams video content from the server based on the provided stream key.")
    @GetMapping("/{streamKeyFile}")
    public void streaming(
            @Parameter(description = "The HTTP response object to send the stream content back to the client.") 
            HttpServletResponse response,
            @Parameter(description = "The unique identifier (stream key) for the stream to be fetched from the Nginx HLS server.") 
            @PathVariable String streamKeyFile) {

        if(Security.containsSQLInjection(streamKeyFile)){
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }

        String nginxStreamUrl = "http://" + streamServerIp + ":8088/hls/" + streamKeyFile;
        try (@SuppressWarnings("deprecation")
             InputStream is = new URL(nginxStreamUrl).openStream();
             OutputStream os = response.getOutputStream()) {

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
            response.setContentType("video/mp2t");

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Check if a specific stream is live", description = "Checks whether the stream with the provided name is live.")
    @GetMapping("/isStreamLive/{streamName}")
    public Boolean isStreamLive(
            @Parameter(description = "The name of the stream to check its live status.") 
            @PathVariable String streamName) {

        return livestreamService.isStreamLive(streamServerIp, streamName);
    }

    @Operation(summary = "Check if multiple streams are live", description = "Takes a list of stream names and checks if each stream is live.")
    @PostMapping("/isStreamsLive")
    public List<Boolean> isLive(
            @Parameter(description = "A list of stream names to check if they are live.") 
            @RequestBody Livestreams streamNameList) {

        return livestreamService.isStreamsLive(streamServerIp, streamNameList.streamNames);
    }

    @Operation(summary = "Retrieve current streaming accounts", description = "Fetches a list of accounts that are currently streaming, with optional pagination.")
    @GetMapping("/streaming")
    public List<accountSecureResponseDTO> getCurrentStreaming(
            @Parameter(description = "The page number to fetch for paginated results. Defaults to 1 if not provided.") 
            @RequestParam(required = false) Integer page) {

        if(page == null) page = 1;
        return livestreamService.getCurrentStreaming(page, streamServerIp);
    }

    @Operation(summary = "Validate a stream key from Nginx server", description = "Validates a provided stream key by checking if it's registered and valid.")
    @PostMapping("/validate")
    public ResponseEntity<String> validateStreamKey(
            @Parameter(description = "The body of the RTMP request, including the stream key to validate.") 
            @RequestBody MultiValueMap<String, String> rtmpBody) {

        if (livestreamService.isValidStreamKey(rtmpBody.getFirst("name"))) {
            return ResponseEntity.ok("Valid stream key");
        }
        return ResponseEntity.status(403).body("Invalid stream key");
    }
}
