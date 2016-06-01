package se.loftux.modules.smarttenant.core;

import se.loftux.modules.smarttenant.overrides.SmartTenantPersonInfo;
import se.loftux.modules.smarttenant.overrides.exceptions.MultipleUsersWithSameEmailException;

/**
 * Created by bhagyasilva on 30/05/16.
 */
public interface SmartTenantService {

    SmartTenantPersonInfo getPersonByEmailOrNull(String email) throws MultipleUsersWithSameEmailException;

}
