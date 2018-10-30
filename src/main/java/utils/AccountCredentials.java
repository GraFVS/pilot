package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static utils.Evaluator.getVariable;

public class AccountCredentials {
    private static final Logger LOG = LoggerFactory.getLogger(AccountCredentials.class);
    private final String login;
    private final String password;
    private final String name;
    private final String surname;
    private final String email;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public AccountCredentials(String role) {
        login = getVariable("#{" + role + ".login}");
        password = getVariable("#{" + role + ".password}");
        name = getVariable("#{" + role + ".name}");
        surname = getVariable("#{" + role + ".surname}");
        email = getVariable("#{" + role + ".email}");
        LOG.debug("Инициализирован пользователь с параметрами\n Имя:[{}] Фамилия:[{}] Email:[{}] "
                , name, surname, login);
    }
}
