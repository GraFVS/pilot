package enviroment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Evaluator;
import utils.UTF8Control;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Класс предоставляет доступ к конфигурационным файлам
 * с использованием api ResourceBundle
 **/
public class Stand {
    private static final Logger LOG = LoggerFactory.getLogger(Stand.class);
    private ResourceBundle config;
    private ResourceBundle accounts;
    private static final Stand current = new Stand();

    private Stand() {
        init();
    }

    private void init() {
        Locale stand = new Locale(System.getProperty("properties.bundle", "default"));
        try {
            config = ResourceBundle.getBundle("config", stand, new UTF8Control());
            putAllInStash(config);
        } catch (MissingResourceException e) {
            LOG.info("Не найден бандл config.properties, функционал, связанный с данными настройками будет недоступен");
        }
        try {
            accounts = ResourceBundle.getBundle("accounts", stand, new UTF8Control());
            putAllInStash(accounts);
        } catch (MissingResourceException e) {
            LOG.info("Не найден бандл accounts.properties, функционал, связанный с данными настройками будет недоступен");
        }
    }

    private void putAllInStash(ResourceBundle config) {
        Enumeration<String> keys = config.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Evaluator.setVariable(key, config.getString(key));
        }
    }

    public static Stand getCurrentStand() {
        return current;
    }

    public ResourceBundle getConfig() {
        return config;
    }

    public ResourceBundle getAccounts() {
        return accounts;
    }
}