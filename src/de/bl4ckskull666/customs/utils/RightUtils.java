package de.bl4ckskull666.customs.utils;

import de.bl4ckskull666.customs.Customs;
import org.anjocaido.groupmanager.GroupManager;

public final class RightUtils {
    public static boolean addPermission(String p, String w, String perm) {
        if(!Customs.getPlugin().isGroupManager())
            return false;
        
        GroupManager grpmng = Customs.getPlugin().getGroupManager();
        grpmng.getWorldsHolder().getWorldData(w).getUser(p).addPermission(perm);
        return grpmng.getWorldsHolder().getWorldData(w).getUser(p).hasSamePermissionNode(perm);
    }
    
    public static boolean delPermission(String p, String w, String perm) {
        if(!Customs.getPlugin().isGroupManager())
            return false;
        
        GroupManager grpmng = Customs.getPlugin().getGroupManager();
        if(!grpmng.getWorldsHolder().getWorldData(w).getUser(p).hasSamePermissionNode(perm))
            return false;
        
        grpmng.getWorldsHolder().getWorldData(w).getUser(p).removePermission(perm);
        return true;
    }
}
