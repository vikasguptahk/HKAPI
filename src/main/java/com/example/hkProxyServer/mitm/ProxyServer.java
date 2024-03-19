package com.example.hkProxyServer.mitm;

import com.example.hkProxyServer.model.Apicalls;

import com.example.hkProxyServer.repository.ApicallRepo;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.channel.ChannelHandlerContext;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import org.littleshoot.proxy.mitm.CertificateSniffingMitmManager;
import org.littleshoot.proxy.mitm.RootCertificateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;


@Component
public class ProxyServer {
    int i=0;

    @Autowired
    private ApicallRepo apicallRepo;

    private HttpProxyServer proxyServer;

    private final Pattern apiPattern = Pattern.compile("^/api/.*$");

    public void start(int port) throws RootCertificateException {

        System.setProperty("javax.net.ssl.debug","ssl");
        //load keystore
        System.setProperty("javax.net.ssl.trustStore","/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts");
        String truststore = System.getProperty("javax.net.ssl.trustStore");
        if(truststore == null) {
            System.out.println("null truststore viasl");
        }
        System.out.println("Truststore: " + truststore);


       /* String keystorePath = Objects.requireNonNull(getClass().getClassLoader().getResource("mykeystore.jks")).getPath();
        System.setProperty("javax.net.ssl.keyStore", keystorePath);

        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists() || !keystoreFile.canRead()) {
            throw new RuntimeException("Keystore file is not accessible");
        }
        else System.out.println("keystore file is accesssible vikas");
        System.setProperty("javax.net.ssl.keyStore", keystorePath);
        //System.setProperty("javax.net.ssl.keyStore","/usr/lib/jvm/java-17-openjdk-amd64/lib/security/cacerts");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStore", keystorePath);
        System.setProperty("javax.net.ssl.trustStorePassword","changeit");*/


        proxyServer = DefaultHttpProxyServer.bootstrap()
                .withPort(port)
                .withTransparent(true)
                //.withManInTheMiddle(new SelfSignedMitmManager())
                //.withManInTheMiddle(new CertificateSniffingMitmManager())
                .withFiltersSource(new HttpFiltersSourceAdapter(){
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest,ChannelHandlerContext ctx){
                        return new HttpFiltersAdapter(originalRequest, ctx){

                            Apicalls apicalls = new Apicalls();
                            @Override
                            public HttpResponse clientToProxyRequest(HttpObject httpObject){
                                //System.out.println("truststoreSL:"+System.getProperty("javax.net.ssl.trustStore"));
                                //System.out.println("request: " + httpObject);
                                //i++;
                                //if(i<100) System.out.println(httpObject.toString());
                                String uri = originalRequest.uri();
                                //Apicalls apicalls = new Apicalls();
                                apicalls.setApicall_id(i);i++;
                                apicalls.setRequest_url(uri);
                                apicalls.setRequest_method(originalRequest.method().toString());
                                apicalls.setRequest_headers(originalRequest.headers().toString());
                                apicalls.setTime(LocalDateTime.now());
                                apicalls.setReferrer_policy(originalRequest.headers().get("Referrer-Policy"));
                                apicalls.setRemote_address(ctx.channel().remoteAddress().toString());
                                if(apiPattern.matcher(uri).matches()){
                                    System.out.println("API calls: "+uri);
                                }
                                return null;
                            }
                            @Override
                            public HttpObject serverToProxyResponse(HttpObject httpObject){
                                System.out.println("response: "+httpObject);
                                if(httpObject instanceof HttpResponse){
                                    HttpResponse httpResponse = (HttpResponse) httpObject;
                                    int statusCode = httpResponse.status().code();
                                    apicalls.setStatus_code(statusCode);
                                    apicalls.setResponse(httpResponse.toString());
                                    apicallRepo.save(apicalls);
                                }
                                return null;
                            }
                        };
            }
                    @Override
                    public int getMaximumRequestBufferSizeInBytes() {
                        return 10 * 1024 * 1024; // 10 MB
                    }

                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return 10 * 1024 * 1024; // 10 MB
                    }
        })
                .start();
        System.out.println("Proxy server started on port " + port);
    }

    public void stop() {
        if (proxyServer != null) {
            proxyServer.stop();
            System.out.println("Proxy server stopped");
        }
    }
    //private static class SelfSignedMitmManager extends org.littleshoot.proxy.mitm.
}

