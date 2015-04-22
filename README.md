![Locomotive](https://github.com/erickzanardo/locomotive/blob/master/locomotive.png?raw=true)

This is a simple Java Rest API, based on the NodeJs lib Express.

## Maven repository

Repository
```xml
<repository>
  <id>erickzanardo-releases</id>
  <url>http://erickzanardo.github.com/maven/releases/</url>
</repository>
```
Dependency
```xml
<dependency>
  <groupId>org.eck</groupId>
  <artifactId>locomotive</artifactId>
  <version>1.0.1-ALPHA</version>
</dependency>
```

## Charset

For now, locomotive only read and write data using UTF-8 charset, but we are already implementing this :)

## Hello World
```java
Locomotive locomotive = new Locomotive(8080);
locomotive.get("/hello", (req, resp) -> {
    resp.append("Hello sire");
});
locomotive.boot();
```

## Params
```java
// QueryString
locomotive.get("/user", (req, resp) -> {
    req.param("id").asInteger(); // 2
});
// GET http://localhost:8080/user?id=2

// FormData
locomotive.post("/user", (req, resp) -> {
    req.param("name").asString(); // James
});
// POST http://localhost:8080/user
// FormData: name=James
```

## Request body
```java
locomotive.post("/user", (req, resp) -> {
    req.body(); // { "name": "James" }
});
// POST http://localhost:8080/user
// Body: { "name": "James" }
```

## Route params
```java
locomotive.get("/users/:id", (req, resp) -> {
    req.param("id").asInteger(); // 2
});

// GET http://localhost:8080/users/2
```

