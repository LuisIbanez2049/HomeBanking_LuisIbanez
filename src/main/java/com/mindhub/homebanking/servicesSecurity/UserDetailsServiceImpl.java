package com.mindhub.homebanking.servicesSecurity;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


 // Ademas de decir que es un servicio, "@Service"  nos va a permitir inyectar esta clase en la clase "JwtRequesFilter" ubicado
 // En el paquete "filters"
@Service
public class UserDetailsServiceImpl  implements UserDetailsService {
    // Implementamos la logica de la interfaz "UserDetailsService" el cual nos provee el framework spring securuty
    // Este tiene el metodo "loadUserByUsername"

    //Inyectamos clientRepository
    @Autowired
    private ClientRepository clientRepository;


    // Declaramos el metodo "loadUserByUsername" que es el metodo que nos proporciona "UserDetailsService" y la sobre escribimos
    // Para que tenga el comportamiento que nosotros deseamos
    // Por eso tiene la anotacion "@Override"
    // El metodo "loadUserByUsername" nos devuelve un "UserDetails" que va a ser el usuario que queremos tener en el context holder
    // Este metodo no arroja "throws"
    // Una exepcion "UsernameNotFoundException"
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Con el repositorio de client vamos a buscar al cliente, que esta en la base de datos, por su email
        // Al metodo "findByEmail" le paso un email y me devulve un client 
        Client client = clientRepository.findByEmail(username);

        // Si el cliente es nulo, no se encuentra arrojamos la exepcion "UsernameNotFoundException(username)"
        if (client == null) {
            throw new UsernameNotFoundException(username);
        }


        // Si se encuentra al usuario retornamos al usuario que vamos a guardar en el context holder
        return User
                .withUsername(username) // Va a tener el email
                .password(client.getPassword()) // Obtengo la contraseña del cliente
                .roles("CLIENT") // Le doy el rol de client
                .build(); // Mando a contruir a este usuario

        // Esta instancia de User que creo lo pongo en el contexto para despues mandarle a crear un token
    }
}
