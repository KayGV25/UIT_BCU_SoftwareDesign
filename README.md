# SETUP guide
## A few words
- **Hi there**, this is a small project to help you get familiar with setting **NGINX** to make it a rtmp livestreaming server and code to run it in the backend of your website
- You can use this as a reference
### Project information
- **Java Spring Boot** for backend server
- **NGINX** on **Ubuntu** (I'm using Ubuntu 22.04 for this project) or any Unix-base operating system that support NGINX 
> Author: **KaygV**

## Setting your NGINX server
- Here i'm following a guide from **DigitalOcean** (Source: [DigitalOcean Guide](https://www.digitalocean.com/community/tutorials/how-to-set-up-a-video-streaming-server-using-nginx-rtmp-on-ubuntu-20-04))
### Intstalling and Configuring Nginx-RTMP
#### Installing
- Most of the time Nginx RTMP modules does not come along with Nginx but from Ubuntu 22.04 you can install it as a additional package 
```bash
sudo apt update
sudo apt install libnginx-mod-rtmp
```
#### Configuring
- After you have downloaded the package use the command below to start configuring Nginx to run RTMP server 
```bash
# This will open the editor for you to edit the configuration file
# You can use vim or nvim for better expirience
sudo nano /etc/nginx/nginx.conf
```
- Add this to the end of the file
```apacheconf 
# /etc/nginx/nginx.conf
. . .
rtmp {
        server {
                listen 1935;
                chunk_size 4096;

                # allow publish 127.0.0.1;
                # deny publish all;

                # Not recommended but you can do this in order to enable multiple user 
                # to stream at the same time
                allow publish all;

                application live {
                        live on;

                        # Call Java server for authentication before accepting stream
                        on_publish http://<JAVA_SERVER_IP>:8080/api/stream/validate;
                        record off;
                }
        }
}
```
- `listen 1935` means that RTMP will be listening for connections on port 1935 (standard)
- `chunk_size 4096` means that RTMP will be sending data in 4KB blocks (standard)
- `allow publish 127.0.0.1` and `deny publish all` mean that the server will only allow video to be published from the same server, to avoid any other users pushing their own streams.
    - Alternatively you can add `allow publish all` to make everybody have the ability to access the rtmp server
- `application live` defines an application block that will be available at the /live URL path.
- `live on` enables live mode so that multiple users can connect to your stream concurrently, a baseline assumption of video streaming.
- `record off` disables Nginx-RTMP’s recording functionality, so that all streams are not separately saved to disk by default

#### Running
- By default, it listens on port `1935`, which means you’ll need to open that port in your firewall. If you configured ufw as part of your initial server setup run the following command
```bash
sudo ufw allow 1935/tcp
```
- Check the Nginx config file syntax
```bash
sudo nginx -t
```
- Reload Nginx with changes
```bash
sudo systemctl reload nginx.service
```

## Setting your JAVA SPRING BOOT server
### If you are pulling this whole project to use
- The only thing you need to change is the application.properties in the resource folder
    - Change the streamserver.ip to your rtmp server ip

### If you are building from scratch
#### Packages used
- Spring Web
#### Document
- To be updated