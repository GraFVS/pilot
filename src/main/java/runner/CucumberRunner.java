package runner;

import cucumber.api.CucumberOptions;

import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        format = {"pretty"},
        features = "src/test/resources/features",
        glue = "stepdefs",
        plugin = "io.qameta.allure.cucumber2jvm.AllureCucumber2Jvm"
)

public class CucumberRunner {

}
