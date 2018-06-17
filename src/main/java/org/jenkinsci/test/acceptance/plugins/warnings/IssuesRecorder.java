package org.jenkinsci.test.acceptance.plugins.warnings;

import java.util.ArrayList;
import java.util.List;

import org.jenkinsci.test.acceptance.po.AbstractStep;
import org.jenkinsci.test.acceptance.po.Control;
import org.jenkinsci.test.acceptance.po.Describable;
import org.jenkinsci.test.acceptance.po.Job;
import org.jenkinsci.test.acceptance.po.PageArea;
import org.jenkinsci.test.acceptance.po.PageAreaImpl;
import org.jenkinsci.test.acceptance.po.PostBuildStep;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Page object for the IssuesRecorder of the warnings plugin (white mountains release).
 *
 * @author Ullrich Hafner
 */
@Describable("Record static analysis results")
public class IssuesRecorder extends AbstractStep implements PostBuildStep {
    private Control toolsRepeatable = control("repeatable-add");
    private Control filtersRepeatable = control("repeatable-add[1]");
    private Control advancedButton = control("advanced-button");
    private Control enabledForFailureCheckBox = control("enabledForFailure");
    private Control ignoreAnalysisResultCheckBox = control("ignoreAnalysisResult");
    private Control overallResultMustBeSuccessCheckBox = control("overallResultMustBeSuccess");
    private Control referenceJobField = control("referenceJob");
    private IssueFilterPanel issueFilterPanel;

    /**
     * Creates a new page object.
     *
     * @param parent
     *         parent page object
     * @param path
     *         path on the parent page
     */
    public IssuesRecorder(final Job parent, final String path) {
        super(parent, path);
    }

    /**
     * Sets the name of the static analysis tool to use.
     *
     * @param toolName
     *         the tool name
     */
    public void setTool(final String toolName) {
        StaticAnalysisTool tool = new StaticAnalysisTool(this, "tools");
        tool.setTool(toolName);
    }

    /**
     * Adds a new static analysis tool configuration. The pattern will be empty, i.e. the console log is scanned.
     *
     * @param toolName
     *         the tool name
     */
    public void addTool(final String toolName) {
        createToolPageArea(toolName);
    }

    /**
     * Adds a new static analysis tool configuration.
     *
     * @param toolName
     *         the tool name
     * @param pattern
     *         the file name pattern
     */
    public void addTool(final String toolName, final String pattern) {
        StaticAnalysisTool tool = createToolPageArea(toolName);
        tool.setPattern(pattern);
    }

    /**
     * Enables or disables the checkbox 'enabledForFailure'.
     *
     * @param isChecked
     *         determines if the checkbox should be checked or not
     */
    public void setEnabledForFailure(final boolean isChecked) {
        enabledForFailureCheckBox.check(isChecked);
    }

    /**
     * Enables or disables the checkbox 'ignoreAnalysisResult'.
     *
     * @param isChecked
     *         determines if the checkbox should be checked or not
     */
    public void setIgnoreAnalysisResult(final boolean isChecked) {
        ignoreAnalysisResultCheckBox.check(isChecked);
    }

    /**
     * Enables or disables the checkbox 'overallResultMustBeSuccess'.
     *
     * @param isChecked
     *         determines if the checkbox should be checked or not
     */
    public void setOverallResultMustBeSuccess(final boolean isChecked) {
        overallResultMustBeSuccessCheckBox.check(isChecked);
    }

    /**
     * Sets the value of the input field 'referenceJob'.
     *
     * @param referenceJob
     *         the name of the referenceJob
     */
    public void setReferenceJobField(final String referenceJob) {
        referenceJobField.set(referenceJob);
    }

    /**
     * Opens the advanced section.
     */
    public void openAdvancedOptions() {
        advancedButton.click();
    }

    private StaticAnalysisTool createToolPageArea(final String toolName) {
        String path = createPageArea("tools", () -> toolsRepeatable.click());

        StaticAnalysisTool tool = new StaticAnalysisTool(this, path);
        tool.setTool(toolName);
        return tool;
    }

    /**
     * Page area of a static analysis tool configuration.
     */
    private static class StaticAnalysisTool extends PageAreaImpl {
        private final Control pattern = control("pattern");

        StaticAnalysisTool(final PageArea issuesRecorder, final String path) {
            super(issuesRecorder, path);
        }

        public void setTool(final String toolName) {
            Select select = new Select(self().findElement(By.className("dropdownList")));
            select.selectByVisibleText(toolName);
        }

        public void setPattern(final String pattern) {
            this.pattern.set(pattern);
        }
    }

    /**
     * Page area of a issue filter configuration.
     */
    private static class IssueFilterPanel extends PageAreaImpl {
        private final Control regexField = control("pattern");

        IssueFilterPanel(final PageArea area, final String path) {
            super(area, path);
        }

        private void setFilter(final String filter, final String regex) {
            Select filterField = new Select(self().findElement(By.className("dropdownList")));
            filterField.selectByVisibleText(filter);
            regexField.set(regex);
        }

    }

    /**
     * Adds a new issue filter.
     * @param filterName name of the filter
     * @param regex regular expression to apply
     */
    public void addIssueFilter(final String filterName, final String regex) {
        if (issueFilterPanel == null) {
            // fill initial existing filter
            issueFilterPanel = new IssueFilterPanel(this, "filters");
            issueFilterPanel.setFilter(filterName, regex);
        }
        else {
            // create new filter
            String path = createPageArea("filters", () -> filtersRepeatable.click());
            IssueFilterPanel filter = new IssueFilterPanel(this, path);
            filter.setFilter(filterName, regex);
        }
    }

}
