package gpms.selenium;

/* ResearchDirectorApprovalFailure
 * Made By: Nick
 * Research Director attempts to approve a proposal that has not been submitted by PI yet. Program will close and throw an invalid element state
 * exeption if test is successful.
 */

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class ResearchDirectorApprovalFail {
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();

	@Before
	public void setUp() throws Exception {
		String seleniumDriverFolderName = "/selenium_driver";
		String seleniumDriverLocation = this.getClass()
				.getResource(seleniumDriverFolderName).toURI().getPath();
		System.setProperty("webdriver.chrome.driver", seleniumDriverLocation
				+ File.separator + "chromedriver.exe");
		driver = new ChromeDriver();
		baseUrl = "http://localhost:8080/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testNewProposal() throws Exception {
		driver.get(baseUrl + "GPMS-NGAC/");
		driver.findElement(By.id("user_email")).clear();
		driver.findElement(By.id("user_email")).sendKeys(
				"nazmul");
		driver.findElement(By.id("user_password")).clear();
		driver.findElement(By.id("user_password")).sendKeys("gpmspassword");
		Thread.sleep(500);
		driver.findElement(By.name("commit")).click();

		assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*$"));
		driver.findElement(By.cssSelector("li.sfLevel1 > a > span")).click();
		driver.findElement(By.id("btnAddNew")).click();
		Thread.sleep(500);
		driver.findElement(By.cssSelector("i.sidebarExpand")).click();
		Thread.sleep(500);
		driver.findElement(By.id("lblSection2")).click();
		driver.findElement(By.id("txtProjectTitle")).click();
		driver.findElement(By.id("txtProjectTitle")).clear();

		int randTest = (int) (Math.random() * 9999);
		Thread.sleep(500);
		driver.findElement(By.id("txtProjectTitle")).sendKeys(
				"Research Director approval fail Test" + randTest);

		driver.findElement(By.cssSelector("td.cssClassTableRightCol")).click();
		new Select(driver.findElement(By.id("ddlProjectType")))
				.selectByVisibleText("Research-Applied");
		Thread.sleep(500);
		driver.findElement(By.id("txtDueDate")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlTypeOfRequest")).click();
		Thread.sleep(500);
		driver.findElement(By.id("txtDueDate")).click();
		Thread.sleep(500);
		driver.findElement(By.linkText("8")).click();
		new Select(driver.findElement(By.id("ddlTypeOfRequest")))
				.selectByVisibleText("New Proposal");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlLocationOfProject")))
				.selectByVisibleText("On-campus");
		Thread.sleep(500);
		driver.findElement(By.id("txtProjectPeriodFrom")).click();
		Thread.sleep(500);
		driver.findElement(By.linkText("2")).click();
		Thread.sleep(500);
		driver.findElement(By.id("txtProjectPeriodTo")).click();
		Thread.sleep(500);
		driver.findElement(By.linkText("3")).click();
		Thread.sleep(500);
		driver.findElement(By.id("lblSection3")).click();
		Thread.sleep(500);
		driver.findElement(By.id("txtNameOfGrantingAgency")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("txtNameOfGrantingAgency")).sendKeys("NSF");
		Thread.sleep(500);
		driver.findElement(By.id("txtDirectCosts")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("txtDirectCosts")).sendKeys("500");
		Thread.sleep(500);
		driver.findElement(By.id("txtFACosts")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("txtFACosts")).sendKeys("900");
		Thread.sleep(500);
		driver.findElement(By.id("txtTotalCosts")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("txtTotalCosts")).sendKeys("1100");
		Thread.sleep(500);
		driver.findElement(By.id("txtFARate")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("txtFARate")).sendKeys("20");
		Thread.sleep(500);
		driver.findElement(By.id("lblSection4")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlInstitutionalCommitmentCost")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlInstitutionalCommitmentCost > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlThirdPartyCommitmentCost")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlThirdPartyCommitmentCost > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-9")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlNewSpaceRequired")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlNewSpaceRequired")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlRentalSpaceRequired")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		new Select(driver.findElement(By
				.id("ddlInstitutionalCommitmentsRequired")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlInstitutionalCommitmentsRequired > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("lblSection6")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlFinancialCOI")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlFinancialCOI")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlDisclosedFinancialCOI")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlDisclosedFinancialCOI > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlMaterialChanged")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlMaterialChanged > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-13")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlUseHumanSubjects")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlUseHumanSubjects")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlUseHumanSubjects > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlUseHumanSubjects")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlUseHumanSubjects > option[value=\"2\"]"))
				.click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlUseVertebrateAnimals")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlInvovleBioSafety")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlEnvironmentalConcerns")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlEnvironmentalConcerns > option[value=\"2\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-15")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlAnticipateForeignNationals")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlAnticipateForeignNationals")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlAnticipateReleaseTime")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlRelatedToEnergyStudies")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlRelatedToEnergyStudies > option[value=\"2\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-17")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlInvolveNonFundedCollabs")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlInvolveNonFundedCollabs")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlInvolveNonFundedCollabs > option[value=\"2\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-19")).click();
		Thread.sleep(500);
		driver.findElement(By.id("ddlProprietaryInformation")).click();
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlProprietaryInformation")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		new Select(driver.findElement(By.id("ddlOwnIntellectualProperty")))
				.selectByVisibleText("No");
		Thread.sleep(500);
		driver.findElement(
				By.cssSelector("#ddlOwnIntellectualProperty > option[value=\"2\"]"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-21")).click();
		Thread.sleep(500);
		driver.findElement(By.id("pi_signature")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("pi_signature")).sendKeys("Nicholas chapa");
		Thread.sleep(500);
		driver.findElement(By.id("pi_signaturedate")).click();
		Thread.sleep(500);
		driver.findElement(
				By.xpath("//table[@id='trSignPICOPI']/tbody/tr/td[3]")).click();
		Thread.sleep(500);
		driver.findElement(By.name("proposalNotes5cddc20d2edd2f0d3c61c120PI"))
				.clear();
		Thread.sleep(500);
		driver.findElement(By.name("proposalNotes5cddc20d2edd2f0d3c61c120PI"))
				.sendKeys("Test");
		Thread.sleep(500);
		driver.findElement(By.id("ui-id-25")).click();
		Thread.sleep(500);
		driver.findElement(By.id("btnSaveProposal")).click();
		Thread.sleep(500);
		driver.findElement(By.id("BoxConfirmBtnOk")).click();
		Thread.sleep(500);
		assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*$"));
		Thread.sleep(500);
		driver.findElement(By.id("BoxAlertBtnOk")).click();
		Thread.sleep(500);
		driver.findElement(By.cssSelector("span.myProfile.icon-arrow-s"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.linkText("Log Out")).click();
		Thread.sleep(1000);

		// Research Director approval fail
		driver.get(baseUrl + "GPMS-NGAC/");
		driver.findElement(By.id("user_email")).clear();
		driver.findElement(By.id("user_email")).sendKeys(
				"directorcomputerscience@gmail.com");
		driver.findElement(By.id("user_password")).clear();
		driver.findElement(By.id("user_password")).sendKeys("gpmspassword");
		Thread.sleep(500);
		driver.findElement(By.name("commit")).click();
		Thread.sleep(500);

		assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*$"));
		Thread.sleep(500);
		driver.findElement(By.cssSelector("li.sfLevel1 > a > span")).click();
		Thread.sleep(500);

		((JavascriptExecutor) driver)
				.executeScript("var s=document.getElementById('edit0');s.click();");

		Thread.sleep(500);
		driver.findElement(By.id("ui-id-21")).click();
		Thread.sleep(500);

		WebElement txtSign = driver.findElement(By
				.name("5745fd43bcbb29192ce0d459University_Research_Director"));
		((JavascriptExecutor) driver)
				.executeScript(
						"arguments[0].readonly = false;arguments[0].removeAttribute('disabled');",
						txtSign);

		Thread.sleep(500);

		WebElement txtDate = driver
				.findElement(By
						.name("signaturedate5745fd43bcbb29192ce0d459University_Research_Director"));
		((JavascriptExecutor) driver)
				.executeScript(
						"arguments[0].readonly = false;arguments[0].removeAttribute('disabled');",
						txtDate);

		Thread.sleep(500);

		WebElement txtNote = driver
				.findElement(By
						.name("proposalNotes5745fd43bcbb29192ce0d459University_Research_Director"));
		((JavascriptExecutor) driver)
				.executeScript(
						"arguments[0].readonly = false;arguments[0].removeAttribute('disabled');",
						txtNote);

		Thread.sleep(1000);

		driver.findElement(
				By.name("5745fd43bcbb29192ce0d459University_Research_Director"))
				.sendKeys("research director");
		Thread.sleep(500);
		driver.findElement(
				By.name("signaturedate5745fd43bcbb29192ce0d459University_Research_Director"))
				.click();
		Thread.sleep(500);
		driver.findElement(
				By.xpath("//table[@id='trSignDirector']/tbody/tr/td[3]"))
				.click();
		Thread.sleep(500);
		driver.findElement(
				By.name("proposalNotes5745fd43bcbb29192ce0d459University_Research_Director"))
				.clear();
		Thread.sleep(500);
		driver.findElement(
				By.name("proposalNotes5745fd43bcbb29192ce0d459University_Research_Director"))
				.sendKeys("Test");
		Thread.sleep(500);

		((JavascriptExecutor) driver)
				.executeScript("var s=document.getElementById('btnApproveProposal');s.click();");
		Thread.sleep(500);
		driver.findElement(By.id("BoxConfirmBtnOk")).click();
		Thread.sleep(1000);
		
		driver.findElement(By.id("BoxAlertBtnOk")).click();
		Thread.sleep(1500);
		driver.findElement(By.id("ui-id-23")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("ui-id-24")).click();
		Thread.sleep(1000);

		// Fill up OSP Section
		((JavascriptExecutor) driver)
				.executeScript("$('#ui-id-24').find('input, select, textarea').each(function() {$(this).prop('disabled', false);});");
		Thread.sleep(1000);

		driver.findElement(By.id("txtAgencyList")).clear();
		Thread.sleep(500);

		driver.findElement(By.id("txtAgencyList")).sendKeys("Some agency");
		Thread.sleep(500);

		driver.findElement(By.id("chkFederal")).click();
		Thread.sleep(500);

		driver.findElement(By.id("chkNonProfitOrganization")).click();
		Thread.sleep(500);

		driver.findElement(By.id("chkNonIdahoLocalEntity")).click();
		Thread.sleep(500);

		driver.findElement(By.id("txtCFDANo")).clear();
		Thread.sleep(500);

		driver.findElement(By.id("txtCFDANo")).sendKeys("55555555");
		Thread.sleep(500);

		driver.findElement(By.id("txtProgramNo")).click();
		Thread.sleep(500);

		driver.findElement(By.id("txtProgramNo")).clear();
		Thread.sleep(500);
		driver.findElement(By.id("txtProgramNo")).sendKeys("47");
		Thread.sleep(500);
		driver.findElement(By.id("txtProgramTitle")).click();
		Thread.sleep(500);

		driver.findElement(By.id("txtProgramTitle")).clear();
		Thread.sleep(500);

		driver.findElement(By.id("txtProgramTitle")).sendKeys("2");
		Thread.sleep(500);

		driver.findElement(By.id("chkNoRecoveryNormal")).click();
		Thread.sleep(500);

		driver.findElement(By.id("chkTC")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlPISalaryIncluded")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);

		driver.findElement(
				By.cssSelector("#ddlPISalaryIncluded > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("txtPISalary")).clear();
		Thread.sleep(500);

		driver.findElement(By.id("txtPISalary")).sendKeys("1000000");
		Thread.sleep(500);

		driver.findElement(By.id("txtPIFringe")).clear();
		Thread.sleep(500);

		driver.findElement(By.id("txtPIFringe")).sendKeys("1");
		Thread.sleep(500);

		driver.findElement(By.id("txtDepartmentID")).clear();
		Thread.sleep(500);

		driver.findElement(By.id("txtDepartmentID")).sendKeys("10");
		Thread.sleep(500);

		driver.findElement(
				By.xpath("//div[@id='ui-id-24']/table/tbody/tr[13]/td[2]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("ddlInstitutionalCostDocumented")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlInstitutionalCostDocumented")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);

		driver.findElement(
				By.cssSelector("#ddlInstitutionalCostDocumented > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("ddlThirdPartyCostDocumented")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlThirdPartyCostDocumented")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);

		driver.findElement(
				By.cssSelector("#ddlThirdPartyCostDocumented > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("ddlPIEligibilityWaiver")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlPIEligibilityWaiver")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);

		driver.findElement(
				By.cssSelector("#ddlPIEligibilityWaiver > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("ddlCOIForms")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlCOIForms")))
				.selectByVisibleText("No");
		Thread.sleep(500);

		driver.findElement(By.cssSelector("#ddlCOIForms > option[value=\"2\"]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("ddlCOIForms")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlCOIForms")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);
		driver.findElement(By.cssSelector("#ddlCOIForms > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);

		driver.findElement(By.id("ddlCheckedExcludedPartyList")).click();
		Thread.sleep(500);

		new Select(driver.findElement(By.id("ddlCheckedExcludedPartyList")))
				.selectByVisibleText("Yes");
		Thread.sleep(500);

		driver.findElement(
				By.cssSelector("#ddlCheckedExcludedPartyList > option[value=\"1\"]"))
				.click();
		Thread.sleep(500);

		((JavascriptExecutor) driver)
				.executeScript("var s=document.getElementById('btnApproveProposal');s.click();");
		Thread.sleep(500);
		driver.findElement(By.id("BoxConfirmBtnOk")).click();
		Thread.sleep(500);
		assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*$"));
		Thread.sleep(500);

		assertTrue(driver.findElement(By.cssSelector("div.BoxError"))
				.isDisplayed());
		Thread.sleep(500);

		driver.findElement(By.id("BoxAlertBtnOk")).click();
		Thread.sleep(500);
		driver.findElement(By.cssSelector("span.myProfile.icon-arrow-s"))
				.click();
		Thread.sleep(500);
		driver.findElement(By.linkText("Log Out")).click();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

}
