package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stream")
public class LivestreamController {

    // Livestream manager class
    LivestreamManager livestreamManager = new LivestreamManager();

    // Static ip of the nginx server
    @Value("${app.streamserver.ip}")
    private String streamServerIp;
    
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void stream(HttpServletResponse response,
                    //    @RequestParam String token,
                       @RequestParam String streamId) {
        // Validate the token and streamId (you can use the previous JWT-based validation method)

        // If valid, proxy the request to the actual Nginx HLS stream
        String streamKey;
        // This will be checked in the data base
        // Need to have a condition where streams is offline
        if(streamId.equals("hiddenKey")){
            // Set the streaming key which was achieved by quering the database
            streamKey = "demostream";

            if(livestreamManager.isStreamLive(streamServerIp, streamKey)){
                String nginxStreamUrl = "http://"+ streamServerIp + ":8080/hls/" + streamKey + ".m3u8";
                // System.out.println(nginxStreamUrl);
        
                // Set up the response to proxy the stream content
                try (@SuppressWarnings("deprecation")
                    InputStream is = new URL(nginxStreamUrl).openStream();
                    OutputStream os = response.getOutputStream()) {
        
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

        String nginxStreamUrl = "http://" + streamServerIp + ":8080/hls/" + streamKeyFile;

        // Set up the response to proxy the stream content
        try (@SuppressWarnings("deprecation")
            InputStream is = new URL(nginxStreamUrl).openStream();
            OutputStream os = response.getOutputStream()) {

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
