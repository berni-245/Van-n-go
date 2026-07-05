package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface GeneralUserService {
    Optional<User> findByMail(String mail);
}
