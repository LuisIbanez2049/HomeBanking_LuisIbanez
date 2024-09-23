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

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    // Inyecion de dependencia, inyectamos "UserDetailsService" para obtener losdatos del usuario
    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar detalles del usuario.

    // Inyectamos "JwtUtilService" para crear el token
    @Autowired
    private JwtUtilService jwtUtilService; // Servicio para manejar la lógica de JWT (JSON Web Token).

    // "doFilterInternal" Es el metodo que realiza la logica del filtro
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Usamos "tryCatch" para evitar que nos de el error "internal server error" y hacer un correcto manejo de la exepcion
        try {
            // obtengo el valor del encabezado llamado "Authorization"
            final String authorizationHeader = request.getHeader("Authorization");
            String userName = null;
            String jwt = null;

            // Verifica si el encabezado de autorización está presente y comienza con "Bearer ". (Bearer espacio) ya que por lo general los token empiezan de esta forma
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
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
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Agrego la autenticación del usuario actual en el contexto de seguridad para gestionar la autenticacion y la autorizacion del usuario
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // Continúa con la cadena de filtros (pasa la solicitud y la respuesta al siguiente filtro en la cadena).
            filterChain.doFilter(request, response);
        }
    }
}
