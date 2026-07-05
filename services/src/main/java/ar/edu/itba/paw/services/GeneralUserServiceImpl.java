package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.GeneralUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class GeneralUserServiceImpl implements GeneralUserService {

    private final GeneralUserDao generalUserDao;

    @Autowired
    public GeneralUserServiceImpl(GeneralUserDao generalUserDao) {
        this.generalUserDao = generalUserDao;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByMail(String mail) {
        return generalUserDao.findByMail(mail);
    }

}
