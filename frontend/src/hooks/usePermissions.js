import { useCallback } from "react";

import { canAccess } from "../auth/canAccess";

import { getUiNavigation } from "../auth/uiNavigation";



export function usePermissions(user) {

  const canAccessRoute = useCallback(

    (path) => {

      const policy = getUiNavigation(user);

      return canAccess(user, (policy.routes[path] || {}).rule || {});

    },

    [user]

  );

  const canAccessCreditPanel = useCallback(

    (key) => canAccess(user, getUiNavigation(user).creditApprovalPanels[key] || {}),

    [user]

  );

  const canAccessSystemPanel = useCallback(

    (key) => canAccess(user, getUiNavigation(user).systemPanels[key] || {}),

    [user]

  );



  return {

    canAccessRoute,

    canAccessCreditPanel,

    canAccessSystemPanel

  };

}

