package vn.edu.uit.csbu.software_design.software_design_backend.livestream;

import java.net.HttpURLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import vn.edu.uit.csbu.software_design.software_design_backend.account.accountModel;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountRepository;
import vn.edu.uit.csbu.software_design.software_design_backend.account.accountSecureResponseDTO;

/**
 * The `LivestreamService` class contains methods to check if a stream is live, check multiple streams,
 * validate stream keys, and retrieve current streaming information.
 */
@Service
public class LivestreamService {

    @Autowired
    private accountRepository accountRepository;

    /**
     * The function `isStreamLive` checks if a specified stream is live on a server by making a GET
     * request to the server's status endpoint and parsing the XML response to find the stream name.
     *
     * @param serverIP   The `serverIP` parameter is a String representing the IP address of the server where the streaming service is hosted. This IP address is used to construct the URL for checking the status of the stream.
     * @param streamName The `streamName` parameter in the `isStreamLive` method refers to the name of the stream you want to check if it is live on the specified server. This method sends a GET request to the server's status endpoint and parses the XML response to determine if the specified stream is currently live
     * @return This method is checking if a stream with a specific name is live on a server with a given IP address. If the stream is found in the XML response from the server, the method returns true, indicating that the stream is live. If there are any exceptions during the process or if the stream is not found, the method returns false.
     */
    public boolean isStreamLive(String serverIP, String streamName) {
        try {
            String url = "http://" + serverIP + ":8088/status";
            @SuppressWarnings("deprecation")
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode <= 400) {
                // Parse the XML response
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(connection.getInputStream());
                doc.getDocumentElement().normalize();

                // Find all "stream" elements
                NodeList streams = doc.getElementsByTagName("stream");
                for (int i = 0; i < streams.getLength(); i++) {
                    Element stream = (Element) streams.item(i);
                    String name = stream.getElementsByTagName("name").item(0).getTextContent();
                    if (name.equals(streamName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The function `isStreamsLive` checks if a list of stream names are live on a server using a
     * provided server IP address.
     *
     * @param serverIP    The `serverIP` parameter is a String representing the IP address of the server where the streams are hosted.
     * @param streamNames The `streamNames` parameter is a list of strings that contains the names of streams that you want to check if they are live on the specified server.
     * @return A list of boolean values indicating whether each stream in the input list of stream names is live on the server with the specified IP address. Each boolean value corresponds to a stream name in the input list, where `true` indicates that the stream is live and `false` indicates that it is not live.
     */
    public List<Boolean> isStreamsLive(String serverIP, List<String> streamNames) {
        List<Boolean> returnLiveList = new ArrayList<>();
        HashSet<String> liveList = new HashSet<>();
        try {
            String url = "http://" + serverIP + ":8088/status";
            @SuppressWarnings("deprecation")
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Parse the XML response
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(connection.getInputStream());
                doc.getDocumentElement().normalize();

                // Find all "stream" elements
                NodeList streams = doc.getElementsByTagName("stream");
                for (int i = 0; i < streams.getLength(); i++) {
                    Element stream = (Element) streams.item(i);
                    liveList.add(stream.getElementsByTagName("name").item(0).getTextContent());
                }
            }

            for(String streamName:streamNames){
                if(liveList.contains(streamName)){
                    returnLiveList.add(true);
                }
                else{
                    returnLiveList.add(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnLiveList;
    }

    /**
     * The function `isValidStreamKey` checks if a given stream key exists in the account repository.
     *
     * @param streamKey Stream key is a unique identifier used to authenticate and authorize access to a streaming service or content. It is typically a long string of characters that is used to ensure that only authorized users can access the stream.
     * @return The method `isValidStreamKey` returns a boolean value indicating whether a stream key is valid or not based on whether it exists in the account repository.
     */
    public boolean isValidStreamKey(String streamKey) {
        return accountRepository.existsByStreamKey(streamKey);
    }

    /**
     * The function `getCurrentStreaming` retrieves live streaming information from a specified server
     * and returns a list of account details for the live streams.
     *
     * @param p        The parameter `p` in the `getCurrentStreaming` method represents the page number for pagination. It is used to calculate the offset for fetching a subset of streaming data.
     * @param serverIP The `serverIP` parameter in the `getCurrentStreaming` method represents the IP address of the server from which you want to retrieve the streaming data. This IP address is used to construct the URL for the HTTP request to fetch the streaming status from the server.
     * @return A list of `accountSecureResponseDTO` objects representing the current streaming accounts based on the provided page number `p` and server IP address.
     */
    public List<accountSecureResponseDTO> getCurrentStreaming(int p, String serverIP) {
        List<accountSecureResponseDTO> returnLiveList = new ArrayList<>();        
        HashSet<String> liveList = new HashSet<>();
        int offset = (p - 1) * 10;
        try {
            String url = "http://" + serverIP + ":8088/status";
            @SuppressWarnings("deprecation")
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                // Parse the XML response
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(connection.getInputStream());
                doc.getDocumentElement().normalize();

                // Find all "stream" elements
                NodeList streams = doc.getElementsByTagName("stream");
                for (int i = offset; i < offset + 10 && i < streams.getLength(); i++) {
                    Element stream = (Element) streams.item(i);
                    liveList.add(stream.getElementsByTagName("name").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String live:liveList){
            accountModel account = accountRepository.findByStreamKey(live).get();
            returnLiveList.add(new accountSecureResponseDTO(account.getId(), account.getName(), account.getTitle(), account.getDescription()));
        }
        return returnLiveList;
    }
}