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
  <version>1.0.3-ALPHA</version>
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
##  Middlewares

With middlewares you can add custom behavior to all your requests and are easy ways to implement generic things, for example, let's build a simple middleware that prints on the server the content-type header for every request.

```java
Locomotive locomotive = new Locomotive(8080);
locomotive.addMiddleware( (req, resp) -> {
    System.out.println(req.header("content-type"));
} );
locomotive.boot();
```
## Serving static files

Locomotive has a built-in middleware for serving static files, to use it just create an instance of LocomotiveAssetsMiddleware passing on it's constructor the folder name which has the static files and add it to your Locomotive instance, see the following example

```java
Locomotive locomotive = new Locomotive(8080);
locomotive.addMiddleware(new LocomotiveAssetsMiddleware("public"));
locomotive.boot();
```
## Exceptions

Any exception throwed on your endpoints/middlewares, will be catch by Locomotive and it will send the client an Internal Server Error with the exception message on the response body.

If you like to create exceptions that turn into http errors, you can extend from LocomotiveException or throw the GenericLocomotiveException, see the following examples

Extending LocomotiveException
```java
class UnauthorizeException extends LocomotiveException {
    private static final long serialVersionUID = 1821368040400403033L;

    public UnauthorizeException(String message) {
        super(message);
    }

    @Override
    public int code() {
        return 401;
    }

}

locomotive.post("/user", (req, resp) -> {
    throw new UnauthorizeException("You shall not pass");
});
```

Throwing a GenericLocomotiveException
```java
locomotive.post("/user", (req, resp) -> {
    throw new GenericLocomotiveException(401, "You shall not pass");
});
```
