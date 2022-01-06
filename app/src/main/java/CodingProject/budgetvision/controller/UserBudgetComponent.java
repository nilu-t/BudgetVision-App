package CodingProject.budgetvision.controller;

import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component

/**
 * @Singleton annotation is used because UsersBudgetClass scope is limited to the lifetime of one UsersBudgetClass object.
 * This interface acts as "the injector".
 */
public interface UserBudgetComponent {

    /**
     * @return UsersBudgetClass main user object.
     * Dagger generates the implementation for this method through annotation processing.
     */
    UsersBudgetClass getMyMainUser();

}
