package com.mindhub.homebanking.servicesSecurity;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class UserDetailsServiceImpl  implements UserDetailsService {

    //Inyectamos clientRepository
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Client client = clientRepository.findByEmail(username);

        // Si el cliente es nulo, no se encuentra arrojamos la exepcion "UsernameNotFoundException(username)"
        if (client == null) {
            throw new UsernameNotFoundException(username);
        }

        if (client.getEmail().contains("@admin")){
            return User
                    .withUsername(username) // Va a tener el email
                    .password(client.getPassword()) // Obtengo la contraseña del cliente
                    .roles("ADMIN") // Le doy el rol de client
                    .build(); // Mando a contruir a este usuario
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
