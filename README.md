# spring-kotlin-jwt
playground for spring-boot, kotlin , jwt, auth0 ...

poc:
 - parse jwt custom claims (incl. namespaces), 
 - self-signed jwt for pragmatic local development

# build and run
    
    $ ./gradlew bootRun
  
  Browse to ...
    
   - http://localhost:8080/swagger-ui.html


# GIVEN: example jwt

    {
      "https://example-company.com/claims/userid": "USERID-FAKE",
      "https://example-company.com/claims/given_name": "Bat",
      "https://example-company.com/claims/family_name": "Man",
      "https://example-company.com/claims/email": "bat.man@example-company.com",
      "https://awesome-app.example-company.com/claims/roles": [
        "example-role-A",
        "example-role-B"
      ],
      "iss": "https://example-company.eu.auth0.com/",
      "sub": "stuff|example-stuff|USERID-FAKE",
      "aud": [
        "https://awesome-app.example-company.com/api/",
        "https://example-company.eu.auth0.com/userinfo"
      ],
      "iat": 1516794458,
      "exp": 1516880858,
      "azp": "azp-example",
      "scope": "openid profile email"
    }
    
# WHEN: run application - using auth-strategy: JWT_FAKE

# THEN: example api response    

    {
      "now": "2018-02-16T05:39:19.280Z",
      "user": {
        "userId": "USERID-FAKE",
        "email": "bat.man@example-company.com",
        "givenName": "Bat",
        "familyName": "Man"
      },
      "auth": {
        "authorities": [
          "openid",
          "profile",
          "email",
          "example-role-A",
          "example-role-B"
        ],
        "roles": [
          "example-role-A",
          "example-role-B"
        ],
        "scopes": [
          "openid",
          "profile",
          "email"
        ]
      }
    }