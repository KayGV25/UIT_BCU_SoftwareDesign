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

@Service
public class LivestreamService {

    @Autowired
    private accountRepository accountRepository;

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

    public boolean isValidStreamKey(String streamKey) {
        return accountRepository.existsByStreamKey(streamKey);
    }

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