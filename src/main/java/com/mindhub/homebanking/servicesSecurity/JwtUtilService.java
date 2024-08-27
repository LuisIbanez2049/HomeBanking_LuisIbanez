package com.mindhub.homebanking.servicesSecurity;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtilService {

    // Declaramos una propiedad estatica final, final porque una vez que le demos un valor ya no se lo vamos a poder reasignar
    // Vamos a guardar un objeto de tipo "SecretKey" que se va a llamar "SECRET_KEY" en mayuscula porque es una constante
    // "Jwts" Es una clase parte de la libreria java jwt. Utilizada para trabajar con tokens jwt en java
    // "SIG" es un metodo para especificar que quiero firmar el token jwt
    // "HS256" es el algoritmo de firma que se va a utilizar para firmar el token (Hash-based Message Authentication Code)
    // "key()" es el  metodo que indica que esta a punto de especificar la clave que se va a utilizar para firmar el token
    // "build()" es el metodo que me va a finalizar la construccion del objeto y devolver el token jwt ya firmado con la clave proporcionada
    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();


    // Generamos el timpo de expiracion del token para despues generar la fecha de expiracion con la fecha actual
    // "1000" milisegundos * 60 = 1 minuto y multiplicado por 60 nos da una hora
    private static final long EXPIRATION_TOKEN = 1000 * 60 * 60;



    // Verifica un token utilizando una clave secreta y luego extrae y devuelve las claims del token verificado (cerpo del payload)
    //--------------------------------------------------------------------------------------------------------------------------------
    public Claims extractAllClaims(String token){

        // Con el metodo estatico de la clase jwts, se crea un objeto jwts.parser() que se utiliza para analizar y verificar
        // los token jwt
        // Con "verifyWith(SECRET_KEY)" verificamos el token jwt utilizando una clave secreta
        // "build()" finaliza la configuracion de verificacion y va a construir un objeto jwt.paser listo para analizar tokens
        // "parseSignedClaims(token)" al metodo le pasamos el token y analiza ese token jwt firmado y devuelve un objeto SIGNED JWT
        // que contiene las clim firmadas
        // "getPayload()" este metodo obtiene y devuelve el cuerpo del payload del token, las claims (datos Ej.: el usuario)
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }
    //--------------------------------------------------------------------------------------------------------------------------------


    // Este metodo me sirve para extraer un claim en particular
// "<T>" digo que me va a devolver un generico, puede devolver cualquier cosa porque los claim pude ser un nombre, email, fechaExpiracion
    // Como argumento le paso el token y una funcion que la saco de la interfaz de claims
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){

        // declaro una variable de tipo final que van a contener todos los claims
        final Claims claims = extractAllClaims(token);

        // Va a retornar una claim function que le vamos a pasar como parametro un claim en particular
        return claimsTFunction.apply(claims);
    }


    // Despues con el metodo de arriva "extractClaim" extraemos un claim en particular
    // En este caso el email
    public String extractUserName (String token) { return extractClaim(token, Claims :: getSubject);}

    // En esta caso la fechad de expiracion esta antes que la fecha actual
    public Date extractExpiration(String token) { return extractClaim(token, Claims :: getExpiration);}


    // "extractExpiration(token)" extrae la fecha de expiracion del token que le pasamos por argumento
    // ".before(new Date())" verifica si la fecha de expiracion extraida
    // true: el token esta expirado,   false: el token esta vigente
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // Este metodo recive el "Map<String, Object>" del metodo de abajo (la clave rol y el rol), y recibe el username obtenido de userDetails
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts // de la clase Jwts usamos los siguientes metodos
                .builder() // Inicia un objeto de tipo Jwt
                .claims(claims) // Le pasamos los claims que recivimos por parametro
                .subject(username) // Al subject le pasamos el email
                .issuedAt(new Date(System.currentTimeMillis())) // Le pasamos la fecha de emicion del token
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TOKEN)) // Le pasamos la fecha de expiracion del token
                .signWith(SECRET_KEY) // Utilizamos la "SECRET_KEY" que previamente generamos para firmar este token que generamos
                .compact(); // Por ultimo construimos el token jwt completo y lo devolvemos como un string
    }


    // Recibe como parametro "UserDetails" que es el usuario que creamos en la clase "UserDetailsServiceImpl"
    // "Map<String, Object>" es una estructura para poder asociar una clave a un valor.
    // En este caso la clave es de tipo string "rol" y el valor va a ser el rol que vamos a obtener de "userDetails"
    public String generateToken(UserDetails userDetails){

        //Generamos el map de string object y lo instanciamos para recervar un espacio en memoria
        Map<String, Object> claims = new HashMap<>();

        // De "userDetails" con ".getAuthorities()" obtengo las autoridades
        // ".iterator()" convierte la coleccion de autoridades en un iterador para poder recorrer los elementos de una coleccion 1x1
        // ".next()" devolvemos el siguiente elemento de la iteracion. Como el usuario solo tiene un rol se obtiene el primer elemento
        // de la coleccion la autorida, el rol
        // ".getAuthority()" obtiene
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();


        // Se agrega el rol obtenido al "Map<String, Object" con la clave "rol" (el string y el objeto)
        claims.put("rol", rol);

        // Despues se llama al metodo "createToken" y le pasamos por argumento las claims y el nombre de usuario obtenido
        // del userDetails.getUsername() para generar un token, que es un string que es lo que devuelve el metodo
        return createToken(claims, userDetails.getUsername());
    }
}
