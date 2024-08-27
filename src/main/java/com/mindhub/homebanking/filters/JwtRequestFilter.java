package com.mindhub.homebanking.filters;

import com.mindhub.homebanking.servicesSecurity.JwtUtilService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Con esta anotacion marco la clase como un componente de spring
// Spring administrara como un beanlo que esta anotando y lo agregara al contexto de la aplicacion
// Esta clase extiende de la clase abstracta "OncePerRequestFilter" que me garantiza que el filtro se ejecute una vez por cada solicitud http
// Es decir con cada solicita que nos llega se ejecuta una vez esta clase junto con su metodo "doFilterInternal"
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    // Inyecion de dependencia, inyectamos "UserDetailsService" para obtener losdatos del usuario
    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario.

    // Inyectamos "JwtUtilService" para crear el token
    @Autowired
    private JwtUtilService jwtUtilService; // Servicio para manejar la lógica de JWT (JSON Web Token).

    /**
     * Método que realiza el filtrado de solicitudes HTTP y verifica el token JWT.
     *
     * @param request La solicitud HTTP que se está procesando.
     * @param response La respuesta HTTP que se está preparando.
     * @param filterChain La usamos al final para la solicitud con las respuestas al siguiente filtro en la cadena de filtros.
     * @throws ServletException Si ocurre un error durante el procesamiento de la solicitud.
     * Despues arrojo una exepcion que puede ser: "IOException" o "ServletException"
     * @throws IOException Si ocurre un error de recibir la peticion o a la hora de devolverla durante el procesamiento de la solicitud.
     * @throws ServletException Es una exepcion general que puede ocurrir cuando hay una dificultad
     */


    // "doFilterInternal" Es el metodo que realiza la logica del filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Usamos "tryCatch" para evitar que nos de el error "internal server error" y hacer un correcto manejo de la exepcion
        try {
            // Obtiene el encabezado de autorización de la solicitud HTTP.
            final String authorizationHeader = request.getHeader("Authorization");
            String userName = null;
            String jwt = null;

            // Verifica si el encabezado de autorización está presente y comienza con "Bearer ". (Bearer espacio) ya que por lo general los token empiezan de esta forma
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // Extrae el token JWT del encabezado (elimina el prefijo "Bearer ").
                // Lo que hacemos aqui es quitar ese texto ("Bearer "), (Bearer espacio) para quedarnos solamente con el token
                jwt = authorizationHeader.substring(7);
                // Extrae el nombre de usuario del token JWT.
                userName = jwtUtilService.extractUserName(jwt);
            }

            // Verifica si el nombre de usuario no es nulo y si no hay una autenticación ya establecida en el contexto de seguridad.
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Carga los detalles del usuario basados en el nombre de usuario extraído (userName) del token que seria el email
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);

                // Verifica si el token JWT no ha expirado.
                if (!jwtUtilService.isTokenExpired(jwt)) {
             // Crea un objeto de tipo "UsernamePasswordAuthenticationToken" llamado "authentication"  con los detalles del usuario y sus autoridades extraidos del user details
                    // Las "credenciales" las establecemos como "null" porque trabajamos con la autenticacion a través del token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    // Establece los detalles de autenticación (como la solicitud HTTP) en el objeto de autenticación.
                    // De la autenticaion vamos a setear los detalles con el constructor "WebAuthenticationDetailsSource()" que le pasamos el metodo "buildDetails()" con el
                    // que vamos a crear y establecer los detalles a la autenticaion basados en la peticion proporcionada ("request")
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Establece la autenticación del usuario actual en el contexto de seguridad para gestionar la autenticacion y la autorizacion de los usuarios
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Maneja cualquier excepción que ocurra durante el proceso de filtrado.
            // En un entorno de producción, sería más adecuado usar un logger en lugar de System.out.println.
            System.out.println(e.getMessage());
        } finally {
            // Continúa con la cadena de filtros (pasa la solicitud y la respuesta al siguiente filtro en la cadena).
            filterChain.doFilter(request, response);
        }
    }
}
