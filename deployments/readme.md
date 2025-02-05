# Backend application

application.yaml template 
```yaml
ktor:
  application:
    modules:
      - com.makassar.ApplicationKt.module
  deployment:
    port: 8080

  appConfig:

  environment:
    allowedUploadFileTypes: "png,jpeg,jpg"

    mail:
      smtpHost: "smtp.gmail.com"
      smtpPort: "587"
      emailUsername: ""
      emailPassword: ""

    database:
      mongo:
        user: "$MONGO_USER:db_username"
        password: "$MONGO_PASSWORD:db_password"
        host: "$MONGO_HOST:localhost"
        port: "$MONGO_PORT:27017"
        dbname: "$MONGO_DBNAME:db_name"

    jwt:
      secret: "$JWT_SECRET:secret"
      issuer: "$JWT_ISSUER:http://0.0.0.0:8080/"
      audience: "$JWT_AUDIENCE:http://0.0.0.0:8080"
      realm: "$JWT_REALM:Access to api"
      accessToken:
        lifetime: "$JWT_ACCESS_LIFETIME:600000000"
      refreshToken:
        lifetime: "$JWT_REFRESH_LIFETIME:3600000"

```