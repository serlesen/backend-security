# 4 Authentication Systems with Spring Security

This project shows 4 ways to authenticate to a Spring Boot project using Spring Security 6.

For more details, check the video at: 

Each authentication method is implemented in a separate package.

For each authentication method, the users are created in memory with the class `InMemoryUserConfig`.

## Basic Authentication

The configuration for using the Basic Authentication is located in the package `com.sergio.security.backend.basic`.

The Basic authentication is the easiest one to implement.

Each request must contain the username and password to access the endpoints. This information is encrypted in base64 
and sent in an HTTP header.

The disadvantage of this authentication method is that the credentials are sent on each request. And if someone catches
a request, the information about the username and password is clearly readable, as it's only encoded in base64.

Here is the command to do it with curl:

```
curl localhost:8080/hi -u 'username:password'
```

## Session Authentication

The configuration for using the Session Authentication is located in the package `com.sergio.security.backend.session`.

The next simplest way to authenticate requests is using a session. For this workflow, the application needs an initial
request which validates the credentials of the user. Once the credentials are correctly validated, a session is created
and the authentication information is stored in it.

This session is stored in memory and identifiable with a cookie. Everytime the user sends the cookie to the backend,
Spring Security is able to retrieve the original session. If the user validated his credentials in a previous request,
he can access the authorized endpoints.

The advantage of this authentication method is that the credentials of the user are only sent one time. Then, all relies
on the cookie of the session. Nevertheless, the cookies are objects that are stored and managed in the browser. If the
browser have security leaks, it can comprise the access. However, the credentials are not stolen, and the session can
be invalidated after some time. This reduces the time range where the application is vulnerable. 

Here is the command to do it with curl:

```
curl localhost:8080/hi -c 'JSESSION=E254492C7733FA422D973A2E8131A6FA'
```

## JWT Authentication

The configuration for using the JWT Authentication is located in the package `com.sergio.security.backend.jwt`.

Instead of using a session and a cookie, we can use a JWT. As with the session, the JWT authentication also needs an
initial request to validate the user's credentials. Once done, the backend creates a JWT that can be used by any 
external system (browser, another backend, or more). The JWT can contain some information about the user, but it can't
contain sensitive information as it's readable.

The advantage of the JWT is that it does not rely on a browser, the security is increased. The JWT contains autonomous 
information about the validity time. However, as the backend doesn't controller where the JWT is stored, it's still
vulnerable.

Here is the command to do it with curl:

```
curl localhost:8080/hi -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXJyZW50X3VzZXIifQ.X1_IVbmt9XUGENh-Dm-KjZMqrdJDPb007ndqCQEZHJo"
```

## OAuth2 Authentication

The configuration for using the OAuth2 Authentication is located in the package `com.sergio.security.backend.oauth2`.

The last authentication system presented is the OAuth2 workflow. This authentication system needs an external system
that stores the credentials of the user. This way, the backend doesn't need to handle the authentication.

The OAuth2 workflow needs 3 servers: the authentication server (or Identity Provider like Google, Facebook Connect,
Github or other), the resources server (which is the current instance and contains sensitive information), and the 
client (which is the server which tries to access protected resources).

When a request reaches the backend, which is considered as a resources server, if the request isn't authenticated, the 
user is redirected to the authentication server. The authentication server will validate the user's credentials. Once
done, a code is shared to the user. This code is send to backend to obtain a token. The token is then used by the user
to authenticate each request done to the backend.

The advantages of this authentication system is that the credentials are not even stored in the current backend. And
the session information is managed by a third-party system. The current backend, the resources server, only validates
a token against the IdP.

The code presented was only tested with AWS Cognito. Others IdP may need changes.

The OAuth2 workflow is harder to test from the command line, as the IdP may have a login page and set some cookies
after the user sends his credentials.