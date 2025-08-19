package co.e2e.wompi.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"co.e2e.wompi.stepdefinitions"},
        tags = "",
        plugin = {"pretty", "html:target/cucumber-reports.html", "json:target/cucumber.json"}

)
public class WompiRunner {}
