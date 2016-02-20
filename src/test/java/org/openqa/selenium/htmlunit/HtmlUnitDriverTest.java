// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.htmlunit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.SessionNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test the proxy setting.
 */
public class HtmlUnitDriverTest extends TestBase {

  private HtmlUnitDriver driver;

  @Before
  public void initDriver() {
    driver = new HtmlUnitDriver(true);
    driver.get(testServer.page("/"));
  }

  @After
  public void stopDriver() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Test
  public void canGetAPage() {
    driver.get(testServer.page("/"));
    assertThat(driver.getCurrentUrl(), equalTo(testServer.page("/")));
  }

  @Test
  public void canSetImplicitWaitTimeout() {
    driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
  }

  @Test
  public void canNavigateToAPage() {
    driver.get(testServer.page("/"));
    assertThat(driver.getCurrentUrl(), equalTo(testServer.page("/")));
  }

  @Test
  public void canRefreshAPage() {
    driver.get(testServer.page("/"));
    driver.navigate().refresh();
    assertThat(driver.getCurrentUrl(), equalTo(testServer.page("/")));
  }

  @Test
  public void throwsOnMalformedUrl() {
    thrown.expect(WebDriverException.class);
    driver.get("www.test.com");
  }

  @Test
  public void doesNotThrowsOnUnknownHost() {
    driver.get("http://www.thisurldoesnotexist.comx/");
    assertThat(driver.getCurrentUrl(), equalTo("http://www.thisurldoesnotexist.comx/"));
  }

  @Test
  public void throwsOnAnyOperationAfterQuit() {
    driver.quit();
    thrown.expect(SessionNotFoundException.class);
    driver.get(testServer.page("/"));
  }

  @Test
  public void canGetPageTitle() {
    driver.get(testServer.page("/"));
    assertThat(driver.getTitle(), equalTo("Hello, world!"));
  }

  @Test
  public void canOpenNewWindow() {
    String mainWindow = driver.getWindowHandle();
    openNewWindow(driver);
    assertThat(driver.getWindowHandle(), equalTo(mainWindow));
  }

  @Test
  public void canGetWindowHandles() {
    openNewWindow(driver);
    assertThat(driver.getWindowHandles().size(), equalTo(2));
  }

  @Test
  public void canSwitchToAnotherWindow() {
    String mainWindow = driver.getWindowHandle();
    openNewWindow(driver);
    Set<String> windowHandles = driver.getWindowHandles();
    windowHandles.remove(mainWindow);
    driver.switchTo().window(windowHandles.iterator().next());
    assertThat(driver.getWindowHandle(), not(equalTo(mainWindow)));
  }

  @Test
  public void canSwitchToFrame() {
    driver.get(testServer.page("/frame.html"));
    driver.switchTo().frame(driver.findElement(By.id("iframe")));
    driver.switchTo().parentFrame();
    driver.switchTo().frame(driver.findElement(By.id("iframe")));
    driver.switchTo().defaultContent();
    driver.switchTo().frame(driver.findElement(By.id("iframe")));
  }

  @Test
  public void canExecuteAsync() {
    Object result = driver.executeAsyncScript("arguments[arguments.length - 1](123);");
    assertThat(result, instanceOf(Number.class));
    assertThat(123, equalTo(((Number) result).intValue()));
  }

  @Test
  public void canFindElementByTagName() {
    driver.get(testServer.page("/form.html"));
    WebElement input = driver.findElement(By.tagName("form"))
        .findElement(By.tagName("input"));
    assertThat(input.getTagName(), equalTo("input"));
  }

  @Test
  public void canFindElementsByTagName() {
    driver.get(testServer.page("/form.html"));
    List<WebElement> forms = driver.findElements(By.tagName("form"));
    assertThat(forms.size(), equalTo(1));
    List<WebElement> inputs = forms.get(0).findElements(By.tagName("input"));
    assertThat(inputs.size(), equalTo(3));
  }

  @Test
  public void canFindElementByCssSelector() {
    driver.get(testServer.page("/form.html"));
    WebElement input = driver.findElement(By.cssSelector("#form_id"))
        .findElement(By.cssSelector("input"));
    assertThat(input.getTagName(), equalTo("input"));
  }

  @Test
  public void canFindElementsByCssSelector() {
    driver.get(testServer.page("/form.html"));
    List<WebElement> forms = driver.findElements(By.cssSelector("#form_id"));
    assertThat(forms.size(), equalTo(1));
    List<WebElement> inputs = forms.get(0).findElements(By.cssSelector("input"));
    assertThat(inputs.size(), equalTo(3));
  }

  @Test
  public void canFindElementByXpath() {
    driver.get(testServer.page("/form.html"));
    WebElement input = driver.findElement(By.xpath("//form"))
        .findElement(By.xpath("./input"));
    assertThat(input.getTagName(), equalTo("input"));
  }

  @Test
  public void canFindElementsByXpath() {
    driver.get(testServer.page("/form.html"));
    List<WebElement> forms = driver.findElements(By.xpath("//form"));
    assertThat(forms.size(), equalTo(1));
    List<WebElement> inputs = forms.get(0).findElements(By.xpath("./input"));
    assertThat(inputs.size(), equalTo(3));
  }

  @Test
  public void canFindElementByName() {
    driver.get(testServer.page("/form.html"));
    WebElement input = driver.findElement(By.name("form_name"))
        .findElement(By.name("text"));
    assertThat(input.getTagName(), equalTo("input"));
  }

  @Test
  public void canFindElementById() {
    driver.get(testServer.page("/form.html"));
    WebElement form = driver.findElement(By.id("form_id"));
    assertThat(form.getTagName(), equalTo("form"));
  }

  @Test
  public void canSendKeysToAnInput() {
    driver.get(testServer.page("/form.html"));
    WebElement input = driver.findElement(By.name("text"));
    assertThat(input.getAttribute("value"), equalTo("default text"));
    input.sendKeys(" changed");
    assertThat(input.getAttribute("value"), equalTo("default text changed"));
    input.clear();
    assertThat(input.getAttribute("value"), equalTo(""));
  }

  @Test
  public void canClickACheckbox() {
    driver.get(testServer.page("/form.html"));
    WebElement input = driver.findElement(By.name("checkbox"));
    assertThat(input.getAttribute("selected"), is(nullValue()));
    assertThat(input.isSelected(), is(false));
    input.click();
    assertThat(input.getAttribute("selected"), is("true"));
    assertThat(input.isSelected(), is(true));
    input.click();
    assertThat(input.getAttribute("selected"), is(nullValue()));
    assertThat(input.isSelected(), is(false));
  }

  @Test
  public void canSubmitAForm() {
    driver.get(testServer.page("/form.html"));
    driver.findElement(By.tagName("form")).submit();
    assertThat(driver.getCurrentUrl(), equalTo(testServer.page("/index.html")));
  }

  @Test
  public void canSubmitAFormFromAnInput() {
    driver.get(testServer.page("/form.html"));
    driver.findElement(By.name("text")).submit();
    assertThat(driver.getCurrentUrl(), equalTo(testServer.page("/index.html")));
  }

  @Test
  public void canSubmitAFormFromAnyElementInTheForm() {
    driver.get(testServer.page("/form.html"));
    driver.findElement(By.id("div")).submit();
    assertThat(driver.getCurrentUrl(), equalTo(testServer.page("/index.html")));
  }

  @Test
  public void canUseActions() {
    driver.get(testServer.page("/form.html"));
    WebElement text = driver.findElement(By.name("text"));
    WebElement checkbox = driver.findElement(By.name("checkbox"));
    new Actions(driver)
        .click(text).keyDown(Keys.SHIFT).sendKeys("changed ").keyUp(Keys.SHIFT)
        .click(checkbox)
        .perform();
    assertThat(text.getAttribute("value"), equalTo("default textCHANGED "));
    assertThat(checkbox.isSelected(), is(true));
  }

  @Test
  public void throwsOnMissingAlertAcceptAnAlert() {
    thrown.expect(NoAlertPresentException.class);
    driver.switchTo().alert();
  }

  @Test
  public void canAcceptAnAlert() {
    driver.get(testServer.page("/alert.html"));
    driver.findElement(By.id("link")).click();
    Alert alert = driver.switchTo().alert();
    assertThat(alert.getText(), equalTo("An alert"));
    alert.accept();
  }

  @Test
  public void canDismissAnAlert() {
    driver.get(testServer.page("/alert.html"));
    driver.findElement(By.id("link")).click();
    Alert alert = driver.switchTo().alert();
    assertThat(alert.getText(), equalTo("An alert"));
    alert.dismiss();
  }

  @Test
  public void canSetAndGetWindowSize() {
    driver.manage().window().setSize(new Dimension(200, 300));
    assertThat(driver.manage().window().getSize(), equalTo(new Dimension(200, 300)));
  }

  @Test
  public void canSetAndGetWindowPosition() {
    driver.manage().window().setPosition(new Point(200, 300));
    assertThat(driver.manage().window().getPosition(), equalTo(new Point(200, 300)));
  }

  @Test
  public void canSetGetAndDeleteCookie() {
    driver.manage().addCookie(new Cookie("xxx", "yyy"));
    assertThat(driver.manage().getCookieNamed("xxx"), equalTo(new Cookie("xxx", "yyy")));
    assertThat(driver.manage().getCookies().size(), equalTo(1));
    assertThat(driver.manage().getCookies().iterator().next(), equalTo(new Cookie("xxx", "yyy")));
    driver.manage().deleteCookieNamed("xxx");
    assertThat(driver.manage().getCookieNamed("xxx"), is(nullValue()));
  }

  @Test
  public void canSetGetAndDeleteMultipleCookies() {
    driver.manage().addCookie(new Cookie("xxx", "yyy"));
    driver.manage().addCookie(new Cookie("yyy", "xxx"));
    assertThat(driver.manage().getCookies().size(), equalTo(2));
    driver.manage().deleteAllCookies();
    assertThat(driver.manage().getCookies().size(), equalTo(0));
  }

  private void openNewWindow(HtmlUnitDriver driver) {
    driver.executeScript("window.open('new')");
  }

}