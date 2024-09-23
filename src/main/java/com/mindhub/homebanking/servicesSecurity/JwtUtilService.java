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

    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final long EXPIRATION_TOKEN = 1000 * 60 * 60;

    // Verifica un token utilizando una clave secreta y luego extrae y devuelve las claims del token verificado (cerpo del payload)
    //--------------------------------------------------------------------------------------------------------------------------------
    public Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }
    //--------------------------------------------------------------------------------------------------------------------------------
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction){

        // declaro una variable de tipo final que van a contener todos los claims
        final Claims claims = extractAllClaims(token);

        // En el retorno utiliza la funcion "claimsTFunction" que sera aplicado al objeto Claims. Dependendiendo de la funcion va a extraer una claim en particular.
        return claimsTFunction.apply(claims);
    }

    public String extractUserName (String token) { return extractClaim(token, Claims :: getSubject);}

    // En esta caso la fechad de expiracion esta antes que la fecha actual
    public Date extractExpiration(String token) { return extractClaim(token, Claims :: getExpiration);}

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // Este metodo recive el "Map<String, Object>" del metodo de abajo (la clave rol y el rol), y recibe el username obtenido de userDetails
    private String createToken(Map<String, Object> claims, String username) {
        return Jwts // de la clase Jwts usamos los siguientes metodos
                .builder() // Inicia un objeto de tipo Jwt
                .claims(claims) // Le pasamos los claims que recivimos por parametro para agregar una claim personalizada al token
                .subject(username) // Al subject le pasamos el email
                .issuedAt(new Date(System.currentTimeMillis())) // Le pasamos la fecha de emicion del token
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TOKEN)) // Le pasamos la fecha de expiracion del token
                .signWith(SECRET_KEY) // Utilizamos la "SECRET_KEY" que previamente generamos para firmar este token que generamos
                .compact(); // Por ultimo construimos el token jwt completo y lo devolvemos como un string
    }

    public String generateToken(UserDetails userDetails){
        //Generamos el map de string object y lo instanciamos para recervar un espacio en memoria
        Map<String, Object> claims = new HashMap<>();

        String rol = userDetails.getAuthorities().iterator().next().getAuthority();

        // Se agrega el rol obtenido al "Map<String, Object" con la clave "rol" (el string y el objeto)
        claims.put("rol", rol);

        return createToken(claims, userDetails.getUsername());
    }
}
