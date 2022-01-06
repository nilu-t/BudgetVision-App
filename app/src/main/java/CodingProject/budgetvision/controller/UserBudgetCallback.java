package CodingProject.budgetvision.controller;

import java.io.IOException;

public interface UserBudgetCallback {

    void addBackgroundAnimation(Boolean isBkgAnimated);

    void welcomeBackGreeting(String firstName, String lastName);

    void updateTotalIncome();

    void updateDailyBudget();

    void addSubcategoriesToSheet(String categoryName, String subCategoryToAdd, String cost) throws IOException;

    String getSpreadsheetUrl();

    void setContentsOfTextView(int id, String newContents);

    void clearAllWarningMessage();

    void updateInitialValues();

    String getDailyBudget();

    String getTotalIncome();

}
