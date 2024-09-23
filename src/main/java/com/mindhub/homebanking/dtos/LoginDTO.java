package com.mindhub.homebanking.dtos;
// Record me permite crear clases inmutables de una forma mas sencilla, es decir que me permite representar datos que no van a cambiar
public record LoginDTO (String email, String password){
}
