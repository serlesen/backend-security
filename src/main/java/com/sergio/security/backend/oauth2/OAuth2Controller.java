package com.sergio.security.backend.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@RestController
public class OAuth2Controller {

    @Value("${spring.security.oauth2.resourceserver.jwt.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.jwt.clientSecret}")
    private String clientSecret;

    @Value("${auth.idpUri}")
    private String idpUri;

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @GetMapping("/auth/oauth2/url")
    public ResponseEntity<UrlDto> auth() {
        String url = idpUri +
                "/oauth2/authorize?" +
                "response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=http://localhost:4200/oauth2/idpresponse" +
                "&scope=email+openid+profile";
        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/auth/oauth2/callback")
    public ResponseEntity<TokenDto> callback(@RequestParam("code") String code) throws URISyntaxException {

        String urlStr = idpUri + "/oauth2/token?"
                + "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&code=" + code +
                "&redirect_uri=http://localhost:4200/oauth2/idpresponse";

        String authenticationInfo = clientId + ":" + clientSecret;
        String basicAuthenticationInfo = Base64.getEncoder().encodeToString(authenticationInfo.getBytes());

        HttpRequest request;
        try {
            request = HttpRequest.newBuilder(new URI(urlStr))
                    .header("Content-type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + basicAuthenticationInfo)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to build IDP URL");
        }

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Unable to request IDP");
        }

        if (response.statusCode() != 200) {
            throw new RuntimeException("Authentication failed");
        }

        TokenResponseDto token;
        try {
            token = JSON_MAPPER.readValue(response.body(), TokenResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to decode Token response");
        }

        return ResponseEntity.ok(new TokenDto(token.id_token()));
    }
}
