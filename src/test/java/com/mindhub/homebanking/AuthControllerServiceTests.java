//package com.mindhub.homebanking;
//
//import com.mindhub.homebanking.dtos.RegisterDTO;
//import com.mindhub.homebanking.services.AuthControllerService;
//import com.mindhub.homebanking.services.serviceImpl.AuthControllerServiceImpl;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//
//@SpringBootTest
//public class AuthControllerServiceTests {
//    @Autowired
//    private AuthControllerService authControllerService;
//
//    @Test
//    void testNotExistEmail(){
//        assertThat(authControllerService.existEmail("test@gmail.com"), is(false));
//    }
//    @Test
//    void testExistEmail(){
//        assertThat(authControllerService.existEmail("melba@mindhub.com"), is(true));
//    }
//    @Test
//    void testParameterIsBlank(){
//        String parameter = "";
//        assertThat(authControllerService.parameterIsBlank(parameter), is(true));
//    }
//    @Test
//    void testParameterIsNotBlank(){
//        String parameter = "firstName";
//        assertThat(authControllerService.parameterIsBlank(parameter), is(false));
//    }
//    @Test
//    void testPassWordIsShort(){
//        String password = "1234567";
//        assertThat(authControllerService.passWordIsShort(password), is(true));
//    }
//    @Test
//    void testRegisterNewClient(){
//        RegisterDTO registerDTO = new RegisterDTO("nameTest", "lastNameTest", "test@gmail.com", "123456789");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "Client registered successfully";
//        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//    @Test
//    void testRegisterNewClient_EmailAlreadyExist(){
//        RegisterDTO registerDTO = new RegisterDTO("nameTest", "lastNameTest", "melba@mindhub.com", "123456789");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "Email already exists";
//        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//    @Test
//    void testRegisterNewClient_NoParameterFirstName(){
//        RegisterDTO registerDTO = new RegisterDTO("", "lastNameTest", "test@gmail.com", "123456789");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "First name cannot be empty";
//        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//    @Test
//    void testRegisterNewClient_NoParameterLastName(){
//        RegisterDTO registerDTO = new RegisterDTO("nameTest", "", "test@gmail.com", "123456789");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "Last name cannot be empty";
//        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//    @Test
//    void testRegisterNewClient_NoParameterEmail(){
//        RegisterDTO registerDTO = new RegisterDTO("nameTest", "lastNameTest", "", "123456789");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "Email cannot be empty";
//        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//    @Test
//    void testRegisterNewClient_NoParameterPassword(){
//        RegisterDTO registerDTO = new RegisterDTO("nameTest", "lastNameTest", "test@gmail.com", "");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "Password cannot be empty";
//        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//    @Test
//    void testRegisterNewClient_ParameterPasswordShort(){
//        RegisterDTO registerDTO = new RegisterDTO("nameTest", "lastNameTest", "test@gmail.com", "1234567");
//        ResponseEntity<?> response = authControllerService.registerNewClient(registerDTO);
//        String expectedMessage = "Password must be at least 8 characters long";
//        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(response.getBody(), is(expectedMessage));
//    }
//}
