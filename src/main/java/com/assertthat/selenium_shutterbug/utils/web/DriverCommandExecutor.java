package com.assertthat.selenium_shutterbug.utils.web;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.HttpMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Anna Galkina on 23-06-2018.
 */
public class DriverCommandExecutor {

    private ChromeDriver driver;

    public DriverCommandExecutor(ChromeDriver driver) throws Exception {
        this.driver = driver;
        CommandInfo cmd = new CommandInfo("/session/:sessionId/chromium/send_command_and_get_result", HttpMethod.POST);
        Method defineCommand = HttpCommandExecutor.class.getDeclaredMethod("defineCommand", String.class, CommandInfo.class);
        defineCommand.setAccessible(true);
        defineCommand.invoke(driver.getCommandExecutor(), "sendCommand", cmd);
    }

    public Object sendCommand(String cmd, Object params) {
        try {
            Method execute = RemoteWebDriver.class.getDeclaredMethod("execute", String.class, Map.class);
            execute.setAccessible(true);
            Response res = (Response) execute.invoke(driver, "sendCommand", ImmutableMap.of("cmd", cmd, "params", params));
            return res.getValue();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object evaluate(String script) {
        Object response = sendCommand("Runtime.evaluate", ImmutableMap.of("returnByValue", true, "expression", script));
        Object result = ((Map<String, ?>) response).get("result");
        return ((Map<String, ?>) result).get("value");
    }
}
